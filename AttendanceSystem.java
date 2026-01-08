import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import DataClass.Employee; // Added import

public class AttendanceSystem {

    private static LocalTime clockInTime; // Made static

    // Changed parameter to Employee to match MAIN and MainGUI
    public static void clockIn(Employee user) { 
        clockInTime = LocalTime.now();

        System.out.println("=== Attendance Clock In ===");
        System.out.println("Employee ID: " + user.getID()); // Fixed getter
        System.out.println("Name: " + user.getName());
        // Removed getOutlet() as Employee class doesn't have it
        System.out.println();

        System.out.println("Clock In Successful!");
        System.out.println("Date: " + LocalDate.now());
        System.out.println("Time: " + formatTime(clockInTime));
        System.out.println();
    }

    // Changed parameter to Employee to match MAIN and MainGUI
    public static void clockOut(Employee user) {
        if (clockInTime == null) {
            System.out.println("Error: You must clock in first.");
            return;
        }
        
        LocalTime clockOutTime = LocalTime.now();
        double hoursWorked =
                Duration.between(clockInTime, clockOutTime).toMinutes() / 60.0;

        System.out.println("=== Attendance Clock Out ===");
        System.out.println("Employee ID: " + user.getID()); // Fixed getter
        System.out.println("Name: " + user.getName());
        System.out.println();

        System.out.println("Clock Out Successful!");
        System.out.println("Date: " + LocalDate.now());
        System.out.println("Time: " + formatTime(clockOutTime));
        System.out.printf("Total Hours Worked: %.1f hours%n", hoursWorked);
        System.out.println();
    }

    private static String formatTime(LocalTime time) {
        DateTimeFormatter f = DateTimeFormatter.ofPattern("hh:mm a");
        return time.format(f).toLowerCase();
    }
}
