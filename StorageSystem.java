import java.io.*;
import java.util.ArrayList;
import java.util.List;
import DataClass.Employee;
import DataClass.Outlet;
import DataClass.Model;


public class StorageSystem{

   
// 1. IN-MEMORY DATABASE (Global Lists)
// ==========================================
// Other classes (Login, Sales) will access these static lists directly.
public static List<Employee> allEmployees = new ArrayList<>();
public static List<Outlet> allOutlets = new ArrayList<>();
public static List<Model> allModels = new ArrayList<>();

// File Names (Must match exactly what is in your project folder)
private static final String FILE_MODEL      = "model.csv";
private static final String FILE_SALES      = "sales_history.csv";
private static final String FILE_ATTENDANCE = "attendance_log.csv";

// ==========================================
// 2. INITIALIZATION (Run this ONCE at start)
// ==========================================
public static void initialize() {
    System.out.println(">> System Starting: Loading Data...");

    // LINK: Call the DataStateLoad class (Part 2) to fetch data
    allEmployees = dataStateLoad.loadEmployees();
    allOutlets   = dataStateLoad.loadOutlets();
    allModels    = dataStateLoad.loadModels();

    // Ensure log files exist so we don't crash when writing later
    createFileIfNotExists(FILE_SALES, "Date,Time,EmpID,Customer,Model,Qty,Total");
    createFileIfNotExists(FILE_ATTENDANCE, "EmpID,Type,Date,Time");

    System.out.println(">> Data Loaded Successfully.");
    System.out.println(">> Employees: " + allEmployees.size());
    System.out.println(">> Watch Models: " + allModels.size());
}

// ==========================================
// 3. SAVING DATA (Writing back to Files)
// ==========================================

/* CRITICAL: Call this method whenever stock changes (e.g., after a sale).
     It overwrites model.csv with the current numbers in memory. */
public static void saveAllModels() {
    try (BufferedWriter bw = new BufferedWriter(new FileWriter(FILE_MODEL))) {
        // Write Header
        bw.write("Model,Price,C60,C61,C62,C63,C64,C65,C66,C67,C68,C69");
        bw.newLine();

        // Loop through the list and write each object
        for (Model m : allModels) {
            bw.write(m.toCSV()); // Uses the toCSV() method we added to Model.java
            bw.newLine();
        }
        // System.out.println("Debug: Models saved to CSV."); 
    } catch (IOException e) {
        System.out.println("Error saving model data: " + e.getMessage());
    }
}

    /*
      Call this to log a new sale transaction.
     Appends to the bottom of the file (does not overwrite).
     */
    public static void logSale(String saleRecordCSV) {
        appendToFile(FILE_SALES, saleRecordCSV);
    }

    /*
     Call this when an employee logs in or out.
     */
    public static void logAttendance(String attendanceRecordCSV) {
        appendToFile(FILE_ATTENDANCE, attendanceRecordCSV);
    }

    // ==========================================
    // 4. HELPER METHODS (Internal Logic)
    // ==========================================

    private static void appendToFile(String fileName, String data) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(fileName, true))) {
            bw.write(data);
            bw.newLine();
        } catch (IOException e) {
            System.out.println("Error logging to " + fileName);
        }
    }

    private static void createFileIfNotExists(String fileName, String header) {
        File f = new File(fileName);
        if (!f.exists()) {
            try (BufferedWriter bw = new BufferedWriter(new FileWriter(f))) {
                bw.write(header);
                bw.newLine();
            } catch (IOException e) {
                System.out.println("Error creating file: " + fileName);
            }
        }
    }
}