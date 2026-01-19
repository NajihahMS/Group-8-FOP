import DataClass.Employee;
import DataClass.Model;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Scanner;

public class MAIN {
 
    // Global System State
    private static Scanner scanner = new Scanner(System.in);
    private static Employee currentUser = null;
    private static boolean isRunning = true;

    // Sub-Systems
    private static SalesSystem salesSystem;
    private static StockManagement stockSystem;

    public static void main(String[] args) {
        // 1. DATA LOAD STATE
        // Initialize storage and load CSVs into memory
        StorageSystem.initialize();

        // Initialize Logic Classes
        // Note: Casting List to ArrayList because SalesSystem constructor asks for ArrayList
        salesSystem = new SalesSystem((ArrayList<Model>) StorageSystem.allModels);
        stockSystem = new StockManagement(StorageSystem.allModels);

        System.out.println("\nWelcome to GoldenHour Store Operations Management System");

        // 2. MAIN PROGRAM LOOP
        while (isRunning) {
            if (currentUser == null) {
                // If no one is logged in, show Login Screen
                currentUser = LoginSystem.login(scanner);
                
                // If user typed 'EXIT' in login, currentUser remains null and we break
                if (currentUser == null) {
                    System.out.println("System Shutting Down.");
                    break;
                }
            } else {
                // User is logged in, show Main Menu
                showMainMenu();
            }
        }
        
        scanner.close();
    }

    // ==========================================
    // MAIN MENU & ROUTING
    // ==========================================
    private static void showMainMenu() {
        System.out.println("\n--- Main Menu ---");
        System.out.println("Logged in as: " + currentUser.getName() + " (" + currentUser.getRole() + ")");
        System.out.println("1. Attendance (Clock In/Out)");
        System.out.println("2. Stock Management");
        System.out.println("3. Sales System");
        System.out.println("4. Search Information");
        System.out.println("5. Edit Information");
        System.out.println("6. Data Analytics");
        System.out.println("7. Filter & Sort Sales History");
        
        // Manager Restricted Features
        if (currentUser.getRole().equalsIgnoreCase("Manager")) {
            System.out.println("8. Register New Employee (Manager)");
            System.out.println("9. Employee Performance Metrics (Manager)");
        }
        
        System.out.println("0. Logout");
        System.out.print("Select option: ");

        String choice = scanner.nextLine().trim();

        switch (choice) {
            case "1":
                handleAttendance();
                break;
            case "2":
                handleStock();
                break;
            case "3":
                handleSales();
                break;
            case "4":
                // Polymorphism: Run SearchInfo
                new SearchInfo(currentUser.getName(), 0).run();
                break;
            case "5":
                // Polymorphism: Run EditInfo
                new EditInfo(currentUser.getName(), 0).run();
                break;
            case "6":
                // Pass the customer list to DataAnalytic
                new DataAnalytic(salesSystem.getCustomers()).displaySummary();
                break;
            case "7":
                // Polymorphism: Run FilterSortSalesHistory
                new FilterSortSalesHistory(currentUser.getName(), 0).run();
                break;
            case "8":
                // Manager check is also done inside LoginSystem, but we route it here
                LoginSystem.registerNewEmployee(scanner, currentUser);
                break;
            case "9":
                if (currentUser.getRole().equalsIgnoreCase("Manager")) {
                    new EmployeePerformanceReport(salesSystem).generateReport();
                } else {
                    System.out.println("Access Denied.");
                }
                break;
            case "0":
                logout();
                break;
            default:
                System.out.println("Invalid option. Please try again.");
        }
    }

    // ==========================================
    // FEATURE HANDLERS
    // ==========================================

    private static void handleAttendance() {
        System.out.println("\n--- Attendance ---");
        System.out.println("1. Clock In");
        System.out.println("2. Clock Out");
        System.out.print("Choose: ");
        String attChoice = scanner.nextLine();

        if (attChoice.equals("1")) {
            AttendanceSystem.clockIn(currentUser);
        } else if (attChoice.equals("2")) {
            AttendanceSystem.clockOut(currentUser);
        } else {
            System.out.println("Invalid choice.");
        }
    }

