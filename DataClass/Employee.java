package DataClass;

public class Employee {
    private String employeeID;
    private String employeeName;
    private String role;
    private String password;

    public Employee(String employeeID, String employeeName, String role, String password) {
        this.employeeID = employeeID;
        this.employeeName = employeeName;
        this.role = role;
        this.password = password;
    }
    // Getters are required for your Login System later
    public String getID() { return employeeID; }
    public String getPassword() { return password; }
    public String getName() { return employeeName; }
    public String getRole() { return role; }
    
    // Helper to format for saving back to CSV
    public String toCSV() {
        return employeeID + "," + employeeName + "," + role + "," + password;
}
}