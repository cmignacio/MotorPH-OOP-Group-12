package motorph.gui.employee;

import javax.swing.table.DefaultTableModel;
import java.util.List;
import motorph.Attendance;
import motorph.FileHandler;
import java.time.LocalDate;
import java.time.DayOfWeek;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import javax.swing.JOptionPane;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.ArrayList;
import java.util.Comparator;


/**
 * A panel for an employee to view their own attendance records.
 * Weeks are calculated from Monday to Sunday and belong to the month where the Friday falls.
 */
public class EmpAttendancePanel extends javax.swing.JPanel {

    private FileHandler fileHandler;
    private DefaultTableModel tableModel; 
    private String loggedInEmployeeId; 
    private List<LocalDate> weekStartDates = new ArrayList<>();

    /**
     * Constructs a new EmpAttendancePanel.
     * @param employeeId The ID of the logged-in employee.
     */
    public EmpAttendancePanel(String employeeId) {
        this.loggedInEmployeeId = employeeId;
        initComponents();
        fileHandler = new FileHandler();
        initializeTable(); 

        employeeIdComboBox.setVisible(false);
        jLabelEmployeeFilter.setVisible(false);

        populateMonthFilter();
        monthComboBox.addActionListener(e -> populateWeekFilter());
        filterAttendanceData();
    }

    /**
     * Sets up the columns for the attendance table.
     */
    private void initializeTable() {
        tableModel = new DefaultTableModel();
        jTable1.setModel(tableModel); 

        tableModel.addColumn("Employee ID");
        tableModel.addColumn("Date");
        tableModel.addColumn("Time In");
        tableModel.addColumn("Time Out");
    }

