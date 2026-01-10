import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.ArrayList;

// Import your Data Classes
import DataClass.Employee;
import DataClass.Model;
// Sale and Customer are in the default package, no import needed

public class MainGUI extends JFrame {

    // =========================================
    // GLOBAL STATE
    // =========================================
    private Employee currentUser;
    private SalesSystem salesSystem;
    
    // Layout & Panels
    private CardLayout cardLayout;
    private JPanel mainPanel;
    private final String LOGIN = "LOGIN";
    private final String DASHBOARD = "DASHBOARD";
    private final String ATTENDANCE = "ATTENDANCE";
    private final String STOCK = "STOCK";
    private final String SALES = "SALES";
    private final String SEARCH = "SEARCH";
    private final String EDIT = "EDIT";
    private final String ANALYTICS = "ANALYTICS";
    private final String HISTORY = "HISTORY";
    private final String REGISTER = "REGISTER";
    private final String PERFORMANCE = "PERFORMANCE";

    // Components needing refresh
    private JLabel welcomeLabel;
    private JButton btnRegister, btnPerformance;
    private DefaultTableModel historyModel;
    private JTextArea analyticsArea;
    private DefaultTableModel performanceModel;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                new MainGUI().setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public MainGUI() {
        // 1. INITIALIZE DATA 
        StorageSystem.initialize(); 
        salesSystem = new SalesSystem((ArrayList<Model>) StorageSystem.allModels);

        // 2. SETUP FRAME
        setTitle("GoldenHour Operations System");
        setSize(1200, 800); // Slightly wider to fit the new stock table
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // 3. INIT LAYOUT
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        // 4. ADD SCREENS
        mainPanel.add(createLoginPanel(), LOGIN);
        mainPanel.add(createDashboardPanel(), DASHBOARD);
        mainPanel.add(createAttendancePanel(), ATTENDANCE);
        mainPanel.add(createStockPanel(), STOCK);
        mainPanel.add(createSalesPanel(), SALES);
        mainPanel.add(createSearchPanel(), SEARCH);
        mainPanel.add(createEditPanel(), EDIT);
        mainPanel.add(createAnalyticsPanel(), ANALYTICS);
        mainPanel.add(createHistoryPanel(), HISTORY);
        mainPanel.add(createRegisterPanel(), REGISTER);
        mainPanel.add(createPerformancePanel(), PERFORMANCE);

        add(mainPanel);
        cardLayout.show(mainPanel, LOGIN);
    }

    // =======================================================
    // 1. LOGIN
    // =======================================================
    private JPanel createLoginPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        JLabel title = new JLabel("GoldenHour System Login");
        title.setFont(new Font("Arial", Font.BOLD, 24));
        JTextField userField = new JTextField(15);
        JPasswordField passField = new JPasswordField(15);
        JButton btnLogin = new JButton("Login");

        gbc.gridx=0; gbc.gridy=0; gbc.gridwidth=2; panel.add(title, gbc);
        gbc.gridwidth=1; gbc.gridy=1; panel.add(new JLabel("User ID:"), gbc);
        gbc.gridx=1; panel.add(userField, gbc);
        gbc.gridx=0; gbc.gridy=2; panel.add(new JLabel("Password:"), gbc);
        gbc.gridx=1; panel.add(passField, gbc);
        gbc.gridx=1; gbc.gridy=3; panel.add(btnLogin, gbc);

