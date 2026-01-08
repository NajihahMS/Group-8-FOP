

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import DataClass.Employee;

public class AttendanceSystem {

    // Helper formatter for displaying dates nicely (e.g., 2025-10-13 09:58 a.m.)
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    // We store the 'Clock In' time in memory to calculate total hours later
    private static LocalDateTime currentSessionClockInTime = null;

    // ==========================================
    // CLOCK IN
    // ==========================================
    public static void clockIn(Employee currentUser) {
        // Prevent double clock-in
        if (currentSessionClockInTime != null) {
            System.out.println("\n[!] You are already clocked in since " + currentSessionClockInTime.format(formatter));
            return;
        }

        currentSessionClockInTime = LocalDateTime.now();
        String dateStr = currentSessionClockInTime.toLocalDate().toString();
        String timeStr = currentSessionClockInTime.toLocalTime().toString();

        System.out.println("\n=== Attendance Clock In ===");
        System.out.println("Employee ID: " + currentUser.getID());
        System.out.println("Name: " + currentUser.getName());
        System.out.println("Clock In Successful!");
        System.out.println("Time: " + currentSessionClockInTime.format(formatter));

        // Format: EmpID,Type,Date,Time
        // Example: C6001,IN,2025-10-13,09:58
        String record = currentUser.getID() + ",IN," + dateStr + "," + timeStr;
        
        // Send to Storage
        StorageSystem.logAttendance(record);
    }

    // ==========================================
    // CLOCK OUT
    // ==========================================
    public static void clockOut(Employee currentUser) {
        if (currentSessionClockInTime == null) {
            System.out.println("\n[!] Error: You haven't clocked in yet!");
            return;
        }

        LocalDateTime clockOutTime = LocalDateTime.now();
        String dateStr = clockOutTime.toLocalDate().toString();
        String timeStr = clockOutTime.toLocalTime().toString();

        System.out.println("\n=== Attendance Clock Out ===");
        System.out.println("Employee ID: " + currentUser.getID());
        System.out.println("Name: " + currentUser.getName());
        System.out.println("Clock Out Successful!");
        System.out.println("Time: " + clockOutTime.format(formatter));

        // Calculate Total Hours Worked
        Duration duration = Duration.between(currentSessionClockInTime, clockOutTime);
        long hours = duration.toHours();
        long minutes = duration.toMinutes() % 60;
        
        // Use %.1f for decimal hours if preferred (e.g. 8.1 hours) or format as H:M
        System.out.printf("Total Hours Worked: %d hours %d minutes\n", hours, minutes);

        // Format: EmpID,Type,Date,Time
        String record = currentUser.getID() + ",OUT," + dateStr + "," + timeStr;
        
        // Send to Storage
        StorageSystem.logAttendance(record);
        
        // Reset session time so they can clock in again later if needed
        currentSessionClockInTime = null;
    }
}