package gui;

import javax.swing.table.DefaultTableModel;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import javax.swing.JOptionPane;
import java.sql.PreparedStatement;
import java.text.SimpleDateFormat;
import java.util.Date;

public class viewticket extends javax.swing.JFrame {
    public void loadTicketsFromDatabase() {
String[] columns = {"DB ID", "Ticket ID", "Student Name", "Student ID", "Building", "Status"};
    DefaultTableModel model = new DefaultTableModel(columns, 0);

    String loggedInUser = UserSession.getUsername();
    String sql = "SELECT * FROM tbl_tickets WHERE name LIKE ?";

    try {
        Connection conn = dbconnection.getConnection();
        PreparedStatement pst = conn.prepareStatement(sql);
        pst.setString(1, "%" + loggedInUser + "%");

        ResultSet rs = pst.executeQuery();
        int counter = 1;

while (rs.next()) {
    int dbTicketId = rs.getInt("id"); 
    String displayTicketId = "TID-" + String.format("%04d", counter++);
    String name = rs.getString("name");
    String studentId = rs.getString("student_id");
    String building = rs.getString("building");
    String status = rs.getString("status");

    model.addRow(new Object[]{dbTicketId, displayTicketId, name, studentId, building, status});
}

        rs.close();
        pst.close();
        conn.close();

        jTable1.setModel(model);
        
        // Hide the database ID column (column 0)
        jTable1.getColumnModel().getColumn(0).setMinWidth(0);
        jTable1.getColumnModel().getColumn(0).setMaxWidth(0);
        jTable1.getColumnModel().getColumn(0).setWidth(0);

        applyTableStyling();

    } catch (Exception e) {
        JOptionPane.showMessageDialog(this, "Error loading tickets: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
    
    
  
        jTable1.setModel(model);
        
       
        jTable1.setRowHeight(30); 
        jTable1.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 14)); 
        
        // Header styling
        jTable1.getTableHeader().setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 14));
        jTable1.getTableHeader().setBackground(new java.awt.Color(0, 102, 51)); 
        jTable1.getTableHeader().setForeground(java.awt.Color.WHITE); 
        
        jTable1.setRowHeight(30);
        jTable1.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 14));
        jTable1.getTableHeader().setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 14));


        jTable1.getTableHeader().setBackground(new java.awt.Color(0, 153, 51)); 
        jTable1.getTableHeader().setForeground(java.awt.Color.BLACK);
        
    }
    }

