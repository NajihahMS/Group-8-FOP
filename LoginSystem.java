

import java.util.Scanner;

public class AuthSystem {

    // ==========================================
    // 1. LOGIN SYSTEM
    // ==========================================
    public static Employee login(Scanner scanner) {
        System.out.println("\n=== Employee Login ===");
        
        while (true) {
            System.out.print("Enter User ID: ");
            String inputId = scanner.nextLine().trim();

            System.out.print("Enter Password: ");
            String inputPass = scanner.nextLine().trim();

            // Search in the loaded data from storageManagement
            for (Employee emp : storageManagement.allEmployees) {
                if (emp.getId().equalsIgnoreCase(inputId) && emp.getPassword().equals(inputPass)) {
                    System.out.println("\nLogin Successful!");
                    System.out.println("Welcome, " + emp.getName() + " (" + emp.getRole() + ")");
                    return emp; // Return the logged-in object
                }
            }

            [span_5](start_span)//[span_5](end_span) Display unsuccessful attempt message
            System.out.println("Login Failed: Invalid User ID or Password. Try again.");
            System.out.print("Press Enter to retry or type 'EXIT' to quit: ");
            if (scanner.nextLine().equalsIgnoreCase("EXIT")) return null;
        }
    }

    // ==========================================
    // 2. REGISTRATION SYSTEM (Manager Only)
    // ==========================================
    public static void registerNewEmployee(Scanner scanner, Employee currentUser) {
        [span_6](start_span)//[span_6](end_span) Only manager is authorized
        if (!currentUser.getRole().equalsIgnoreCase("Manager")) {
            System.out.println("\n[!] Access Denied: Only Managers can register new employees.");
            return;
        }

        System.out.println("\n=== Register New Employee ===");

        System.out.print("Enter Employee Name: ");
        String name = scanner.nextLine();

        String id;
        while (true) {
            System.out.print("Enter New Employee ID: ");
            id = scanner.nextLine().trim();

            // Check for duplicate ID
            boolean exists = false;
            for (Employee emp : storageManagement.allEmployees) {
                if (emp.getId().equalsIgnoreCase(id)) {
                    exists = true;
                    break;
                }
            }

            if (exists) {
                System.out.println("Error: Employee ID '" + id + "' already exists. Please choose another.");
            } else {
                break; // ID is valid
            }
        }

        System.out.print("Set Password: ");
        String password = scanner.nextLine();

        System.out.print("Set Role (Manager/Full-time/Part-time): ");
        String role = scanner.nextLine();

        // Create new object and add to global list
        // Note: Ensure your Employee constructor matches this order
        Employee newEmp = new Employee(name, id, password, role); 
        storageManagement.allEmployees.add(newEmp);

        // Reminder: You still need to implement saveEmployees() in storageManagement to persist this!
        System.out.println("Employee successfully registered!");
    }
}