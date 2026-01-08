package GoldenHour;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import pkgfinal.WatchModel;

public class SalesSystem {

    private ArrayList<Customer> customers;
    private ArrayList<WatchModel> outletInventory;

    public SalesSystem(ArrayList<WatchModel> outletInventory) {
        this.customers = new ArrayList<>();
        this.outletInventory = outletInventory;
    }

    public void addCustomer(Customer newCustomer) {
        customers.add(newCustomer);
        for (Sale item : newCustomer.getPurchaseList()) updateStock(item);
    }

    private void updateStock(Sale item) {
        for (WatchModel wm : outletInventory) {
            if (wm.getName().equalsIgnoreCase(item.getModelName())) {
                int remain = wm.getQuantity() - item.getQuantity();
                if (remain < 0) remain = 0;
                wm.setQuantity(remain);
                return;
            }
        }
    }

    public String generateReceipt(Customer customer) {
        LocalDateTime now = LocalDateTime.now();
        String receipt = "Date/Time: " + now + "\n";
        receipt += "Customer: " + customer.getCustomerName() + "\n";
        receipt += "Handled By: " + customer.getEmployeeName() + "\n";
        receipt += "Payment Method: " + customer.getPaymentMethod() + "\n";
        receipt += "Items:\n";

        for (Sale s : customer.getPurchaseList()) {
            receipt += "- " + s.getModelName() +
                       " | Qty: " + s.getQuantity() +
                       " | Unit: " + s.getUnitPrice() +
                       " | Total: " + s.getTotalPrice() + "\n";
        }

        receipt += "TOTAL PAYMENT: " + customer.getTotalPrice() + "\n";
        return receipt;
    }

    public void saveDailyTransactions() {
        LocalDateTime now = LocalDateTime.now();
        String filename = "transactions_" + now.toLocalDate() + ".csv";

        try (FileWriter fw = new FileWriter(filename, true)) {
            fw.write("DateTime,Customer,Employee,PaymentMethod,Item,Quantity,UnitPrice,TotalPrice\n");

            for (Customer c : customers) {
                for (Sale s : c.getPurchaseList()) {
                    fw.write(now + "," +
                             c.getCustomerName() + "," +
                             c.getEmployeeName() + "," +
                             c.getPaymentMethod() + "," +
                             s.getModelName() + "," +
                             s.getQuantity() + "," +
                             s.getUnitPrice() + "," +
                             s.getTotalPrice() + "\n");
                }
            }
        } catch (IOException e) {
            System.out.println("Transaction save failed.");
        }
    }

    public ArrayList<Customer> getCustomers() {
        return customers;
    }
}
