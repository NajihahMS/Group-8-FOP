import java.io.*;
import java.util.*;
import DataClass.Model;
import DataClass.Outlet;

/**
 * CHILD CLASS: Search Information (Stock + Sales)
 * - Follows PDF output style for Search Stock and Search Sales
 * - Uses polymorphism via Info reference calling run()
 */
public class SearchInfo extends Info {

    private static final String FILE_SALES = "sales_history.csv"; // change if your team uses different file name

    public SearchInfo(String employeeName, int outletIndex) {
        super(employeeName, outletIndex);
    }

    @Override
    public void run() {
        while (true) {
            System.out.println("\n=== Search Information ===");
            System.out.println("1. Search Stock Information");
            System.out.println("2. Search Sales Information");
            System.out.println("0. Back");
            System.out.print("Choose: ");

            String choice = sc.nextLine().trim();

            if (choice.equals("1")) {
                searchStock();
            } else if (choice.equals("2")) {
                searchSales();
            } else if (choice.equals("0")) {
                return;
            } else {
                System.out.println("Invalid choice.");
            }
        }
    }

    // =========================
    // 1) Search Stock Information (PDF page 8)
    // =========================
    private void searchStock() {
        System.out.println("\n=== Search Stock Information ===");
        System.out.print("Search Model Name: ");
        String modelName = sc.nextLine().trim();

        System.out.println("Searching...");

        // CALLING OTHER CLASS:
        // StorageSystem.allModels is a shared in-memory list loaded at startup
        // (StorageSystem class)
        Model found = findModelByName(modelName);

        if (found == null) {
            System.out.println("No record found.");
            return;
        }

        // CALLING OTHER CLASS:
        // Model.getName() and Model.getPrice() come from DataClass.Model
        System.out.println("Model: " + found.getName());
        System.out.println("Unit Price: " + formatRM(found.getPrice()));
        System.out.println("Stock by Outlet:");

        // Print outlet stock in 4 per line like sample output
        // CALLING OTHER CLASS:
        // StorageSystem.allOutlets list comes from StorageSystem
        // Outlet.getName() comes from DataClass.Outlet
        int count = 0;
        for (int i = 0; i < StorageSystem.allOutlets.size(); i++) {

            Outlet o = StorageSystem.allOutlets.get(i); // from StorageSystem + DataClass.Outlet
            String outletLabel = outletShortName(o.getName()); // uses Outlet.getName()

            int stock = getStockByOutletIndex(found, i); // read Model.c60..c69 (DataClass.Model)

            System.out.print(outletLabel + ": " + stock);

            count++;

            // spacing / line break control (no StringBuilder)
            if (i != StorageSystem.allOutlets.size() - 1) {
                System.out.print(" ");
            }

            if (count % 4 == 0 && i != StorageSystem.allOutlets.size() - 1) {
                System.out.println(); // new line after every 4 outlets
            }
        }
        System.out.println(); // end line
    }

    // =========================
    // 2) Search Sales Information (PDF page 8)
    // =========================
    private void searchSales() {
        System.out.println("\n=== Search Sales Information ===");
        System.out.print("Search keyword: ");
        String keyword = sc.nextLine().trim();

        System.out.println("Searching...");

        SaleRecord rec = findFirstMatchingSale(keyword);

        if (rec == null) {
            System.out.println("No record found.");
            return;
        }

        // Output follows PDF sample structure
        System.out.println("Sales Record Found:");
        System.out.println("Date: " + rec.date + " Time: " + rec.time);
        System.out.println("Customer: " + rec.customer);
        System.out.println("Item(s): " + rec.model + " Quantity: " + rec.qty);
        System.out.println("Total: " + formatRM(rec.total));
        System.out.println("Transaction Method: " + rec.method);
        System.out.println("Employee: " + rec.employee);
        System.out.println("Status: Transaction verified.");
    }

    // ========= Helpers =========

