package motorph.gui.employee;

import motorph.model.Attendance;
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

public class EmpAttendancePanel extends javax.swing.JPanel {

    private final FileHandler fileHandler;
    private final String loggedInEmployeeId;
    private DefaultTableModel tableModel;
    private List<LocalDate> weekStartDates;

    private static final DateTimeFormatter MONTH_FORMATTER =
            DateTimeFormatter.ofPattern("MMMM yyyy");

    public EmpAttendancePanel(String employeeId) {
        this.loggedInEmployeeId = employeeId;
        this.fileHandler = new FileHandler();
        this.weekStartDates = new ArrayList<>();


       initComponents();
        initializeTable();
        hideUnusedEmployeeFilter();
        loadFilters();
        loadEmployeeAttendanceData();

        monthComboBox.addActionListener(e -> populateWeekFilter());
    }

    private void initializeTable() {
        tableModel = new DefaultTableModel(
                new Object[]{"Employee ID", "Date", "Time In", "Time Out"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        jTable1.setModel(tableModel);
    }

    private void hideUnusedEmployeeFilter() {
        employeeIdComboBox.setVisible(false);
        jLabelEmployeeFilter.setVisible(false);
    }

    private void loadFilters() {
        populateMonthFilter();
        populateWeekFilter();
    }
 private List<Attendance> getEmployeeAttendanceRecords() {
        return fileHandler.getAllAttendanceRecords().stream()
                .filter(record -> record.getEmployeeId() != null &&
                        record.getEmployeeId().equals(loggedInEmployeeId))
                .collect(Collectors.toList());
    }

    private void loadEmployeeAttendanceData() {
        try {
            List<Attendance> records = getEmployeeAttendanceRecords()
                    .stream()
                    .sorted(Comparator.comparing(Attendance::getDate))
                    .collect(Collectors.toList());

            populateTable(records);

        } catch (Exception e) {
            showError("Error loading attendance data: " + e.getMessage());
        }
    }

    private void populateMonthFilter() {
        try {
            List<Attendance> records = getEmployeeAttendanceRecords();

            Set<YearMonth> payrollMonths = records.stream()
                    .map(record -> YearMonth.from(
                            record.getDate().with(
                                    TemporalAdjusters.nextOrSame(DayOfWeek.FRIDAY))))
                    .collect(Collectors.toSet());
         monthComboBox.removeAllItems();
            monthComboBox.addItem("All Months");

            payrollMonths.stream()
                    .sorted()
                    .forEach(m -> monthComboBox.addItem(m.format(MONTH_FORMATTER)));

        } catch (Exception e) {
            showError("Error loading months: " + e.getMessage());
        }
    }

    private void populateWeekFilter() {
        weekComboBox.removeAllItems();
        weekComboBox.addItem("All Weeks");
        weekStartDates.clear();

        Object selectedMonth = monthComboBox.getSelectedItem();
        if (selectedMonth == null || "All Months".equals(selectedMonth.toString()))
            return;

        try {
            YearMonth payrollMonth =
                    YearMonth.parse(selectedMonth.toString(), MONTH_FORMATTER);

            List<Attendance> records = getEmployeeAttendanceRecords();
            
         weekStartDates = records.stream()
                    .filter(r -> YearMonth.from(
                            r.getDate().with(
                                    TemporalAdjusters.nextOrSame(DayOfWeek.FRIDAY)))
                            .equals(payrollMonth))
                    .map(r -> r.getDate().with(
                            TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)))
                    .distinct()
                    .sorted()
                    .collect(Collectors.toList());

            for (int i = 0; i < weekStartDates.size(); i++) {
                weekComboBox.addItem("Week " + (i + 1));
            }

        } catch (Exception e) {
            showError("Error loading weeks: " + e.getMessage());
        }
    }

    private void filterAttendanceData() {
        try {
            String month = monthComboBox.getSelectedItem().toString();
            int weekIndex = weekComboBox.getSelectedIndex();

            List<Attendance> filtered = getEmployeeAttendanceRecords();

            if (!"All Months".equals(month)) {
                YearMonth selectedMonth =
                        YearMonth.parse(month, MONTH_FORMATTER);

                filtered = filtered.stream()
                        .filter(r -> YearMonth.from(
                                r.getDate().with(
                                        TemporalAdjusters.nextOrSame(DayOfWeek.FRIDAY)))
                                .equals(selectedMonth))
                        .collect(Collectors.toList());
            }
       if (weekIndex > 0 && weekIndex - 1 < weekStartDates.size()) {
                LocalDate start = weekStartDates.get(weekIndex - 1);
                LocalDate end = start.plusDays(6);

                filtered = filtered.stream()
                        .filter(r -> !r.getDate().isBefore(start)
                                && !r.getDate().isAfter(end))
                        .collect(Collectors.toList());
            }

            populateTable(filtered);

        } catch (Exception e) {
            showError("Error filtering data: " + e.getMessage());
        }
    }

    private void populateTable(List<Attendance> records) {
        tableModel.setRowCount(0);

        for (Attendance r : records) {
            tableModel.addRow(new Object[]{
                    r.getEmployeeId(),
                    r.getDate(),
                    r.getTimeIn(),
                    r.getTimeOut()
            });
        }
    }

    private void showError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Error",
                JOptionPane.ERROR_MESSAGE);
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
