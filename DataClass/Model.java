package DataClass;

public class Model{
    private String modelName;
    private double price; // Changed to double for Math
    // Stocks for specific outlets (Changed to int for Math)
    public int c60, c61, c62, c63, c64, c65, c66, c67, c68, c69;

    public Model(String model, double price, int[] stocks) {
        this.modelName = model;
        this.price = price;
        // Assigning array to individual fields
        this.c60 = stocks[0]; this.c61 = stocks[1]; this.c62 = stocks[2];
        this.c63 = stocks[3]; this.c64 = stocks[4]; this.c65 = stocks[5];
        this.c66 = stocks[6]; this.c67 = stocks[7]; this.c68 = stocks[8];
        this.c69 = stocks[9];
    }

    public String getName() { return modelName; }
    public double getPrice() { return price; }
    
    // Crucial for saving later
    public String toCSV() {
        return modelName + "," + price + "," + c60 + "," + c61 + "," + c62 + 
               "," + c63 + "," + c64 + "," + c65 + "," + c66 + "," + c67 + "," + c68 + "," + c69;
    }
}
    