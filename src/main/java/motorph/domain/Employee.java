package motorph.domain;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

public abstract class Employee {

    // Basic Info
    protected String employeeId;
    protected String lastName;
    protected String firstName;
    protected LocalDate birthday;
    protected String address;
    protected String phoneNumber;

    // Government IDs
    protected String sssNumber;
    protected String philhealthNumber;
    protected String tinNumber;
    protected String pagibigNumber;

    // Employment Details
    protected String position;
    protected String immediateSupervisor;

    protected EmploymentType employmentType;

    protected String status;

    protected double basicSalary;
    protected double riceSubsidy;
    protected double phoneAllowance;
    protected double clothingAllowance;

    protected double grossSemiMonthlyRate;
    protected double hourlyRate;

    protected static final DateTimeFormatter MDY_FORMATTER =
            DateTimeFormatter.ofPattern("MM/dd/yyyy");

    public Employee() {}

    public Employee(Map<String, String> data) {
        this.employeeId = data.get("Employee #");
        this.lastName = data.get("Last Name");
        this.firstName = data.get("First Name");

        String bday = data.get("Birthday");
        if (bday != null && !bday.isBlank()) {
            this.birthday = LocalDate.parse(bday.trim(), MDY_FORMATTER);
        }

        this.address = data.get("Address");
        this.phoneNumber = data.get("Phone Number");

        this.sssNumber = data.get("SSS #");
        this.philhealthNumber = data.get("Philhealth #");
        this.tinNumber = data.get("TIN #");
        this.pagibigNumber = data.get("Pag-ibig #");

        this.position = data.get("Position");
        this.immediateSupervisor = data.get("Immediate Supervisor");

        String type = data.get("Employment Type");
        if (type != null && !type.isBlank()) {
            this.employmentType = EmploymentType.valueOf(type.trim().toUpperCase());
            this.status = this.employmentType.name(); // keep old flow alive
        }

        this.basicSalary = parseDoubleSafe(data.get("Basic Salary"));
        this.riceSubsidy = parseDoubleSafe(data.get("Rice Subsidy"));
        this.phoneAllowance = parseDoubleSafe(data.get("Phone Allowance"));
        this.clothingAllowance = parseDoubleSafe(data.get("Clothing Allowance"));
        this.grossSemiMonthlyRate = parseDoubleSafe(
                data.getOrDefault("Gross Semi-monthly Rate", data.get("Gross Semi-mor"))
        );

        this.hourlyRate = parseDoubleSafe(data.get("Hourly Rate"));
    }

    private double parseDoubleSafe(String value) {
        if (value == null || value.isBlank()) return 0;
        return Double.parseDouble(value.trim());
    }

    public String getEmployeeId() { return employeeId; }
    public String getLastName() { return lastName; }
    public String getFirstName() { return firstName; }
    public String getBirthday() {
        return (birthday == null) ? "" : birthday.format(MDY_FORMATTER);
    }

    public String getAddress() { return address; }
    public String getPhoneNumber() { return phoneNumber; }

    public String getSssNumber() { return sssNumber; }
    public String getPhilhealthNumber() { return philhealthNumber; }
    public String getTinNumber() { return tinNumber; }
    public String getPagibigNumber() { return pagibigNumber; }

    public String getPosition() { return position; }

    public String getSupervisor() { return immediateSupervisor; }

    public double getGrossRate() { return grossSemiMonthlyRate; }

    public double getBasicSalary() { return basicSalary; }
    public double getRiceSubsidy() { return riceSubsidy; }
    public double getPhoneAllowance() { return phoneAllowance; }
    public double getClothingAllowance() { return clothingAllowance; }
    public double getHourlyRate() { return hourlyRate; }

    public String getStatus() { return status; }
    public EmploymentType getEmploymentType() { return employmentType; }

    
    public void setLastName(String lastName) { this.lastName = lastName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public void setBirthday(String birthdayStr) {
        if (birthdayStr == null || birthdayStr.isBlank()) {
            this.birthday = null;
            return;
        }
        this.birthday = LocalDate.parse(birthdayStr.trim(), MDY_FORMATTER);
    }

    public void setAddress(String address) { this.address = address; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public void setSssNumber(String sssNumber) { this.sssNumber = sssNumber; }
    public void setPhilhealthNumber(String philhealthNumber) { this.philhealthNumber = philhealthNumber; }
    public void setTinNumber(String tinNumber) { this.tinNumber = tinNumber; }
    public void setPagibigNumber(String pagibigNumber) { this.pagibigNumber = pagibigNumber; }

    public void setStatus(String status) {
        this.status = status;
        try {
            this.employmentType = EmploymentType.valueOf(status.trim().toUpperCase());
        } catch (Exception ignored) {}
    }

    public void setPosition(String position) { this.position = position; }

    public void setSupervisor(String supervisor) { this.immediateSupervisor = supervisor; }

    public void setBasicSalary(double basicSalary) { this.basicSalary = basicSalary; }
    public void setRiceSubsidy(double riceSubsidy) { this.riceSubsidy = riceSubsidy; }
    public void setPhoneAllowance(double phoneAllowance) { this.phoneAllowance = phoneAllowance; }
    public void setClothingAllowance(double clothingAllowance) { this.clothingAllowance = clothingAllowance; }
    public void setHourlyRate(double hourlyRate) { this.hourlyRate = hourlyRate; }

   
    public Map<String, String> toMap() {
        Map<String, String> map = new HashMap<>();
        map.put("Employee #", employeeId);
        map.put("Last Name", lastName);
        map.put("First Name", firstName);
        map.put("Birthday", getBirthday());
        map.put("Address", address);
        map.put("Phone Number", phoneNumber);
        map.put("SSS #", sssNumber);
        map.put("Philhealth #", philhealthNumber);
        map.put("TIN #", tinNumber);
        map.put("Pag-ibig #", pagibigNumber);
        map.put("Employment Type", (employmentType == null) ? "" : employmentType.name());
        map.put("Position", position);
        map.put("Immediate Supervisor", immediateSupervisor);
        map.put("Basic Salary", String.valueOf(basicSalary));
        map.put("Rice Subsidy", String.valueOf(riceSubsidy));
        map.put("Phone Allowance", String.valueOf(phoneAllowance));
        map.put("Clothing Allowance", String.valueOf(clothingAllowance));
        map.put("Gross Semi-monthly Rate", String.valueOf(grossSemiMonthlyRate));
        map.put("Hourly Rate", String.valueOf(hourlyRate));
        return map;
    }

    public abstract double computePay(
            double regularHours,
            double overtimeRegularHours,
            double overtimeRestHours,
            int lateMinutes
    );
}