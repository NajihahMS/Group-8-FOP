package labassignmentfop;                 
import java.util.ArrayList;               
import java.util.Scanner;                

// Employee class
class Employee {

    private String empID;                 // Store employee ID
    private String name;                  // Store employee name
    private String password;              // Store login password
    private String role;                  // Employee role (Full-time / Part-time)
    private String outlet;                // Outlet location (e.g. C60 KLCC)

    // Constructor to initialize employee object
    public Employee(String empID, String name, String password, String role, String outlet) {
        this.empID = empID;               // Assign employee ID
        this.name = name;                 // Assign employee name
        this.password = password;         // Assign password
        this.role = role;                 // Assign role
        this.outlet = outlet;             // Assign outlet
    }

    // Getter method to get employee ID
    public String getEmpID() {
        return empID;
    }

    // Getter method to get employee name
    public String getName() {
        return name;
    }

    // Getter method to get employee password
    public String getPassword() {
        return password;
    }

    // Getter method to get employee role
    public String getRole() {
        return role;
    }

    // Getter method to get outlet
    public String getOutlet() {
        return outlet;
    }
}

// Login & Logout management class
public class loginLogout {

    private ArrayList<Employee> employees = new ArrayList<>();  // ArrayList to store all registered employees
    private Scanner sc = new Scanner(System.in);   // Scanner object to read user input
    private Employee loggedInEmployee = null;   // Store currently logged in employee (null if no one logged in)

    // Method to get the current logged-in employee
    public Employee getLoggedInEmployee() {
        return loggedInEmployee;
    }

    // Register new employee   
    public void registerEmployee() {

        System.out.println("\n=== Register New Employee ===");
        System.out.print("Enter Employee Name: ");
        String name = sc.nextLine();       // Read employee name
        System.out.print("Enter Employee ID: ");
        String empID = sc.nextLine();      // Read employee ID
        System.out.print("Set Password: ");
        String password = sc.nextLine();   // Read password
        System.out.print("Set Role: ");
        String role = sc.nextLine();       // Read role

        // Generate outlet based on first 3 characters of employee ID
        String outlet = empID.substring(0, 3) + " (Kuala Lumpur City Centre)";

        // Check if employee ID already exists (avoid duplicate)
        for (Employee e : employees) {
            if (e.getEmpID().equals(empID)) {
                System.out.println("Employee ID already exists!");
                return;   // Stop registration if duplicate found
            }
        }

        // Add new employee into ArrayList
        employees.add(new Employee(empID, name, password, role, outlet));
        System.out.println("\nEmployee successfully registered!");
    }

    // Employee login function
    public void login() {

        System.out.println("\n=== Employee Login ===");
        System.out.print("Enter User ID: ");
        String empID = sc.nextLine();      // Read input employee ID
        System.out.print("Enter Password: ");
        String password = sc.nextLine();   // Read input password
        
        // Loop through employee list to find matching account
        for (Employee e : employees) {

            // Check if ID and password match
            if (e.getEmpID().equals(empID) && e.getPassword().equals(password)) {
                loggedInEmployee = e;     // Set logged in employee
                System.out.println("Login Successful!");
                System.out.println("Welcome, " + e.getName() +
                                   " (" + e.getOutlet().substring(0, 3) + ")");
                return;                   // Exit method after successful login
            }
        }

        // If no matching account found
        System.out.println("Login Failed: Invalid User ID or Password.");
    }

    // Logout function
    public void logout() {

        // Check if someone is logged in
        if (loggedInEmployee != null) {
            System.out.println("Logging out " + loggedInEmployee.getName());
            loggedInEmployee = null;      // Reset logged-in employee
        }
    }

    // Add default employee (for testing purpose)
    public void addDefaultEmployee(Employee e) {
        employees.add(e);                // Add employee into list directly
    }
}