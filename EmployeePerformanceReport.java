import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EmployeePerformanceReport {

    private SalesSystem salesSystem;

    // Constructor: takes the existing SalesSystem
    public EmployeePerformanceReport(SalesSystem salesSystem) {
        this.salesSystem = salesSystem;
    }

    // Generates and prints the employee performance report
    public void generateReport() {
        // Map to store total sales and number of transactions per employee
        Map<String, EmployeeStats> performanceMap = new HashMap<>();

        // Loop through all customers to collect stats
        for (Customer c : salesSystem.getCustomers()) {
            String employee = c.getEmployeeName();
            double totalSales = c.getTotalPrice();

            EmployeeStats stats = performanceMap.getOrDefault(employee, new EmployeeStats());
            stats.totalSales += totalSales; // accumulate sales
            stats.transactions++;           // count transaction
            performanceMap.put(employee, stats);
        }

        // Convert map entries to a list for sorting
        List<Map.Entry<String, EmployeeStats>> sortedList = new ArrayList<>(performanceMap.entrySet());
        sortedList.sort((a, b) -> Double.compare(b.getValue().totalSales, a.getValue().totalSales)); // descending order

        // Print the report
        System.out.println("\n--- Employee Performance Report ---");
        System.out.printf("%-20s %-15s %-10s%n", "Employee", "Total Sales", "Transactions");
        for (Map.Entry<String, EmployeeStats> entry : sortedList) {
            System.out.printf("%-20s %-15.2f %-10d%n",
                    entry.getKey(),
                    entry.getValue().totalSales,
                    entry.getValue().transactions);
        }
    }

    // Helper class to hold stats per employee
    private static class EmployeeStats {
        double totalSales = 0;
        int transactions = 0;
    }
}