    private Model findModelByName(String modelName) {
        // CALLING OTHER CLASS:
        // StorageSystem.allModels is from StorageSystem
        for (Model m : StorageSystem.allModels) {
            // CALLING OTHER CLASS:
            // Model.getName() is from DataClass.Model
            if (m.getName().equalsIgnoreCase(modelName)) {
                return m;
            }
        }
        return null;
    }

    /**
     * Read stock from Model based on outlet list index (0..9).
     * CALLING OTHER CLASS:
     * Model.c60..c69 are public fields in DataClass.Model.
     */
    private int getStockByOutletIndex(Model m, int idx) {
        switch (idx) {
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

    private String outletShortName(String fullName) {
        // Simple mapping to match PDF display style as close as possible
        String x = fullName.toLowerCase();
        if (x.contains("kuala lumpur city centre") || x.contains("klcc")) return "KLCC";
        if (x.contains("mid valley")) return "MidValley";
        if (x.contains("lalaport")) return "Lalaport";
        if (x.contains("kl east")) return "KL East";
        if (x.contains("nu sentral")) return "Nu Sentral";
        if (x.contains("pavilion")) return "Pavillion KL"; // matches sample spelling
        if (x.contains("mytown")) return "MyTown";
        return fullName; // fallback
    }

    // Represents one matched sales row
    private static class SaleRecord {
        String date, time, customer, model, method, employee;
        int qty;
        double total;
    }

    /**
     * Finds FIRST sales record matching keyword in ANY column.
     * Uses CSV file reading (java.io classes).
     */
    private SaleRecord findFirstMatchingSale(String keyword) {
        File f = new File(FILE_SALES);
        if (!f.exists()) return null;

        String key = keyword.toLowerCase();

        // CALLING OTHER CLASS:
        // BufferedReader/FileReader are from java.io
        try (BufferedReader br = new BufferedReader(new FileReader(f))) {

            String header = br.readLine();
            if (header == null) return null;

            // Build header index map (support flexible column order)
            String[] cols = header.split("\\s*,\\s*");
            Map<String, Integer> idx = new HashMap<>();
            for (int i = 0; i < cols.length; i++) {
                idx.put(cols[i].trim().toLowerCase(), i);
            }

            String line;
            while ((line = br.readLine()) != null) {
                String[] row = line.split("\\s*,\\s*");
                if (row.length == 0) continue;

                String joined = String.join(" ", row).toLowerCase();
                if (!joined.contains(key)) continue;

                SaleRecord r = new SaleRecord();
                r.date = getCol(row, idx, "date", 0, "");
                r.time = getCol(row, idx, "time", 1, "");
                r.customer = getCol(row, idx, "customer", -1, "Unknown");
                r.model = getCol(row, idx, "model", -1, "Unknown");
                r.qty = parseIntSafe(getCol(row, idx, "qty", -1, "0"));
                r.total = parseDoubleSafe(getCol(row, idx, "total", -1, "0"));

                // Support both "Transaction Method" and "TransactionMethod"
                r.method = getCol(row, idx, "transaction method", -1,
                          getCol(row, idx, "transactionmethod", -1, "Unknown"));

                r.employee = getCol(row, idx, "employee", -1, "Unknown");
                return r;
            }

        } catch (IOException e) {
            System.out.println("Error reading sales file: " + e.getMessage());
        }

        return null;
    }

    private String getCol(String[] row, Map<String, Integer> idx, String key, int fallbackIndex, String def) {
        Integer i = idx.get(key);
        if (i != null && i >= 0 && i < row.length) return row[i].trim();
        if (fallbackIndex >= 0 && fallbackIndex < row.length) return row[fallbackIndex].trim();
        return def;
    }

    private int parseIntSafe(String s) {
        try { return Integer.parseInt(s.trim()); }
        catch (Exception e) { return 0; }
    }

    private double parseDoubleSafe(String s) {
        try { return Double.parseDouble(s.trim()); }
        catch (Exception e) { return 0; }
    }
}
