package motorph.gui.admin;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.List;
import motorph.PayrollCalculator;
import motorph.FileHandler;
import motorph.Employee;
import java.util.ArrayList;

/**
 * A frame for calculating and displaying payroll information for employees.
 */
public class PayrollFrame extends JFrame {

    private PayrollCalculator payrollCalculator;
    private FileHandler fileHandler;
    private Employee currentEmployee; 
    private List<Employee> employeeList; 

    /**
     * Constructs a new PayrollFrame and initializes its components.
     */
    public PayrollFrame() {
        initComponents();
        fileHandler = new FileHandler();
        payrollCalculator = new PayrollCalculator(fileHandler);

        // Load initial data for dropdowns
        try {
            employeeList = fileHandler.readEmployees();
            // Handle case where employee list is empty or null
            if (employeeList == null || employeeList.isEmpty()) {
                employeeList = new ArrayList<>();
                System.err.println("Employee list is empty or null after loading in PayrollFrame.");
            }
            populateEmployeeComboBox(); // Fill employee dropdown
            populateMonthComboBox(); // Fill month dropdown
            populateWeekComboBox(); // Fill week dropdown
        } catch (Exception e) {
            System.err.println("Error loading employee data in PayrollFrame: " + e.getMessage());
            e.printStackTrace();
        }

        // Set initial text in display areas
        employeeDetailsTextArea.setText("Select an employee and click Calculate to view details.");
        resultTextArea.setText("Payroll results will appear here.");

        setupComputeButton(); // Set up action for the calculate button
        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE); // Close only this frame
    }

    /**
     * Fills the employee dropdown with employee names and IDs.
     */
    private void populateEmployeeComboBox() {
        employeeComboBox.removeAllItems(); // Clear existing items
        if (employeeList != null && !employeeList.isEmpty()) {
            // Add each employee to the dropdown
            for (Employee employee : employeeList) {
                employeeComboBox.addItem(employee.getEmployeeId() + " - " + employee.getLastName() + ", " + employee.getFirstName());
            }
        } else {
            // Add a message if no employees are found
            employeeComboBox.addItem("No employees found");
        }
    }

    /**
     * Fills the month dropdown with available months from payroll data.
     */
    private void populateMonthComboBox() {
        monthComboBox.removeAllItems(); 
        List<YearMonth> months = payrollCalculator.getAllAvailableMonths(); // Get months from calculator
        if (months != null && !months.isEmpty()) {
            // Add each month to the dropdown, formatted as "Month Year"
            for (YearMonth month : months) {
                monthComboBox.addItem(month.format(DateTimeFormatter.ofPattern("MMMM yyyy")));
            }
        } else {
             // Add a message if no months are available
            monthComboBox.addItem("No months available");
        }
    }

    /**
     * Fills the week dropdown with week options.
     */
    private void populateWeekComboBox() {
        weekComboBox.removeAllItems(); // Clear existing items
        weekComboBox.addItem("All Weeks");
        weekComboBox.addItem("Week 1");
        weekComboBox.addItem("Week 2");
        weekComboBox.addItem("Week 3");
        weekComboBox.addItem("Week 4");
    }

    /**
     * Sets up the action listener for the calculate button.
     */
    private void setupComputeButton() {
        calculateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                calculateAndDisplayPayroll(); // Call the calculation method
            }
        });
    }

    /**
     * Calculates and displays payroll based on selected options.
     */
    private void calculateAndDisplayPayroll() {
        // Get selected employee, month, and week
        Object selectedEmployeeItem = employeeComboBox.getSelectedItem();
        Object selectedMonthItem = monthComboBox.getSelectedItem();
        int selectedWeekIndex = weekComboBox.getSelectedIndex(); // 0 for All, 1-4 for weeks

        // Check if necessary items are selected
        if (selectedEmployeeItem == null || selectedMonthItem == null
                || selectedEmployeeItem.equals("No employees found")
                || selectedMonthItem.equals("No months available")) {
            resultTextArea.setText("Please select an employee and a month.");
            return;
        }

        try {
            // Get employee ID from the selected employee dropdown item
            String selectedItemText = selectedEmployeeItem.toString();
            String selectedEmployeeId = selectedItemText.split(" - ")[0];

            // Get the month string and parse it
            String selectedMonthString = selectedMonthItem.toString();
            YearMonth selectedMonth = parseYearMonth(selectedMonthString);

            // Find the employee object
            currentEmployee = findEmployee(selectedEmployeeId);
            if (currentEmployee == null) {
                resultTextArea.setText("Employee not found: " + selectedEmployeeId);
                employeeDetailsTextArea.setText("No employee details available.");
                return;
            }

            // Update the employee details display area
            updateEmployeeDetails();

            // Convert week index to a number (0 for All, 1 for Week 1, etc.)
            int selectedWeek = selectedWeekIndex; // The index directly corresponds to the week number (0=All, 1=Week 1)

            // Redirect System.out to capture the payroll output
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            PrintStream ps = new PrintStream(baos);
            PrintStream old = System.out; // Store original System.out
            System.setOut(ps); // Set System.out to the new stream

            // Call the payroll calculation logic
            payrollCalculator.processPayroll(currentEmployee.getEmployeeId(), selectedMonth, selectedWeek);

            // Restore original System.out
            System.out.flush();
            System.setOut(old);

            // Display the captured output
            String payrollResults = baos.toString();
            if (payrollResults == null || payrollResults.trim().isEmpty()) {
                resultTextArea.setText("No payroll data available for the selected criteria.");
            } else {
                resultTextArea.setText(payrollResults);
            }

        } catch (DateTimeParseException ex) {
            // Handle errors in date format
            JOptionPane.showMessageDialog(this, "Invalid month format selected.", "Input Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            // Handle other calculation errors
            resultTextArea.setText("Error processing payroll: " + ex.getMessage());
            ex.printStackTrace(); // Print error details to console
        }
    }

    /**
     * Updates the employee details text area with current employee information.
     */
    private void updateEmployeeDetails() {
        if (currentEmployee != null) {
            String details = "Employee ID: " + currentEmployee.getEmployeeId() + "\n" +
                             "Name: " + currentEmployee.getFirstName() + " " + currentEmployee.getLastName() + "\n" +
                             "Status: " + currentEmployee.getStatus() + "\n" +
                             "Position: " + currentEmployee.getPosition();
            employeeDetailsTextArea.setText(details);
        } else {
            employeeDetailsTextArea.setText("Employee details not available.");
        }
    }

    /**
     * Parses a month string into a YearMonth object.
     * @param monthString The string to parse 
     * @return The parsed YearMonth object, or current month if parsing fails
     */
    private YearMonth parseYearMonth(String monthString) {
        if (monthString == null || monthString.isEmpty() || monthString.equals("No months available")) {
            return YearMonth.now(); // Return current month as a default or error indicator
        }

        try {
            // Attempt to parse with expected format "Month Year"
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM yyyy");
            return YearMonth.parse(monthString, formatter);
        } catch (DateTimeParseException e) {
             // If parsing fails, show an error message and return current month
             JOptionPane.showMessageDialog(this,
                "Invalid month format: " + monthString +
                "\nExpected format: 'Month Year' (e.g., January 2023)",
                "Date Format Error",
                JOptionPane.ERROR_MESSAGE);
            return YearMonth.now();
        }
    }

    /**
     * Finds an employee object from the list by their ID.
     * @param employeeId The ID of the employee to find
     * @return The Employee object if found, null otherwise
     */
    private Employee findEmployee(String employeeId) {
        if (employeeList == null) {
            return null;
        }
        // Iterate through the list to find the employee with the matching ID
        for (Employee emp : employeeList) {
            if (emp != null && emp.getEmployeeId() != null && emp.getEmployeeId().equals(employeeId)) {
                return emp; // Return the found employee
            }
        }
        return null; // Return null if employee is not found
    }


    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel2 = new javax.swing.JPanel();
        payrollTitle = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        calculateButton = new javax.swing.JButton();
        jLabel4 = new javax.swing.JLabel();
        employeeComboBox = new javax.swing.JComboBox<>();
        monthComboBox = new javax.swing.JComboBox<>();
        weekComboBox = new javax.swing.JComboBox<>();
        jPanel15 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        resultTextArea = new javax.swing.JTextArea();
        jLabel1 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jScrollPane4 = new javax.swing.JScrollPane();
        employeeDetailsTextArea = new javax.swing.JTextArea();
        jLabel6 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setBackground(new java.awt.Color(255, 255, 255));

        jPanel2.setBackground(new java.awt.Color(239, 239, 239));
        jPanel2.setForeground(new java.awt.Color(0, 0, 0));

        payrollTitle.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        payrollTitle.setForeground(new java.awt.Color(24, 59, 78));
        payrollTitle.setText("Payroll");

        jLabel2.setForeground(new java.awt.Color(0, 0, 0));
        jLabel2.setText("Employee");

        jLabel3.setForeground(new java.awt.Color(0, 0, 0));
        jLabel3.setText("Month");

        calculateButton.setText("Compute");
        calculateButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                calculateButtonActionPerformed(evt);
            }
        });

        jLabel4.setForeground(new java.awt.Color(0, 0, 0));
        jLabel4.setText("Week");

        employeeComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        monthComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        monthComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                monthComboBoxActionPerformed(evt);
            }
        });

        weekComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        weekComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                weekComboBoxActionPerformed(evt);
            }
        });

        jPanel15.setBackground(new java.awt.Color(139, 0, 0));
        jPanel15.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        javax.swing.GroupLayout jPanel15Layout = new javax.swing.GroupLayout(jPanel15);
        jPanel15.setLayout(jPanel15Layout);
        jPanel15Layout.setHorizontalGroup(
            jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        jPanel15Layout.setVerticalGroup(
            jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 41, Short.MAX_VALUE)
        );

        resultTextArea.setEditable(false);
        resultTextArea.setColumns(20);
        resultTextArea.setRows(5);
        jScrollPane1.setViewportView(resultTextArea);

        jLabel1.setForeground(new java.awt.Color(0, 0, 0));
        jLabel1.setText("Payroll Report");

        jLabel5.setForeground(new java.awt.Color(0, 0, 0));
        jLabel5.setText("Employee Details:");

        employeeDetailsTextArea.setEditable(false);
        employeeDetailsTextArea.setColumns(20);
        employeeDetailsTextArea.setRows(5);
        jScrollPane4.setViewportView(employeeDetailsTextArea);

        jLabel6.setForeground(new java.awt.Color(0, 0, 0));
        jLabel6.setIcon(new javax.swing.ImageIcon(getClass().getResource("/circle-user_18706369.png"))); // NOI18N

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel15, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addGroup(jPanel2Layout.createSequentialGroup()
                            .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 118, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                            .addComponent(monthComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 203, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel2Layout.createSequentialGroup()
                            .addGap(71, 71, 71)
                            .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 118, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                            .addComponent(employeeComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 203, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(jPanel2Layout.createSequentialGroup()
                            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 128, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 118, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                            .addComponent(weekComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 203, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(16, 16, 16)
                        .addComponent(payrollTitle, javax.swing.GroupLayout.PREFERRED_SIZE, 265, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(260, 260, 260)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel6)
                            .addComponent(calculateButton))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 96, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(488, Short.MAX_VALUE))
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(209, 209, 209)
                .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 375, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(49, 49, 49))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addComponent(jPanel15, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(payrollTitle, javax.swing.GroupLayout.PREFERRED_SIZE, 53, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(employeeComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2)
                    .addComponent(jLabel1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(monthComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel3))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(weekComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel4))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(calculateButton)
                        .addGap(34, 34, 34)
                        .addComponent(jLabel5)
                        .addGap(9, 9, 9)
                        .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 395, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(57, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void calculateButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_calculateButtonActionPerformed
        calculateAndDisplayPayroll();
    }//GEN-LAST:event_calculateButtonActionPerformed

    private void monthComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_monthComboBoxActionPerformed
       
    }//GEN-LAST:event_monthComboBoxActionPerformed

    private void weekComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_weekComboBoxActionPerformed
   
    }//GEN-LAST:event_weekComboBoxActionPerformed


    public static void main(String args[]) {

        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(PayrollFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(PayrollFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(PayrollFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(PayrollFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>


        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new PayrollFrame().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton calculateButton;
    private javax.swing.JComboBox<String> employeeComboBox;
    private javax.swing.JTextArea employeeDetailsTextArea;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JPanel jPanel15;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JComboBox<String> monthComboBox;
    private javax.swing.JLabel payrollTitle;
    private javax.swing.JTextArea resultTextArea;
    private javax.swing.JComboBox<String> weekComboBox;
    // End of variables declaration//GEN-END:variables
}
