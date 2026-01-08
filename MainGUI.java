import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import DataClass.Employee;
import DataClass.Model;

public class MainGUI extends JFrame {

    // Global State
    private Employee currentUser;
    private SalesSystem salesSystem;
    
    // GUI Components
    private CardLayout cardLayout;
    private JPanel mainPanel;
    
    // Screens
    private JPanel loginPanel;
    private JPanel dashboardPanel;
    private JPanel stockPanel;
    private JPanel salesPanel;
    private JPanel attendancePanel;

    public static void main(String[] args) {
        // Run GUI in Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            try {
                new GoldenHourGUI().setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public GoldenHourGUI() {
        // 1. Initialize Backend Data
        StorageSystem.initialize(); // [cite: 209]
        // Initialize SalesSystem with the loaded models
        salesSystem = new SalesSystem((ArrayList<Model>) StorageSystem.allModels);

        // 2. Setup Main Frame
        setTitle("GoldenHour Store Operations System");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Center window

        // 3. Setup Layout (CardLayout to switch screens)
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        // 4. Create Screens
        initLoginPanel();
        initDashboardPanel();
        initStockPanel();
        initSalesPanel();
        initAttendancePanel();

        // 5. Add Screens to Main Panel
        mainPanel.add(loginPanel, "LOGIN");
        mainPanel.add(dashboardPanel, "DASHBOARD");
        mainPanel.add(stockPanel, "STOCK");
        mainPanel.add(salesPanel, "SALES");
        mainPanel.add(attendancePanel, "ATTENDANCE");

        add(mainPanel);
        cardLayout.show(mainPanel, "LOGIN"); // Show login first [cite: 27]
    }

    // ==========================================
    // SCREEN 1: LOGIN
    // ==========================================
    private void initLoginPanel() {
        loginPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        JLabel title = new JLabel("GoldenHour Login");
        title.setFont(new Font("Arial", Font.BOLD, 24));
        
        JLabel userLabel = new JLabel("User ID:");
        JTextField userField = new JTextField(15);
        
        JLabel passLabel = new JLabel("Password:");
        JPasswordField passField = new JPasswordField(15);
        
        JButton loginBtn = new JButton("Login");

        // UI Layout
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        loginPanel.add(title, gbc);
        
        gbc.gridwidth = 1; gbc.gridy = 1;
        loginPanel.add(userLabel, gbc);
        gbc.gridx = 1;
        loginPanel.add(userField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 2;
        loginPanel.add(passLabel, gbc);
        gbc.gridx = 1;
        loginPanel.add(passField, gbc);
        
        gbc.gridx = 1; gbc.gridy = 3;
        loginPanel.add(loginBtn, gbc);

        // Logic
        loginBtn.addActionListener(e -> {
            String id = userField.getText().trim();
            String pass = new String(passField.getPassword()).trim();
            
            // Check credentials against loaded data
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
                refreshDashboard(); // Update names
                cardLayout.show(mainPanel, "DASHBOARD");
            } else {
                JOptionPane.showMessageDialog(this, "Invalid ID or Password", "Login Failed", JOptionPane.ERROR_MESSAGE); // [cite: 28]
            }
        });
    }

    // ==========================================
    // SCREEN 2: DASHBOARD
    // ==========================================
    private JLabel welcomeLabel;

    private void initDashboardPanel() {
        dashboardPanel = new JPanel(new BorderLayout());
        
        // Header
        JPanel header = new JPanel();
        welcomeLabel = new JLabel("Welcome!");
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 18));
        header.add(welcomeLabel);
        
