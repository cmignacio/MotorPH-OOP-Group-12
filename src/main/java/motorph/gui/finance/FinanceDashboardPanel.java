package motorph.gui.finance;

import motorph.gui.MainApplication;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.PrintWriter;
import motorph.service.PayrollCalculator;

public class FinanceDashboardPanel extends JPanel {

    private final MainApplication mainApp;
    private final String employeeNumber;
    private final boolean isManager;

    private JLabel employeeIdLabel;
    private JLabel employeeNameLabel;
    private JLabel roleLabel;

    private JTextArea attendanceArea;
    private JTextArea payslipArea;
    private JTextArea remindersArea;

    private JButton generatePayrollButton;
    private JButton viewPayrollButton;
    private JButton exportReportButton;
    private JButton approveFinalPayrollButton;
    private JButton fileLeaveButton;

    public FinanceDashboardPanel(MainApplication mainApp, String employeeNumber, boolean isManager) {
        this.mainApp = mainApp;
        this.employeeNumber = employeeNumber;
        this.isManager = isManager;
        initComponents();
        loadFinanceData();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBackground(new Color(237, 237, 237));

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(new Color(237, 237, 237));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel titleLabel = new JLabel("Finance Dashboard");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(new Color(28, 60, 90));
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel(new GridLayout(2, 3, 12, 12));
        centerPanel.setBackground(new Color(237, 237, 237));

        centerPanel.add(createProfilePanel());
        centerPanel.add(createAttendancePanel());
        centerPanel.add(createPayslipPanel());
        centerPanel.add(createPayrollActionsPanel());
        centerPanel.add(createLeavePanel());
        centerPanel.add(createRemindersPanel());

        mainPanel.add(centerPanel, BorderLayout.CENTER);
        add(mainPanel, BorderLayout.CENTER);
    }

    private JPanel createProfilePanel() {
        JPanel panel = createCardPanel("Profile");

        JPanel content = new JPanel(new GridLayout(3, 1, 5, 5));
        content.setOpaque(false);

        employeeIdLabel = new JLabel("Employee ID: ");
        employeeNameLabel = new JLabel("Name: ");
        roleLabel = new JLabel("Role: ");

        content.add(employeeIdLabel);
        content.add(employeeNameLabel);
        content.add(roleLabel);

        panel.add(content, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createAttendancePanel() {
        JPanel panel = createCardPanel("Attendance Summary");

        attendanceArea = new JTextArea();
        attendanceArea.setEditable(false);
        attendanceArea.setLineWrap(true);
        attendanceArea.setWrapStyleWord(true);

        panel.add(new JScrollPane(attendanceArea), BorderLayout.CENTER);
        return panel;
    }

    private JPanel createPayslipPanel() {
        JPanel panel = createCardPanel("Payslip Summary");

        payslipArea = new JTextArea();
        payslipArea.setEditable(false);
        payslipArea.setLineWrap(true);
        payslipArea.setWrapStyleWord(true);

        panel.add(new JScrollPane(payslipArea), BorderLayout.CENTER);
        return panel;
    }

    private JPanel createPayrollActionsPanel() {
        JPanel panel = createCardPanel("Payroll Actions");

        JPanel buttonsPanel = new JPanel(new GridLayout(isManager ? 4 : 3, 1, 8, 8));
        buttonsPanel.setOpaque(false);

        generatePayrollButton = new JButton("Generate Payroll");
        viewPayrollButton = new JButton("View Payroll");
        exportReportButton = new JButton("Export Payroll Report");
        approveFinalPayrollButton = new JButton("Approve Final Payroll");

        generatePayrollButton.addActionListener(e ->
                mainApp.showPanel(MainApplication.EMP_PAYROLL));

        viewPayrollButton.addActionListener(e ->
                mainApp.showPanel(MainApplication.EMP_PAYROLL));

        exportReportButton.addActionListener(e -> exportPayrollReport());

        approveFinalPayrollButton.addActionListener(e -> approveFinalPayroll());

        buttonsPanel.add(generatePayrollButton);
        buttonsPanel.add(viewPayrollButton);
        buttonsPanel.add(exportReportButton);

        if (isManager) {
            buttonsPanel.add(approveFinalPayrollButton);
        }

        panel.add(buttonsPanel, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createLeavePanel() {
        JPanel panel = createCardPanel("Leave");

        JPanel content = new JPanel(new BorderLayout());
        content.setOpaque(false);

        fileLeaveButton = new JButton("File Leave");
        fileLeaveButton.addActionListener(e ->
                mainApp.showPanel(MainApplication.EMP_LEAVE));

        content.add(new JLabel("Finance users can file leave requests."), BorderLayout.CENTER);
        content.add(fileLeaveButton, BorderLayout.SOUTH);

        panel.add(content, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createRemindersPanel() {
        JPanel panel = createCardPanel("Reminders");

        remindersArea = new JTextArea();
        remindersArea.setEditable(false);
        remindersArea.setLineWrap(true);
        remindersArea.setWrapStyleWord(true);

        panel.add(new JScrollPane(remindersArea), BorderLayout.CENTER);
        return panel;
    }

    private JPanel createCardPanel(String title) {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createLineBorder(new Color(210, 210, 210)));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setOpaque(true);
        titleLabel.setBackground(new Color(255, 140, 0));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));

        panel.add(titleLabel, BorderLayout.NORTH);
        return panel;
    }

    private void loadFinanceData() {
        employeeIdLabel.setText("Employee ID: " + employeeNumber);
        employeeNameLabel.setText("Name: Finance User");
        roleLabel.setText("Role: " + (isManager ? "Finance Manager" : "Finance Staff"));

        attendanceArea.setText(
                "Recent Attendance:\n" +
                "03/09/2026  08:00 AM - 05:00 PM\n" +
                "03/08/2026  08:05 AM - 05:02 PM\n" +
                "03/07/2026  08:02 AM - 05:01 PM"
        );

        payslipArea.setText(
                "Latest Payslip:\n" +
                "Payroll Period: March 2026\n" +
                "Gross Pay: ₱25,000\n" +
                "Deductions: ₱4,200\n" +
                "Net Pay: ₱20,800"
        );

        remindersArea.setText(
                "• Review attendance before payroll generation.\n" +
                "• Payroll cutoff is every 15th and 30th.\n" +
                (isManager
                        ? "• Final payroll approval is required before release."
                        : "• Coordinate with Finance Manager for final approval.")
        );
    }

    private void exportPayrollReport() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save Payroll Report");

        int userSelection = fileChooser.showSaveDialog(this);

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();

            try (PrintWriter writer = new PrintWriter(fileToSave + ".txt")) {
                writer.println("MotorPH Payroll Report");
                writer.println("----------------------");
                writer.println("Employee ID: " + employeeNumber);
                writer.println("Role: " + (isManager ? "Finance Manager" : "Finance Staff"));
                writer.println();
                writer.println("Payroll Period: March 2026");
                writer.println("Gross Pay: ₱25,000");
                writer.println("Deductions: ₱4,200");
                writer.println("Net Pay: ₱20,800");

                JOptionPane.showMessageDialog(this, "Payroll report exported successfully.");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error exporting payroll report.");
                ex.printStackTrace();
            }
        }
    }

    private void approveFinalPayroll() {
        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Approve final payroll?",
                "Confirm Approval",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm == JOptionPane.YES_OPTION) {
            JOptionPane.showMessageDialog(this, "Final payroll approved successfully.");
        }
    }
}