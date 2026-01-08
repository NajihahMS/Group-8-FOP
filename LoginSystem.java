import java.util.ArrayList;
import java.util.Scanner;
import DataClass.Employee; 

public class LoginSystem {

    // Simpan data employee (Must be static to be accessed by static methods in MAIN)
    private static ArrayList<String> ids = new ArrayList<>();
    private static ArrayList<String> names = new ArrayList<>();
    private static ArrayList<String> passwords = new ArrayList<>();
    private static ArrayList<String> outlets = new ArrayList<>();

    // Initialize default data (Replaces constructor)
    static {
        ids.add("C6001");
        names.add("Tan Guan Han");
        passwords.add("a2b1c0");
        outlets.add("C60 (Kuala Lumpur City Centre)");
    }

    // LOGIN
    // Changed return type to Employee to satisfy MAIN.java (line 36)
    public static Employee login(Scanner sc) {
        // Scanner passed from MAIN, so we don't need 'new Scanner(System.in)' here

        System.out.println("=== Employee Login ===");
        System.out.print("Enter User ID: ");
        String id = sc.nextLine();

        System.out.print("Enter Password: ");
        String pass = sc.nextLine();
        System.out.println();

        for (int i = 0; i < ids.size(); i++) {
            if (ids.get(i).equals(id) && passwords.get(i).equals(pass)) {
                
                System.out.println("Login Successful!"); 
                System.out.println("Welcome, " + names.get(i) + " (" + outlets.get(i).substring(0, 3) + ")");
                System.out.println();
                
                // Return an Employee object so MAIN can store it in 'currentUser'
                // We use "Part-time" as default role since your list didn't have roles
                return new Employee(ids.get(i), names.get(i), "Part-time", passwords.get(i));
            }
        }

        System.out.println("Login Failed: Invalid User ID or Password."); 
        return null;
    }

    // REGISTER EMPLOYEE
    // Renamed to registerNewEmployee to match MAIN.java (line 84)
    public static void registerNewEmployee(Scanner sc, Employee currentUser) {
        
        System.out.println("=== Register New Employee ==="); 
        System.out.print("Enter Employee Name: ");
        String name = sc.nextLine();

        System.out.print("Enter Employee ID: ");
        String id = sc.nextLine();

        System.out.print("Set Password: ");
        String pass = sc.nextLine();

        System.out.print("Set Role: ");
        sc.nextLine(); // role tak digunakan lagi (basic feature)

        ids.add(id);
        names.add(name);
        passwords.add(pass);
        outlets.add("C60 (Kuala Lumpur City Centre)");

        // Also add to StorageSystem so other parts of the app (like GUI) see it
        StorageSystem.allEmployees.add(new Employee(id, name, "Part-time", pass));

        System.out.println();
        System.out.println("Employee successfully registered!"); 
        System.out.println();
    }
}