        // Buttons
        JPanel buttonPanel = new JPanel(new GridLayout(2, 2, 20, 20));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(50, 50, 50, 50));
        
        JButton btnStock = new JButton("View Stock");
        JButton btnSales = new JButton("Record Sales");
        JButton btnAttend = new JButton("Attendance");
        JButton btnLogout = new JButton("Logout");

        // Styling
        Font btnFont = new Font("Arial", Font.PLAIN, 16);
        btnStock.setFont(btnFont);
        btnSales.setFont(btnFont);
        btnAttend.setFont(btnFont);
        btnLogout.setFont(btnFont);

        buttonPanel.add(btnStock);
        buttonPanel.add(btnSales);
        buttonPanel.add(btnAttend);
        buttonPanel.add(btnLogout);

        dashboardPanel.add(header, BorderLayout.NORTH);
        dashboardPanel.add(buttonPanel, BorderLayout.CENTER);

        // Actions
        btnStock.addActionListener(e -> {
            refreshStockTable();
            cardLayout.show(mainPanel, "STOCK");
        });
        
        btnSales.addActionListener(e -> cardLayout.show(mainPanel, "SALES"));
        
        btnAttend.addActionListener(e -> cardLayout.show(mainPanel, "ATTENDANCE"));

        btnLogout.addActionListener(e -> {
            currentUser = null;
            cardLayout.show(mainPanel, "LOGIN"); // [cite: 29]
        });
    }

    private void refreshDashboard() {
        if(currentUser != null) {
            welcomeLabel.setText("Welcome, " + currentUser.getName() + " (" + currentUser.getRole() + ")");
        }
    }

    // ==========================================
    // SCREEN 3: STOCK VIEW (Read-Only)
    // ==========================================
    private JTable stockTable;
    private DefaultTableModel stockModel;

    private void initStockPanel() {
        stockPanel = new JPanel(new BorderLayout());
        
        JButton btnBack = new JButton("Back to Dashboard");
        btnBack.addActionListener(e -> cardLayout.show(mainPanel, "DASHBOARD"));

        // Table Setup
        String[] columns = {"Model", "Price (RM)", "KLCC", "MidValley", "Lalaport", "KL East", "Nu Sentral", "Pavillion", "MyTown"};
        stockModel = new DefaultTableModel(columns, 0);
        stockTable = new JTable(stockModel);
        JScrollPane scrollPane = new JScrollPane(stockTable);

        stockPanel.add(new JLabel("   Current Inventory Levels"), BorderLayout.NORTH);
        stockPanel.add(scrollPane, BorderLayout.CENTER);
        stockPanel.add(btnBack, BorderLayout.SOUTH);
    }

    private void refreshStockTable() {
        stockModel.setRowCount(0); // Clear existing data
        for (Model m : StorageSystem.allModels) {
            // Add row to table
            Object[] row = {
                m.getName(),
                m.getPrice(),
                m.c60, m.c61, m.c62, m.c63, m.c64, m.c65, m.c66 // Displaying first 7 outlets for space
            };
            stockModel.addRow(row);
        }
    }

    // ==========================================
    // SCREEN 4: SALES RECORDING
    // ==========================================
    private void initSalesPanel() {
        salesPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel title = new JLabel("New Transaction");
        title.setFont(new Font("Arial", Font.BOLD, 18));
        
        JTextField custNameField = new JTextField(20);
        JTextField modelField = new JTextField(10); // Simple text input for now
        JTextField qtyField = new JTextField(5);
        String[] methods = {"Cash", "Credit Card", "E-Wallet"};
        JComboBox<String> methodBox = new JComboBox<>(methods);
        JButton btnProcess = new JButton("Process Sale");
        JButton btnBack = new JButton("Cancel");

        // Layout
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        salesPanel.add(title, gbc);
        
        gbc.gridwidth = 1; gbc.gridy = 1;
        salesPanel.add(new JLabel("Customer Name:"), gbc);
        gbc.gridx = 1;
        salesPanel.add(custNameField, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        salesPanel.add(new JLabel("Model Name:"), gbc);
        gbc.gridx = 1;
        salesPanel.add(modelField, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        salesPanel.add(new JLabel("Quantity:"), gbc);
        gbc.gridx = 1;
        salesPanel.add(qtyField, gbc);

        gbc.gridx = 0; gbc.gridy = 4;
        salesPanel.add(new JLabel("Payment Method:"), gbc);
        gbc.gridx = 1;
        salesPanel.add(methodBox, gbc);

        gbc.gridx = 1; gbc.gridy = 5;
        salesPanel.add(btnProcess, gbc);
        
        gbc.gridx = 1; gbc.gridy = 6;
        salesPanel.add(btnBack, gbc);

        // Logic
        btnBack.addActionListener(e -> cardLayout.show(mainPanel, "DASHBOARD"));

        btnProcess.addActionListener(e -> {
            try {
                String cName = custNameField.getText();
                String mName = modelField.getText();
                int qty = Integer.parseInt(qtyField.getText());
                String method = (String) methodBox.getSelectedItem();

                // Validate Model Exists
                boolean modelExists = false;
                for(Model m : StorageSystem.allModels) {
                    if(m.getName().equalsIgnoreCase(mName)) modelExists = true;
                }

                if(!modelExists) {
                    JOptionPane.showMessageDialog(this, "Model not found!");
                    return;
                }

                // Create Customer & Sale objects
                Customer c = new Customer(cName, method, currentUser.getName());
                Sale s = new Sale(mName, qty);
                c.addPurchase(s);

                // Process via Backend
                salesSystem.addCustomer(c); // Updates stock & saves to daily file

                // Success Message
                JOptionPane.showMessageDialog(this, "Sale Recorded!\nTotal: RM" + c.getTotalPrice());
                
                // Clear fields
                custNameField.setText("");
                modelField.setText("");
                qtyField.setText("");

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Invalid Quantity");
            }
        });
    }

    // ==========================================
    // SCREEN 5: ATTENDANCE
    // ==========================================
    private void initAttendancePanel() {
        attendancePanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        JLabel title = new JLabel("Attendance Log");
        title.setFont(new Font("Arial", Font.BOLD, 18));
        
        JButton btnIn = new JButton("CLOCK IN");
        JButton btnOut = new JButton("CLOCK OUT");
        JButton btnBack = new JButton("Back");
        
        // Make buttons big
        btnIn.setPreferredSize(new Dimension(150, 50));
        btnOut.setPreferredSize(new Dimension(150, 50));

        gbc.gridx = 0; gbc.gridy = 0;
        attendancePanel.add(title, gbc);
        gbc.gridy = 1;
        attendancePanel.add(btnIn, gbc);
        gbc.gridy = 2;
        attendancePanel.add(btnOut, gbc);
        gbc.gridy = 3;
        attendancePanel.add(btnBack, gbc);

        // Logic
        btnBack.addActionListener(e -> cardLayout.show(mainPanel, "DASHBOARD"));
        
        btnIn.addActionListener(e -> {
            AttendanceSystem.clockIn(currentUser);
            JOptionPane.showMessageDialog(this, "Clocked In Successfully!");
        });

        btnOut.addActionListener(e -> {
            AttendanceSystem.clockOut(currentUser);
            JOptionPane.showMessageDialog(this, "Clocked Out Successfully!\nCheck console for total hours.");
        });
    }
}