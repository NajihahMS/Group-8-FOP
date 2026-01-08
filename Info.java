import java.util.Scanner;

/
 * PARENT CLASS (Base class)
 * Child classes like SearchInfo and EditInfo will EXTEND this class
 * and OVERRIDE run() (polymorphism).
 */
public abstract class Info {

    // Scanner is from java.util (built-in Java library)
    protected Scanner sc = new Scanner(System.in);

    // Common info for any module (Search/Edit/etc.)
    protected String employeeName;
    protected int outletIndex; // example: 0=C60, 1=C61, ... 9=C69

    public Info(String employeeName, int outletIndex) {
        this.employeeName = employeeName;
        this.outletIndex = outletIndex;
    }

    /
     * Polymorphism method:
     * Every child class MUST override run().
     * When you do:
     *    Info ref = new SearchInfo(...);
     *    ref.run();
     * Java will call SearchInfo.run() at runtime (polymorphism).
     */
    public abstract void run();

    /
     * Read an integer safely (keeps asking until user enters valid number)
     */
    protected int readInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = sc.nextLine(); // Scanner method
            try {
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Invalid number. Try again.");
            }
        }
    }

    /
     * Read Y/N safely
     */
    protected boolean readYes(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = sc.nextLine().trim();

            if (input.equalsIgnoreCase("Y")) return true;
            if (input.equalsIgnoreCase("N")) return false;

            System.out.println("Please enter Y or N.");
        }
    }

    /**
     * Format RM like sample output:
     * RM349 (not RM349.00)
     * This helps match the PDF output style for Unit Price and Total :contentReference[oaicite:0]{index=0}
     */
    protected String formatRM(double amount) {
        // If it's a whole number, print without decimal
        if (amount == (long) amount) {
            return "RM" + (long) amount;
        }
        // Otherwise print 2 decimals
        return "RM" + String.format("%.2f", amount);
    }
}