private void fetchTicketDetails(String studentId) {
    int selectedRow = jTable1.getSelectedRow();
    if (selectedRow == -1) return;

    String displayedTicketId = jTable1.getValueAt(selectedRow, 0).toString();
    String targetBuilding = jTable1.getValueAt(selectedRow, 3).toString();

   
    String sql = "SELECT * FROM tbl_tickets WHERE student_id = ? AND building = ? ORDER BY ticket_id ASC";

    try (Connection conn = dbconnection.getConnection();
         PreparedStatement pst = conn.prepareStatement(sql)) {
        
        pst.setString(1, studentId);
        pst.setString(2, targetBuilding);
        
        try (ResultSet rs = pst.executeQuery()) {
            // Loop through to find the match corresponding to how many times this building appears up to this row
            int matchIndex = 0;
            int targetMatchCount = 0;
            
            // Count how many times this building appears from row 0 up to the selected row
            for (int i = 0; i <= selectedRow; i++) {
                if (jTable1.getValueAt(i, 3).toString().equals(targetBuilding)) {
                    targetMatchCount++;
                }
            }

            boolean found = false;
            while (rs.next()) {
                matchIndex++;
                if (matchIndex == targetMatchCount) {
                    found = true;
                    
                    TicketId.setText(displayedTicketId);

                    java.sql.Timestamp ts = rs.getTimestamp("date_submitted");
                    if (ts != null) {
                        String formattedDate = new SimpleDateFormat("MMMM dd, yyyy, hh:mm a").format(new Date(ts.getTime()));
                        DateSubmitted.setText(formattedDate);
                    }

                    Subject.setText(rs.getString("issue_type"));
                    
                    String statusVal = "Pending"; 
                    Status.setText(statusVal);

                    if (statusVal.equalsIgnoreCase("Pending")) {
                        Status.setForeground(new java.awt.Color(220, 38, 38)); 
                    } else if (statusVal.equalsIgnoreCase("In Progress")) {
                        Status.setForeground(new java.awt.Color(202, 138, 4)); 
                    } else if (statusVal.equalsIgnoreCase("Resolved")) {
                        Status.setForeground(new java.awt.Color(22, 163, 74)); 
                    }

                    Category.setText(rs.getString("building"));
                    Priority.setText(rs.getString("room_no"));
                    Desc.setText(rs.getString("description"));
                    break;
                }
            }
        }
    } catch (Exception ex) {
        JOptionPane.showMessageDialog(this, "Error fetching details: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
    }
}
    
private void applyTableStyling() {
        jTable1.setRowHeight(30);
        jTable1.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 14));
        jTable1.getTableHeader().setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 14));
        jTable1.getTableHeader().setBackground(new java.awt.Color(0, 128, 64)); 
        jTable1.getTableHeader().setForeground(java.awt.Color.WHITE);

        jTable1.setDefaultRenderer(Object.class, new javax.swing.table.DefaultTableCellRenderer() {
            @Override
            public java.awt.Component getTableCellRendererComponent(javax.swing.JTable table, Object value, 
                    boolean isSelected, boolean hasFocus, int row, int column) {
                
                java.awt.Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                
                if (column == 4 && value != null) {
                    String statusText = value.toString().trim();
                    
                    if (statusText.equalsIgnoreCase("Pending")) {
                        c.setForeground(new java.awt.Color(220, 38, 38)); 
                    } else if (statusText.equalsIgnoreCase("In Progress")) {
                        c.setForeground(new java.awt.Color(202, 138, 4)); 
                    } else if (statusText.equalsIgnoreCase("Resolved")) {
                        c.setForeground(new java.awt.Color(22, 163, 74)); 
                    }
                    
                    setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 14));
                } else if (!isSelected) {
                    c.setForeground(java.awt.Color.BLACK); 
                }
                
                return c;
            }
        });
}

