import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.IOException;
import java.util.Scanner;
import DataClass.Employee;

public class LoginSystem {

    // Method to handle employee login
    public static Employee login(Scanner sc) {

        System.out.println("=== Employee Login ===");

        // Ask for User ID
        System.out.println("Enter User ID: ");
        String id = sc.nextLine().trim();

        // Allow user to exit login
        if (id.equalsIgnoreCase("EXIT")) {
            return null;
        }

        // Ask for Password
        System.out.println("Enter Password: ");
        String pass = sc.nextLine().trim();

        // Check entered data with stored employee records
        for (Employee e : StorageSystem.allEmployees) {

            // ID is case-insensitive, Password is case-sensitive
            if (e.getID().equalsIgnoreCase(id) && e.getPassword().equals(pass)) {

                System.out.println("Login Successful!");

                // Get outlet code from first 3 characters of ID
                String outletCode = (e.getID().length() >= 3)
                        ? e.getID().substring(0, 3) : "N/A";

                System.out.println("Welcome, " + e.getName() + " (" + outletCode + ")");
                System.out.println();

                return e; // Return logged-in employee
            }
        }

        // If no matching employee found
        System.out.println("Login Failed: Invalid User ID or Password.");
        System.out.println();
        return null;
    }

    // Method to register new employees (Manager only)
    public static void registerNewEmployee(Scanner sc, Employee currentUser) {

        // Authorization check
        if (!currentUser.getRole().equalsIgnoreCase("Manager")) {
            System.out.println("Error: Only Managers can register new employees.");
            return;
        }

        System.out.println("\n=== Register New Employee ===");

        // Input employee details
        System.out.println("Enter Employee Name: ");
        String name = sc.nextLine();

        System.out.println("Enter Employee ID: ");
        String id = sc.nextLine();

        // Prevent duplicate ID
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

        // Create and store new employee
        Employee newEmp = new Employee(id, name, role, pass);
        StorageSystem.allEmployees.add(newEmp);

        // Save data permanently into CSV file
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
