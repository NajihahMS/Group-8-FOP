import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Reads from "sales_history.csv"
 * EXTENDS Info
 */
public class FilterSortSalesHistory extends Info {

    // initialize FILE_SALE to the filename
    private static final String FILE_SALES = "sales_history.csv";   // fixed value

    // yyyy-MM-dd (e.g. 2025-10-13)
    private static final DateTimeFormatter DF = DateTimeFormatter.ofPattern("yyyy-MM-dd");  // fix value

    // constructor
    public FilterSortSalesHistory(String employeeName, int outletIndex) {
        super(employeeName, outletIndex);
    }

    @Override   //from the parent class
    public void run() {
        System.out.println("\n=== Filter & Sort Sales History ===");

        // 1) Load all sales rows from CSV
        List<SalesRow> all = loadSalesFromCSV();    // load from dataStateLoad class

        if (all.isEmpty()) {
            System.out.println("No sales records found.");
            return;
        }

        // 2) Ask user for date range
        LocalDate start = readDate("Enter start date (yyyy-MM-dd): ");
        LocalDate end   = readDate("Enter end date   (yyyy-MM-dd): ");

        // If user typed end earlier than start, swap so it still works
        if (end.isBefore(start)) {
            LocalDate temp = start;
            start = end;
            end = temp;
        }

        // 3) Filter list by date range (inclusive)
        List<SalesRow> filtered = filterByDateRange(all, start, end);

        if (filtered.isEmpty()) {
            System.out.println("No transactions between " + start + " and " + end + ".");
            return;
        }

        // 4) Ask sorting option
        while (true) {
            System.out.println("\nSort by:");
            System.out.println("1. Date (Ascending)");
            System.out.println("2. Date (Descending)");
            System.out.println("3. Amount (Lowest -> Highest)");
            System.out.println("4. Amount (Highest -> Lowest)");
            System.out.println("5. Customer Name (A-Z)");
            System.out.println("0. Back");
            System.out.print("Choose: ");

            int opt = parseIntSafe(sc.nextLine());

            if (opt == 0) return;

            // bubble sort
            // can change to Collections.sort() later
            sortSales(filtered, opt);

            // 5) Print table + cumulative total
            printTable(filtered, start, end);

            System.out.println("\n(You can choose another sort option, or 0 to exit.)");
        }
    }

    // =========================================================
    //  A) Read + Parse CSV
    // =========================================================

    /**
     * Load all sales records from CSV file.
     * do not call StorageSystem.createFileIfNotExists() here
     * because that method is private in StorageSystem.
     * only READ. If file doesn't exist, it will just return empty list.
     */
    private List<SalesRow> loadSalesFromCSV() {
        List<SalesRow> list = new ArrayList<>();

        File f = new File(FILE_SALES);
        if (!f.exists()) return list;

        try (BufferedReader br = new BufferedReader(new FileReader(f))) {

            String header = br.readLine();
            if (header == null) return list;

            // Build a column name -> index map
            // makes the code still work even if columns change order
            String[] cols = header.split("\\s*,\\s*");
            Map<String, Integer> idx = new HashMap<>();
            for (int i = 0; i < cols.length; i++) {
                idx.put(cols[i].trim().toLowerCase(), i);
            }

            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;

                String[] row = line.split("\\s*,\\s*");

                SalesRow r = new SalesRow();

                // Required columns (based on StorageSystem header):
                r.dateStr   = getCol(row, idx, "date", 0, "");
                r.time      = getCol(row, idx, "time", 1, "");
                r.empId     = getCol(row, idx, "empid", 2, "");
                r.customer  = getCol(row, idx, "customer", 3, "");
                r.model     = getCol(row, idx, "model", 4, "");
                r.qty       = parseIntSafe(getCol(row, idx, "qty", 5, "0"));
                r.total     = parseDoubleSafe(getCol(row, idx, "total", 6, "0"));

                // Optional column (incase someone later added it):
                r.method = getCol(row, idx, "transaction method", -1,
                         getCol(row, idx, "transactionmethod", -1, ""));

                // Convert date string to LocalDate for filtering/sorting
                r.date = parseDateSafe(r.dateStr);

                // Only add valid dated row 
                if (r.date != null) list.add(r);    // to avoid crash if bad data
            }

        } catch (IOException e) {
            System.out.println("Error reading sales file: " + e.getMessage());
        }