private void fetchTicketDetailsById(int dbTicketId) {
    int selectedRow = jTable1.getSelectedRow();
    String displayedTicketId = jTable1.getValueAt(selectedRow, 1).toString();
    String sql = "SELECT * FROM tbl_tickets WHERE ticket_id = ?";

    try (Connection conn = dbconnection.getConnection();
         PreparedStatement pst = conn.prepareStatement(sql)) {
        
        pst.setInt(1, dbTicketId);
        
        try (ResultSet rs = pst.executeQuery()) {
            if (rs.next()) {
                populateFields(rs, displayedTicketId);
            }
        }
    } catch (Exception ex) {
        JOptionPane.showMessageDialog(this, "Error fetching details: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
    }
}

private void fetchTicketDetailsByRow(int selectedRow) {
    String displayedTicketId = jTable1.getValueAt(selectedRow, 1).toString();
    String studentId = jTable1.getValueAt(selectedRow, 3).toString();
    String targetBuilding = jTable1.getValueAt(selectedRow, 4).toString();

    String sql = "SELECT * FROM tbl_tickets WHERE student_id = ? AND building = ? ORDER BY ticket_id ASC LIMIT 1";

    try (Connection conn = dbconnection.getConnection();
         PreparedStatement pst = conn.prepareStatement(sql)) {
        
        pst.setString(1, studentId);
        pst.setString(2, targetBuilding);
        
        try (ResultSet rs = pst.executeQuery()) {
            if (rs.next()) {
                populateFields(rs, displayedTicketId);
            }
        }
    } catch (Exception ex) {
        JOptionPane.showMessageDialog(this, "Error fetching details: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
    }
}

public void populateFields(ResultSet rs, String ticketIdStr) {
    try {
        String status = rs.getString("status");

        // Toggle buttons based on status
        if (status != null && !status.equalsIgnoreCase("Pending")) {
            jButton8.setVisible(false); 
            jButton9.setVisible(false); 
        } else {
            jButton8.setVisible(true);
            jButton9.setVisible(true);
        }

        java.sql.Timestamp ts = rs.getTimestamp("date_submitted");
        if (ts != null) {
            String formattedDate = new SimpleDateFormat("MMMM dd, yyyy, hh:mm a").format(new Date(ts.getTime()));
            DateSubmitted.setText(formattedDate);
        } else {
            DateSubmitted.setText("");
        }

        Subject.setText(rs.getString("issue_type") != null ? rs.getString("issue_type") : "");
        
        String statusVal = status != null ? status : "";
        Status.setText(statusVal);

        if (statusVal.equalsIgnoreCase("Pending")) {
            Status.setForeground(new java.awt.Color(220, 38, 38)); 
        } else if (statusVal.equalsIgnoreCase("In Progress")) {
            Status.setForeground(new java.awt.Color(202, 138, 4)); 
        } else if (statusVal.equalsIgnoreCase("Resolved")) {
            Status.setForeground(new java.awt.Color(22, 163, 74)); 
        }

        Category.setText(rs.getString("building") != null ? rs.getString("building") : "");
        Priority.setText(rs.getString("room_no") != null ? rs.getString("room_no") : "");
        Desc.setText(rs.getString("description") != null ? rs.getString("description") : "");
        
        String technician = rs.getString("assigned_technician");
        String targetDate = rs.getString("target_date");
        
        AssignedTech.setText(technician != null && !technician.isEmpty() ? technician : "Not yet assigned");
        TargetDate.setText(targetDate != null && !targetDate.isEmpty() ? targetDate : "TBD");

    } catch (Exception e) {
        JOptionPane.showMessageDialog(this, "Error populating fields: " + e.getMessage());
    }
}
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(viewticket.class.getName());

    /**
     * Creates new form viewticket
     */
    public viewticket() {
    initComponents();
    loadTicketsFromDatabase();
    jButton8.setEnabled(false); 
    jButton9.setEnabled(false); 
    loadTicketsFromDatabase(); 
    
 
    jTable1.getSelectionModel().addListSelectionListener(event -> {
        if (!event.getValueIsAdjusting() && jTable1.getSelectedRow() != -1) {
            int selectedRow = jTable1.getSelectedRow();
            String selectedStudentId = jTable1.getValueAt(selectedRow, 2).toString();
            fetchTicketDetails(selectedStudentId);
        }
    });
}

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        popupMenu1 = new java.awt.PopupMenu();
        jSplitPane1 = new javax.swing.JSplitPane();
        jPanel32 = new javax.swing.JPanel();
        jPanel33 = new javax.swing.JPanel();
        jLabel42 = new javax.swing.JLabel();
        jPanel34 = new javax.swing.JPanel();
        jPanel35 = new javax.swing.JPanel();
        jButton10 = new javax.swing.JButton();
        jPanel4 = new javax.swing.JPanel();
        jPanel5 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jPanel10 = new javax.swing.JPanel();
        jLabel9 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        Desc = new javax.swing.JTextArea();
        TicketId = new javax.swing.JLabel();
        DateSubmitted = new javax.swing.JLabel();
        Subject = new javax.swing.JLabel();
        Status = new javax.swing.JLabel();
        Category = new javax.swing.JLabel();
        Priority = new javax.swing.JLabel();
        jButton8 = new javax.swing.JButton();
        jButton9 = new javax.swing.JButton();
        jLabel21 = new javax.swing.JLabel();
        jLabel22 = new javax.swing.JLabel();
        AssignedTech = new javax.swing.JLabel();
        TargetDate = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jLabel17 = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();

        popupMenu1.setLabel("popupMenu1");

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel32.setBackground(new java.awt.Color(255, 255, 255));
        jPanel32.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 255, 255), 2));
        jPanel32.setForeground(new java.awt.Color(0, 153, 0));
        jPanel32.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel33.setBackground(new java.awt.Color(0, 153, 51));

        jLabel42.setForeground(new java.awt.Color(255, 255, 255));
        jLabel42.setText("PHINMA COC @ 2026 ALL RIGHTS RESERVE.");

        javax.swing.GroupLayout jPanel33Layout = new javax.swing.GroupLayout(jPanel33);
        jPanel33.setLayout(jPanel33Layout);
        jPanel33Layout.setHorizontalGroup(
            jPanel33Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel33Layout.createSequentialGroup()
                .addContainerGap(1305, Short.MAX_VALUE)
                .addComponent(jLabel42)
                .addGap(31, 31, 31))
        );
        jPanel33Layout.setVerticalGroup(
            jPanel33Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel33Layout.createSequentialGroup()
                .addContainerGap(10, Short.MAX_VALUE)
                .addComponent(jLabel42)
                .addGap(14, 14, 14))
        );

        jPanel32.add(jPanel33, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 800, 1570, 40));

        jPanel34.setBackground(new java.awt.Color(0, 102, 102));
        jPanel34.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());
        jPanel32.add(jPanel34, new org.netbeans.lib.awtextra.AbsoluteConstraints(250, 160, -1, -1));

        jPanel35.setBackground(new java.awt.Color(0, 153, 51));

        javax.swing.GroupLayout jPanel35Layout = new javax.swing.GroupLayout(jPanel35);
        jPanel35.setLayout(jPanel35Layout);
        jPanel35Layout.setHorizontalGroup(
            jPanel35Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1570, Short.MAX_VALUE)
        );
        jPanel35Layout.setVerticalGroup(
            jPanel35Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 40, Short.MAX_VALUE)
        );

        jPanel32.add(jPanel35, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 1570, 40));

        jButton10.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jButton10.setIcon(new javax.swing.ImageIcon("C:\\Users\\Neil\\Downloads\\icons8-back-60.png")); // NOI18N
        jButton10.setText("Back");
        jButton10.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 153, 51), 3, true));
        jButton10.addActionListener(this::jButton10ActionPerformed);
        jPanel32.add(jButton10, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 710, 130, 60));

        jPanel4.setBackground(new java.awt.Color(0, 153, 51));

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 60, Short.MAX_VALUE)
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 770, Short.MAX_VALUE)
        );

        jPanel32.add(jPanel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(1510, 30, 60, 770));

        jPanel5.setBackground(new java.awt.Color(0, 153, 51));

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 30, Short.MAX_VALUE)
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 780, Short.MAX_VALUE)
        );

        jPanel32.add(jPanel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 20, 30, 780));

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));
        jPanel2.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 153, 51), 2, true));

        jPanel10.setBackground(new java.awt.Color(0, 153, 51));

        jLabel9.setFont(new java.awt.Font("Segoe UI", 1, 20)); // NOI18N
        jLabel9.setText("Ticket Details");

        javax.swing.GroupLayout jPanel10Layout = new javax.swing.GroupLayout(jPanel10);
        jPanel10.setLayout(jPanel10Layout);
        jPanel10Layout.setHorizontalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel9)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel10Layout.setVerticalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel10Layout.createSequentialGroup()
                .addContainerGap(20, Short.MAX_VALUE)
                .addComponent(jLabel9)
                .addContainerGap())
        );

        jLabel11.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel11.setText("Date Submitted:");

        jLabel12.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel12.setText("Subject:");

        jLabel13.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel13.setText("Status:");

        jLabel14.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel14.setText("Building:");

        jLabel15.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel15.setText("Room Number:");

        jLabel16.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel16.setText("Description:");

        Desc.setColumns(20);
        Desc.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        Desc.setLineWrap(true);
        Desc.setRows(5);
        Desc.setText("\n");
        Desc.setWrapStyleWord(true);
        jScrollPane1.setViewportView(Desc);

        TicketId.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N

        DateSubmitted.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N

        Subject.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N

        Status.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        Status.setForeground(new java.awt.Color(255, 204, 51));

        Category.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N

        Priority.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N

        jButton8.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jButton8.setIcon(new javax.swing.ImageIcon("C:\\Users\\Neil\\Pictures\\icon plus.png")); // NOI18N
        jButton8.setText("Update Ticket");
        jButton8.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 153, 51), 3, true));
        jButton8.addActionListener(this::jButton8ActionPerformed);

        jButton9.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jButton9.setIcon(new javax.swing.ImageIcon("C:\\Users\\Neil\\Pictures\\icon plus.png")); // NOI18N
        jButton9.setText("Delete Ticket");
        jButton9.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 153, 51), 3, true));
        jButton9.addActionListener(this::jButton9ActionPerformed);

        jLabel21.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel21.setText("Tech Assigned:");

        jLabel22.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel22.setText("Target Date:");

        AssignedTech.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        AssignedTech.setText(".");

        TargetDate.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        TargetDate.setText(".");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel10, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(160, 160, 160)
                        .addComponent(TicketId, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel11)
                            .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel15))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(Status)
                            .addComponent(DateSubmitted)
                            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addComponent(Priority, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 184, Short.MAX_VALUE)
                                .addComponent(Category, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addComponent(Subject)))
                    .addComponent(jLabel16)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(15, 15, 15)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 468, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jButton8, javax.swing.GroupLayout.PREFERRED_SIZE, 215, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jButton9, javax.swing.GroupLayout.PREFERRED_SIZE, 213, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel21)
                            .addComponent(jLabel22))
                        .addGap(31, 31, 31)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(TargetDate, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(AssignedTech, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addContainerGap(27, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(jPanel10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(TicketId)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel11)
                    .addComponent(DateSubmitted))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel12)
                    .addComponent(Subject))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel13)
                    .addComponent(Status))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel14)
                    .addComponent(Category))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel15)
                    .addComponent(Priority))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel21)
                    .addComponent(AssignedTech))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel22)
                    .addComponent(TargetDate))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 34, Short.MAX_VALUE)
                .addComponent(jLabel16)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 167, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton8)
                    .addComponent(jButton9))
                .addGap(16, 16, 16))
        );

        jPanel32.add(jPanel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(960, 80, 520, 670));

        jTable1.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jTable1.setForeground(new java.awt.Color(0, 153, 51));
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
        jTable1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTable1MouseClicked(evt);
            }
        });
        jScrollPane2.setViewportView(jTable1);

        jPanel32.add(jScrollPane2, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 160, 820, 530));

        jLabel17.setFont(new java.awt.Font("Verdana", 1, 34)); // NOI18N
        jLabel17.setForeground(new java.awt.Color(0, 102, 0));
        jLabel17.setText("ITS Ticketing");
        jPanel32.add(jLabel17, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 60, 270, 40));

        jLabel18.setFont(new java.awt.Font("Verdana", 1, 34)); // NOI18N
        jLabel18.setForeground(new java.awt.Color(0, 102, 0));
        jLabel18.setText("System");
        jPanel32.add(jLabel18, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 90, 150, 40));

        jLabel7.setIcon(new javax.swing.ImageIcon("C:\\Users\\Neil\\Documents\\NetBeansProjects\\phinma_coc_ITS_Ticketing_System\\src\\coc (1).png")); // NOI18N
        jPanel32.add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 50, -1, -1));

        jLabel19.setFont(new java.awt.Font("Nirmala Text", 1, 16)); // NOI18N
        jLabel19.setForeground(new java.awt.Color(51, 204, 0));
        jLabel19.setText("PHINMA COC");
        jPanel32.add(jLabel19, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 50, 110, -1));

        jLabel20.setBackground(new java.awt.Color(0, 102, 0));
        jLabel20.setFont(new java.awt.Font("Yu Gothic UI Semilight", 1, 24)); // NOI18N
        jLabel20.setForeground(new java.awt.Color(0, 102, 0));
        jLabel20.setText("TICKET STATUS");
        jPanel32.add(jLabel20, new org.netbeans.lib.awtextra.AbsoluteConstraints(290, 100, -1, -1));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel32, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel32, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton10ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton10ActionPerformed
        homepage page = new homepage();
        page .setVisible(true);
        this.dispose();
    }//GEN-LAST:event_jButton10ActionPerformed

    private void jTable1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable1MouseClicked

