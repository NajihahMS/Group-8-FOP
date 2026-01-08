import java.io.*;
import java.util.Scanner;
import java.util.ArrayList;
import DataClass.Employee;

public class AttendanceSystem {
    private dataStateLoad data;
    private Scanner sc;
    private Employee loggedInEmployee = null;

    public AttendanceSystem(dataStateLoad data) {
        this.data = data;
        this.sc = new Scanner(System.in);
    }

    // ===========================
    // 1. LOGIN FUNCTION
    // ===========================
    public Employee login() {
        System.out.println("\n--- LOGIN ---");
        System.out.print("Enter Username (Employee Name): ");
        String username = sc.nextLine();
        System.out.print("Enter Password: ");
        String password = sc.nextLine();

        // Check against loaded data
        for (Employee e : data.employees) {
            // You can match by Name or ID. Here we use Name as per prompt context.
            if (e.employeeName.equalsIgnoreCase(username) && e.password.equals(password)) {
                loggedInEmployee = e;
                System.out.println("Login Successful! Welcome, " + e.employeeName);
                return loggedInEmployee;
            }
        }

        System.out.println("Invalid Username or Password.");
        return null;
    }

    // ===========================
    // 2. LOGOUT FUNCTION
    // ===========================
    public void logout() {
        if (loggedInEmployee != null) {
            System.out.println("Goodbye, " + loggedInEmployee.employeeName + "!");
            loggedInEmployee = null;
        } else {
            System.out.println("No user is currently logged in.");
        }
    }

    // ===========================
    // 3. REGISTER NEW EMPLOYEE
    // ===========================
    public void registerNewEmployee() {
        // Security check: Only Managers usually register new staff
        if (loggedInEmployee != null && !loggedInEmployee.role.equalsIgnoreCase("Manager")) {
            System.out.println("Access Denied: Only Managers can register new employees.");
            return;
        }

        System.out.println("\n--- REGISTER NEW EMPLOYEE ---");
        
        System.out.print("Enter New Employee ID (e.g., C6005): ");
        String id = sc.nextLine();

        // Check for duplicate ID
        for (Employee e : data.employees) {
            if (e.employeeID.equalsIgnoreCase(id)) {
                System.out.println("Error: Employee ID already exists.");
                return;
            }
        }

        System.out.print("Enter Name: ");
        String name = sc.nextLine();
        
        System.out.print("Enter Role (Manager/Staff): ");
        String role = sc.nextLine();
        
        System.out.print("Create Password: ");
        String pass = sc.nextLine();

        // 1. Create Object and add to memory list (so we can use it immediately)
        Employee newEmp = new Employee(id, name, role, pass);
        data.employees.add(newEmp);

        // 2. Append to CSV file (Persistent Storage)
        saveEmployeeToCSV(id, name, role, pass);
    }

    private void saveEmployeeToCSV(String id, String name, String role, String pass) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter("employee.csv", true))) {
            // Format matches Najihah's loader: ID,Name,Role,Password
            String line = String.format("%s,%s,%s,%s", id, name, role, pass);
            bw.write(line);
            bw.newLine();
            System.out.println("Employee registered and saved to database successfully.");
        } catch (IOException e) {
            System.out.println("Error saving to employee.csv: " + e.getMessage());
        }
    }
    
    // Helper to check login status
    public boolean isLoggedIn() {
        return loggedInEmployee != null;
    }
    
    public Employee getCurrentUser() {
        return loggedInEmployee;
    }
}