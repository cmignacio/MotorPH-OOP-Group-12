package motorph.gui;

import java.awt.CardLayout;
import java.awt.Container;
import javax.swing.JPanel;
import motorph.gui.admin.DashboardPanel;
import motorph.gui.admin.AttendancePanel;
import motorph.gui.admin.EmployeesPanel;
import motorph.gui.employee.EmpDashboardPanel;
import motorph.gui.employee.EmpAttendancePanel;
import motorph.gui.admin.PayrollFrame;
import motorph.gui.employee.EmpPayrollFrame;
import motorph.gui.admin.AdminLeavePanel;
import motorph.gui.employee.EmpLeavePanel;
import motorph.gui.finance.FinanceDashboardPanel;



/**
 * The main application window for MotorPH employee management system.
 * Displays different panels based on user role (admin or employee).
 */
public class MainApplication extends javax.swing.JFrame {
    // Panels for different sections
    private JPanel dashboardPanel;
    private JPanel employeesPanel;
    private JPanel attendancePanel;
    private JPanel payrollPanel;
    
    private CardLayout cardLayout; // Layout to switch panels
    private motorph.model.Role userRole; 
    private String employeeNumber; 

    // Names for panels in CardLayout
    public static final String ADMIN_DASHBOARD = "dashboard";
    public static final String ADMIN_EMPLOYEES = "employees";
    public static final String ADMIN_ATTENDANCE = "attendance";
    public static final String ADMIN_PAYROLL = "payroll";

    public static final String EMP_DASHBOARD = "emp_dashboard";
    public static final String EMP_ATTENDANCE = "emp_attendance";
    public static final String EMP_PAYROLL = "emp_payroll";
    public static final String ADMIN_LEAVE = "ADMIN_LEAVE";
    public static final String EMP_LEAVE = "EMP_LEAVE";
    
    public static final String FINANCE_DASHBOARD = "finance_dashboard";

    /**
     * Creates new form MainApplication
     * @param role The user role ("admin" or "employee")
     * @param employeeNumber The employee ID (null for admin)
     */
    public MainApplication(motorph.model.Role role, String employeeNumber) {
        this.userRole = role;
        this.employeeNumber = employeeNumber;
        initComponents();
        setLocationRelativeTo(null);
        initializePanels();
        navigationPanel1.setMainApp(this, role == motorph.model.Role.ADMIN);
        setTitle("MotorPH - " + role.name());
    }
    
    public MainApplication(motorph.model.Role role) {
    this(role, null);
    }

    /**
     * Gets the content panel
     * @return The content panel
     */
    public JPanel getContentPanel() {
        return contentPanel;
    }

    /**
     * Initializes panels based on user role
     */
    private void initializePanels() {
    cardLayout = new CardLayout();
    contentPanel.setLayout(cardLayout);

    // Create panels based on user role
        if (userRole == motorph.model.Role.ADMIN) {
    initializeAdminPanels();
    }
    else if (userRole == motorph.model.Role.FINANCE_STAFF
        || userRole == motorph.model.Role.FINANCE_MANAGER) {
    initializeFinancePanels();
    }
    else {
    initializeEmployeePanels();
    }

    // Show default panel
    if (userRole == motorph.model.Role.ADMIN) {
        cardLayout.show(contentPanel, ADMIN_DASHBOARD);
    } else if (userRole == motorph.model.Role.FINANCE_STAFF
            || userRole == motorph.model.Role.FINANCE_MANAGER) {
        cardLayout.show(contentPanel, FINANCE_DASHBOARD);
    } else {
        cardLayout.show(contentPanel, EMP_DASHBOARD);
    }
}

