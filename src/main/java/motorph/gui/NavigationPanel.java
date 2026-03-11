package motorph.gui;

import javax.swing.JOptionPane;
import javax.swing.*;
import java.awt.*;

public class NavigationPanel extends JPanel {

private CardLayout cardLayout;
private MainApplication mainApp;
private boolean isAdmin;
private JPanel mainPanel;

public void setMainApp(MainApplication mainApp, boolean isAdmin) {
    this.mainApp = mainApp;
    this.isAdmin = isAdmin;
    this.mainPanel = mainApp.getContentPanel();

    LayoutManager layout = this.mainPanel.getLayout();
    if (layout instanceof CardLayout) {
        this.cardLayout = (CardLayout) layout;
    } else {
        this.cardLayout = new CardLayout();
        this.mainPanel.setLayout(this.cardLayout);
    }

    updateButtonVisibility();
}

public NavigationPanel() {
    initComponents();
    this.isAdmin = false;
}

private void updateButtonVisibility() {

    dashboardButton.setVisible(true);
    attendanceButton.setVisible(true);
    logoutButton.setVisible(true);

    if (isAdmin) {
        employeesButton.setVisible(true);
        payrollButton.setVisible(true);
    } else {
        employeesButton.setVisible(false);
        payrollButton.setVisible(false);
    }
}

@SuppressWarnings("unchecked")
private void initComponents() {

    jPanel1 = new javax.swing.JPanel();
    jLabel5 = new javax.swing.JLabel();
    dashboardButton = new javax.swing.JButton();
    employeesButton = new javax.swing.JButton();
    payrollButton = new javax.swing.JButton();
    attendanceButton = new javax.swing.JButton();
    jPanel2 = new javax.swing.JPanel();
    jLabel1 = new javax.swing.JLabel();
    logoutButton = new javax.swing.JButton();

    setPreferredSize(new java.awt.Dimension(160, 200));

    jPanel1.setBackground(new java.awt.Color(139, 0, 0));
    jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());
    jPanel1.setMinimumSize(new java.awt.Dimension(160, 600));
    jPanel1.setPreferredSize(new java.awt.Dimension(160, 600));
    jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

    jLabel5.setFont(new java.awt.Font("Segoe UI", 1, 20));
    jLabel5.setForeground(new java.awt.Color(255, 255, 255));
    jLabel5.setText("Menu");
    jPanel1.add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 160, -1, -1));

    dashboardButton.setFont(new java.awt.Font("Segoe UI", 0, 14));
    dashboardButton.setText("Dashboard");
    dashboardButton.addActionListener(evt -> dashboardButtonActionPerformed(evt));
    jPanel1.add(dashboardButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 200, 140, -1));

    employeesButton.setFont(new java.awt.Font("Segoe UI", 0, 14));
    employeesButton.setText("Employees");
    employeesButton.addActionListener(evt -> employeesButtonActionPerformed(evt));
    jPanel1.add(employeesButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 250, 140, -1));

    payrollButton.setFont(new java.awt.Font("Segoe UI", 0, 14));
    payrollButton.setText("Payroll");
    payrollButton.addActionListener(evt -> payrollButtonActionPerformed(evt));
    jPanel1.add(payrollButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 350, 140, -1));

    attendanceButton.setFont(new java.awt.Font("Segoe UI", 0, 14));
    attendanceButton.setText("Attendance");
    attendanceButton.addActionListener(evt -> attendanceButtonActionPerformed(evt));
    jPanel1.add(attendanceButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 300, 140, -1));

    jPanel2.setBackground(new java.awt.Color(139, 0, 0));
    jPanel2.setBorder(javax.swing.BorderFactory.createEtchedBorder());
    jPanel2.setLayout(null);

    jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
    jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/motorph_logo (6).png")));
    jPanel2.add(jLabel1);
    jLabel1.setBounds(0, 0, 160, 150);

    jPanel1.add(jPanel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 160, 150));

    logoutButton.setFont(new java.awt.Font("Segoe UI", 0, 14));
    logoutButton.setText("Logout");
    logoutButton.addActionListener(evt -> logoutButtonActionPerformed(evt));
    jPanel1.add(logoutButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 540, -1, -1));

    javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
    this.setLayout(layout);
    layout.setHorizontalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE,
                    javax.swing.GroupLayout.PREFERRED_SIZE));

    layout.setVerticalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE,
                    javax.swing.GroupLayout.PREFERRED_SIZE));
}

private void employeesButtonActionPerformed(java.awt.event.ActionEvent evt) {
    if (this.isAdmin && this.cardLayout != null && this.mainPanel != null) {
        this.cardLayout.show(this.mainPanel, MainApplication.ADMIN_EMPLOYEES);
    } else if (!this.isAdmin && this.cardLayout != null && this.mainPanel != null) {
        this.cardLayout.show(this.mainPanel, MainApplication.EMP_EMPLOYEES);
    }
}

private void logoutButtonActionPerformed(java.awt.event.ActionEvent evt) {

    int confirm = JOptionPane.showConfirmDialog(
            this,
            "Are you sure you want to log out?",
            "Logout",
            JOptionPane.YES_NO_OPTION);

    if (confirm == JOptionPane.YES_OPTION) {

        Window window = SwingUtilities.getWindowAncestor(this);
        if (window != null) {
            window.setVisible(false);
        }

        new motorph.gui.AppStart().setVisible(true);
    }
}

private void dashboardButtonActionPerformed(java.awt.event.ActionEvent evt) {
    if (mainApp != null) {
        mainApp.showPanel(isAdmin
                ? MainApplication.ADMIN_DASHBOARD
                : MainApplication.EMP_DASHBOARD);
    }
}

private void attendanceButtonActionPerformed(java.awt.event.ActionEvent evt) {

    if (this.cardLayout != null && this.mainPanel != null) {
        this.cardLayout.show(
                this.mainPanel,
                this.isAdmin
                        ? MainApplication.ADMIN_ATTENDANCE
                        : MainApplication.EMP_ATTENDANCE);
    }
}

private void payrollButtonActionPerformed(java.awt.event.ActionEvent evt) {

    if (this.cardLayout != null && this.mainPanel != null) {

        this.cardLayout.show(
                this.mainPanel,
                this.isAdmin
                        ? MainApplication.ADMIN_PAYROLL
                        : MainApplication.EMP_PAYROLL);

        System.out.println("Navigating to "
                + (this.isAdmin
                        ? MainApplication.ADMIN_PAYROLL
                        : MainApplication.EMP_PAYROLL)
                + " panel");

    } else {

        System.out.println("Error: cardLayout or mainPanel is null.");
    }
}

private javax.swing.JButton attendanceButton;
private javax.swing.JButton dashboardButton;
private javax.swing.JButton employeesButton;
private javax.swing.JLabel jLabel1;
private javax.swing.JLabel jLabel5;
private javax.swing.JPanel jPanel1;
private javax.swing.JPanel jPanel2;
private javax.swing.JButton logoutButton;
private javax.swing.JButton payrollButton;

}