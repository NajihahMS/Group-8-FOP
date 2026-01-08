import java.util.ArrayList;
import java.util.Scanner;

public class LoginSystem {

    // Simpan data employee
    private ArrayList<String> ids = new ArrayList<>();
    private ArrayList<String> names = new ArrayList<>();
    private ArrayList<String> passwords = new ArrayList<>();
    private ArrayList<String> outlets = new ArrayList<>();

    // Employee yang sedang login
    private int currentIndex = -1;

    public LoginSystem() {
        // Default employee
        ids.add("C6001");
        names.add("Tan Guan Han");
        passwords.add("a2b1c0");
        outlets.add("C60 (Kuala Lumpur City Centre)");
    }

    // LOGIN
    public boolean login() {
        Scanner sc = new Scanner(System.in);

        System.out.println("=== Employee Login ===");
        System.out.print("Enter User ID: ");
        String id = sc.nextLine();

        System.out.print("Enter Password: ");
        String pass = sc.nextLine();
        System.out.println();

        for (int i = 0; i < ids.size(); i++) {
            if (ids.get(i).equals(id) && passwords.get(i).equals(pass)) {
                currentIndex = i;
                System.out.println("Login Successful!");
                System.out.println("Welcome, " + names.get(i) + " (" + outlets.get(i).substring(0, 3) + ")");
                System.out.println();
                return true;
            }
        }

        System.out.println("Login Failed: Invalid User ID or Password.");
        System.out.println();
        return false;
    }

    // REGISTER EMPLOYEE
    public void registerEmployee() {
        Scanner sc = new Scanner(System.in);

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

        System.out.println();
        System.out.println("Employee successfully registered!");
        System.out.println();
    }

    // Getter untuk AttendanceSystem
    public String getId() {
        return ids.get(currentIndex);
    }

    public String getName() {
        return names.get(currentIndex);
    }

    public String getOutlet() {
        return outlets.get(currentIndex);
    }
}
