public class WatchModel { //blueprint
    private String modelName;
    private double unitPrice;
    private int quantity; // For the KLCC outlet (C60)

    //constructor
    public WatchModel(String modelName, double unitPrice, int quantity) {
        this.modelName = modelName;
        this.unitPrice = unitPrice;
        this.quantity = quantity;
    }

    // Getters - used to read info for the tally [cite: 79]
    public String getName() { 
        return modelName; 
    }
    public int getQuantity() { 
        return quantity; 
    }

    // Setter - used to update stock during "Stock In/Out" [cite: 92]
    public void setQuantity(int newQty) { 
        this.quantity = newQty; 
    }
}
