import java.util.Scanner;
import java.util.List;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import DataClass.Model;

/**
 * Handles the logic for physical stock counts and stock movements (In/Out).
 * Updates the inventory list and logs transactions to text files.
 */
public class StockManagement {
    private List<Model> inventory; // Refer to the main list of watch models
    private Scanner input = new Scanner(System.in);
    private final int OUTLET_INDEX = 0; // Fixed index represent outlet (e.g., C60)

    public StockManagement(List<Model> inventory) { //constructor
        this.inventory = inventory;
    }

    // --- FEATURE 1: STOCK COUNT ---
    public void performStockCount(String session) {
        // Initialize date and time
        DateTimeFormatter dtfDate = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        DateTimeFormatter dtfTime = DateTimeFormatter.ofPattern("hh:mm a");
        LocalDateTime now = LocalDateTime.now(); 

        System.out.println("\n=== " + session + " Stock Count ===");
        System.out.println("Date: " + now.format(dtfDate));
        System.out.println("Time: " + now.format(dtfTime).toLowerCase());

        int tallyCorrect = 0;
        int mismatches = 0;

        // loop through every watch in the inventory
        for (Model watch : inventory) {
            System.out.print("Model: " + watch.getName() + " - Counted: ");
            int counted = input.nextInt(); // get User input for physical count
            
            // get current system stock for the specific outlet index
            int systemRecord = watch.getStocks()[OUTLET_INDEX];
            
            System.out.println("Store Record: " + systemRecord);
            
            //  Check if physical count matches system record
            if (counted == systemRecord) {
                System.out.println("Stock tally correct.\n");
                tallyCorrect++;
            } else { //calculate diff if not matched
                int diff = Math.abs(counted - systemRecord);
                System.out.println("! Mismatch detected (" + diff + " unit difference)\n");
                mismatches++;
            }
        }
        System.out.println("Count completed. Correct: " + tallyCorrect + " | Mismatches: " + mismatches);
    }

    // --- FEATURE 2: STOCK IN/OUT ---
    public void handleMovement(String type, String staffName) {
        LocalDateTime now = LocalDateTime.now();
        String dateStr = now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        String timeStr = now.format(DateTimeFormatter.ofPattern("hh:mm a")).toLowerCase();

        //  Where is the stock coming from and going to?
        System.out.print("From (Outlet Code): ");
        input.nextLine(); // Clear scanner buffer
        String from = input.nextLine();
        System.out.print("To (Outlet Code): ");
        String to = input.nextLine();

        java.util.ArrayList<String> movedModels = new java.util.ArrayList<>();
        int totalQuantity = 0;
        boolean updated = false;

        // Loop allows user to add multiple different models in one transaction
        while (true) {
            System.out.print("Enter Model Name (or 'done'): ");
            String name = input.nextLine();
            if (name.equalsIgnoreCase("done")) break; //exit loop when user type done

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

                // Calculate the new stock level based on movement type
                int currentQty = foundWatch.getStocks()[OUTLET_INDEX];
                int newQty = type.equalsIgnoreCase("Stock In") ? currentQty + qty : currentQty - qty;
                
                // Update the actual object in memory
                foundWatch.setStock(OUTLET_INDEX, newQty);
                
                // Track itemized list for the receipt
                movedModels.add("- " + foundWatch.getName() + " (Qty: " + qty + ")");
                totalQuantity += qty;
                updated = true;
            } else {
                System.out.println("Model not found!");
            }
        }

        // If at least one item was updated, persist changes to CSV and generate receipt
        if (updated) {
            StorageSystem.saveAllModels(); 
            generateReceipt(type, from, to, movedModels, totalQuantity, staffName, dateStr, timeStr);
        }
    }

  //generate reciept
    private void generateReceipt(String type, String from, String to, java.util.ArrayList<String> models, 
                                 int totalQty, String staff, String date, String time) {
        String fileName = "receipts_" + date + ".txt";
        try (FileWriter fw = new FileWriter(fileName, true); // 'true' enables appending to file
             PrintWriter pw = new PrintWriter(fw)) {
            pw.println("=== " + type + " ===");
            pw.println("Date: " + date + " | Time: " + time);
            pw.println("From: " + from + " | To: " + to);
            for (String m : models) pw.println(m); // Print each itemized movement
            pw.println("Total: " + totalQty + " | Staff: " + staff);
            pw.println("---------------------------");
            System.out.println("Receipt saved to " + fileName);
        } catch (IOException e) {
            System.out.println("Error saving receipt: " + e.getMessage());
        }
    }
}
