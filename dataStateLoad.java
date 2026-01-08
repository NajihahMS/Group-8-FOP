
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

//=========================================
//           DATA CLASSES 
//=========================================

public class Employee {
    private String employeeID;
    private String employeeName;
    private String role;
    private String password;

    public Employee(String employeeID, String employeeName, String role, String password) {
        this.employeeID = employeeID;
        this.employeeName = employeeName;
        this.role = role;
        this.password = password;
    }
    // Getters are required for your Login System later
    public String getID() { return employeeID; }
    public String getPassword() { return password; }
    public String getName() { return employeeName; }
    public String getRole() { return role; }
    
    // Helper to format for saving back to CSV
    public String toCSV() {
        return employeeID + "," + employeeName + "," + role + "," + password;
}
}


public class Outlet{
    private String outletCode;
    private String outletName;

    public Outlet(String outletCode, String outletName) {
        this.outletCode = outletCode;
        this.outletName = outletName;
    }
    public String getCode() { return outletCode; }
    public String getName() { return outletName; }
}

public class Model{
    private String modelName;
    private double price; // Changed to double for Math
    // Stocks for specific outlets (Changed to int for Math)
    public int c60, c61, c62, c63, c64, c65, c66, c67, c68, c69;

    public Model(String model, double price, int[] stocks) {
        this.modelName = model;
        this.price = price;
        // Assigning array to individual fields
        this.c60 = stocks[0]; this.c61 = stocks[1]; this.c62 = stocks[2];
        this.c63 = stocks[3]; this.c64 = stocks[4]; this.c65 = stocks[5];
        this.c66 = stocks[6]; this.c67 = stocks[7]; this.c68 = stocks[8];
        this.c69 = stocks[9];
    }

    public String getName() { return modelName; }
    public double getPrice() { return price; }
    
    // Crucial for saving later
    public String toCSV() {
        return modelName + "," + price + "," + c60 + "," + c61 + "," + c62 + 
               "," + c63 + "," + c64 + "," + c65 + "," + c66 + "," + c67 + "," + c68 + "," + c69;
    }
}
    

//======================================================
//            FETCH DATA FROM CSV FILES
//======================================================

public class dataStateLoad {
    // load employee
  public static List<Employee> loadEmployees() {
        List<Employee> list = new ArrayList<>();
        String line;

        try (BufferedReader br = new BufferedReader(new FileReader("employee.csv"))) {
            br.readLine(); // Skip header
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");
                if(data.length >= 4) {
                    // Create object
                    Employee emp = new Employee(data[0].trim(), data[1].trim(), data[2].trim(), data[3].trim());
                    list.add(emp);
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading employee.csv: " + e.getMessage());
        }
        return list;
    }

    //load outlets 
    public static List<Outlet> loadOutlets() {
        List<Outlet> list = new ArrayList<>();
        String line;

        try (BufferedReader br = new BufferedReader(new FileReader("outlet.csv"))) {
            br.readLine(); 
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");
                if(data.length >= 2) {
                    list.add(new Outlet(data[0].trim(), data[1].trim()));
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading outlet.csv");
        }
        return list;
    }
    //load models
    public static List<Model> loadModels() {
        List<Model> list = new ArrayList<>();
        String line;

        try (BufferedReader br = new BufferedReader(new FileReader("model.csv"))) {
            br.readLine(); 
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");
                
                // Safety check
                if(data.length < 12) continue; 

                String name = data[0].trim();
                double price = Double.parseDouble(data[1].trim());
                
                // Parse all stocks into an array
                int[] stocks = new int[10];
                for(int i=0; i<10; i++) {
                    stocks[i] = Integer.parseInt(data[i+2].trim()); // Columns 2 to 11
                }

                Model mod = new Model(name, price, stocks);
                list.add(mod);
            }
        } catch (IOException | NumberFormatException e) {
            System.out.println("Error reading model.csv (Check number formats): " + e.getMessage());
        }
        return list;
    }
}
  
    //========================================
    //           ACCESSOR METHODS
    //========================================
    public ArrayList<Employee> getEmployees() {
         return employees;
    }

    public ArrayList<Outlet> getOutlets() {
        return outlets;
    }

    public ArrayList<Model> getModels() {
        return models;
    }   
}