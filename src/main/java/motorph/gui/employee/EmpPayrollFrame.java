package motorph.gui.employee;

import javax.swing.*;
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
import java.text.DecimalFormat;

/**
 * A frame for an employee to view their own payroll details.
 */
public class EmpPayrollFrame extends JFrame {

    private PayrollCalculator payrollCalculator;
    private FileHandler fileHandler;
    private Employee currentEmployee; 
    private List<Employee> employeeList; 
    private String loggedInEmployeeId; 
    private static final DecimalFormat df = new DecimalFormat("#,##0.00");

    /**
     * Constructs a new EmpPayrollFrame for a specific employee.
     * @param employeeId The ID of the logged-in employee.
     */
    public EmpPayrollFrame(String employeeId) {
        this.loggedInEmployeeId = employeeId;
        initComponents();
        fileHandler = new FileHandler();
        payrollCalculator = new PayrollCalculator(fileHandler);

        try {
            employeeList = fileHandler.readEmployees();
            if (employeeList == null || employeeList.isEmpty()) {
                employeeList = new ArrayList<>();
            }
            // Hide elements not needed for employee view
            employeeComboBox.setVisible(false); 
            jLabel2.setVisible(false); 

            populateMonthComboBox(); 
            populateWeekComboBox(); 
        } catch (Exception e) {
            System.err.println("Error loading employee data in EmpPayrollFrame: " + e.getMessage());
            e.printStackTrace(); 
        }

        employeeDetailsTextArea.setText("Loading employee details...");
        resultTextArea.setText("Payroll results will appear here after selecting month and week.");

        setupComputeButton(); 
        displayLoggedInEmployeeDetails(); 
        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE); 
    }

    //Constructor
    public EmpPayrollFrame() {
        initComponents();
        employeeComboBox.setVisible(false);
        jLabel2.setVisible(false);
        calculateButton.setVisible(false);
        employeeDetailsTextArea.setText("Employee details will load here.");
        resultTextArea.setText("Payroll results will load here.");
    }

    /**
     * Fills the month dropdown with available months for the logged-in employee.
     */
    private void populateMonthComboBox() {
        monthComboBox.removeAllItems();
        List<YearMonth> months = payrollCalculator.getAvailableMonths(loggedInEmployeeId);
        if (months != null && !months.isEmpty()) {
            for (YearMonth month : months) {
                monthComboBox.addItem(month.format(DateTimeFormatter.ofPattern("MMMM yyyy")));
            }
        } else {
            monthComboBox.addItem("No months available");
        }
    }

    /**
     * Fills the week dropdown with standard week options.
     */
    private void populateWeekComboBox() {
        weekComboBox.removeAllItems(); 
        weekComboBox.addItem("All Weeks");
        weekComboBox.addItem("Week 1");
        weekComboBox.addItem("Week 2");
        weekComboBox.addItem("Week 3");
        weekComboBox.addItem("Week 4");
    }

    /**
     * Sets up the action listener for the "Compute" button.
     */
    private void setupComputeButton() {
        calculateButton.addActionListener(e -> calculateAndDisplayPayroll());
    }

    /**
     * Calculates and displays the payroll for the logged-in employee
     */
    private void calculateAndDisplayPayroll() {
        Object selectedMonthItem = monthComboBox.getSelectedItem();
        int selectedWeekIndex = weekComboBox.getSelectedIndex(); 

        if (selectedMonthItem == null || selectedMonthItem.equals("No months available")) {
            resultTextArea.setText("Please select a month.");
            return;
        }

        try {
            String selectedEmployeeId = this.loggedInEmployeeId;
            String selectedMonthString = selectedMonthItem.toString();
            YearMonth selectedMonth = parseYearMonth(selectedMonthString);

            currentEmployee = findEmployee(selectedEmployeeId);
            if (currentEmployee == null) {
                resultTextArea.setText("Employee not found: " + selectedEmployeeId);
                employeeDetailsTextArea.setText("Employee details not available.");
                return;
            }

            updateEmployeeDetails();
            int selectedWeek = selectedWeekIndex; 

            // Redirect System.out to capture the payroll report from PayrollCalculator
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            PrintStream ps = new PrintStream(baos);
            PrintStream old = System.out; 
            System.setOut(ps);

            payrollCalculator.processPayroll(currentEmployee.getEmployeeId(), selectedMonth, selectedWeek);

            System.out.flush();
            System.setOut(old);

            String payrollResults = baos.toString();
            if (payrollResults == null || payrollResults.trim().isEmpty()) {
                resultTextArea.setText("No payroll data available for the selected criteria.");
            } else {
                resultTextArea.setText(payrollResults);
            }

        } catch (DateTimeParseException ex) {
            JOptionPane.showMessageDialog(this, "Invalid month format selected.", "Input Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            resultTextArea.setText("Error processing payroll: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    /**
     * Finds and displays the details of the currently logged-in employee.
     */
    private void displayLoggedInEmployeeDetails() {
         if (this.loggedInEmployeeId != null) {
            currentEmployee = findEmployee(this.loggedInEmployeeId);
            updateEmployeeDetails();
         } else {
             employeeDetailsTextArea.setText("Employee ID not set.");
         }
    }

    /**
     * Updates the employee details text area with the current employee's information.
     */
    private void updateEmployeeDetails() {
        if (currentEmployee != null) {
            String details = "Employee ID: " + currentEmployee.getEmployeeId() + "\n" +
                             "Name: " + currentEmployee.getFirstName() + " " + currentEmployee.getLastName() + "\n" +
                             "Status: " + currentEmployee.getStatus() + "\n" +
                             "Position: " + currentEmployee.getPosition() + "\n" +
                             "Basic Salary: ₱" + df.format(currentEmployee.getBasicSalary()) + "\n" +
                             "Rice Subsidy: ₱" + df.format(currentEmployee.getRiceSubsidy()) + "\n" +
                             "Phone Allowance: ₱" + df.format(currentEmployee.getPhoneAllowance()) + "\n" +
                             "Clothing Allowance: ₱" + df.format(currentEmployee.getClothingAllowance()) + "\n" +
                             "Hourly Rate: ₱" + df.format(currentEmployee.getHourlyRate());
            employeeDetailsTextArea.setText(details);
        } else {
            employeeDetailsTextArea.setText("Employee details not available.");
        }
    }

    /**
     * Parses a month string into a YearMonth object.
     * @param monthString The string to parse.
     * @return The parsed YearMonth object.
     */
    private YearMonth parseYearMonth(String monthString) {
        if (monthString == null || monthString.isEmpty() || monthString.equals("No months available")) {
            return YearMonth.now(); 
        }
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM yyyy");
            return YearMonth.parse(monthString, formatter);
        } catch (DateTimeParseException e) {
             JOptionPane.showMessageDialog(this,
                "Invalid month format: " + monthString +
                "\nExpected format: 'Month Year' (e.g., January 2023)",
                "Date Format Error",
                JOptionPane.ERROR_MESSAGE);
            return YearMonth.now();
        }
    }

     /**
      * Finds an employee from the loaded list by their ID.
      * @param employeeId The ID of the employee to find.
      * @return The Employee object, or null if not found.
      */
    private Employee findEmployee(String employeeId) {
        if (employeeList == null) return null;
        
        for (Employee emp : employeeList) {
            if (emp != null && emp.getEmployeeId() != null && emp.getEmployeeId().equals(employeeId)) {
                return emp; 
            }
        }
        return null;
    }
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel2 = new javax.swing.JPanel();
        payrollTitle = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        calculateButton = new javax.swing.JButton();
        jLabel4 = new javax.swing.JLabel();
        monthComboBox = new javax.swing.JComboBox<>();
        weekComboBox = new javax.swing.JComboBox<>();
        jPanel15 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        resultTextArea = new javax.swing.JTextArea();
        jLabel1 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jScrollPane4 = new javax.swing.JScrollPane();
        employeeDetailsTextArea = new javax.swing.JTextArea();
        employeeComboBox = new javax.swing.JComboBox<>();
        jLabel2 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setBackground(new java.awt.Color(255, 255, 255));

        jPanel2.setBackground(new java.awt.Color(239, 239, 239));
        jPanel2.setForeground(new java.awt.Color(255, 255, 255));
        jPanel2.setMaximumSize(null);

        payrollTitle.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        payrollTitle.setForeground(new java.awt.Color(24, 59, 78));
        payrollTitle.setText("Payroll");

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

        employeeComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        jLabel2.setText("jLabel2");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel15, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(49, 49, 49)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 96, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 326, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(16, 16, 16)
                        .addComponent(payrollTitle, javax.swing.GroupLayout.PREFERRED_SIZE, 265, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(260, 260, 260)
                        .addComponent(calculateButton))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(30, 30, 30)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 118, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(weekComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 203, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 118, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(employeeComboBox, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(monthComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 203, javax.swing.GroupLayout.PREFERRED_SIZE))))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 35, Short.MAX_VALUE)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 96, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 375, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(49, 49, 49))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addComponent(jPanel15, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(payrollTitle, javax.swing.GroupLayout.PREFERRED_SIZE, 53, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 395, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(employeeComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel2))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(monthComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel3))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(weekComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel4))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(calculateButton)
                        .addGap(37, 37, 37)
                        .addComponent(jLabel5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 233, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(63, Short.MAX_VALUE))
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
            java.util.logging.Logger.getLogger(EmpPayrollFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(EmpPayrollFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(EmpPayrollFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(EmpPayrollFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new EmpPayrollFrame().setVisible(true);
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
