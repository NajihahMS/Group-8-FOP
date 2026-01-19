import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import DataClass.Employee;

public class AttendanceSystem {

    // Store clock-in time for the current user
    private static LocalTime clockInTime;

    // Record employee starting time
    public static void clockIn(Employee user) {

        clockInTime = LocalTime.now(); // Save current time

        System.out.println("=== Attendance Clock In ===");
        System.out.println("Employee ID: " + user.getID());
        System.out.println("Name: " + user.getName());
        System.out.println();

        System.out.println("Clock In Successful!");
        System.out.println("Date: " + LocalDate.now());
        System.out.println("Time: " + formatTime(clockInTime));
        System.out.println();
    }

    // Record employee ending time and calculate working hours
    public static void clockOut(Employee user) {

        // Prevent clock out before clock in
        if (clockInTime == null) {
            System.out.println("Error: You must clock in first.");
            return;
        }

        LocalTime clockOutTime = LocalTime.now(); // Save end time

        // Calculate hours worked
        double hoursWorked =
                Duration.between(clockInTime, clockOutTime).toMinutes() / 60.0;

        System.out.println("=== Attendance Clock Out ===");
        System.out.println("Employee ID: " + user.getID());
        System.out.println("Name: " + user.getName());
        System.out.println();

        System.out.println("Clock Out Successful!");
        System.out.println("Date: " + LocalDate.now());
        System.out.println("Time: " + formatTime(clockOutTime));
        System.out.printf("Total Hours Worked: %.1f hours%n", hoursWorked);
        System.out.println();
    }

    // Format time to readable form (e.g. 08:30 am)
    private static String formatTime(LocalTime time) {
        DateTimeFormatter f = DateTimeFormatter.ofPattern("hh:mm a");
        return time.format(f).toLowerCase();
    }
}
