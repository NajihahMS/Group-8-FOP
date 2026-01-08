import java.util.ArrayList;
import java.time.LocalDateTime; // Required for date/time functionality

public class Customer {

    private String customerName;
    private String paymentMethod;
    private String employeeName;
    private LocalDateTime saleDateTime; // New field to track when the sale happened
    private ArrayList<Sale> purchaseList;

    public Customer(String customerName, String paymentMethod, String employeeName) {
        this.customerName = customerName;
        this.paymentMethod = paymentMethod;
        this.employeeName = employeeName;
        this.purchaseList = new ArrayList<>();
        // Automatically sets the sale time to the moment the Customer object is created
        this.saleDateTime = LocalDateTime.now(); 
    }

    // Getter for DataAnalytic to use
    public LocalDateTime getSaleDateTime() {
        return saleDateTime;
    }

    public void addPurchase(Sale newItem) {
        if (newItem != null) purchaseList.add(newItem);
    }

    public ArrayList<Sale> getPurchaseList() { return purchaseList; }
    public String getCustomerName() { return customerName; }
    public String getPaymentMethod() { return paymentMethod; }
    public String getEmployeeName() { return employeeName; }

    public double getTotalPrice() {
        double total = 0;
        for (Sale item : purchaseList) total += item.getTotalPrice();
        return total;
    }
}
