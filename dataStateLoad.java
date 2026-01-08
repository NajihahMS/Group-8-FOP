
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import DataClass.Employee;
import DataClass.Outlet;    
import DataClass.Model;

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
                    //.trim() to remove extra spaces
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
            System.out.println("Error reading outlet.csv: " + e.getMessage());
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
  