    private static void handleStock() {
        System.out.println("\n--- Stock Management ---");
        System.out.println("1. Perform Stock Count (Morning/Night)");
        System.out.println("2. Stock Movement (In/Out)");
        System.out.print("Choose: ");
        String stockChoice = scanner.nextLine();

        if (stockChoice.equals("1")) {
            System.out.print("Enter Session Name (e.g., Morning): ");
            String session = scanner.nextLine();
            stockSystem.performStockCount(session);
        } else if (stockChoice.equals("2")) {
            System.out.println("1. Stock In");
            System.out.println("2. Stock Out");
            System.out.print("Type: ");
            String typeChoice = scanner.nextLine();
            String type = typeChoice.equals("1") ? "Stock In" : "Stock Out";
            stockSystem.handleMovement(type, currentUser.getName());
        } else {
            System.out.println("Invalid choice.");
        }
    }

    private static void handleSales() {
        System.out.println("\n--- Record New Sale ---");
        
        // 1. Gather Customer Details
        System.out.print("Customer Name: ");
        String custName = scanner.nextLine();
        
        // 2. Add Items
        Customer customer = new Customer(custName, "Unknown", currentUser.getName());
        boolean buying = true;
        
        while (buying) {
            System.out.print("Enter Model Name: ");
            String modelName = scanner.nextLine();
            
            // Validate model exists
            boolean exists = false;
            for(Model m : StorageSystem.allModels) {
                if(m.getName().equalsIgnoreCase(modelName)) exists = true;
            }
            
            if(!exists) {
                System.out.println("Error: Model not found in system.");
                continue;
            }

            System.out.print("Enter Quantity: ");
            int qty = 0;
            try {
                qty = Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Invalid quantity.");
                continue;
            }
            
            Sale newItem = new Sale(modelName, qty);
            customer.addPurchase(newItem);
            
            System.out.print("Add another item? (Y/N): ");
            if (scanner.nextLine().equalsIgnoreCase("N")) buying = false;
        }

        // 3. Payment Method
        System.out.print("Enter transaction method (Cash/Card/E-wallet): ");
        String method = scanner.nextLine();
        // We have to recreate the customer object or set method because 
        // your Customer.java constructor forces method at the start.
        // For simplicity, we create a final customer object here with the correct method
        Customer finalCustomer = new Customer(custName, method, currentUser.getName());
        for(Sale s : customer.getPurchaseList()) {
            finalCustomer.addPurchase(s);
        }

        // 4. Process Sale
        System.out.println("Subtotal: RM" + finalCustomer.getTotalPrice());
        salesSystem.addCustomer(finalCustomer); // This updates stock and prints receipt
        
        // 5. CRITICAL BRIDGE: Write to sales_history.csv for FilterSortSalesHistory feature
        // Your SalesSystem writes to "transactions_date.csv", but FilterSort reads "sales_history.csv"
        // We log it here manually to ensure Feature 7 works.
        logToGlobalHistory(finalCustomer);
    }
    
    // Helper to ensure Filter/Sort/Search works (Feature Integration)
    private static void logToGlobalHistory(Customer c) {
        LocalDateTime now = LocalDateTime.now();
        String date = now.toLocalDate().toString();
        String time = now.toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm"));
        
        for (Sale s : c.getPurchaseList()) {
            // CSV Format: Date,Time,EmpID,Customer,Model,Qty,Total,Method
            String record = String.format("%s,%s,%s,%s,%s,%d,%.2f,%s",
                date,
                time,
                currentUser.getID(),
                c.getCustomerName(),
                s.getModelName(),
                s.getQuantity(),
                s.getTotalPrice(),
                c.getPaymentMethod()
            );
            StorageSystem.logSale(record);
        }
        System.out.println("Transaction logged to sales_history.csv");
    }

    private static void logout() {
        System.out.println("Logging out...");
        currentUser = null;
        // The loop in main() will now see currentUser is null and trigger login again
    }
}