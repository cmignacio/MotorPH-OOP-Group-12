package motorph.service;

import motorph.domain.Employee;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.stream.Collectors;
import motorph.domain.Attendance;
import motorph.repository.FileHandler;

public class PayrollCalculator {

    private FileHandler fileHandler;

    public PayrollCalculator(FileHandler fileHandler) {
        this.fileHandler = fileHandler;
    }

    private static final double REGULAR_DAY_OT_MULTIPLIER = 1.25;
    private static final double REST_DAY_OT_MULTIPLIER = 1.30;
    private static final LocalTime WORK_START_TIME = LocalTime.of(8, 0);
    private static final int GRACE_PERIOD_MINUTES = 10;
    private static final int REGULAR_HOURS_PER_DAY = 8;
    private static final int MINUTES_PER_HOUR = 60;

    private double calculateRegularHours(LocalTime timeIn, LocalTime timeOut) {
        long totalMinutes = ChronoUnit.MINUTES.between(timeIn, timeOut);
        double totalHours = totalMinutes / (double) MINUTES_PER_HOUR;
        return Math.min(totalHours, REGULAR_HOURS_PER_DAY);
    }

    private double calculateOvertimeHours(LocalDate date, LocalTime timeIn, LocalTime timeOut) {
        long totalMinutes = ChronoUnit.MINUTES.between(timeIn, timeOut);
        double totalHours = totalMinutes / (double) MINUTES_PER_HOUR;
        return Math.max(0, totalHours - REGULAR_HOURS_PER_DAY);
    }

    public int calculateLateMinutes(LocalTime timeIn) {
        LocalTime graceTime = WORK_START_TIME.plusMinutes(GRACE_PERIOD_MINUTES);
        if (timeIn.isAfter(graceTime)) {
            return (int) ChronoUnit.MINUTES.between(WORK_START_TIME, timeIn);
        }
        return 0;
    }

    private double calculateSSS(double monthlySalary) {
        if (monthlySalary < 3250) return 135.00;
        else if (monthlySalary <= 24750) return 1102.50;
        else return 1125.00;
    }

    private double calculatePhilHealth(double monthlySalary) {
        if (monthlySalary <= 10000.00) return 150.00;
        else if (monthlySalary < 60000.00) return monthlySalary * 0.015;
        else return 900.00;
    }

    private double calculatePagIBIG(double monthlySalary) {
        if (monthlySalary <= 1500.00) return monthlySalary * 0.01;
        else return Math.min(monthlySalary * 0.02, 100.00);
    }

    private double calculateWithholdingTax(double monthlySalary,
                                           double sss,
                                           double philhealth,
                                           double pagibig) {

        double taxableIncome = monthlySalary - (sss + philhealth + pagibig);

        if (taxableIncome <= 20833.00) return 0.00;
        else if (taxableIncome <= 33333.00)
            return (taxableIncome - 20833.00) * 0.20;
        else if (taxableIncome <= 66667.00)
            return 2500.00 + (taxableIncome - 33333.00) * 0.25;
        else
            return 10833.00 + (taxableIncome - 66667.00) * 0.30;
    }

    public void processPayroll(String employeeId, YearMonth month, int weekNumber) {

        Employee employee = fileHandler.getEmployeeById(employeeId);
        if (employee == null) {
            System.out.println("Employee not found!");
            return;
        }

        List<Attendance> records = fileHandler.getAllAttendanceRecords().stream()
                .filter(r -> r.getEmployeeId().equals(employeeId))
                .filter(r -> YearMonth.from(
                        r.getDate().with(TemporalAdjusters.nextOrSame(DayOfWeek.FRIDAY))
                ).equals(month))
                .sorted(Comparator.comparing(Attendance::getDate))
                .collect(Collectors.toList());

        if (records.isEmpty()) {
            System.out.println("No attendance records found for " + month);
            return;
        }

        Map<LocalDate, List<Attendance>> weeklyData = records.stream()
                .collect(Collectors.groupingBy(
                        r -> r.getDate().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
                ));

        printPayrollReport(employee, weeklyData, month, weekNumber);
    }

