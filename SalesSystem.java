import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;

public class SalesSystem {

    private ArrayList<Customer> customers;
    private ArrayList<Model> outletInventory;

    public SalesSystem(ArrayList<Model> outletInventory) {
        this.customers = new ArrayList<>();
        this.outletInventory = outletInventory;
    }

    // Adds a customer and processes their sale
    public void addCustomer(Customer newCustomer) {
        customers.add(newCustomer);

        // Deduct stock for each item
        for (Sale item : newCustomer.getPurchaseList()) {
            updateStock(item);
        }

        // Generate receipt (optional: can save to disk)
        String receipt = generateReceipt(newCustomer);
        System.out.println(receipt);

        // Log daily transactions to CSV
        saveDailyTransactionsForCustomer(newCustomer);
    }

    private void updateStock(Sale item) {
        for (Model m : outletInventory) {
            if (m.getName().equalsIgnoreCase(item.getModelName())) {
                m.deductStock(item.getQuantity());
                StorageSystem.saveAllModels();  // Persist stock changes
                return;
            }
        }
    }

    public String generateReceipt(Customer customer) {
        LocalDateTime now = LocalDateTime.now();
        StringBuilder receipt = new StringBuilder();
        receipt.append("DATE/TIME: ").append(now).append("\n");
        receipt.append("Customer: ").append(customer.getCustomerName()).append("\n");
        receipt.append("Handled By: ").append(customer.getEmployeeName()).append("\n");
        receipt.append("Payment Method: ").append(customer.getPaymentMethod()).append("\n");
        receipt.append("Items:\n");

        for (Sale s : customer.getPurchaseList()) {
            receipt.append("- ").append(s.getModelName())
                   .append(" | Qty: ").append(s.getQuantity())
                   .append(" | Unit: ").append(s.getUnitPrice())
                   .append(" | Total: ").append(s.getTotalPrice())
                   .append("\n");
        }

        receipt.append("TOTAL PAYMENT: ").append(customer.getTotalPrice()).append("\n");
        return receipt.toString();
    }

    // Logs sales for a single customer into today's transaction file
    private void saveDailyTransactionsForCustomer(Customer customer) {
        LocalDate today = LocalDate.now();
        String filename = "transactions_" + today + ".csv";

        try (FileWriter fw = new FileWriter(filename, true)) {

            // Add header if file is new
            java.io.File f = new java.io.File(filename);
            if (f.length() == 0) {
                fw.write("DateTime,Customer,Employee,PaymentMethod,Item,Quantity,UnitPrice,TotalPrice\n");
            }

            LocalDateTime now = LocalDateTime.now();
            for (Sale s : customer.getPurchaseList()) {
                fw.write(now + "," +
                         customer.getCustomerName() + "," +
                         customer.getEmployeeName() + "," +
                         customer.getPaymentMethod() + "," +
                         s.getModelName() + "," +
                         s.getQuantity() + "," +
                         s.getUnitPrice() + "," +
                         s.getTotalPrice() + "\n");
            }

        } catch (IOException e) {
            System.out.println("Failed to save daily transactions: " + e.getMessage());
        }
    }

    public ArrayList<Customer> getCustomers() { return customers; }
}
