package DataClass;

public class Outlet{
    private String outletCode;
    private String outletName;

    public Outlet(String outletCode, String outletName) {
        this.outletCode = outletCode;
        this.outletName = outletName;
    }
    public String getCode() { return outletCode; }
    public String getName() { return outletName; }
}