        return list;
    }

    /**
     * Helper: get a column value safely.
     *   idx map uses header names
     *   fallbackIndex is used if header name not found
     */
    private String getCol(String[] row, Map<String,Integer> idx,
                          String key, int fallbackIndex, String def) {

        Integer i = idx.get(key.toLowerCase());

        if (i != null && i >= 0 && i < row.length) return row[i].trim();

        if (fallbackIndex >= 0 && fallbackIndex < row.length) return row[fallbackIndex].trim();

        return def;
    }

    // =========================================================
    //  B) Filter
    // =========================================================

    private List<SalesRow> filterByDateRange(List<SalesRow> all, LocalDate start, LocalDate end) {
        List<SalesRow> out = new ArrayList<>();
        for (SalesRow r : all) {
            // inclusive range check
            if ((r.date.isEqual(start) || r.date.isAfter(start)) &&
                (r.date.isEqual(end)   || r.date.isBefore(end))) {
                out.add(r);
            }
        }
        return out;
    }

    // =========================================================
    //  C) Sort (Bubble Sort for easy explanation)
    // =========================================================

    /**
     * Bubble sort based on selected option.
     * Why bubble sort?
     * - Very easy to explain in class/report
     * - Small dataset (sales history) is fine for bubble sort
     */
    private void sortSales(List<SalesRow> list, int opt) {
        int n = list.size();

        // Bubble sort: compare neighbor items and swap if wrong order
        for (int pass = 0; pass < n - 1; pass++) {
            for (int i = 0; i < n - 1 - pass; i++) {

                SalesRow a = list.get(i);
                SalesRow b = list.get(i + 1);

                // If true => swap needed
                if (shouldSwap(a, b, opt)) {
                    list.set(i, b);
                    list.set(i + 1, a);
                }
            }
        }
    }

    /**
     * Decide whether to swap two rows (a and b) depending on sort option.
     */
    private boolean shouldSwap(SalesRow a, SalesRow b, int opt) {
        switch (opt) {
            case 1: // Date Asc
                return a.date.isAfter(b.date);

            case 2: // Date Desc
                return a.date.isBefore(b.date);

            case 3: // Amount Low -> High
                return a.total > b.total;

            case 4: // Amount High -> Low
                return a.total < b.total;

            case 5: // Customer A-Z
                return a.customer.compareToIgnoreCase(b.customer) > 0;

            default:
                // if user typed weird option, just keep original order
                return false;
        }
    }

    // =========================================================
    //  D) Print Table + Cumulative Total
    // =========================================================

    private void printTable(List<SalesRow> list, LocalDate start, LocalDate end) {

        System.out.println("\nTransactions from " + start + " to " + end + ":");
        System.out.println("---------------------------------------------------------------------------------------------");
        System.out.printf("%-12s %-10s %-8s %-20s %-12s %-5s %-10s %-15s%n",
                "Date", "Time", "EmpID", "Customer", "Model", "Qty", "Total", "Method");
        System.out.println("---------------------------------------------------------------------------------------------");

        double sum = 0;

        for (SalesRow r : list) {
            sum += r.total;

            // If method is empty, show "-"
            String methodShow = (r.method == null || r.method.trim().isEmpty()) ? "-" : r.method;

            System.out.printf("%-12s %-10s %-8s %-20s %-12s %-5d RM%-9.2f %-15s%n",
                    r.dateStr, r.time, r.empId, cut(r.customer, 20), r.model, r.qty, r.total, cut(methodShow, 15));
        }

        System.out.println("---------------------------------------------------------------------------------------------");
        System.out.printf("Total cumulative sales in range: RM%.2f%n", sum);
    }

    // Cut long strings so table stays nice
    private String cut(String s, int max) {
        if (s == null) return "";
        if (s.length() <= max) return s;
        return s.substring(0, max - 3) + "...";
    }

    // =========================================================
    //  E) Input + Safe Parsing
    // =========================================================

    private LocalDate readDate(String prompt) {
        while (true) {
            System.out.print(prompt);
            String s = sc.nextLine().trim();
            LocalDate d = parseDateSafe(s);
            if (d != null) return d;
            System.out.println("Invalid date format. Please use yyyy-MM-dd (example: 2025-10-13).");
        }
    }

    private LocalDate parseDateSafe(String s) {
        try {
            return LocalDate.parse(s.trim(), DF);
        } catch (Exception e) {
            return null;
        }
    }

    private int parseIntSafe(String s) {
        try {
            return Integer.parseInt(s.trim());
        } catch (Exception e) {
            return 0;
        }
    }

    private double parseDoubleSafe(String s) {
        try {
            return Double.parseDouble(s.trim());
        } catch (Exception e) {
            return 0.0;
        }
    }

    // =========================================================
    //  Data Holder (1 row from CSV)
    // =========================================================
    private static class SalesRow {
        LocalDate date;      // for filter/sort
        String dateStr;      // for display
        String time;
        String empId;
        String customer;
        String model;
        int qty;
        double total;
        String method;       // optional column
    }
}
