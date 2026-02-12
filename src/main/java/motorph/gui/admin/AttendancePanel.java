package motorph.gui.admin;

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
import motorph.gui.MainApplication;

/**
 * A panel for administrators to view and filter all employee attendance records.
 */
public class AttendancePanel extends javax.swing.JPanel {
    private FileHandler fileHandler; 
    private DefaultTableModel tableModel; 
    private List<LocalDate> weekStartDates = new ArrayList<>();

    /**
     * Constructs a new AttendancePanel.
     * @param mainApp A reference to the main application frame.
     */
    public AttendancePanel(MainApplication mainApp) {
        initComponents(); 
        fileHandler = new FileHandler(); 
        initializeTable(); 
        populateEmployeeFilter(); 
        populateMonthFilter(); 
        monthComboBox.addActionListener(e -> populateWeekFilter());
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
        loadAttendanceData(); 
    }

    /**
     * Loads all attendance data from the file and populates the table initially.
     */
    private void loadAttendanceData() {
        try {
            List<Attendance> attendanceRecords = fileHandler.getAllAttendanceRecords();
            tableModel.setRowCount(0); 

            for (Attendance record : attendanceRecords) {
                Object[] rowData = {
                    record.getEmployeeId(),
                    record.getDate(),
                    record.getTimeIn(),
                    record.getTimeOut()
                };
                tableModel.addRow(rowData);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading attendance data: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Fills the employee ID filter dropdown with unique, sorted employee IDs.
     */
    private void populateEmployeeFilter() {
        try {
            List<Attendance> attendanceRecords = fileHandler.getAllAttendanceRecords();
            employeeIdComboBox.removeAllItems(); 
            employeeIdComboBox.addItem("All"); 

            List<String> sortedEmployeeIds = attendanceRecords.stream()
                .map(Attendance::getEmployeeId)
                .distinct()
                .sorted(Comparator.comparingInt(Integer::parseInt))
                .collect(Collectors.toList());

            for (String employeeId : sortedEmployeeIds) {
                employeeIdComboBox.addItem(employeeId);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error populating employee filter: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Fills the month filter dropdown based on the payroll month of the attendance.
     * A week's records belong to the month where its Friday falls.
     */
    private void populateMonthFilter() {
        try {
            List<Attendance> records = fileHandler.getAllAttendanceRecords();
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
            List<Attendance> records = fileHandler.getAllAttendanceRecords();

            // Find all unique Mondays from weeks that belong to the selected payroll month
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
            String employeeId = employeeIdComboBox.getSelectedItem() != null ? employeeIdComboBox.getSelectedItem().toString() : "All";
            String monthStr = monthComboBox.getSelectedItem() != null ? monthComboBox.getSelectedItem().toString() : "All Months";
            int weekIndex = weekComboBox.getSelectedIndex(); // 0 for "All Weeks"

            List<Attendance> filteredRecords = fileHandler.getAllAttendanceRecords();

            // Filter by Employee ID
            if (!"All".equals(employeeId)) {
                filteredRecords = filteredRecords.stream()
                    .filter(record -> record.getEmployeeId().equals(employeeId))
                    .collect(Collectors.toList());
            }
            
            // Filter by Payroll Month
            if (!"All Months".equals(monthStr)) {
                YearMonth selectedPayrollMonth = YearMonth.parse(monthStr, DateTimeFormatter.ofPattern("MMMM yyyy"));
                filteredRecords = filteredRecords.stream()
                    .filter(record -> YearMonth.from(record.getDate().with(TemporalAdjusters.nextOrSame(DayOfWeek.FRIDAY))).equals(selectedPayrollMonth))
                    .collect(Collectors.toList());
            }
            
            // Filter by the selected week range
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

            // Update the table with the final filtered records
            tableModel.setRowCount(0);
            filteredRecords.stream()
                .sorted(Comparator.comparing(Attendance::getDate)) // Sort for final display
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
        employeeIdComboBox = new javax.swing.JComboBox<>();
        weekComboBox = new javax.swing.JComboBox<>();
        monthComboBox = new javax.swing.JComboBox<>();
        filterEmpButton = new javax.swing.JButton();

        setBackground(new java.awt.Color(239, 239, 239));
        setMaximumSize(null);

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

        employeeIdComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        weekComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        weekComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                weekComboBoxActionPerformed(evt);
            }
        });

        monthComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        filterEmpButton.setText("Filter");
        filterEmpButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                filterEmpButtonActionPerformed(evt);
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
                                .addComponent(employeeIdComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 337, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(24, 24, 24)
                                .addComponent(monthComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 127, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(24, 24, 24)
                                .addComponent(weekComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 127, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(filterEmpButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(15, 15, 15)
                        .addComponent(attendanceTitle)))
                .addContainerGap(213, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel14, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(attendanceTitle, javax.swing.GroupLayout.PREFERRED_SIZE, 53, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(employeeIdComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(monthComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(weekComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(filterEmpButton))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 12, Short.MAX_VALUE)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 432, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(19, 19, 19))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void weekComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_weekComboBoxActionPerformed

    }//GEN-LAST:event_weekComboBoxActionPerformed

    private void filterEmpButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_filterEmpButtonActionPerformed
        filterAttendanceData();
    }//GEN-LAST:event_filterEmpButtonActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel attendanceTitle;
    private javax.swing.JComboBox<String> employeeIdComboBox;
    private javax.swing.JButton filterEmpButton;
    private javax.swing.JPanel jPanel14;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JComboBox<String> monthComboBox;
    private javax.swing.JComboBox<String> weekComboBox;
    // End of variables declaration//GEN-END:variables
}