    /**
     * Initializes panels for admin users
     */
    private void initializeAdminPanels() {
    dashboardPanel = new DashboardPanel();
    employeesPanel = new EmployeesPanel(contentPanel, cardLayout, this);
    attendancePanel = new AttendancePanel(this);

    Container payrollContainer = new PayrollFrame().getContentPane();
    if (payrollContainer instanceof JPanel) {
        payrollPanel = (JPanel) payrollContainer;
    } else {
        System.err.println("Error: PayrollFrame content pane is not a JPanel.");
        payrollPanel = new JPanel();
        payrollPanel.add(payrollContainer);
    }

    contentPanel.add(dashboardPanel, ADMIN_DASHBOARD);
    contentPanel.add(employeesPanel, ADMIN_EMPLOYEES);
    contentPanel.add(attendancePanel, ADMIN_ATTENDANCE);
    contentPanel.add(payrollPanel, ADMIN_PAYROLL);
    contentPanel.add(new AdminLeavePanel(), ADMIN_LEAVE);
}

private void initializeFinancePanels() {
    boolean isManager = userRole == motorph.model.Role.FINANCE_MANAGER;

    dashboardPanel = new FinanceDashboardPanel(this, employeeNumber, isManager);
    attendancePanel = new EmpAttendancePanel(employeeNumber);

    Container empPayrollContainer = new EmpPayrollFrame(employeeNumber).getContentPane();
    if (empPayrollContainer instanceof JPanel) {
        payrollPanel = (JPanel) empPayrollContainer;
    } else {
        System.err.println("Error: EmpPayrollFrame content pane is not a JPanel.");
        payrollPanel = new JPanel();
        payrollPanel.add(empPayrollContainer);
    }

    contentPanel.add(dashboardPanel, FINANCE_DASHBOARD);
    contentPanel.add(attendancePanel, EMP_ATTENDANCE);
    contentPanel.add(payrollPanel, EMP_PAYROLL);
    contentPanel.add(new EmpLeavePanel(employeeNumber), EMP_LEAVE);
}
private void initializeEmployeePanels() {
    dashboardPanel = new EmpDashboardPanel(this);
    attendancePanel = new EmpAttendancePanel(employeeNumber);

    Container empPayrollContainer = new EmpPayrollFrame(employeeNumber).getContentPane();
    if (empPayrollContainer instanceof JPanel) {
        payrollPanel = (JPanel) empPayrollContainer;
    } else {
        System.err.println("Error: EmpPayrollFrame content pane is not a JPanel.");
        payrollPanel = new JPanel();
        payrollPanel.add(empPayrollContainer);
    }

    contentPanel.add(dashboardPanel, EMP_DASHBOARD);
    contentPanel.add(attendancePanel, EMP_ATTENDANCE);
    contentPanel.add(payrollPanel, EMP_PAYROLL);
    contentPanel.add(new EmpLeavePanel(employeeNumber), EMP_LEAVE);
}

    /**
     * Shows a specific panel by name
     * @param panelName The name of the panel to show
     */
    public void showPanel(String panelName) {
        if (cardLayout != null && contentPanel != null) {
            cardLayout.show(contentPanel, panelName);
        } else {
            System.err.println("Error: cardLayout or contentPanel is null.");
        }
    }

    /**
     * Checks if user is admin
     * @return true if user is admin, false otherwise
     */
    public boolean isAdmin() {
        return userRole == motorph.model.Role.ADMIN;
    }

    /**
     * Gets the employee number
     * @return The employee number
     */
    public String getEmployeeNumber() {
        return employeeNumber;
    }
    
    public motorph.model.Role getUserRole() {
        return userRole;
    }
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        navigationPanel1 = new motorph.gui.NavigationPanel();
        contentPanel = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setMaximumSize(new java.awt.Dimension(1000, 600));
        setResizable(false);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        navigationPanel1.setMinimumSize(new java.awt.Dimension(160, 200));
        getContentPane().add(navigationPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 160, 600));

        contentPanel.setBackground(new java.awt.Color(237, 237, 237));
        contentPanel.setMaximumSize(new java.awt.Dimension(2147483647, 2147483647));
        contentPanel.setPreferredSize(new java.awt.Dimension(840, 600));
        contentPanel.setLayout(new java.awt.BorderLayout());
        getContentPane().add(contentPanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(160, 0, 840, 600));

        pack();
    }// </editor-fold>//GEN-END:initComponents


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
            java.util.logging.Logger.getLogger(MainApplication.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(MainApplication.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(MainApplication.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(MainApplication.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        // display the form
        java.awt.EventQueue.invokeLater(() -> {
            new MainApplication(motorph.model.Role.ADMIN, "10001").setVisible(true);
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel contentPanel;
    private motorph.gui.NavigationPanel navigationPanel1;
    // End of variables declaration//GEN-END:variables
}
