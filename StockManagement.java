import java.util.Scanner;
import java.util.List;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import DataClass.Model;

public class StockManagement {
    private List<Model> inventory;
    private Scanner input = new Scanner(System.in);
    private final int OUTLET_INDEX = 0; 

    public StockManagement(List<Model> inventory) {
        this.inventory = inventory;
    }

    // --- FEATURE 1: STOCK COUNT ---
    public void performStockCount(String session) {
        DateTimeFormatter dtfDate = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        DateTimeFormatter dtfTime = DateTimeFormatter.ofPattern("hh:mm a");
        LocalDateTime now = LocalDateTime.now(); 

        System.out.println("\n=== " + session + " Stock Count ===");
        System.out.println("Date: " + now.format(dtfDate));
        System.out.println("Time: " + now.format(dtfTime).toLowerCase());
        System.out.println(); // Blank line before list

        int tallyCorrect = 0;
        int mismatches = 0;

        for (Model watch : inventory) {
            System.out.print("Model: " + watch.getName() + " - Counted: ");
            int counted = input.nextInt();
            int systemRecord = watch.getStocks()[OUTLET_INDEX];
            
            System.out.println("Store Record: " + systemRecord);
            
            if (counted == systemRecord) {
                System.out.println("Stock tally correct.");
                tallyCorrect++;
            } else {
                int diff = Math.abs(counted - systemRecord);
                System.out.println("! Mismatch detected (" + diff + " unit difference)");
                mismatches++;
            }
            System.out.println(); // Empty line between models as per sample
        }

        // Summary section matching the blue box in Image 1
        System.out.println("// (Repeat for each model)");
        System.out.println("\nTotal Models Checked: " + inventory.size());
        System.out.println("Tally Correct: " + tallyCorrect);
        System.out.println("Mismatches: " + mismatches);
        System.out.println(session + " stock count completed.");
        if (mismatches > 0) {
            System.out.println("Warning: Please verify stock.");
        }
    }

    // --- FEATURE 2: STOCK IN/OUT ---
    public void handleMovement(String type, String staffName) {
        LocalDateTime now = LocalDateTime.now();
        String dateStr = now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        String timeStr = now.format(DateTimeFormatter.ofPattern("hh:mm a")).toLowerCase();

        System.out.print("From (Outlet Code): ");
        input.nextLine(); 
        String from = input.nextLine();
        System.out.print("To (Outlet Code): ");
        String to = input.nextLine();

        java.util.ArrayList<String> movedModels = new java.util.ArrayList<>();
        int totalQuantity = 0;
        boolean updated = false;

        while (true) {
            System.out.print("Enter Model Name (or 'done'): ");
            String name = input.nextLine();
            if (name.equalsIgnoreCase("done")) break;

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
                input.nextLine(); 

                int currentQty = foundWatch.getStocks()[OUTLET_INDEX];
                int newQty = type.equalsIgnoreCase("Stock In") ? currentQty + qty : currentQty - qty;
                
                foundWatch.setStock(OUTLET_INDEX, newQty);
                movedModels.add("  - " + foundWatch.getName() + " (Quantity: " + qty + ")");
                totalQuantity += qty;
                updated = true;
            } else {
                System.out.println("Model not found!");
            }
        }

        if (updated) {
            // Updated console confirmation to match Image 2
            System.out.println("\nModel quantities updated successfully.");
            System.out.println(type + " recorded.");
            generateReceipt(type, from, to, movedModels, totalQuantity, staffName, dateStr, timeStr);
        }
    }

    private void generateReceipt(String type, String from, String to, java.util.ArrayList<String> models, 
                                 int totalQty, String staff, String date, String time) {
        String fileName = "receipts_" + date + ".txt";
        
        // Console output to match the last line of Image 2
        System.out.println("Receipt generated: " + fileName);

        try (FileWriter fw = new FileWriter(fileName, true);
             PrintWriter pw = new PrintWriter(fw)) {
            pw.println("=== " + type + " ===");
            pw.println("Date: " + date);
            pw.println("Time: " + time);
            pw.println("From: " + from);
            pw.println("To: " + to);
            pw.println("Models Received:"); // Specific header from Image 2
            for (String m : models) pw.println(m);
            pw.println("Total Quantity: " + totalQty);
            // Employee name is required by requirements but hidden in some sample views; added here
            pw.println("Employee in Charge: " + staff); 
            pw.println("\n---------------------------\n");
        } catch (IOException e) {
            System.out.println("Error saving receipt: " + e.getMessage());
        }
    }
}
