package motorph.gui.employee;

import motorph.model.LeaveRequest;
import motorph.model.LeaveType;
import motorph.dao.LeaveDAO;
import motorph.service.LeaveService;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneId;
import com.toedter.calendar.JDateChooser;

public class EmpLeavePanel extends JPanel {

    private final String employeeNumber;
    private LeaveService leaveService;

    private JComboBox<LeaveType> leaveType;
    private JDateChooser startDateChooser;
    private JDateChooser endDateChooser;
    private JTextArea reasonArea;

    private JTable leaveTable;
    private DefaultTableModel tableModel;

    public EmpLeavePanel(String employeeNumber) {

        this.employeeNumber = employeeNumber;
        this.leaveService = new LeaveService(new LeaveDAO("leave.csv"));

        initUI();
        loadLeaveHistory();
    }

    private void initUI() {

        setLayout(new BorderLayout(15, 15));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel title = new JLabel("Leave Request", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 26));
        add(title, BorderLayout.NORTH);

        JPanel form = new JPanel(new GridLayout(5, 2, 10, 10));
        

        form.add(new JLabel("Employee ID:"));
        form.add(new JLabel(employeeNumber));

        form.add(new JLabel("Leave Type:"));
        leaveType = new JComboBox<>(LeaveType.values());
        form.add(leaveType);

        form.add(new JLabel("Start Date:"));
        startDateChooser = new JDateChooser();
        startDateChooser.setDateFormatString("yyyy-MM-dd");
        form.add(startDateChooser);

        form.add(new JLabel("End Date:"));
        endDateChooser = new JDateChooser();
        endDateChooser.setDateFormatString("yyyy-MM-dd");
        form.add(endDateChooser);

        form.add(new JLabel("Reason:"));
        
        form.setBorder(BorderFactory.createCompoundBorder(
        BorderFactory.createTitledBorder("Leave Request Form"),
        BorderFactory.createEmptyBorder(10, 10, 10, 10)
));

        reasonArea = new JTextArea(3, 20);
        reasonArea.setLineWrap(true);
        reasonArea.setWrapStyleWord(true);

        JScrollPane reasonScroll = new JScrollPane(reasonArea);
        form.add(reasonScroll);

        JButton submit = new JButton("Submit Leave Request");
        submit.addActionListener(e -> submitLeave());
        submit.setPreferredSize(new Dimension(180, 35));

        JButton refresh = new JButton("Refresh");
        refresh.addActionListener(e -> loadLeaveHistory());
        refresh.setPreferredSize(new Dimension(100, 35));

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(submit);
        buttonPanel.add(refresh);

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(form, BorderLayout.CENTER);
        topPanel.add(buttonPanel, BorderLayout.SOUTH);
        topPanel.setBorder(BorderFactory.createEmptyBorder(5, 40, 5, 40));

        add(topPanel, BorderLayout.CENTER);

        String[] columns = {"Leave ID", "Type", "Start Date", "End Date", "Reason", "Status"};
        tableModel = new DefaultTableModel(columns, 0);

        leaveTable = new JTable(tableModel);

        leaveTable.getColumnModel().getColumn(5).setCellRenderer(
            new DefaultTableCellRenderer() {
                @Override
                public Component getTableCellRendererComponent(
                        JTable table, Object value,
                        boolean isSelected, boolean hasFocus,
                        int row, int column) {

                    Component c = super.getTableCellRendererComponent(
                            table, value, isSelected, hasFocus, row, column);

                    if (!isSelected && value != null) {

                        String status = value.toString().toUpperCase();

                        if (status.equals("APPROVED")) {
                            c.setForeground(new Color(0, 128, 0));
                        } else if (status.equals("PENDING")) {
                            c.setForeground(new Color(255, 140, 0));
                        } else if (status.equals("REJECTED")) {
                            c.setForeground(Color.RED);
                        } else {
                            c.setForeground(Color.BLACK);
                        }
                    }

                    return c;
                }
            }
        );

        JScrollPane tableScroll = new JScrollPane(leaveTable);
        tableScroll.setPreferredSize(new Dimension(700, 220));

        add(tableScroll, BorderLayout.SOUTH);
    }

    private void submitLeave() {

        try {

            LeaveType type = (LeaveType) leaveType.getSelectedItem();

            if (startDateChooser.getDate() == null || endDateChooser.getDate() == null) {
                JOptionPane.showMessageDialog(this, "Please select both dates.");
                return;
            }

            LocalDate start = startDateChooser.getDate()
                    .toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate();

            LocalDate end = endDateChooser.getDate()
                    .toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate();

            String reason = reasonArea.getText().trim();

            if (reason.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter a reason.");
                return;
            }

            if (end.isBefore(start)) {
                JOptionPane.showMessageDialog(this,
                        "End date cannot be earlier than start date.");
                return;
            }

            leaveService.fileLeave(employeeNumber, type, start, end, reason);

            loadLeaveHistory();

            JOptionPane.showMessageDialog(this, "Leave request submitted.");

            startDateChooser.setDate(null);
            endDateChooser.setDate(null);
            reasonArea.setText("");

        } catch (IOException ex) {

            JOptionPane.showMessageDialog(this, "Error saving leave request.");

        } catch (Exception ex) {

            JOptionPane.showMessageDialog(this,
                    "Invalid date format.");
        }
    }

    private void loadLeaveHistory() {

        try {

            tableModel.setRowCount(0);

            for (LeaveRequest leave : leaveService.getAllLeaves()) {

                if (leave.getEmployeeId().equals(employeeNumber)) {

                    tableModel.addRow(new Object[]{
                            leave.getLeaveId(),
                            leave.getLeaveType(),
                            leave.getStartDate(),
                            leave.getEndDate(),
                            leave.getReason(),
                            leave.getStatus()
});
                    
                }
            }

        } catch (IOException ex) {

            JOptionPane.showMessageDialog(this,
                    "Error loading leave history.");
        }
    }
}