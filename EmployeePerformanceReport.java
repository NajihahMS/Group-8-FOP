import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EmployeePerformanceReport {

    // Reference to the sales system
    private SalesSystem salesSystem;

    // Constructor receives SalesSystem object
    public EmployeePerformanceReport(SalesSystem salesSystem) {
        this.salesSystem = salesSystem;
    }

    // Generate and display employee performance report
    public void generateReport() {

        // Store performance data for each employee
        Map<String, EmployeeStats> performanceMap = new HashMap<>();

        // Collect sales data from all customers
        for (Customer c : salesSystem.getCustomers()) {

            String employee = c.getEmployeeName();   // Employee name
            double totalSales = c.getTotalPrice();   // Transaction amount

            // Get or create statistics object
            EmployeeStats stats =
                    performanceMap.getOrDefault(employee, new EmployeeStats());

            stats.totalSales += totalSales;  // Add sales
            stats.transactions++;            // Increase transaction count

            performanceMap.put(employee, stats); // Save to map
        }

        // Convert map to list for sorting
        List<Map.Entry<String, EmployeeStats>> sortedList =
                new ArrayList<>(performanceMap.entrySet());

        // Sort employees by highest sales first
        sortedList.sort((a, b) ->
                Double.compare(b.getValue().totalSales, a.getValue().totalSales));

        // Display report
        System.out.println("\n--- Employee Performance Report ---");
        System.out.printf("%-20s %-15s %-10s%n",
                "Employee", "Total Sales", "Transactions");

        for (Map.Entry<String, EmployeeStats> entry : sortedList) {
            System.out.printf("%-20s %-15.2f %-10d%n",
                    entry.getKey(),
                    entry.getValue().totalSales,
                    entry.getValue().transactions);
        }
    }

    // Helper class to store performance statistics
    private static class EmployeeStats {
        double totalSales = 0;
        int transactions = 0;
    }
}
