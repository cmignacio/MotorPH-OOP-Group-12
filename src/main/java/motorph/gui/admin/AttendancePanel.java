package motorph.gui.admin;

import motorph.model.Attendance;
import motorph.gui.MainApplication;
import motorph.dao.FileHandler;

import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Admin attendance panel for viewing and filtering all employee attendance records.
 */
public class AttendancePanel extends javax.swing.JPanel {

    private final MainApplication mainApp;
    private final FileHandler fileHandler;
    private DefaultTableModel tableModel;
    private List<LocalDate> weekStartDates;

    private static final DateTimeFormatter MONTH_FORMATTER =
            DateTimeFormatter.ofPattern("MMMM yyyy");

    public AttendancePanel(MainApplication mainApp) {
        this.mainApp = mainApp;
        this.fileHandler = new FileHandler();
        this.weekStartDates = new ArrayList<>();

        initComponents();
        initializeTable();
        loadFilters();
        loadAllAttendanceData();

        monthComboBox.addActionListener(e -> populateWeekFilter());
    }

    /**
     * Initializes the attendance table columns.
     */
    private void initializeTable() {
        tableModel = new DefaultTableModel(
                new Object[]{"Employee ID", "Date", "Time In", "Time Out"},
                0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        jTable1.setModel(tableModel);
        jTable1.setRowHeight(24);
    }

    /**
     * Loads all filter dropdowns.
     */
    private void loadFilters() {
        populateEmployeeFilter();
        populateMonthFilter();
        populateWeekFilter();
    }

    /**
     * Loads all attendance records into the table.
     */
    private void loadAllAttendanceData() {
        try {
            List<Attendance> records = fileHandler.getAllAttendanceRecords();

            records = records.stream()
                    .sorted(Comparator.comparing(Attendance::getEmployeeId)
                            .thenComparing(Attendance::getDate))
                    .collect(Collectors.toList());

            populateTable(records);

        } catch (Exception e) {
            showError("Error loading attendance data: " + e.getMessage());
        }
    }

    /**
     * Populates the employee filter combo box.
     */
    private void populateEmployeeFilter() {
        try {
            List<Attendance> records = fileHandler.getAllAttendanceRecords();

            employeeIdComboBox.removeAllItems();
            employeeIdComboBox.addItem("All");
           

           List<String> employeeIds = records.stream()
                    .map(Attendance::getEmployeeId)
                    .filter(id -> id != null && !id.isBlank())
                    .distinct()
                    .sorted(Comparator.comparingInt(Integer::parseInt))
                    .collect(Collectors.toList());

            for (String employeeId : employeeIds) {
                employeeIdComboBox.addItem(employeeId);
            }

        } catch (Exception e) {
            showError("Error loading employee filter: " + e.getMessage());
        }
    }

    /**
     * Populates the month filter combo box.
     * Payroll month is based on the Friday of the attendance week.
     */
    private void populateMonthFilter() {
        try {
            List<Attendance> records = fileHandler.getAllAttendanceRecords();

            Set<YearMonth> payrollMonths = records.stream()
                    .map(record -> YearMonth.from(
                            record.getDate().with(TemporalAdjusters.nextOrSame(DayOfWeek.FRIDAY))
                    ))
                    .collect(Collectors.toSet());

            monthComboBox.removeAllItems();
            monthComboBox.addItem("All Months");

            payrollMonths.stream()
                    .sorted()
                    .forEach(month -> monthComboBox.addItem(month.format(MONTH_FORMATTER)));

        } catch (Exception e) {
            showError("Error loading month filter: " + e.getMessage());
        }
    }

    /**
     * Populates the week filter based on the selected payroll month.
     */
    private void populateWeekFilter() {
        weekComboBox.removeAllItems();
        weekComboBox.addItem("All Weeks");
        weekStartDates.clear();

        Object selectedMonth = monthComboBox.getSelectedItem();
        if (selectedMonth == null || "All Months".equals(selectedMonth.toString())) {
            return;
        }


try {
            YearMonth selectedPayrollMonth =
                    YearMonth.parse(selectedMonth.toString(), MONTH_FORMATTER);

            List<Attendance> records = fileHandler.getAllAttendanceRecords();

            weekStartDates = records.stream()
                    .filter(record -> {
                        YearMonth payrollMonth = YearMonth.from(
                                record.getDate().with(TemporalAdjusters.nextOrSame(DayOfWeek.FRIDAY))
                        );
                        return payrollMonth.equals(selectedPayrollMonth);
                    })
                    .map(record -> record.getDate().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)))
                    .distinct()
                    .sorted()
                    .collect(Collectors.toList());

            for (int i = 0; i < weekStartDates.size(); i++) {
                LocalDate start = weekStartDates.get(i);
                LocalDate end = start.plusDays(6);
                weekComboBox.addItem("Week " + (i + 1) + " (" + start + " to " + end + ")");
            }

        } catch (Exception e) {
            showError("Error loading week filter: " + e.getMessage());
        }
    }
    /**
     * Filters the attendance records based on selected filters.
     */
    private void filterAttendanceData() {
        try {
            List<Attendance> filteredRecords = fileHandler.getAllAttendanceRecords();

            String selectedEmployee = employeeIdComboBox.getSelectedItem() != null
                    ? employeeIdComboBox.getSelectedItem().toString()
                    : "All";

            String selectedMonth = monthComboBox.getSelectedItem() != null
                    ? monthComboBox.getSelectedItem().toString()
                    : "All Months";

            int selectedWeekIndex = weekComboBox.getSelectedIndex();

            if (!"All".equals(selectedEmployee)) {
                filteredRecords = filteredRecords.stream()
                        .filter(record -> record.getEmployeeId().equals(selectedEmployee))
                        .collect(Collectors.toList());
            }

            if (!"All Months".equals(selectedMonth)) {
                YearMonth selectedPayrollMonth =
                        YearMonth.parse(selectedMonth, MONTH_FORMATTER);

                filteredRecords = filteredRecords.stream()
                        .filter(record -> {
                            YearMonth payrollMonth = YearMonth.from(
                                    record.getDate().with(TemporalAdjusters.nextOrSame(DayOfWeek.FRIDAY))
                            );
                            return payrollMonth.equals(selectedPayrollMonth);
                        })
         
                        .collect(Collectors.toList());
            }

            if (selectedWeekIndex > 0 && selectedWeekIndex - 1 < weekStartDates.size()) {
                LocalDate weekStart = weekStartDates.get(selectedWeekIndex - 1);
                LocalDate weekEnd = weekStart.plusDays(6);

                filteredRecords = filteredRecords.stream()
                        .filter(record -> !record.getDate().isBefore(weekStart)
                                && !record.getDate().isAfter(weekEnd))
                        .collect(Collectors.toList());
            }

            filteredRecords = filteredRecords.stream()
                    .sorted(Comparator.comparing(Attendance::getEmployeeId)
                            .thenComparing(Attendance::getDate))
                    .collect(Collectors.toList());

            populateTable(filteredRecords);

         } catch (Exception e) {
            showError("Error filtering attendance data: " + e.getMessage());
        }
    }

    /**
     * Refreshes the attendance panel data.
     */
    public void refreshAttendanceData() {
        loadFilters();
        loadAllAttendanceData();
    }

    /**
     * Populates the table with attendance records.
     */
    private void populateTable(List<Attendance> records) {
        tableModel.setRowCount(0);

        for (Attendance record : records) {
            tableModel.addRow(new Object[]{
                    record.getEmployeeId(),
                    record.getDate(),
                    record.getTimeIn(),
                    record.getTimeOut()
            });
        }
    }

    /**
     * Shows an error dialog.
     */
    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
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
