import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.IOException;
import java.util.Scanner;
import DataClass.Employee; 

public class LoginSystem {

    public static Employee login(Scanner sc) {
        System.out.println("=== Employee Login ===");
        
        // Use println to ensure "Enter Password" starts on a new line
        System.out.println("Enter User ID: ");
        String id = sc.nextLine().trim();

        // Check if user wants to exit
        if (id.equalsIgnoreCase("EXIT")) {
            return null;
        }

        System.out.println("Enter Password: ");
        String pass = sc.nextLine().trim();

        // Check against the Central Storage System
        for (Employee e : StorageSystem.allEmployees) {
            // Case-insensitive ID, Case-sensitive Password
            if (e.getID().equalsIgnoreCase(id) && e.getPassword().equals(pass)) {
                System.out.println("Login Successful!"); 
                
                // Formatting name and outlet code per requirements
                // We use substring(0,3) to get the Outlet Code (e.g., C60) from the ID
                String outletCode = (e.getID().length() >= 3) ? e.getID().substring(0, 3) : "N/A";
                System.out.println("Welcome, " + e.getName() + " (" + outletCode + ")");
                System.out.println();
                
                return e; 
            }
        }

        // If loop finishes without return, login failed
        System.out.println("Login Failed: Invalid User ID or Password."); 
        System.out.println();
        return null;
    }

    public static void registerNewEmployee(Scanner sc, Employee currentUser) {
        // Security Check: Only Manager can register
        if (!currentUser.getRole().equalsIgnoreCase("Manager")) {
            System.out.println("Error: Only Managers are authorized to register new employees.");
            return;
        }

        System.out.println("\n=== Register New Employee ==="); 
        
        System.out.println("Enter Employee Name: ");
        String name = sc.nextLine();

        System.out.println("Enter Employee ID: ");
        String id = sc.nextLine();

        // Duplicate check
        for (Employee e : StorageSystem.allEmployees) {
            if (e.getID().equalsIgnoreCase(id)) {
                System.out.println("Error: Employee ID already exists.");
                return;
            }
        }

        System.out.println("Set Password: ");
        String pass = sc.nextLine();

        System.out.println("Set Role: ");
        String role = sc.nextLine(); 

        // Update Memory
        Employee newEmp = new Employee(id, name, role, pass);
        StorageSystem.allEmployees.add(newEmp);

        // Update CSV File
        try (FileWriter fw = new FileWriter("employees.csv", true); 
             PrintWriter pw = new PrintWriter(fw)) {
            pw.println(id + "," + name + "," + role + "," + pass);
            System.out.println("Employee successfully registered!"); 
        } catch (IOException e) {
            System.out.println("Error: Could not save to database.");
        }
        System.out.println();
    }
}
