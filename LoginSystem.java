import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.IOException;
import java.util.Scanner;
import DataClass.Employee; 

public class LoginSystem {

    // =========================
    // LOGIN FUNCTION
    // =========================
    public static Employee login(Scanner sc) {
        System.out.println("=== Employee Login ===");
        
        System.out.print("Enter User ID: ");
        String id = sc.nextLine().trim();

        System.out.print("Enter Password: ");
        String pass = sc.nextLine().trim();
        System.out.println();

        // Check against the Central Storage System
        for (Employee e : StorageSystem.allEmployees) {
            if (e.getID().equalsIgnoreCase(id) && e.getPassword().equals(pass)) {
                
                System.out.println("Login Successful!"); 
                System.out.println("Welcome, " + e.getName() + " (" + e.getRole() + ")");
                System.out.println();
                
                return e; // Return the valid employee object
            }
        }

        System.out.println("Login Failed: Invalid User ID or Password."); 
        return null;
    }

    // =========================
    // REGISTER FUNCTION
    // =========================
    public static void registerNewEmployee(Scanner sc, Employee currentUser) {
        // 1. Security Check
        if (!currentUser.getRole().equalsIgnoreCase("Manager")) {
            System.out.println("Error: Only Managers can register new employees.");
            return;
        }

        System.out.println("\n=== Register New Employee ==="); 
        
        // 2. Input Data
        System.out.print("Enter Employee Name: ");
        String name = sc.nextLine();

        System.out.print("Enter Employee ID: ");
        String id = sc.nextLine();

        // Check for duplicates
        for (Employee e : StorageSystem.allEmployees) {
            if (e.getID().equalsIgnoreCase(id)) {
                System.out.println("Error: Employee ID " + id + " already exists.");
                return;
            }
        }

        System.out.print("Set Password: ");
        String pass = sc.nextLine();

        System.out.print("Set Role (Manager/Staff/Part-time): ");
        String role = sc.nextLine(); 

        // 3. Update Memory (RAM)
        Employee newEmp = new Employee(id, name, role, pass);
        StorageSystem.allEmployees.add(newEmp);

        // 4. Update File (CSV) - Keeps data after restart
        try {
            // 'true' turns on append mode so we don't delete old data
            FileWriter fw = new FileWriter("employees.csv", true); 
            PrintWriter pw = new PrintWriter(fw);
            
            // Format: ID,Name,Role,Password (Ensure this matches your CSV structure)
            pw.println(id + "," + name + "," + role + "," + pass);
            
            pw.close();
            System.out.println("Employee saved to database.");
        } catch (IOException e) {
            System.out.println("Warning: Could not save to CSV file.");
        }

        System.out.println("Employee " + name + " successfully registered!"); 
        System.out.println();
    }
}