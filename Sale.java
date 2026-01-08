public class Sale {

    private String modelName;
    private int quantity;
    private double unitPrice;

    public Sale(String modelName, int quantity) {
        this.modelName = modelName;
        this.quantity = quantity;
        this.unitPrice = resolveUnitPrice(modelName);
    }

    private double resolveUnitPrice(String modelName) {
        for (Model m : StorageSystem.allModels) {
            if (m.getName().equalsIgnoreCase(modelName)) {
                return m.getPrice();
            }
        }
        return 0;
    }

    public String getModelName() { return modelName; }
    public int getQuantity() { return quantity; }
    public double getUnitPrice() { return unitPrice; }
    public double getTotalPrice() { return unitPrice * quantity; }
}
