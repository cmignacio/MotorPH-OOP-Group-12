package motorph.gui.admin;

import java.awt.CardLayout;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import motorph.FileHandler;
import motorph.Employee;
import java.util.List;
import java.util.Map;
import java.awt.Frame;
import motorph.gui.MainApplication;

/**
 * A panel for displaying and managing employee records.
 * Provides functionality to view, add, and refresh employee data.
 */
public class EmployeesPanel extends javax.swing.JPanel {

    private FileHandler fileHandler;
    private JDialog addEmployeeDialog;
    private EmployeeDetailsFrame detailsFrame;

    /**
     * Constructs a new EmployeesPanel.
     */
    public EmployeesPanel(JPanel contentPanel, CardLayout cardLayout, MainApplication mainApp) {

        initComponents();
        fileHandler = new FileHandler();
        detailsFrame = new EmployeeDetailsFrame(this); 
        setupTableSelectionListener();
        displayEmployees();
    }

    /**
     * Loads employee data and displays it in the table.
     */
    private void displayEmployees() {
        List<Employee> employees = fileHandler.readEmployees();

        // Create a non-editable table model
        DefaultTableModel model = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        employeesPanelTable.setModel(model);
        String[] columnNames = {
            "Employee Number", "Last Name", "First Name", 
            "SSS Number", "PhilHealth Number", "TIN", "Pag-IBIG Number"
        };
        model.setColumnIdentifiers(columnNames);
        model.setRowCount(0);

        // Populate table with employee data
        for (Employee employee : employees) {
            Map<String, String> employeeData = employee.toMap();
            Object[] row = {
                employeeData.get("Employee #"),
                employeeData.get("Last Name"),
                employeeData.get("First Name"),
                employeeData.get("SSS #"),
                employeeData.get("Philhealth #"),
                employeeData.get("TIN #"),
                employeeData.get("Pag-ibig #")
            };
            
            // Ensure no null values in the row
            for (int i = 0; i < row.length; i++) {
                row[i] = row[i] == null ? "" : String.valueOf(row[i]);
            }
            model.addRow(row);
        }

        viewEmployeeDetailsButton.setEnabled(false);
    }

    /**
     * Sets up table selection listener to enable/disable view button.
     */
    private void setupTableSelectionListener() {
        employeesPanelTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    boolean rowSelected = !employeesPanelTable.getSelectionModel().isSelectionEmpty();
                    viewEmployeeDetailsButton.setEnabled(rowSelected);
                }
            }
        });
    }

    /**
     * Opens the EmployeeDetailsFrame for the selected employee in view mode.
     */
    private void viewEmployeeDetails() {
        int selectedRow = employeesPanelTable.getSelectedRow();
        if (selectedRow != -1) {
            String employeeId = (String) employeesPanelTable.getValueAt(selectedRow, 0);
            Employee employee = fileHandler.getEmployeeById(employeeId);

            if (employee != null) {
                detailsFrame.populateFields(employee);
                detailsFrame.setMode(false);
                detailsFrame.setVisible(true);
            } else {
                JOptionPane.showMessageDialog(this, 
                    "Employee not found!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Refreshes the employee table data.
     */
    public void refreshEmployeeTable() {
        displayEmployees();
    }
    
    @SuppressWarnings("unchecked")

    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel13 = new javax.swing.JPanel();
        employeesTitle = new javax.swing.JLabel();
        addEmployeeButton = new javax.swing.JButton();
        employeeTable = new javax.swing.JScrollPane();
        employeesPanelTable = new javax.swing.JTable();
        viewEmployeeDetailsButton = new javax.swing.JButton();
        refreshEmployeeTable = new javax.swing.JButton();

        setBackground(new java.awt.Color(239, 239, 239));
        setMaximumSize(null);

        jPanel13.setBackground(new java.awt.Color(139, 0, 0));
        jPanel13.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        javax.swing.GroupLayout jPanel13Layout = new javax.swing.GroupLayout(jPanel13);
        jPanel13.setLayout(jPanel13Layout);
        jPanel13Layout.setHorizontalGroup(
            jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        jPanel13Layout.setVerticalGroup(
            jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 41, Short.MAX_VALUE)
        );

        employeesTitle.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        employeesTitle.setForeground(new java.awt.Color(24, 59, 78));
        employeesTitle.setText("Employees");

        addEmployeeButton.setText("New Employee");
        addEmployeeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addEmployeeButtonActionPerformed(evt);
            }
        });

        employeesPanelTable.setModel(new javax.swing.table.DefaultTableModel(
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
        employeeTable.setViewportView(employeesPanelTable);

        viewEmployeeDetailsButton.setText("View Employee");
        viewEmployeeDetailsButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                viewEmployeeDetailsButtonActionPerformed(evt);
            }
        });

        refreshEmployeeTable.setText("Refresh");
        refreshEmployeeTable.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                refreshEmployeeTableActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel13, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(addEmployeeButton)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                            .addGap(15, 15, 15)
                            .addComponent(employeesTitle, javax.swing.GroupLayout.PREFERRED_SIZE, 265, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(layout.createSequentialGroup()
                            .addGap(25, 25, 25)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                    .addComponent(refreshEmployeeTable)
                                    .addGap(18, 18, 18)
                                    .addComponent(viewEmployeeDetailsButton))
                                .addComponent(employeeTable, javax.swing.GroupLayout.PREFERRED_SIZE, 781, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addContainerGap(194, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel13, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(employeesTitle, javax.swing.GroupLayout.PREFERRED_SIZE, 53, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 29, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(viewEmployeeDetailsButton)
                    .addComponent(refreshEmployeeTable))
                .addComponent(employeeTable, javax.swing.GroupLayout.PREFERRED_SIZE, 347, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(addEmployeeButton)
                .addGap(0, 60, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents



    private void addEmployeeButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addEmployeeButtonActionPerformed
        if (addEmployeeDialog != null && addEmployeeDialog.isVisible()) {
            addEmployeeDialog.toFront(); // Bring existing dialog to front
            return;
        }

        Frame parentFrame = null;
        java.awt.Window window = SwingUtilities.getWindowAncestor(this);
        if (window instanceof Frame) {
            parentFrame = (Frame) window;
        }

        addEmployeeDialog = new EmployeeDialog(parentFrame, true, this);
        addEmployeeDialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        addEmployeeDialog.setVisible(true);
    }//GEN-LAST:event_addEmployeeButtonActionPerformed

    private void viewEmployeeDetailsButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_viewEmployeeDetailsButtonActionPerformed
        viewEmployeeDetails();
    }//GEN-LAST:event_viewEmployeeDetailsButtonActionPerformed

    private void refreshEmployeeTableActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_refreshEmployeeTableActionPerformed
        refreshEmployeeTable();

    }//GEN-LAST:event_refreshEmployeeTableActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addEmployeeButton;
    private javax.swing.JScrollPane employeeTable;
    private javax.swing.JTable employeesPanelTable;
    private javax.swing.JLabel employeesTitle;
    private javax.swing.JPanel jPanel13;
    private javax.swing.JButton refreshEmployeeTable;
    private javax.swing.JButton viewEmployeeDetailsButton;
    // End of variables declaration//GEN-END:variables
}
