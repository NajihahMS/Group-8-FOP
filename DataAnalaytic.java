import java.util.ArrayList;
import java.util.HashMap; // essential for grouping & totaling data
import java.util.Map;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalFields;
import java.time.temporal.WeekFields;
import java.util.Locale;

public class DataAnalytic {
   private ArrayList<Customer> customerRecords; //store customer list

    // Constructor 
    public DataAnalytic(ArrayList<Customer> records) {
        this.customerRecords = records;
    }

    public void displaySummary() {
        if (customerRecords == null || customerRecords.isEmpty()) {
            System.out.println("No records found to analyze.");
            return;
        }
      
        double grandTotalRevenue = 0; //initialised total money made
        
        // HashMaps to group revenue by different time periods
        HashMap<String, Double> dailyMap = new HashMap<>();   // Key: YYYY-MM-DD
        HashMap<String, Double> weeklyMap = new HashMap<>();  // Key: YYYY-WeekNumber
        HashMap<String, Double> monthlyMap = new HashMap<>(); // Key: YYYY-MM
        
        // HashMap to count how many units of each watch model were sold
        HashMap<String, Integer> productTally = new HashMap<>();

        // week calculation
        WeekFields weekFields = WeekFields.of(Locale.getDefault());

        // Loop through every customer record 
        for (Customer customer : customerRecords) {
            // add customer total to grand total revenue
            double orderTotal = customer.getTotalPrice();
            grandTotalRevenue += orderTotal;

            // Extract the date of the sale
            java.time.LocalDate date = customer.getSaleDateTime().toLocalDate();
            
            // Format keys for Daily, Weekly, and Monthly grouping
            String dayKey = date.toString(); // e.g., "2026-01-09"
            String weekKey = date.getYear() + "-W" + date.get(weekFields.weekOfWeekBasedYear());
            String monthKey = date.getYear() + "-" + date.getMonthValue();

            // Update revenue for that specific day
            dailyMap.put(dayKey, dailyMap.getOrDefault(dayKey, 0.0) + orderTotal);
            
            // Update revenue for that specific week
            weeklyMap.put(weekKey, weeklyMap.getOrDefault(weekKey, 0.0) + orderTotal);
            
            // Update revenue for that specific month
            monthlyMap.put(monthKey, monthlyMap.getOrDefault(monthKey, 0.0) + orderTotal);

            // Loop through each item in the customer's purchase list to tally quantities
            for (Sale sale : customer.getPurchaseList()) {
                String model = sale.getModelName();
                productTally.put(model, productTally.getOrDefault(model, 0) + sale.getQuantity());
            }
        }

        // --- Output Section ---
        System.out.println("\n========= FINANCIAL PERFORMANCE =========");
        System.out.printf("Grand Total Revenue:     $%.2f\n", grandTotalRevenue);
        
        // per Day
        System.out.println("\n--- Sales Per Day ---");
        dailyMap.forEach((k, v) -> System.out.printf("Date %s: $%.2f\n", k, v));

        // per Week
        System.out.println("\n--- Sales Per Week ---");
        weeklyMap.forEach((k, v) -> System.out.printf("Week %s: $%.2f\n", k, v));

        // per Month
        System.out.println("\n--- Sales Per Month ---");
        monthlyMap.forEach((k, v) -> System.out.printf("Month %s: $%.2f\n", k, v));

        // most sold model
        displayTopProduct(productTally);
        
        // simple visual chart for product performance
        displaySimpleChart(productTally);
    }

    private void displayTopProduct(HashMap<String, Integer> tally) { // string: model name, int: quantities sold
        String topModel = "N/A";
        int max = 0;
        for (Map.Entry<String, Integer> entry : tally.entrySet()) {
            if (entry.getValue() > max) {
                max = entry.getValue();
                topModel = entry.getKey();
            }
        }
        System.out.println("\n========================================");
        System.out.println("MOST SOLD MODEL: " + topModel + " (" + max + " units)");
        System.out.println("========================================\n");
    }

    private void displaySimpleChart(HashMap<String, Integer> tally) {
        System.out.println("Product Sales Chart (Units Sold):");
        tally.forEach((model, qty) -> {
            System.out.printf("%-15s | ", model);
            // Print one asterisk for every unit sold (Visual Chart requirement)
            for (int i = 0; i < qty; i++) System.out.print("*");
            System.out.println(" (" + qty + ")");
        });
    }
}
