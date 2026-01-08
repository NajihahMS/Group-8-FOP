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

    private double resolveUnitPrice(String modelName) {
        ArrayList<dataStateLoad.Model> modelList = loader.getModels();
        for (dataStateLoad.Model m : modelList) {
            if (m.model.equalsIgnoreCase(modelName)) {
                try { return Double.parseDouble(m.price); }
                catch (Exception e) { return 0; }
            }
        }
        return 0;
    }
    public String getModelName() { return modelName; }
    public int getQuantity() { return quantity; }
    public double getUnitPrice() { return unitPrice; }
    public double getTotalPrice() { return unitPrice * quantity; }
}