    private void printPayrollReport(Employee employee,
                                    Map<LocalDate, List<Attendance>> weeklyData,
                                    YearMonth month,
                                    int weekNumber) {

        System.out.println("\n-------------------------------");
        System.out.println("       PAYROLL REPORT");
        System.out.println("-------------------------------");
        System.out.printf("Employee: %s, %s (%s)\n",
                employee.getLastName(),
                employee.getFirstName(),
                employee.getEmployeeId());

        System.out.println("Payroll Month: " +
                month.format(DateTimeFormatter.ofPattern("MMMM yyyy")));

        List<LocalDate> weekStarts = weeklyData.keySet().stream()
                .sorted()
                .collect(Collectors.toList());

        for (int i = 0; i < weekStarts.size(); i++) {
            if (weekNumber == 0 || weekNumber == i + 1) {
                printWeekDetails(i + 1, weeklyData.get(weekStarts.get(i)), employee);
            }
        }
    }

    private void printWeekDetails(int weekNumber,
                                  List<Attendance> records,
                                  Employee employee) {

        double totalRegularHours = 0;
        int totalLateMinutes = 0;

        Map<Boolean, Double> overtimeHours = new HashMap<>();
        overtimeHours.put(true, 0.0);
        overtimeHours.put(false, 0.0);

        for (Attendance record : records) {

            totalRegularHours += calculateRegularHours(
                    record.getTimeIn(),
                    record.getTimeOut()
            );

            double overtime = calculateOvertimeHours(
                    record.getDate(),
                    record.getTimeIn(),
                    record.getTimeOut()
            );

            totalLateMinutes += calculateLateMinutes(record.getTimeIn());

            boolean isRestDay =
                    record.getDate().getDayOfWeek() == DayOfWeek.SATURDAY ||
                    record.getDate().getDayOfWeek() == DayOfWeek.SUNDAY;

            overtimeHours.put(isRestDay,
                    overtimeHours.get(isRestDay) + overtime);
        }

        double overtimeRegular = overtimeHours.get(false);
        double overtimeRest = overtimeHours.get(true);

        double grossPay = employee.computePay(
                totalRegularHours,
                overtimeRegular,
                overtimeRest,
                totalLateMinutes
        );

        double sss = calculateSSS(employee.getBasicSalary());
        double philhealth = calculatePhilHealth(employee.getBasicSalary());
        double pagibig = calculatePagIBIG(employee.getBasicSalary());

        double deductions =
                (sss + philhealth + pagibig) / 4 +
                calculateWithholdingTax(
                        employee.getBasicSalary(),
                        sss,
                        philhealth,
                        pagibig
                ) / 4;

        double netPay = grossPay - deductions;

        System.out.println("\nWeek " + weekNumber);
        System.out.printf("Gross Weekly Pay: PHP %,.2f\n", grossPay);
        System.out.printf("Net Weekly Pay: PHP %,.2f\n", netPay);
    }

    public void calculateWeeklyPayroll(String employeeId,
                                       YearMonth month,
                                       int weekNumber) {
        processPayroll(employeeId, month, weekNumber);
    }

    public void calculateAllWeeklyPayroll(YearMonth month,
                                          int weekNumber) {

        for (Employee employee : fileHandler.readEmployees()) {
            processPayroll(employee.getEmployeeId(), month, weekNumber);
        }
    }

    // REQUIRED FOR GUI AND MotorPH.java

    public List<YearMonth> getAvailableMonths(String employeeId) {
        return fileHandler.getAllAttendanceRecords().stream()
                .filter(r -> r.getEmployeeId().equals(employeeId))
                .map(r -> YearMonth.from(
                        r.getDate().with(TemporalAdjusters.nextOrSame(DayOfWeek.FRIDAY))
                ))
                .distinct()
                .sorted()
                .collect(Collectors.toList());
    }

    public List<YearMonth> getAllAvailableMonths() {
        return fileHandler.getAllAttendanceRecords().stream()
                .map(r -> YearMonth.from(
                        r.getDate().with(TemporalAdjusters.nextOrSame(DayOfWeek.FRIDAY))
                ))
                .distinct()
                .sorted()
                .collect(Collectors.toList());
    }
}