        btnLogin.addActionListener(e -> {
            String id = userField.getText().trim();
            String pass = new String(passField.getPassword()).trim();
            
            boolean found = false;
            for (Employee emp : StorageSystem.allEmployees) {
                if (emp.getID().equalsIgnoreCase(id) && emp.getPassword().equals(pass)) {
                    currentUser = emp;
                    found = true;
                    break;
                }
            }

            if (found) {
                userField.setText("");
                passField.setText("");
                refreshDashboard();
                cardLayout.show(mainPanel, DASHBOARD);
            } else {
                JOptionPane.showMessageDialog(this, "Invalid ID or Password", "Login Failed", JOptionPane.ERROR_MESSAGE);
            }
        });
        return panel;
    }

    // =======================================================
    // 2. DASHBOARD (Updated Layout & Labels)
    // =======================================================
    private JPanel createDashboardPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        
        JPanel header = new JPanel(new FlowLayout(FlowLayout.CENTER));
        welcomeLabel = new JLabel("Welcome!");
        welcomeLabel.setFont(new Font("SansSerif", Font.BOLD, 20));
        header.add(welcomeLabel);
        panel.add(header, BorderLayout.NORTH);

        // Used GridBagLayout for cleaner centering instead of simple GridLayout
        JPanel grid = new JPanel(new GridLayout(4, 3, 20, 20)); 
        grid.setBorder(BorderFactory.createEmptyBorder(40, 80, 40, 80));

        JButton btnAttend = new JButton("1. Attendance");
        JButton btnStock = new JButton("2. Stock Management"); // Updated Label
        JButton btnSales = new JButton("3. Sales System");
        JButton btnSearch = new JButton("4. Search Info");
        JButton btnEdit = new JButton("5. Edit Info");
        JButton btnAnalytics = new JButton("6. Data Analytics");
        JButton btnHistory = new JButton("7. History");
        btnRegister = new JButton("8. Register (Manager Only)"); // Updated Label
        btnPerformance = new JButton("9. Performance"); // Updated Label
        JButton btnLogout = new JButton("0. Logout");
        btnLogout.setBackground(new Color(255, 200, 200)); // Light red for logout

        grid.add(btnAttend); 
        grid.add(btnStock); 
        grid.add(btnSales);
        
        grid.add(btnSearch); 
        grid.add(btnEdit); 
        grid.add(btnAnalytics);
        
        grid.add(btnHistory); 
        grid.add(btnRegister); 
        grid.add(btnPerformance);
        
        // Center the logout button in the last row (hacky way for GridLayout)
        grid.add(new JLabel("")); // Spacer
        grid.add(btnLogout);
        grid.add(new JLabel("")); // Spacer

        panel.add(grid, BorderLayout.CENTER);

        // Actions
        btnAttend.addActionListener(e -> cardLayout.show(mainPanel, ATTENDANCE));
        btnStock.addActionListener(e -> cardLayout.show(mainPanel, STOCK));
        btnSales.addActionListener(e -> cardLayout.show(mainPanel, SALES));
        btnSearch.addActionListener(e -> cardLayout.show(mainPanel, SEARCH));
        btnEdit.addActionListener(e -> cardLayout.show(mainPanel, EDIT));
        btnAnalytics.addActionListener(e -> { loadAnalytics(); cardLayout.show(mainPanel, ANALYTICS); });
        btnHistory.addActionListener(e -> { loadHistory(); cardLayout.show(mainPanel, HISTORY); });
        btnRegister.addActionListener(e -> cardLayout.show(mainPanel, REGISTER));
        btnPerformance.addActionListener(e -> { loadPerformance(); cardLayout.show(mainPanel, PERFORMANCE); });
        btnLogout.addActionListener(e -> { currentUser = null; cardLayout.show(mainPanel, LOGIN); });

        return panel;
    }

    private void refreshDashboard() {
        if(currentUser != null) {
            welcomeLabel.setText("Welcome, " + currentUser.getName() + " (" + currentUser.getRole() + ")");
            boolean isMgr = currentUser.getRole().equalsIgnoreCase("Manager");
            btnRegister.setEnabled(isMgr);
            btnPerformance.setEnabled(isMgr);
        }
    }

    // =======================================================
    // 3. ATTENDANCE
    // =======================================================
    private JPanel createAttendancePanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10,10,10,10);

        JLabel lbl = new JLabel("Attendance Log");
        lbl.setFont(new Font("Arial", Font.BOLD, 18));
        JButton btnIn = new JButton("CLOCK IN");
        JButton btnOut = new JButton("CLOCK OUT");
        JButton btnBack = new JButton("Back");

        btnIn.setPreferredSize(new Dimension(150, 50));
        btnOut.setPreferredSize(new Dimension(150, 50));

        gbc.gridx=0; gbc.gridy=0; panel.add(lbl, gbc);
        gbc.gridy=1; panel.add(btnIn, gbc);
        gbc.gridy=2; panel.add(btnOut, gbc);
        gbc.gridy=3; panel.add(btnBack, gbc);

        btnIn.addActionListener(e -> {
            String time = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
            String rec = currentUser.getID() + ",IN," + time.replace(" ", ",");
            StorageSystem.logAttendance(rec);
            JOptionPane.showMessageDialog(this, "Clocked IN at " + time);
        });

        btnOut.addActionListener(e -> {
            String time = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
            String rec = currentUser.getID() + ",OUT," + time.replace(" ", ",");
            StorageSystem.logAttendance(rec);
            JOptionPane.showMessageDialog(this, "Clocked OUT at " + time);
        });

        btnBack.addActionListener(e -> cardLayout.show(mainPanel, DASHBOARD));
        return panel;
    }

    // =======================================================
    // 4. STOCK (View, Count, Move) - UPDATED TABLE
    // =======================================================
    private JPanel createStockPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        JTabbedPane tabs = new JTabbedPane();

        // -- View Tab (Updated to show ALL Outlets) --
        JPanel viewP = new JPanel(new BorderLayout());
        // Added columns for C60 to C69
        String[] cols = {"Model", "Price", "C60", "C61", "C62", "C63", "C64", "C65", "C66", "C67", "C68", "C69"};
        DefaultTableModel model = new DefaultTableModel(cols, 0);
        JTable table = new JTable(model);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF); // Enable scroll bars for wide table
        table.getColumnModel().getColumn(0).setPreferredWidth(100); // Model name wider
        
        JButton btnRefresh = new JButton("Refresh");
        viewP.add(new JScrollPane(table), BorderLayout.CENTER);
        viewP.add(btnRefresh, BorderLayout.SOUTH);

        btnRefresh.addActionListener(e -> {
            model.setRowCount(0);
            for(Model m : StorageSystem.allModels) {
                // Get stock array
                int[] s = m.getStocks(); 
                // Add row with all data
                model.addRow(new Object[]{
                    m.getName(), m.getPrice(), 
                    s[0], s[1], s[2], s[3], s[4], s[5], s[6], s[7], s[8], s[9]
                });
            }
        });

        // -- Count Tab --
        JPanel countP = new JPanel(new GridLayout(4, 2, 20, 20));
        countP.setBorder(BorderFactory.createEmptyBorder(50,50,50,50));
        JTextField cModel = new JTextField();
        JTextField cQty = new JTextField();
        JButton btnVerify = new JButton("Verify");
        JLabel cRes = new JLabel("Result: -");

        countP.add(new JLabel("Model Name:")); countP.add(cModel);
        countP.add(new JLabel("Physical Count:")); countP.add(cQty);
        countP.add(new JLabel("")); countP.add(btnVerify);
        countP.add(cRes);

        btnVerify.addActionListener(e -> {
            String name = cModel.getText().trim();
            try {
                int qty = Integer.parseInt(cQty.getText().trim());
                boolean found = false;
                for(Model m : StorageSystem.allModels) {
                    if(m.getName().equalsIgnoreCase(name)) {
                        int sys = m.getStock(0);
                        if(sys == qty) cRes.setText("MATCH! (System: " + sys + ")");
                        else cRes.setText("MISMATCH! (System: " + sys + ")");
                        found = true; break;
                    }
                }
                if(!found) cRes.setText("Model Not Found");
            } catch(Exception ex) { cRes.setText("Invalid Number"); }
        });

        // -- Move Tab (In/Out) --
        JPanel moveP = new JPanel(new GridLayout(6, 2, 10, 10));
        JComboBox<String> typeBox = new JComboBox<>(new String[]{"Stock In", "Stock Out"});
        JTextField mName = new JTextField();
        JTextField mQty = new JTextField();
        JTextField fromF = new JTextField();
        JTextField toF = new JTextField();
        JButton btnMove = new JButton("Process");

        moveP.add(new JLabel("Type:")); moveP.add(typeBox);
        moveP.add(new JLabel("Model:")); moveP.add(mName);
        moveP.add(new JLabel("Quantity:")); moveP.add(mQty);
        moveP.add(new JLabel("From (Code):")); moveP.add(fromF);
        moveP.add(new JLabel("To (Code):")); moveP.add(toF);
        moveP.add(new JLabel("")); moveP.add(btnMove);

        btnMove.addActionListener(e -> {
            try {
                String mod = mName.getText();
                int q = Integer.parseInt(mQty.getText());
                String type = (String)typeBox.getSelectedItem();
                
                boolean found = false;
                for(Model m : StorageSystem.allModels) {
                    if(m.getName().equalsIgnoreCase(mod)) {
                        int curr = m.getStock(0);
                        int newQ = type.equals("Stock In") ? curr + q : curr - q;
                        if(newQ < 0) {
                            JOptionPane.showMessageDialog(this, "Error: Not enough stock!");
                            return;
                        }
                        m.setStock(0, newQ);
                        StorageSystem.saveAllModels(); 
                        
                        logReceipt(type, fromF.getText(), toF.getText(), mod, q);
                        JOptionPane.showMessageDialog(this, "Success! Receipt Saved.");
                        found = true; break;
                    }
                }
                if(!found) JOptionPane.showMessageDialog(this, "Model not found");
            } catch(Exception ex) { JOptionPane.showMessageDialog(this, "Invalid Input"); }
        });

        tabs.addTab("Inventory (All Outlets)", viewP);
        tabs.addTab("Stock Count", countP);
        tabs.addTab("Movement", moveP);

        panel.add(tabs, BorderLayout.CENTER);
        JButton btnBack = new JButton("Back");
        btnBack.addActionListener(e -> cardLayout.show(mainPanel, DASHBOARD));
        panel.add(btnBack, BorderLayout.SOUTH);
        return panel;
    }

    private void logReceipt(String type, String from, String to, String mod, int qty) {
        try (FileWriter fw = new FileWriter("receipts_log.txt", true);
             PrintWriter pw = new PrintWriter(fw)) {
            pw.println("=== " + type + " ===");
            pw.println("Date: " + LocalDate.now());
            pw.println("From: " + from + " | To: " + to);
            pw.println("Model: " + mod + " | Qty: " + qty);
            pw.println("Staff: " + currentUser.getName());
            pw.println("-------------------------");
        } catch(IOException e) { e.printStackTrace(); }
    }

    // =======================================================
    // 5. SALES
    // =======================================================
    private JPanel createSalesPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5,5,5,5);

        JTextField cName = new JTextField(15);
        JTextField mName = new JTextField(15);
        JTextField qtyF = new JTextField(5);
        String[] methods = {"Cash", "Card", "E-Wallet"};
        JComboBox<String> mBox = new JComboBox<>(methods);
        JButton btnProc = new JButton("Process Sale");
        JButton btnBack = new JButton("Back");

        gbc.gridx=0; gbc.gridy=0; panel.add(new JLabel("Customer:"), gbc);
        gbc.gridx=1; panel.add(cName, gbc);
        gbc.gridx=0; gbc.gridy=1; panel.add(new JLabel("Model:"), gbc);
        gbc.gridx=1; panel.add(mName, gbc);
        gbc.gridx=0; gbc.gridy=2; panel.add(new JLabel("Qty:"), gbc);
        gbc.gridx=1; panel.add(qtyF, gbc);
        gbc.gridx=0; gbc.gridy=3; panel.add(new JLabel("Method:"), gbc);
        gbc.gridx=1; panel.add(mBox, gbc);
        gbc.gridx=1; gbc.gridy=4; panel.add(btnProc, gbc);
        gbc.gridx=1; gbc.gridy=5; panel.add(btnBack, gbc);

        btnBack.addActionListener(e -> cardLayout.show(mainPanel, DASHBOARD));
        btnProc.addActionListener(e -> {
            try {
                String mod = mName.getText();
                int qty = Integer.parseInt(qtyF.getText());
                
                boolean exists = false;
                for(Model m : StorageSystem.allModels) if(m.getName().equalsIgnoreCase(mod)) exists = true;
                if(!exists) { JOptionPane.showMessageDialog(this, "Model not found"); return; }

                Customer c = new Customer(cName.getText(), (String)mBox.getSelectedItem(), currentUser.getName());
                Sale s = new Sale(mod, qty);
                c.addPurchase(s);

                salesSystem.addCustomer(c);
                logToHistory(c);
                
                JOptionPane.showMessageDialog(this, "Sale Recorded! Total: RM" + c.getTotalPrice());
            } catch(Exception ex) { JOptionPane.showMessageDialog(this, "Invalid Input"); }
        });
        return panel;
    }

    private void logToHistory(Customer c) {
        String date = LocalDate.now().toString();
        String time = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm"));
        for (Sale s : c.getPurchaseList()) {
            String row = String.format("%s,%s,%s,%s,%s,%d,%.2f,%s",
                date, time, currentUser.getID(), c.getCustomerName(),
                s.getModelName(), s.getQuantity(), s.getTotalPrice(), c.getPaymentMethod());
            StorageSystem.logSale(row);
        }
    }

    // =======================================================
    // 6. SEARCH
    // =======================================================
    private JPanel createSearchPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        JPanel top = new JPanel();
        JTextField keyF = new JTextField(15);
        JButton btnStock = new JButton("Search Stock");
        JButton btnSales = new JButton("Search Sales");
        JTextArea resArea = new JTextArea();
        resArea.setEditable(false);

        top.add(new JLabel("Keyword:")); top.add(keyF);
        top.add(btnStock); top.add(btnSales);

        panel.add(top, BorderLayout.NORTH);
        panel.add(new JScrollPane(resArea), BorderLayout.CENTER);
        JButton btnBack = new JButton("Back");
        btnBack.addActionListener(e -> cardLayout.show(mainPanel, DASHBOARD));
        panel.add(btnBack, BorderLayout.SOUTH);

        btnStock.addActionListener(e -> {
            String k = keyF.getText().toLowerCase();
            StringBuilder sb = new StringBuilder();
            for(Model m : StorageSystem.allModels) {
                if(m.getName().toLowerCase().contains(k)) {
                    sb.append("Model: ").append(m.getName())
                      .append(" | Price: RM").append(m.getPrice())
                      .append(" | C60 Stock: ").append(m.getStock(0)).append("\n");
                }
            }
            resArea.setText(sb.length() > 0 ? sb.toString() : "No stock found.");
        });

        btnSales.addActionListener(e -> {
            String k = keyF.getText().toLowerCase();
            resArea.setText("Scanning Sales History...\n");
            File f = new File("sales_history.csv");
            if(f.exists()) {
                try(BufferedReader br = new BufferedReader(new FileReader(f))) {
                    String line;
                    while((line = br.readLine()) != null) {
                        if(line.toLowerCase().contains(k)) resArea.append(line + "\n");
                    }
                } catch(Exception ex) {}
            }
        });
        return panel;
    }

    // =======================================================
    // 7. EDIT - UPDATED LABEL AND OUTLET SELECTOR
    // =======================================================
    private JPanel createEditPanel() {
        JPanel panel = new JPanel(new GridLayout(5, 2, 20, 20));
        panel.setBorder(BorderFactory.createEmptyBorder(50,50,50,50));
        
        JTextField mName = new JTextField();
        JTextField newQ = new JTextField();
        // Added Outlet Selector to update "The Rest" of the stocks
        String[] outlets = {"C60", "C61", "C62", "C63", "C64", "C65", "C66", "C67", "C68", "C69"};
        JComboBox<String> outletBox = new JComboBox<>(outlets);
        JButton btnUpd = new JButton("Update");
        JButton btnBack = new JButton("Back");

        panel.add(new JLabel("Model Name:")); panel.add(mName);
        panel.add(new JLabel("Select Outlet:")); panel.add(outletBox);
        panel.add(new JLabel("New Quantity:")); panel.add(newQ); // Updated Label
        panel.add(new JLabel("")); panel.add(btnUpd);
        panel.add(btnBack);

        btnUpd.addActionListener(e -> {
            try {
                int q = Integer.parseInt(newQ.getText());
                String mod = mName.getText();
                int outletIndex = outletBox.getSelectedIndex(); // 0 to 9

                boolean found = false;
                for(Model m : StorageSystem.allModels) {
                    if(m.getName().equalsIgnoreCase(mod)) {
                        m.setStock(outletIndex, q); // Updates specific outlet
                        StorageSystem.saveAllModels();
                        JOptionPane.showMessageDialog(this, "Stock Updated for " + outlets[outletIndex]);
                        found = true; break;
                    }
                }
                if(!found) JOptionPane.showMessageDialog(this, "Model Not Found");
            } catch(Exception ex) { JOptionPane.showMessageDialog(this, "Invalid Input"); }
        });

        btnBack.addActionListener(e -> cardLayout.show(mainPanel, DASHBOARD));
        return panel;
    }

    // =======================================================
    // 8. DATA ANALYTICS
    // =======================================================
    private JPanel createAnalyticsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        analyticsArea = new JTextArea();
        analyticsArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        panel.add(new JScrollPane(analyticsArea), BorderLayout.CENTER);
        JButton btnBack = new JButton("Back");
        btnBack.addActionListener(e -> cardLayout.show(mainPanel, DASHBOARD));
        panel.add(btnBack, BorderLayout.SOUTH);
        return panel;
    }

    private void loadAnalytics() {
        double totalRev = 0;
        int totalTx = 0;
        HashMap<String, Integer> popular = new HashMap<>();

        File f = new File("sales_history.csv");
        if(f.exists()) {
            try(BufferedReader br = new BufferedReader(new FileReader(f))) {
                String line = br.readLine(); 
                while((line = br.readLine()) != null) {
                    String[] d = line.split(",");
                    if(d.length < 7) continue;
                    double amt = Double.parseDouble(d[6]); 
                    String mod = d[4];
                    totalRev += amt;
                    totalTx++;
                    popular.put(mod, popular.getOrDefault(mod, 0) + 1);
                }
            } catch(Exception e) {}
        }

        StringBuilder sb = new StringBuilder();
        sb.append("=== SALES ANALYTICS ===\n\n");
        sb.append("Total Revenue: RM").append(String.format("%.2f", totalRev)).append("\n");
        sb.append("Total Transactions: ").append(totalTx).append("\n\n");
        sb.append("Most Popular Models:\n");
        popular.forEach((k,v) -> sb.append("- ").append(k).append(": ").append(v).append(" sold\n"));
        
        analyticsArea.setText(sb.toString());
    }

    // =======================================================
    // 9. HISTORY
    // =======================================================
    private JPanel createHistoryPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        JPanel top = new JPanel();
        JButton btnSort = new JButton("Sort by Amount");
        top.add(btnSort);

        String[] cols = {"Date", "Time", "Customer", "Model", "Total"};
        historyModel = new DefaultTableModel(cols, 0);
        JTable table = new JTable(historyModel);
        
        panel.add(top, BorderLayout.NORTH);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        JButton btnBack = new JButton("Back");
        btnBack.addActionListener(e -> cardLayout.show(mainPanel, DASHBOARD));
        panel.add(btnBack, BorderLayout.SOUTH);
        
        btnSort.addActionListener(e -> {
            ArrayList<String[]> rows = new ArrayList<>();
            for(int i=0; i<historyModel.getRowCount(); i++) {
                String[] r = new String[5];
                for(int j=0; j<5; j++) r[j] = (String)historyModel.getValueAt(i, j);
                rows.add(r);
            }
            rows.sort((a,b) -> Double.compare(Double.parseDouble(b[4]), Double.parseDouble(a[4])));
            historyModel.setRowCount(0);
            for(String[] r : rows) historyModel.addRow(r);
        });

        return panel;
    }

    private void loadHistory() {
        historyModel.setRowCount(0);
        File f = new File("sales_history.csv");
        if(f.exists()) {
            try(BufferedReader br = new BufferedReader(new FileReader(f))) {
                String line = br.readLine();
                while((line = br.readLine()) != null) {
                    String[] d = line.split(",");
                    if(d.length > 6) {
                        historyModel.addRow(new Object[]{d[0], d[1], d[3], d[4], d[6]});
                    }
                }
            } catch(Exception e) {}
        }
    }

    // =======================================================
    // 10. REGISTER
    // =======================================================
    private JPanel createRegisterPanel() {
        JPanel panel = new JPanel(new GridLayout(5, 2, 20, 20));
        panel.setBorder(BorderFactory.createEmptyBorder(50,50,50,50));
        JTextField idF = new JTextField();
        JTextField nameF = new JTextField();
        JTextField passF = new JTextField();
        JComboBox<String> roleB = new JComboBox<>(new String[]{"Part-time", "Full-time", "Manager"});
        JButton btnReg = new JButton("Register");

        panel.add(new JLabel("New ID:")); panel.add(idF);
        panel.add(new JLabel("Name:")); panel.add(nameF);
        panel.add(new JLabel("Password:")); panel.add(passF);
        panel.add(new JLabel("Role:")); panel.add(roleB);
        panel.add(new JLabel("")); panel.add(btnReg);

        JButton btnBack = new JButton("Back");
        panel.add(btnBack);

        btnReg.addActionListener(e -> {
            Employee newEmp = new Employee(idF.getText(), nameF.getText(), (String)roleB.getSelectedItem(), passF.getText());
            StorageSystem.allEmployees.add(newEmp);
            JOptionPane.showMessageDialog(this, "Employee Registered!");
        });
        btnBack.addActionListener(e -> cardLayout.show(mainPanel, DASHBOARD));
        return panel;
    }

    // =======================================================
    // 11. PERFORMANCE
    // =======================================================
    private JPanel createPerformancePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        String[] cols = {"Employee", "Total Sales"};
        performanceModel = new DefaultTableModel(cols, 0);
        JTable table = new JTable(performanceModel);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        JButton btnBack = new JButton("Back");
        btnBack.addActionListener(e -> cardLayout.show(mainPanel, DASHBOARD));
        panel.add(btnBack, BorderLayout.SOUTH);
        return panel;
    }

    private void loadPerformance() {
        performanceModel.setRowCount(0);
        HashMap<String, Double> map = new HashMap<>();
        File f = new File("sales_history.csv");
        if(f.exists()) {
            try(BufferedReader br = new BufferedReader(new FileReader(f))) {
                br.readLine();
                String line;
                while((line = br.readLine()) != null) {
                    String[] d = line.split(",");
                    if(d.length > 6) {
                        String empID = d[2]; 
                        double amt = Double.parseDouble(d[6]);
                        map.put(empID, map.getOrDefault(empID, 0.0) + amt);
                    }
                }
            } catch(Exception e) {}
        }
        map.forEach((k,v) -> performanceModel.addRow(new Object[]{k, String.format("%.2f", v)}));
    }
}