int selectedRow = jTable1.getSelectedRow();
    if (selectedRow == -1) return;

    try {
       
        int dbId = Integer.parseInt(jTable1.getValueAt(selectedRow, 0).toString());

        String sql = "SELECT * FROM tbl_tickets WHERE id = ?";
        try (Connection conn = dbconnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            
            pst.setInt(1, dbId);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    String status = rs.getString("status");

                  
                    if (DateSubmitted != null) {
                        java.sql.Timestamp ts = rs.getTimestamp("date_submitted");
                        if (ts != null) {
                            String formattedDate = new SimpleDateFormat("MMMM dd, yyyy, hh:mm a").format(new Date(ts.getTime()));
                            DateSubmitted.setText(formattedDate);
                        } else {
                            DateSubmitted.setText("");
                        }
                    }

                    
                    if (Subject != null) Subject.setText(rs.getString("issue_type") != null ? rs.getString("issue_type") : "");
                    if (Status != null) Status.setText(status != null ? status : "");
                    if (Category != null) Category.setText(rs.getString("building") != null ? rs.getString("building") : "");
                    if (Priority != null) Priority.setText(rs.getString("room_no") != null ? rs.getString("room_no") : "");
                    if (Desc != null) Desc.setText(rs.getString("description") != null ? rs.getString("description") : "");

                    
                    String technician = rs.getString("assigned_technician");
                    String targetDate = rs.getString("target_date");
                    if (AssignedTech != null) {
                        AssignedTech.setText(technician != null && !technician.isEmpty() ? technician : "Not yet assigned");
                    }
                    if (TargetDate != null) {
                        TargetDate.setText(targetDate != null && !targetDate.isEmpty() ? targetDate : "TBD");
                    }

                   
                    if (jButton8 != null && jButton9 != null) {
                        boolean isPending = (status != null && status.equalsIgnoreCase("Pending"));
                        jButton8.setVisible(isPending);
                        jButton9.setVisible(isPending);
                    }

                } else {
                    JOptionPane.showMessageDialog(this, "No database record found for ID: " + dbId);
                }
            }
        }
    } catch (Exception e) {
        JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
    
    }
    
    // Enable your buttons if needed
    jButton8.setEnabled(true);
    jButton9.setEnabled(true);
   
    
    
    }//GEN-LAST:event_jTable1MouseClicked

    private void jButton9ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton9ActionPerformed
        int selectedRow = jTable1.getSelectedRow();

        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a ticket from the table to delete.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Grab the real database primary key from hidden column 0
        int dbTicketId = Integer.parseInt(jTable1.getValueAt(selectedRow, 0).toString());

        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this ticket?", "Confirm Deletion", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            String sql = "DELETE FROM tbl_tickets WHERE id = ?";

            try (Connection conn = dbconnection.getConnection();
                PreparedStatement pst = conn.prepareStatement(sql)) {

                pst.setInt(1, dbTicketId);

                int rowsDeleted = pst.executeUpdate();

                if (rowsDeleted > 0) {
                    JOptionPane.showMessageDialog(this, "Ticket deleted successfully.");
                    loadTicketsFromDatabase();
                    jButton8.setEnabled(false);
                    jButton9.setEnabled(false);
                } else {
                    JOptionPane.showMessageDialog(this, "Record not found in database.", "Error", JOptionPane.ERROR_MESSAGE);
                }

            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Database Error: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        }
        // TODO add your handling code here:
    }//GEN-LAST:event_jButton9ActionPerformed

    private void jButton8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton8ActionPerformed
        int selectedRow = jTable1.getSelectedRow();

        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a ticket from the table to update.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Grab the exact primary key directly from hidden column 0
        int dbTicketId = Integer.parseInt(jTable1.getValueAt(selectedRow, 0).toString());

        // Create input fields for the update popup
        javax.swing.JTextField txtNewBuilding = new javax.swing.JTextField(15);
        javax.swing.JTextField txtNewRoom = new javax.swing.JTextField(15);
        javax.swing.JTextArea txtNewDesc = new javax.swing.JTextArea(5, 20);
        txtNewDesc.setLineWrap(true);
        txtNewDesc.setWrapStyleWord(true);

        Object[] message = {
            "New Building:", txtNewBuilding,
            "New Room Number:", txtNewRoom,
            "New Description:", new javax.swing.JScrollPane(txtNewDesc)
        };

        int option = JOptionPane.showConfirmDialog(this, message, "Update Ticket Information", JOptionPane.OK_CANCEL_OPTION);

        if (option == JOptionPane.OK_OPTION) {
            String updatedBuilding = txtNewBuilding.getText().trim();
            String updatedRoom = txtNewRoom.getText().trim();
            String updatedDesc = txtNewDesc.getText().trim();

            if (updatedBuilding.isEmpty() || updatedRoom.isEmpty() || updatedDesc.isEmpty()) {
                JOptionPane.showMessageDialog(this, "All fields must be filled out to update.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Update directly using the unique ticket_id primary key
            String updateSql = "UPDATE tbl_tickets SET building = ?, room_no = ?, description = ? WHERE id = ?";

            try (Connection conn = dbconnection.getConnection();
                PreparedStatement pst = conn.prepareStatement(updateSql)) {

                pst.setString(1, updatedBuilding);
                pst.setString(2, updatedRoom);
                pst.setString(3, updatedDesc);
                pst.setInt(4, dbTicketId);

                int rowsUpdated = pst.executeUpdate();

                if (rowsUpdated > 0) {
                    JOptionPane.showMessageDialog(this, "Ticket updated successfully!");
                    loadTicketsFromDatabase();
                    jButton8.setEnabled(false);
                    jButton9.setEnabled(false);
                } else {
                    JOptionPane.showMessageDialog(this, "Update failed. Ticket not found.", "Error", JOptionPane.ERROR_MESSAGE);
                }

            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Database Error: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }//GEN-LAST:event_jButton8ActionPerformed

    /**
     * @param args the command line arguments                    
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
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
        } catch (ReflectiveOperationException | javax.swing.UnsupportedLookAndFeelException ex) {
            logger.log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> new viewticket().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel AssignedTech;
    private javax.swing.JLabel Category;
    private javax.swing.JLabel DateSubmitted;
    private javax.swing.JTextArea Desc;
    private javax.swing.JLabel Priority;
    private javax.swing.JLabel Status;
    private javax.swing.JLabel Subject;
    private javax.swing.JLabel TargetDate;
    private javax.swing.JLabel TicketId;
    private javax.swing.JButton jButton10;
    private javax.swing.JButton jButton8;
    private javax.swing.JButton jButton9;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel42;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel32;
    private javax.swing.JPanel jPanel33;
    private javax.swing.JPanel jPanel34;
    private javax.swing.JPanel jPanel35;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JTable jTable1;
    private java.awt.PopupMenu popupMenu1;
    // End of variables declaration//GEN-END:variables
}
