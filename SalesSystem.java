package Nabilah;

import Najihah.dataStateLoad;
import java.util.ArrayList;

public class Sale {

    private String modelName;
    private int quantity;
    private double unitPrice;

    private static dataStateLoad loader;

    static {
        loader = new dataStateLoad();
        loader.loadModels();
    }

    public Sale(String modelName, int quantity) {
        this.modelName = modelName;
        this.quantity = quantity;
        this.unitPrice = resolveUnitPrice(modelName);
    }

    
    public String getModelName() { return modelName; }
    public int getQuantity() { return quantity; }
    public double getUnitPrice() { return unitPrice; }
    public double getTotalPrice() { return unitPrice * quantity; }
}
