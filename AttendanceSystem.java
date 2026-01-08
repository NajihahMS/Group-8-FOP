import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class AttendanceSystem {

    private LocalTime clockInTime;

    public void clockIn(LoginSystem user) {
        clockInTime = LocalTime.now();

        System.out.println("=== Attendance Clock In ===");
        System.out.println("Employee ID: " + user.getId());
        System.out.println("Name: " + user.getName());
        System.out.println("Outlet: " + user.getOutlet());
        System.out.println();

        System.out.println("Clock In Successful!");
        System.out.println("Date: " + LocalDate.now());
        System.out.println("Time: " + formatTime(clockInTime));
        System.out.println();
    }

    public void clockOut(LoginSystem user) {
        LocalTime clockOutTime = LocalTime.now();
        double hoursWorked =
                Duration.between(clockInTime, clockOutTime).toMinutes() / 60.0;

        System.out.println("=== Attendance Clock Out ===");
        System.out.println("Employee ID: " + user.getId());
        System.out.println("Name: " + user.getName());
        System.out.println("Outlet: " + user.getOutlet());
        System.out.println();

        System.out.println("Clock Out Successful!");
        System.out.println("Date: " + LocalDate.now());
        System.out.println("Time: " + formatTime(clockOutTime));
        System.out.printf("Total Hours Worked: %.1f hours%n", hoursWorked);
        System.out.println();
    }

    private String formatTime(LocalTime time) {
        DateTimeFormatter f = DateTimeFormatter.ofPattern("hh:mm a");
        return time.format(f).toLowerCase();
    }
}
