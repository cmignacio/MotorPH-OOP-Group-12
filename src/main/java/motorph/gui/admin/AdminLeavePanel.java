package motorph.gui.admin;

import motorph.model.LeaveRequest;
import motorph.dao.LeaveDAO;
import motorph.service.LeaveService;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.IOException;
import java.util.List;

public class AdminLeavePanel extends JPanel {

    private final LeaveService leaveService;

    private JTable leaveTable;
    private DefaultTableModel tableModel;
    private JButton refreshButton;
    private JButton approveButton;
    private JButton rejectButton;

    public AdminLeavePanel() {
        this.leaveService = new LeaveService(new LeaveDAO("leave.csv"));
        initUI();
        loadLeaves();
    }

    private void initUI() {
        setLayout(new BorderLayout(10, 10));

        JLabel title = new JLabel("Admin Leave Management", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 26));
        add(title, BorderLayout.NORTH);

        String[] columns = {
                "Leave ID", "Employee ID", "Type", "Start Date", "End Date", "Reason", "Status"
        };

        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        leaveTable = new JTable(tableModel);
        leaveTable.setRowHeight(24);

        leaveTable.getColumnModel().getColumn(6).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
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
        });

        JScrollPane scrollPane = new JScrollPane(leaveTable);
        add(scrollPane, BorderLayout.CENTER);

        refreshButton = new JButton("Refresh");
        approveButton = new JButton("Approve");
        rejectButton = new JButton("Reject");

        refreshButton.addActionListener(e -> loadLeaves());
        approveButton.addActionListener(e -> approveSelectedLeave());
        rejectButton.addActionListener(e -> rejectSelectedLeave());

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(refreshButton);
        buttonPanel.add(approveButton);
        buttonPanel.add(rejectButton);

        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void loadLeaves() {
        try {
            tableModel.setRowCount(0);

            List<LeaveRequest> leaves = leaveService.getAllLeaves();

            for (LeaveRequest leave : leaves) {
                tableModel.addRow(new Object[]{
                        leave.getLeaveId(),
                        leave.getEmployeeId(),
                        leave.getLeaveType(),
                        leave.getStartDate(),
                        leave.getEndDate(),
                        leave.getReason(),
                        leave.getStatus()
                });
            }

        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Error loading leave requests.");
        }
    }

    private void approveSelectedLeave() {
        int selectedRow = leaveTable.getSelectedRow();

        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a leave request first.");
            return;
        }

        String leaveId = tableModel.getValueAt(selectedRow, 0).toString();

        try {
            leaveService.approveLeave(leaveId);
            JOptionPane.showMessageDialog(this, "Leave approved.");
            loadLeaves();
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Error approving leave.");
        }
    }

    private void rejectSelectedLeave() {
        int selectedRow = leaveTable.getSelectedRow();

        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a leave request first.");
            return;
        }

        String leaveId = tableModel.getValueAt(selectedRow, 0).toString();

        try {
            leaveService.rejectLeave(leaveId);
            JOptionPane.showMessageDialog(this, "Leave rejected.");
            loadLeaves();
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Error rejecting leave.");
        }
    }
}