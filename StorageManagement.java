package pkgfinal;



import java.util.Scanner;
import java.util.ArrayList;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class StockManagement {
    private ArrayList<WatchModel> inventory;
    private Scanner input = new Scanner(System.in);

    public StockManagement(ArrayList<WatchModel> inventory) {
        this.inventory = inventory;
    }

    // --- FEATURE 1: MORNING/NIGHT COUNT ---
    public void performStockCount(String session) {
        // Matches the "=== Morning Stock Count ===" header create class 
        DateTimeFormatter dtfDate = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        DateTimeFormatter dtfTime = DateTimeFormatter.ofPattern("hh:mm a");
        LocalDateTime now = LocalDateTime.now(); //get real time

        System.out.println("\n=== " + session + " Stock Count ===");
        System.out.println("Date: " + now.format(dtfDate));
        System.out.println("Time: " + now.format(dtfTime).toLowerCase());
        System.out.println();

        int tallyCorrect = 0;
        int mismatches = 0;

        for (WatchModel watch : inventory) {
            System.out.print("Model: " + watch.getName() + " - Counted: ");
            int counted = input.nextInt();
            int systemRecord = watch.getQuantity();
            
            System.out.println("Store Record: " + systemRecord);
            
            if (counted == systemRecord) {
                System.out.println("Stock tally correct.\n");
                tallyCorrect++;
            } else {
                int diff = Math.abs(counted - systemRecord);
                System.out.println("! Mismatch detected (" + diff + " unit difference)\n");
                mismatches++;
            }
        }

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
        // Prepare Date and Time for console display
        LocalDateTime now = LocalDateTime.now();
        String dateStr = now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        String timeStr = now.format(DateTimeFormatter.ofPattern("hh:mm a")).toLowerCase();

        System.out.println("\n=== " + type + " ===");
        System.out.println("Date: " + dateStr);
        System.out.println("Time: " + timeStr);

        System.out.print("From (Outlet Code): ");
        input.nextLine(); // clear buffer
        String from = input.nextLine();
        System.out.print("To (Outlet Code): ");
        String to = input.nextLine();

        ArrayList<String> movedModels = new ArrayList<>();
        int totalQuantity = 0;
        boolean addingModels = true;

        // Loop to allow multiple models in one transaction (as per image example)
        while (addingModels) {
            System.out.print("Enter Model Name (or 'done' to finish): ");
            String name = input.nextLine();
            if (name.equalsIgnoreCase("done")) break;

            WatchModel foundWatch = null;
            for (WatchModel w : inventory) {
                if (w.getName().equalsIgnoreCase(name)) {
                    foundWatch = w;
                    break;
                }
            }

            if (foundWatch != null) {
                System.out.print("Enter Quantity: ");
                int qty = input.nextInt();
                input.nextLine(); // clear buffer

                if (type.equalsIgnoreCase("Stock In")) {
                    foundWatch.setQuantity(foundWatch.getQuantity() + qty);
                } else {
                    foundWatch.setQuantity(foundWatch.getQuantity() - qty);
                }
                
                movedModels.add("- " + foundWatch.getName() + " (Quantity: " + qty + ")");
                totalQuantity += qty;
            } else {
                System.out.println("Model not found!");
            }
        }

        if (!movedModels.isEmpty()) {
            generateReceipt(type, from, to, movedModels, totalQuantity, staffName, dateStr, timeStr);
        }
    }

    private void generateReceipt(String type, String from, String to, ArrayList<String> models, 
                                 int totalQty, String staff, String date, String time) {
        
        String fileName = "receipts_" + date + ".txt";

        try (FileWriter fw = new FileWriter(fileName, true);
             PrintWriter pw = new PrintWriter(fw)) {
            
            pw.println("=== " + type + " ===");
            pw.println("Date: " + date);
            pw.println("Time: " + time);
            pw.println("From: " + from);
            pw.println("To: " + to);
            pw.println(type.equals("Stock In") ? "Models Received:" : "Models Sent:");
            
            for (String m : models) {
                pw.println(m);
            }
            
            pw.println("Total Quantity: " + totalQty);
            pw.println("Name of Employee in Charge: " + staff);
            pw.println("---------------------------");

            // Success messages matching the image
            System.out.println("\nModel quantities updated successfully.");
            System.out.println(type + " recorded.");
            System.out.println("Receipt generated: " + fileName);
            
        } catch (IOException e) {
            System.out.println("Error saving receipt.");
        }
    }
