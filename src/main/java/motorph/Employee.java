package motorph;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.Map;

/**
 * Represents an employee in the MotorPH payroll system.
 * Contains personal details, employment information, and compensation data.
 */
public abstract class Employee {

    // Personal Information
    private String employeeId;
    private String lastName;
    private String firstName;
    private LocalDate birthday;
    private String address;
    private String phoneNumber;

    // Government IDs
    private String sssNumber;
    private String philhealthNumber;
    private String tinNumber;
    private String pagibigNumber;

    // Employment Details
    private String status;
    private String position;
    private String supervisor;

    // Compensation
    private double basicSalary;
    private double riceSubsidy;
    private double phoneAllowance;
    private double clothingAllowance;
    private double grossRate;
    private double hourlyRate;

    private static final DateTimeFormatter MDY_FORMATTER = DateTimeFormatter.ofPattern("MM/dd/yyyy");

    /**
     * Constructor
     */
    public Employee() {}

    /**
     * Constructs an Employee from a map of data.
     * @param data A map where keys are CSV headers and values are the employee's data.
     */
    public Employee(Map<String, String> data) {
        this.employeeId = data.get("Employee #");
        this.lastName = data.get("Last Name");
        this.firstName = data.get("First Name");
        this.birthday = parseDate(data.get("Birthday"));
        this.address = data.get("Address");
        this.phoneNumber = data.get("Phone Number");
        this.sssNumber = data.get("SSS #");
        this.philhealthNumber = data.get("Philhealth #");
        this.tinNumber = data.get("TIN #");
        this.pagibigNumber = data.get("Pag-ibig #");
        this.status = data.get("Status");
        this.position = data.get("Position");
        this.supervisor = data.get("Immediate Supervisor");
        this.basicSalary = parseFormattedDouble(data.get("Basic Salary"));
        this.riceSubsidy = parseFormattedDouble(data.get("Rice Subsidy"));
        this.phoneAllowance = parseFormattedDouble(data.get("Phone Allowance"));
        this.clothingAllowance = parseFormattedDouble(data.get("Clothing Allowance"));
        this.grossRate = parseFormattedDouble(data.get("Gross Semi-monthly Rate"));
        this.hourlyRate = parseFormattedDouble(data.get("Hourly Rate"));
    }

    /**
     * Abstract method for payroll computation.
     * Each subclass must define how pay is computed.
     */
    public abstract double computePay(
        double regularHours,
        double overtimeRegularHours,
        double overtimeRestHours,
        int lateMinutes
);
    /**
     * Safely parses a string that may contain commas into a double.
     */
    private double parseFormattedDouble(String value) {
        if (value == null || value.trim().isEmpty()) {
            return 0.0;
        }
        try {
            return Double.parseDouble(value.replace(",", ""));
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }

    /**
     * Safely parses a date string into a LocalDate object.
     */
    private LocalDate parseDate(String dateStr) {
        if (dateStr == null || dateStr.trim().isEmpty()) {
            return null;
        }
        try {
            return LocalDate.parse(dateStr, MDY_FORMATTER);
        } catch (DateTimeParseException e) {
            System.err.println("Unable to parse date: '" + dateStr + "'. Expected format: MM/dd/yyyy.");
            return null;
        }
    }

    // Getters and Setters
    public String getEmployeeId() { return employeeId; }
    public void setEmployeeId(String employeeId) { this.employeeId = employeeId; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public LocalDate getBirthday() { return birthday; }
    public void setBirthday(LocalDate birthday) { this.birthday = birthday; }
    public void setBirthday(String birthdayStr) { this.birthday = parseDate(birthdayStr); }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public String getSssNumber() { return sssNumber; }
    public void setSssNumber(String sssNumber) { this.sssNumber = sssNumber; }

    public String getPhilhealthNumber() { return philhealthNumber; }
    public void setPhilhealthNumber(String philhealthNumber) { this.philhealthNumber = philhealthNumber; }

    public String getTinNumber() { return tinNumber; }
    public void setTinNumber(String tinNumber) { this.tinNumber = tinNumber; }

    public String getPagibigNumber() { return pagibigNumber; }
    public void setPagibigNumber(String pagibigNumber) { this.pagibigNumber = pagibigNumber; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getPosition() { return position; }
    public void setPosition(String position) { this.position = position; }

    public String getSupervisor() { return supervisor; }
    public void setSupervisor(String supervisor) { this.supervisor = supervisor; }

    public double getBasicSalary() { return basicSalary; }
    public void setBasicSalary(double basicSalary) { this.basicSalary = basicSalary; }

    public double getRiceSubsidy() { return riceSubsidy; }
    public void setRiceSubsidy(double riceSubsidy) { this.riceSubsidy = riceSubsidy; }

    public double getPhoneAllowance() { return phoneAllowance; }
    public void setPhoneAllowance(double phoneAllowance) { this.phoneAllowance = phoneAllowance; }

    public double getClothingAllowance() { return clothingAllowance; }
    public void setClothingAllowance(double clothingAllowance) { this.clothingAllowance = clothingAllowance; }

    public double getGrossRate() { return grossRate; }
    public void setGrossRate(double grossRate) { this.grossRate = grossRate; }

    public double getHourlyRate() { return hourlyRate; }
    public void setHourlyRate(double hourlyRate) { this.hourlyRate = hourlyRate; }

    /**
     * Converts the employee object into a Map representation.
     */
    public Map<String, String> toMap() {
        Map<String, String> map = new HashMap<>();
        map.put("Employee #", employeeId);
        map.put("Last Name", lastName);
        map.put("First Name", firstName);
        map.put("Birthday", birthday != null ? birthday.format(MDY_FORMATTER) : "");
        map.put("Address", address);
        map.put("Phone Number", phoneNumber);
        map.put("SSS #", sssNumber);
        map.put("Philhealth #", philhealthNumber);
        map.put("TIN #", tinNumber);
        map.put("Pag-ibig #", pagibigNumber);
        map.put("Status", status);
        map.put("Position", position);
        map.put("Immediate Supervisor", supervisor);
        map.put("Basic Salary", String.format("%.2f", basicSalary));
        map.put("Rice Subsidy", String.format("%.2f", riceSubsidy));
        map.put("Phone Allowance", String.format("%.2f", phoneAllowance));
        map.put("Clothing Allowance", String.format("%.2f", clothingAllowance));
        map.put("Gross Semi-monthly Rate", String.format("%.2f", grossRate));
        map.put("Hourly Rate", String.format("%.2f", hourlyRate));
        return map;
    }
}

