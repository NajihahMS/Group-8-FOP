import java.util.Scanner;
import java.util.List;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import DataClass.Model; // Link to the new Model class

public class StockManagement {
    private List<Model> inventory; // list to hold watch model
    private Scanner input = new Scanner(System.in);
    private final int OUTLET_INDEX = 0;  // Assuming 0 is the index for the current outlet (e.g., C60)

    public StockManagement(List<Model> inventory) { //constructor
        this.inventory = inventory;
    }

    // --- FEATURE 1: MORNING/NIGHT COUNT ---
    public void performStockCount(String session) {
        DateTimeFormatter dtfDate = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        DateTimeFormatter dtfTime = DateTimeFormatter.ofPattern("hh:mm a");
        LocalDateTime now = LocalDateTime.now(); 

        System.out.println("\n=== " + session + " Stock Count ===");
        System.out.println("Date: " + now.format(dtfDate));
        System.out.println("Time: " + now.format(dtfTime).toLowerCase());
        System.out.println();

        int tallyCorrect = 0;
        int mismatches = 0;

       // Loop through every watch model in the inventory list
        for (Model watch : inventory) {
            System.out.print("Model: " + watch.getName() + " - Counted: ");
            int counted = input.nextInt(); // Get user's physical count
            
            // Get the current stock number from the system for this specific outlet
            int systemRecord = watch.getStocks()[OUTLET_INDEX];
            
            System.out.println("Store Record: " + systemRecord);
            
            // Compare physical count vs system record
            if (counted == systemRecord) {
                System.out.println("Stock tally correct.\n");
                tallyCorrect++;
            } else {
                // Calculate the difference if they don't match
                int diff = Math.abs(counted - systemRecord);
                System.out.println("! Mismatch detected (" + diff + " unit difference)\n");
                mismatches++;
            }
        }

        // Print final summary of the audit
        System.out.println("\nTotal Models Checked: " + inventory.size());
        System.out.println("Tally Correct: " + tallyCorrect);
        System.out.println("Mismatches: " + mismatches);
    }

public void handleMovement(String type, String staffName) {
        LocalDateTime now = LocalDateTime.now();
        String dateStr = now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        String timeStr = now.format(DateTimeFormatter.ofPattern("hh:mm a")).toLowerCase();

        System.out.println("\n=== " + type + " ===");
        System.out.print("From (Outlet Code): ");
        input.nextLine(); // Clear the scanner buffer to avoid skipping input
        String from = input.nextLine();
        System.out.print("To (Outlet Code): ");
        String to = input.nextLine();

        java.util.ArrayList<String> movedModels = new java.util.ArrayList<>();
        int totalQuantity = 0;
        boolean updated = false;

        // Loop to allow adding multiple different models in one transaction
        while (true) {
            System.out.print("Enter Model Name (or 'done' to finish): ");
            String name = input.nextLine();
            if (name.equalsIgnoreCase("done")) break; // Exit loop when user types 'done'

            // Search for the model in the inventory list by name
            Model foundWatch = null;
            for (Model m : inventory) {
                if (m.getName().equalsIgnoreCase(name)) {
                    foundWatch = m;
                    break;
                }
            }

            if (foundWatch != null) {
                System.out.print("Enter Quantity: ");
                int qty = input.nextInt();
                input.nextLine(); // Clear buffer

                int currentQty = foundWatch.getStocks()[OUTLET_INDEX];
                // Update logic: Add for "Stock In", Subtract for "Stock Out"
                if (type.equalsIgnoreCase("Stock In")) {
                    foundWatch.getStocks()[OUTLET_INDEX] = currentQty + qty;
                } else {
                    foundWatch.getStocks()[OUTLET_INDEX] = currentQty - qty;
                }
                
                // Add details to a list for the receipt
                movedModels.add("- " + foundWatch.getName() + " (Quantity: " + qty + ")");
                totalQuantity += qty;
                updated = true;
            } else {
                System.out.println("Model not found!");
            }
        }

        // If changes were made, save to permanent CSV file and generate a text receipt
        if (updated) {
            StorageSystem.saveAllModels(); 
            generateReceipt(type, from, to, movedModels, totalQuantity, staffName, dateStr, timeStr);
        }
    }

private void generateReceipt(String type, String from, String to, java.util.ArrayList<String> models, 
                                 int totalQty, String staff, String date, String time) {
        // Create a unique filename based on the current date
        String fileName = "receipts_" + date + ".txt";
        
        // try-with-resources 
        try (FileWriter fw = new FileWriter(fileName, true); // 'true' means append to the file
             PrintWriter pw = new PrintWriter(fw)) {
            
            pw.println("=== " + type + " ===");
            pw.println("Date: " + date);
            pw.println("Time: " + time);
            pw.println("From: " + from);
            pw.println("To: " + to);
            pw.println(type.equals("Stock In") ? "Models Received:" : "Models Sent:");
            
            // Print each item moved
            for (String m : models) pw.println(m);
            
            pw.println("Total Quantity: " + totalQty);
            pw.println("Name of Employee in Charge: " + staff);
            pw.println("---------------------------");

            System.out.println("\nModel quantities updated and saved to CSV.");
            System.out.println("Receipt generated: " + fileName);
        } catch (IOException e) {
            System.out.println("Error saving receipt.");
        }
    }
}
