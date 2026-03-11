package motorph;

import motorph.domain.Attendance;
import motorph.repository.FileHandler;
import motorph.service.PayrollCalculator;
import motorph.domain.RegularEmployee;
import motorph.domain.Employee;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.temporal.WeekFields;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.stream.Collectors;

/**
 * The main class for the console-based MotorPH application.
 * Provides a text-based interface for managing employees, attendance, and payroll.
 */
public class MotorPH {

    /**
     * The main entry point for the console application.
     * @param args Command-line arguments (not used).
     */
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        FileHandler fileHandler = new FileHandler();
        PayrollCalculator payroll = new PayrollCalculator(fileHandler);

        printSectionHeader("MOTORPH PAYROLL SYSTEM");

        while (true) {
            System.out.println("MAIN MENU");
            System.out.println("1. Employee Management");
            System.out.println("2. Attendance Management");
            System.out.println("3. Payroll Calculation");
            System.out.println("0. Exit");
            System.out.print("Enter your choice: ");
            String choice = scanner.nextLine();

            switch (choice) {
                case "1":
                    employeeMenu(scanner, fileHandler);
                    break;
                case "2":
                    attendanceMenu(scanner, fileHandler);
                    break;
                case "3":
                    payrollMenu(scanner, payroll, fileHandler);
                    break;
                case "0":
                    System.out.println("Exiting system. Goodbye!");
                    scanner.close();
                    return;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    /**
     * Prints a formatted section header to the console.
     * @param title The title for the header.
     */
    private static void printSectionHeader(String title) {
        System.out.println("\n----------------------------------------------------");
        System.out.println("                " + title + "                ");
        System.out.println("----------------------------------------------------");
    }

    /**
     * Prints a standard section footer line to the console.
     */
    private static void printSectionFooter() {
        System.out.println("----------------------------------------------------\n");
    }

    /**
     * Displays and handles the Employee Management menu options.
     * @param scanner The Scanner object for user input.
     * @param fileHandler The FileHandler object for data operations.
     */
    private static void employeeMenu(Scanner scanner, FileHandler fileHandler) {
        while (true) {
            printSectionHeader("EMPLOYEE MANAGEMENT");
            System.out.println("1. View All Employees");
            System.out.println("2. View Specific Employee");
            System.out.println("3. Add New Employee");
            System.out.println("4. Update Employee");
            System.out.println("5. Delete Employee");
            System.out.println("0. Back to Main Menu");
            System.out.print("Enter your choice: ");
            String choice = scanner.nextLine();

            switch (choice) {
                case "1":
                    viewAllEmployees(fileHandler);
                    break;
                case "2":
                    viewSpecificEmployee(scanner, fileHandler);
                    break;
                case "3":
                    addEmployee(scanner, fileHandler);
                    break;
                case "4":
                    updateEmployee(scanner, fileHandler);
                    break;
                case "5":
                    deleteEmployee(scanner, fileHandler);
                    break;
                case "0":
                    return;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    /**
     * Retrieves and displays all employees in a formatted list.
     * @param fileHandler The FileHandler to read employee data.
     */
    private static void viewAllEmployees(FileHandler fileHandler) {
        List<Employee> employees = fileHandler.readEmployees();
        if (employees.isEmpty()) {
            printSectionHeader("EMPLOYEE LIST");
            System.out.println("No employees found.");
            printSectionFooter();
            return;
        }

        printSectionHeader("EMPLOYEE LIST");
        System.out.printf("%-8s %-20s %-20s %-30s %s\n",
                "ID", "Last Name", "First Name", "Position", "Basic Salary");
        System.out.println("-------------------------------------------------------------------------------------------------");

        for (Employee emp : employees) {
            System.out.printf("%-8s %-20s %-20s %-30s PHP %,.2f\n",
                    emp.getEmployeeId(),
                    emp.getLastName(),
                    emp.getFirstName(),
                    emp.getPosition(),
                    emp.getBasicSalary());
        }
        printSectionFooter();
    }
    
    /**
     * Prompts for an employee ID and displays detailed information for that employee.
     * @param scanner The Scanner object for user input.
     * @param fileHandler The FileHandler to find the employee.
     */
    private static void viewSpecificEmployee(Scanner scanner, FileHandler fileHandler) {
        printSectionHeader("VIEW EMPLOYEE DETAILS");
        System.out.print("Enter Employee ID: ");
        String id = scanner.nextLine();
        Employee employee = fileHandler.getEmployeeById(id);

        if (employee == null) {
            System.out.println("Employee not found!");
            printSectionFooter();
            return;
        }

        System.out.println("\nEmployee Details:");
        System.out.printf("%-20s: %s\n", "Employee ID", employee.getEmployeeId());
        System.out.printf("%-20s: %s, %s\n", "Name", employee.getLastName(), employee.getFirstName());
        System.out.printf("%-20s: %s\n", "Birthday", employee.getBirthday());
        System.out.printf("%-20s: %s\n", "Address", employee.getAddress());
        System.out.printf("%-20s: %s\n", "Phone Number", employee.getPhoneNumber());
        System.out.printf("%-20s: %s\n", "Status", employee.getStatus());
        System.out.printf("%-20s: %s\n", "Position", employee.getPosition());
        System.out.printf("%-20s: %s\n", "Supervisor", employee.getSupervisor());
        System.out.printf("%-20s: PHP %,.2f\n", "Basic Salary", employee.getBasicSalary());
        System.out.printf("%-20s: PHP %,.2f\n", "Rice Subsidy", employee.getRiceSubsidy());
        System.out.printf("%-20s: PHP %,.2f\n", "Phone Allowance", employee.getPhoneAllowance());
        System.out.printf("%-20s: PHP %,.2f\n", "Clothing Allowance", employee.getClothingAllowance());
        System.out.printf("%-20s: PHP %,.2f\n", "Gross Rate", employee.getGrossRate());
        System.out.printf("%-20s: PHP %,.2f\n", "Hourly Rate", employee.getHourlyRate());

        printSectionFooter();
    }

    /**
     * Formats a LocalDate object into "MM/dd/yyyy" format.
     * @param date The LocalDate to format.
     * @return The formatted date string, or "N/A" if the date is null.
     */
    private static String formatDate(LocalDate date) {
        return date != null ? date.format(DateTimeFormatter.ofPattern("MM/dd/yyyy")) : "N/A";
    }
    
    /**
     * Guides the user through adding a new employee to the system.
     * @param scanner The Scanner object for user input.
     * @param fileHandler The FileHandler to save the new employee.
     */
    private static void addEmployee(Scanner scanner, FileHandler fileHandler) {
        printSectionHeader("ADD NEW EMPLOYEE");
        Map<String, String> data = new HashMap<>();

        collectInput(scanner, data, "Employee ID", "Employee #");
        collectInput(scanner, data, "Last Name", "Last Name");
        collectInput(scanner, data, "First Name", "First Name");
        collectInput(scanner, data, "Birthday (MM/DD/YYYY)", "Birthday");
        collectInput(scanner, data, "Address", "Address");
        collectInput(scanner, data, "Phone Number", "Phone Number");
        collectInput(scanner, data, "SSS Number", "SSS #");
        collectInput(scanner, data, "PhilHealth Number", "Philhealth #");
        collectInput(scanner, data, "TIN Number", "TIN #");
        collectInput(scanner, data, "Pag-IBIG Number", "Pag-ibig #");
        collectInput(scanner, data, "Status", "Status");
        collectInput(scanner, data, "Position", "Position");
        collectInput(scanner, data, "Supervisor", "Immediate Supervisor");
        collectInput(scanner, data, "Basic Salary", "Basic Salary");
        collectInput(scanner, data, "Rice Subsidy", "Rice Subsidy");
        collectInput(scanner, data, "Phone Allowance", "Phone Allowance");
        collectInput(scanner, data, "Clothing Allowance", "Clothing Allowance");
        collectInput(scanner, data, "Gross Semi-monthly Rate", "Gross Semi-monthly Rate");
        collectInput(scanner, data, "Hourly Rate", "Hourly Rate");

        Employee employee = new RegularEmployee(data);
        fileHandler.saveEmployee(employee);
        System.out.println("\nEmployee added successfully!");
        printSectionFooter();
    }

    /**
     * A utility method to prompt the user for input and store it in a map.
     * @param scanner The Scanner object.
     * @param data The map to store the data in.
     * @param prompt The message to display to the user.
     * @param key The key to use for storing the data in the map.
     */
    private static void collectInput(Scanner scanner, Map<String, String> data, String prompt, String key) {
        System.out.print(prompt + ": ");
        data.put(key, scanner.nextLine());
    }

    /**
     * Guides the user through updating an existing employee's details.
     * @param scanner The Scanner object for user input.
     * @param fileHandler The FileHandler to update the employee data.
     */
    private static void updateEmployee(Scanner scanner, FileHandler fileHandler) {
        printSectionHeader("UPDATE EMPLOYEE");
        System.out.print("Enter Employee ID to update: ");
        String id = scanner.nextLine();
        Employee employee = fileHandler.getEmployeeById(id);

        if (employee == null) {
            System.out.println("Employee not found!");
            printSectionFooter();
            return;
        }

        displayUpdateMenu(scanner, employee);
        fileHandler.saveEmployee(employee); 
        System.out.println("\nEmployee updated successfully!");
        printSectionFooter();
    }

    /**
     * Displays an interactive menu for updating specific fields of an employee's record.
     * @param scanner The Scanner for user input.
     * @param employee The Employee object to be updated.
     */
    private static void displayUpdateMenu(Scanner scanner, Employee employee) {
        while (true) {
            System.out.println("\nSelect a field to update:");
            System.out.println("1. Last Name: " + employee.getLastName());
            System.out.println("2. First Name: " + employee.getFirstName());
            System.out.println("3. Address: " + employee.getAddress());
            System.out.println("4. Phone Number: " + employee.getPhoneNumber());
            System.out.println("5. Status: " + employee.getStatus());
            System.out.println("6. Position: " + employee.getPosition());
            System.out.println("7. Supervisor: " + employee.getSupervisor());
            System.out.println("0. Finish and Save");

            System.out.print("Enter your choice: ");
            String choice = scanner.nextLine();

            if (choice.equals("0")) break;

            handleFieldUpdate(scanner, choice, employee);
        }
    }
    
    /**
     * Handles the logic for updating a specific field based on user's choice.
     * @param scanner The Scanner for user input.
     * @param choice The user's menu choice.
     * @param employee The Employee object to update.
     */
    private static void handleFieldUpdate(Scanner scanner, String choice, Employee employee) {
        System.out.print("Enter new value: ");
        String newValue = scanner.nextLine();
        switch (choice) {
            case "1": employee.setLastName(newValue); break;
            case "2": employee.setFirstName(newValue); break;
            case "3": employee.setAddress(newValue); break;
            case "4": employee.setPhoneNumber(newValue); break;
            case "5": employee.setStatus(newValue); break;
            case "6": employee.setPosition(newValue); break;
            case "7": employee.setSupervisor(newValue); break;
            default: System.out.println("Invalid choice. Please try again.");
        }
    }

    /**
     * Prompts for an employee ID and deletes the corresponding record.
     * @param scanner The Scanner object for user input.
     * @param fileHandler The FileHandler to delete the employee from.
     */
    private static void deleteEmployee(Scanner scanner, FileHandler fileHandler) {
        printSectionHeader("DELETE EMPLOYEE");
        System.out.print("Enter Employee ID to delete: ");
        String id = scanner.nextLine();

        if (fileHandler.deleteEmployee(id)) {
            System.out.println("\nEmployee deleted successfully!");
        } else {
            System.out.println("\nEmployee not found!");
        }
        printSectionFooter();
    }

    /**
     * Displays and handles the Attendance Management menu.
     * @param scanner The Scanner object for user input.
     * @param fileHandler The FileHandler for attendance data.
     */
    private static void attendanceMenu(Scanner scanner, FileHandler fileHandler) {
        while (true) {
            printSectionHeader("ATTENDANCE MANAGEMENT");
            System.out.println("1. View Employee Attendance Records");
            System.out.println("0. Back to Main Menu");
            System.out.print("Enter your choice: ");
            String choice = scanner.nextLine();

            switch (choice) {
                case "1":
                    viewEmployeeAttendance(scanner, fileHandler);
                    break;
                case "0":
                    return;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }
    
    /**
     * Displays attendance records for a specific employee, with filtering by month and week.
     * @param scanner The Scanner for user input.
     * @param fileHandler The FileHandler for data retrieval.
     */
    private static void viewEmployeeAttendance(Scanner scanner, FileHandler fileHandler) {
        printSectionHeader("VIEW EMPLOYEE ATTENDANCE");
        System.out.print("Enter Employee ID: ");
        String employeeId = scanner.nextLine();
        Employee employee = fileHandler.getEmployeeById(employeeId);

        if (employee == null) {
            System.out.println("Employee not found!");
            printSectionFooter();
            return;
        }

        List<Attendance> allRecords = fileHandler.getAllAttendanceRecords().stream()
                .filter(r -> r.getEmployeeId().equals(employeeId))
                .sorted(Comparator.comparing(Attendance::getDate))
                .collect(Collectors.toList());

        if (allRecords.isEmpty()) {
            System.out.println("No attendance records found for this employee.");
            printSectionFooter();
            return;
        }

        displayAvailableMonths(allRecords);
        int monthChoice = Integer.parseInt(scanner.nextLine());

        List<Attendance> filteredByMonth = filterBySelectedMonth(allRecords, monthChoice);
        if (filteredByMonth == null) return;

        displayWeekOptions();
        int weekChoice = Integer.parseInt(scanner.nextLine());

        displayFilteredAttendance(filteredByMonth, weekChoice, employee);
        printSectionFooter();
    }

    /**
     * Displays available months from a list of attendance records.
     * @param records The list of attendance records.
     */
    private static void displayAvailableMonths(List<Attendance> records) {
        List<YearMonth> availableMonths = records.stream()
                .map(r -> YearMonth.from(r.getDate()))
                .distinct()
                .sorted()
                .collect(Collectors.toList());

        System.out.println("\nAvailable Months:");
        for (int i = 0; i < availableMonths.size(); i++) {
            System.out.printf("%d. %s%n", i + 1, availableMonths.get(i).format(DateTimeFormatter.ofPattern("MMMM")));
        }
        System.out.print("Select month: ");
    }

    /**
     * Filters a list of attendance records by the user's chosen month.
     * @param records The list of records to filter.
     * @param choice The user's integer choice corresponding to the displayed month list.
     * @return A new list filtered by the selected month, or null if choice is invalid.
     */
    private static List<Attendance> filterBySelectedMonth(List<Attendance> records, int choice) {
        List<YearMonth> availableMonths = records.stream()
                .map(r -> YearMonth.from(r.getDate()))
                .distinct()
                .sorted()
                .collect(Collectors.toList());

        if (choice <= 0 || choice > availableMonths.size()) {
            System.out.println("Invalid month selection.");
            return null;
        }

        YearMonth selectedMonth = availableMonths.get(choice - 1);
        return records.stream()
                .filter(r -> YearMonth.from(r.getDate()).equals(selectedMonth))
                .collect(Collectors.toList());
    }

    /**
     * Displays week selection options to the console.
     */
    private static void displayWeekOptions() {
        System.out.println("\nWeek Options:");
        System.out.println("1. Week 1");
        System.out.println("2. Week 2");
        System.out.println("3. Week 3");
        System.out.println("4. Week 4");
        System.out.println("5. All Weeks");
        System.out.print("Select week (1-5): ");
    }

    /**
     * Displays attendance records filtered by the selected week.
     * @param records The records already filtered by month.
     * @param weekChoice The user's choice of week (1-5).
     * @param employee The employee whose records are being displayed.
     */
    private static void displayFilteredAttendance(List<Attendance> records, int weekChoice, Employee employee) {
        Map<Integer, List<Attendance>> weeklyRecords = records.stream()
                .collect(Collectors.groupingBy(
                        r -> r.getDate().get(WeekFields.ISO.weekOfMonth())
                ));

        System.out.println("\nATTENDANCE RECORDS FOR " + employee.getLastName() + ", " + employee.getFirstName());
        System.out.println("----------------------------------------");

        if (weekChoice == 5) { // All weeks
            weeklyRecords.entrySet().stream()
                    .sorted(Map.Entry.comparingByKey())
                    .forEach(entry -> printWeekAttendance(entry.getKey(), entry.getValue()));
        } else if (weekChoice >= 1 && weekChoice <= 4) {
            if (weeklyRecords.containsKey(weekChoice)) {
                printWeekAttendance(weekChoice, weeklyRecords.get(weekChoice));
            } else {
                System.out.println("No records found for week " + weekChoice);
            }
        } else {
            System.out.println("Invalid week selection.");
        }
    }
    
    /**
     * Prints a formatted block of attendance records for a single week.
     * @param weekNumber The week number.
     * @param records The list of attendance records for that week.
     */
    private static void printWeekAttendance(int weekNumber, List<Attendance> records) {
        if (records.isEmpty()) return;
        
        System.out.printf("\nWeek %d (%s to %s):\n",
                weekNumber,
                records.get(0).getDate().format(DateTimeFormatter.ofPattern("MM/dd/yyyy")),
                records.get(records.size()-1).getDate().format(DateTimeFormatter.ofPattern("MM/dd/yyyy")));

        System.out.printf("%-12s %-8s %-8s\n", "Date", "Time In", "Time Out");
        System.out.println("----------------------------");

        for (Attendance record : records) {
            System.out.printf("%-12s %-8s %-8s\n",
                    record.getDate().format(DateTimeFormatter.ofPattern("MM/dd/yyyy")),
                    record.getTimeIn(),
                    record.getTimeOut());
        }
    }
    
    /**
     * Displays the payroll calculation menu and handles user choices.
     * @param scanner The Scanner object for user input.
     * @param payroll The PayrollCalculator for calculations.
     * @param fileHandler The FileHandler for data retrieval.
     */
    private static void payrollMenu(Scanner scanner, PayrollCalculator payroll, FileHandler fileHandler) {
        while (true) {
            printSectionHeader("PAYROLL CALCULATION");
            System.out.println("1. Calculate for Specific Employee");
            System.out.println("2. Calculate for All Employees");
            System.out.println("0. Back to Main Menu");
            System.out.print("Enter your choice: ");
            String choice = scanner.nextLine();

            switch (choice) {
                case "1":
                    calculateEmployeePayroll(scanner, payroll, fileHandler);
                    break;
                case "2":
                    calculateAllEmployeesPayroll(scanner, payroll, fileHandler);
                    break;
                case "0":
                    return;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    /**
     * Guides a user through calculating payroll for a specific employee.
     * @param scanner The Scanner for user input.
     * @param payroll The PayrollCalculator instance.
     * @param fileHandler The FileHandler instance.
     */
    private static void calculateEmployeePayroll(Scanner scanner, PayrollCalculator payroll, FileHandler fileHandler) {
        printSectionHeader("PAYROLL CALCULATION");
        System.out.print("Enter Employee ID: ");
        String employeeId = scanner.nextLine();
        Employee employee = fileHandler.getEmployeeById(employeeId); 

        if (employee == null) {
            System.out.println("Employee not found!");
            printSectionFooter();
            return;
        }

        List<YearMonth> availableMonths = payroll.getAvailableMonths(employeeId);
        if (availableMonths.isEmpty()) {
            System.out.println("No attendance records found for this employee.");
            printSectionFooter();
            return;
        }

        System.out.println("\nAvailable Months:");
        for (int i = 0; i < availableMonths.size(); i++) {
            System.out.printf("%d. %s%n", i+1, availableMonths.get(i).format(DateTimeFormatter.ofPattern("MMMM")));
        }
        System.out.print("Select month (number): ");
        int monthChoice = Integer.parseInt(scanner.nextLine()) - 1;
        YearMonth selectedMonth = availableMonths.get(monthChoice);

        displayWeekOptions();
        int weekChoice = Integer.parseInt(scanner.nextLine());

        payroll.calculateWeeklyPayroll(employeeId, selectedMonth, weekChoice == 5 ? 0 : weekChoice);
        printSectionFooter();
    }

    /**
     * Guides a user through calculating payroll for all employees for a given period.
     * @param scanner The Scanner for user input.
     * @param payroll The PayrollCalculator instance.
     * @param fileHandler The FileHandler instance.
     */
    private static void calculateAllEmployeesPayroll(Scanner scanner, PayrollCalculator payroll, FileHandler fileHandler) {
        printSectionHeader("PAYROLL CALCULATION FOR ALL EMPLOYEES");
        List<YearMonth> availableMonths = payroll.getAllAvailableMonths();

        if (availableMonths.isEmpty()) {
            System.out.println("No attendance records found.");
            printSectionFooter();
            return;
        }

        System.out.println("\nAvailable Months:");
        for (int i = 0; i < availableMonths.size(); i++) {
            System.out.printf("%d. %s%n", i+1, availableMonths.get(i).format(DateTimeFormatter.ofPattern("MMMM")));
        }
        System.out.print("Select month (number): ");
        int monthChoice = Integer.parseInt(scanner.nextLine()) - 1;
        YearMonth selectedMonth = availableMonths.get(monthChoice);
        
        displayWeekOptions();
        int weekChoice = Integer.parseInt(scanner.nextLine());

        payroll.calculateAllWeeklyPayroll(selectedMonth, weekChoice == 5 ? 0 : weekChoice);
        printSectionFooter();
    }

}
