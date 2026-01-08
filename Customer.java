package GoldenHour;

import java.util.ArrayList;

public class Customer {

    private String customerName;
    private String paymentMethod;
    private String employeeName;
    private ArrayList<Sale> purchaseList;

    public Customer(String customerName, String paymentMethod, String employeeName) {
        this.customerName = customerName;
        this.paymentMethod = paymentMethod;
        this.employeeName = employeeName;
        this.purchaseList = new ArrayList<>();
    }

    public void addPurchase(Sale newItem) {
        if (newItem != null) purchaseList.add(newItem);
    }

    public ArrayList<Sale> getPurchaseList() {
        return purchaseList;
    }

    public String getCustomerName() {
        return customerName;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public String getEmployeeName() {
        return employeeName;
    }

    public double getTotalPrice() {
        double total = 0;
        for (Sale item : purchaseList) total += item.getTotalPrice();
        return total;
    }
}
