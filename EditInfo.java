import java.io.*;
import java.util.*;
import DataClass.Model;

/**
 * CHILD CLASS: Edit Information (Stock + Sales)
 * - Compatible with your friends' Model.java (getName(), c60..c69)
 * - Uses StorageSystem.saveAllModels() to persist updated stock
 * - Output wording follows the PDF samples :contentReference[oaicite:1]{index=1}
 */
public class EditInfo extends Info {

    // This file name matches StorageSystem's file setup
    private static final String FILE_SALES = "sales_history.csv";

    public EditInfo(String employeeName, int outletIndex) {
        super(employeeName, outletIndex);
    }

    @Override
    public void run() {
        while (true) {
            System.out.println("\n=== Edit Information ===");
            System.out.println("1. Edit Stock Information");
            System.out.println("2. Edit Sales Information");
            System.out.println("0. Back");
            System.out.print("Choose: ");

            String choice = sc.nextLine().trim();
            if (choice.equals("1")) {
                editStock();
            } else if (choice.equals("2")) {
                editSales();
            } else if (choice.equals("0")) {
                return;
            } else {
                System.out.println("Invalid choice.");
            }
        }
    }

    // =========================
    // 1) Edit Stock Information (PDF page 9)
    // =========================
    private void editStock() {
        System.out.println("\n=== Edit Stock Information ===");
        System.out.print("Enter Model Name: ");
        String modelName = sc.nextLine().trim();

        // CALLING OTHER CLASS:
        // StorageSystem.allModels is from StorageSystem (global list loaded at startup)
        Model found = null;
        for (Model m : StorageSystem.allModels) {
            // CALLING OTHER CLASS:
            // Model.getName() is from DataClass.Model
            if (m.getName().equalsIgnoreCase(modelName)) {
                found = m;
                break;
            }
        }

        if (found == null) {
            System.out.println("No record found.");
            return;
        }

        // Read current stock for THIS outlet only (based on outletIndex 0..9)
        // CALLING OTHER CLASS:
        // Model.c60..c69 are public fields in DataClass.Model
        int current = getStock(found, outletIndex);

        System.out.println("Current Stock: " + current);
        int newStock = readInt("Enter New Stock Value: ");

        // Update stock in memory
        setStock(found, outletIndex, newStock);

        // CALLING OTHER CLASS:
        // StorageSystem.saveAllModels() writes updated list back to model.csv
        StorageSystem.saveAllModels();

        System.out.println("Stock information updated successfully.");
    }

    // =========================
    // 2) Edit Sales Information (PDF page 9)
    // =========================
    private void editSales() {
        System.out.println("\n=== Edit Sales Information ===");
        System.out.print("Enter Transaction Date: ");
        String date = sc.nextLine().trim();
        System.out.print("Enter Customer Name: ");
        String customer = sc.nextLine().trim();

        File f = new File(FILE_SALES);
        if (!f.exists()) {
            System.out.println("No record found.");
            return;
        }

        // Read all rows
        // CALLING OTHER CLASS:
        // ArrayList, BufferedReader, FileReader are from java.util / java.io
        ArrayList<String[]> rows = new ArrayList<>();
        String[] header;

        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String h = br.readLine();
            if (h == null) {
                System.out.println("No record found.");
                return;
            }
            header = h.split("\\s*,\\s*");
            String line;
            while ((line = br.readLine()) != null) {
                rows.add(line.split("\\s*,\\s*"));
            }
        } catch (IOException e) {
            System.out.println("Error reading sales file: " + e.getMessage());
            return;
        }

        // Find column indexes (flexible: supports your friends' header OR extended header)
        int idxDate = findCol(header, "Date");
        int idxCustomer = findCol(header, "Customer");
        int idxModel = findCol(header, "Model");
        int idxQty = findCol(header, "Qty");
        int idxTotal = findCol(header, "Total");

        // Support both "TransactionMethod" and "Transaction Method" if your group added it
        int idxMethod = findCol(header, "TransactionMethod");
        if (idxMethod == -1) idxMethod = findCol(header, "Transaction Method");

        // Locate the record by Date + Customer (like PDF)
        int foundIndex = -1;
        for (int i = 0; i < rows.size(); i++) {
            String[] r = rows.get(i);
            String rDate = getSafe(r, idxDate);
            String rCust = getSafe(r, idxCustomer);

            if (rDate.equalsIgnoreCase(date) && rCust.equalsIgnoreCase(customer)) {
                foundIndex = i;
                break;
            }
        }

        if (foundIndex == -1) {
            System.out.println("No record found.");
            return;
        }

        String[] rec = rows.get(foundIndex);

        // Display like PDF
        System.out.println("Sales Record Found:");
        System.out.println("Model: " + getSafe(rec, idxModel) + " Quantity: " + getSafe(rec, idxQty));
        System.out.println("Total: " + getSafe(rec, idxTotal));

        // If method column doesn't exist in your CSV, display "Unknown" but still allow edit
        System.out.println("Transaction Method: " + getSafe(rec, idxMethod));