    /**
     * Populates the month filter based on the payroll month of the employee's attendance.
     */
    private void populateMonthFilter() {
        try {
            List<Attendance> records = fileHandler.getAllAttendanceRecords().stream()
                .filter(record -> record.getEmployeeId() != null && record.getEmployeeId().equals(this.loggedInEmployeeId))
                .collect(Collectors.toList());

            Set<YearMonth> payrollMonths = records.stream()
                .map(record -> YearMonth.from(record.getDate().with(TemporalAdjusters.nextOrSame(DayOfWeek.FRIDAY))))
                .collect(Collectors.toSet());

            monthComboBox.removeAllItems(); 
            monthComboBox.addItem("All Months"); 

            payrollMonths.stream()
                  .sorted() 
                  .forEach(month -> monthComboBox.addItem(month.format(DateTimeFormatter.ofPattern("MMMM yyyy"))));
        } catch (Exception e) {
             JOptionPane.showMessageDialog(this, "Error loading months: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

     /**
      * Fills the week filter dropdown based on the selected payroll month.
      */
     private void populateWeekFilter() {
        Object selectedMonthItem = monthComboBox.getSelectedItem();
        weekComboBox.removeAllItems(); 
        weekStartDates.clear();
        weekComboBox.addItem("All Weeks"); 

        if (selectedMonthItem == null || "All Months".equals(selectedMonthItem.toString())) {
            return;
        }

        try {
            YearMonth selectedPayrollMonth = YearMonth.parse(selectedMonthItem.toString(), DateTimeFormatter.ofPattern("MMMM yyyy"));
            List<Attendance> records = fileHandler.getAllAttendanceRecords().stream()
                 .filter(record -> record.getEmployeeId() != null && record.getEmployeeId().equals(this.loggedInEmployeeId))
                .collect(Collectors.toList());

            weekStartDates = records.stream()
                .filter(record -> YearMonth.from(record.getDate().with(TemporalAdjusters.nextOrSame(DayOfWeek.FRIDAY))).equals(selectedPayrollMonth))
                .map(record -> record.getDate().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)))
                .distinct()
                .sorted()
                .collect(Collectors.toList());

            for (int i = 0; i < weekStartDates.size(); i++) {
                weekComboBox.addItem("Week " + (i + 1));
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading weeks: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Filters and displays attendance data in the table based on selected criteria.
     */
    private void filterAttendanceData() {
        try {
            String monthStr = monthComboBox.getSelectedItem() != null ? monthComboBox.getSelectedItem().toString() : "All Months";
            int weekIndex = weekComboBox.getSelectedIndex();

            List<Attendance> filteredRecords = fileHandler.getAllAttendanceRecords().stream()
                .filter(record -> record.getEmployeeId() != null && record.getEmployeeId().equals(this.loggedInEmployeeId))
                .collect(Collectors.toList());

            if (!"All Months".equals(monthStr)) {
                YearMonth selectedPayrollMonth = YearMonth.parse(monthStr, DateTimeFormatter.ofPattern("MMMM yyyy"));
                filteredRecords = filteredRecords.stream()
                    .filter(record -> YearMonth.from(record.getDate().with(TemporalAdjusters.nextOrSame(DayOfWeek.FRIDAY))).equals(selectedPayrollMonth))
                    .collect(Collectors.toList());
            }

            if (weekIndex > 0) {
                if (weekIndex - 1 < weekStartDates.size()) {
                    LocalDate weekStart = weekStartDates.get(weekIndex - 1);
                    LocalDate weekEnd = weekStart.plusDays(6);
                    
                    filteredRecords = filteredRecords.stream()
                        .filter(record -> !record.getDate().isBefore(weekStart) && !record.getDate().isAfter(weekEnd))
                        .collect(Collectors.toList());
                } else {
                    filteredRecords.clear();
                }
            }

            tableModel.setRowCount(0);
            filteredRecords.stream()
                .sorted(Comparator.comparing(Attendance::getDate))
                .forEach(record -> tableModel.addRow(new Object[]{ record.getEmployeeId(), record.getDate(), record.getTimeIn(), record.getTimeOut() }));

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error filtering data: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel14 = new javax.swing.JPanel();
        attendanceTitle = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        weekComboBox = new javax.swing.JComboBox<>();
        monthComboBox = new javax.swing.JComboBox<>();
        employeeIdComboBox = new javax.swing.JComboBox<>();
        jLabelEmployeeFilter = new javax.swing.JLabel();
        filterButton = new javax.swing.JButton();

        setBackground(new java.awt.Color(239, 239, 239));
        setMaximumSize(null);
        setName(""); // NOI18N

        jPanel14.setBackground(new java.awt.Color(139, 0, 0));
        jPanel14.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        javax.swing.GroupLayout jPanel14Layout = new javax.swing.GroupLayout(jPanel14);
        jPanel14.setLayout(jPanel14Layout);
        jPanel14Layout.setHorizontalGroup(
            jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        jPanel14Layout.setVerticalGroup(
            jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 41, Short.MAX_VALUE)
        );

        attendanceTitle.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        attendanceTitle.setForeground(new java.awt.Color(24, 59, 78));
        attendanceTitle.setText("Attendance (Based on payroll weekly period)");

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane1.setViewportView(jTable1);

        weekComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        weekComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                weekComboBoxActionPerformed(evt);
            }
        });

        monthComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        employeeIdComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        jLabelEmployeeFilter.setText("Label1");

        filterButton.setText("Filter");
        filterButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                filterButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel14, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(50, 50, 50)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 737, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(11, 11, 11)
                                .addComponent(jLabelEmployeeFilter, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(employeeIdComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 86, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(monthComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 127, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(weekComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 127, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(filterButton))))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(15, 15, 15)
                        .addComponent(attendanceTitle, javax.swing.GroupLayout.PREFERRED_SIZE, 517, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(53, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel14, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(attendanceTitle, javax.swing.GroupLayout.PREFERRED_SIZE, 53, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(monthComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(weekComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(employeeIdComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabelEmployeeFilter)
                    .addComponent(filterButton))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 432, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(19, 19, 19))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void weekComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_weekComboBoxActionPerformed

    }//GEN-LAST:event_weekComboBoxActionPerformed

    private void filterButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_filterButtonActionPerformed
        filterAttendanceData();
    }//GEN-LAST:event_filterButtonActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel attendanceTitle;
    private javax.swing.JComboBox<String> employeeIdComboBox;
    private javax.swing.JButton filterButton;
    private javax.swing.JLabel jLabelEmployeeFilter;
    private javax.swing.JPanel jPanel14;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JComboBox<String> monthComboBox;
    private javax.swing.JComboBox<String> weekComboBox;
    // End of variables declaration//GEN-END:variables
}