        System.out.println("Select number to edit:");
        System.out.println("1. Name 2. Model 3. Quantity 4. Total");
        System.out.println("5. Transaction Method");
        System.out.print("> ");

        int opt;
        try {
            opt = Integer.parseInt(sc.nextLine().trim());
        } catch (Exception e) {
            System.out.println("Invalid choice.");
            return;
        }

        // NOTE about PDF sample:
        // The PDF shows user typed > 4 then it asked "Enter New Transaction Method"
        // (but menu says 4 = Total). To be safe and match sample, we allow 4 OR 5
        // to edit Transaction Method.

        if (opt == 1) {
            System.out.print("Enter New Customer Name: ");
            String newName = sc.nextLine().trim();

            if (readYes("Confirm Update? (Y/N): ")) {
                setSafe(rec, idxCustomer, newName);
                saveSalesFile(header, rows);
                System.out.println("Sales information updated successfully.");
            }

        } else if (opt == 2) {
            System.out.print("Enter New Model Name: ");
            String newModel = sc.nextLine().trim();

            if (readYes("Confirm Update? (Y/N): ")) {
                setSafe(rec, idxModel, newModel);
                saveSalesFile(header, rows);
                System.out.println("Sales information updated successfully.");
            }

        } else if (opt == 3) {
            int newQty = readInt("Enter New Quantity: ");

            if (readYes("Confirm Update? (Y/N): ")) {
                setSafe(rec, idxQty, String.valueOf(newQty));
                saveSalesFile(header, rows);
                System.out.println("Sales information updated successfully.");
            }

        } else if (opt == 4) {
            // Option 4 = Edit Total (as per instruction)
            System.out.print("Enter New Total: ");
            String newTotal = sc.nextLine().trim();
            if (readYes("Confirm Update? (Y/N): ")) {
                setSafe(rec, idxTotal, newTotal);   // update Total column
                saveSalesFile(header, rows);        // save back to CSV
                System.out.println("Sales information updated successfully.");
            }

        } else if (opt == 5) {
            // Option 5 = Edit Transaction Method
            System.out.print("Enter New Transaction Method: ");
            String newMethod = sc.nextLine().trim();

            if (readYes("Confirm Update? (Y/N): ")) {
                setSafe(rec, idxMethod, newMethod); // update Transaction Method column
                saveSalesFile(header, rows);        // save back to CSV
                System.out.println("Sales information updated successfully.");
            }
        }


        } else {
            System.out.println("Invalid choice.");
        }
    }

    // ========= Stock helpers (no sb, simple switch) =========

    // CALLING OTHER CLASS:
    // Model fields c60..c69 are from DataClass.Model
    private int getStock(Model m, int outletIdx) {
        switch (outletIdx) {
            case 0: return m.c60;
            case 1: return m.c61;
            case 2: return m.c62;
            case 3: return m.c63;
            case 4: return m.c64;
            case 5: return m.c65;
            case 6: return m.c66;
            case 7: return m.c67;
            case 8: return m.c68;
            case 9: return m.c69;
            default: return 0;
        }
    }

    private void setStock(Model m, int outletIdx, int newStock) {
        switch (outletIdx) {
            case 0: m.c60 = newStock; break;
            case 1: m.c61 = newStock; break;
            case 2: m.c62 = newStock; break;
            case 3: m.c63 = newStock; break;
            case 4: m.c64 = newStock; break;
            case 5: m.c65 = newStock; break;
            case 6: m.c66 = newStock; break;
            case 7: m.c67 = newStock; break;
            case 8: m.c68 = newStock; break;
            case 9: m.c69 = newStock; break;
        }
    }

    // ========= CSV helpers =========

    private int findCol(String[] header, String colName) {
        for (int i = 0; i < header.length; i++) {
            if (header[i].trim().equalsIgnoreCase(colName)) return i;
        }
        return -1;
    }

    private String getSafe(String[] row, int idx) {
        if (idx < 0  idx >= row.length) return "Unknown";
        return row[idx].trim();
    }

    private void setSafe(String[] row, int idx, String value) {
        if (idx < 0  idx >= row.length) return;
        row[idx] = value;
    }

    private void ensureMethodColumnExistsOrWarn(int idxMethod) {
        // If your CSV does not have transaction method column, editing won't work.
        // You can ignore this if your team already added the column.
        if (idxMethod == -1) {
            System.out.println("Warning: Transaction Method column not found in CSV header.");
            System.out.println("Ask your Sales module team to include TransactionMethod column.");
        }
    }

    private void saveSalesFile(String[] header, ArrayList<String[]> rows) {
        // CALLING OTHER CLASS:
        // PrintWriter and FileWriter are from java.io
        try (PrintWriter pw = new PrintWriter(new FileWriter(FILE_SALES))) {
            pw.println(String.join(",", header));
            for (String[] r : rows) {
                pw.println(String.join(",", r));
            }
        } catch (IOException e) {
            System.out.println("Error saving sales file: " + e.getMessage());
        }
    }
}