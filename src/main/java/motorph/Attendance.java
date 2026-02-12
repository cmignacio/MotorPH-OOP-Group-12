package motorph;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Represents an attendance record for an employee, including their ID, 
 * date of attendance, and the times they clocked in and out.
 */
public class Attendance {
    private String employeeId;
    private LocalDate date;
    private LocalTime timeIn;
    private LocalTime timeOut;

    /**
     * Constructor
     */
    public Attendance() {}

    /**
     * Constructs an Attendance object with specific values.
     * @param employeeId The employee's ID.
     * @param date The date of attendance.
     * @param timeIn The time the employee clocked in.
     * @param timeOut The time the employee clocked out.
     */
    public Attendance(String employeeId, LocalDate date, LocalTime timeIn, LocalTime timeOut) {
        this.employeeId = employeeId;
        this.date = date;
        this.timeIn = timeIn;
        this.timeOut = timeOut;
    }

    // Getters and Setters
    public String getEmployeeId() { return employeeId; }
    public void setEmployeeId(String employeeId) { this.employeeId = employeeId; }
    
    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }
    
    public LocalTime getTimeIn() { return timeIn; }
    public void setTimeIn(LocalTime timeIn) { this.timeIn = timeIn; }
    
    public LocalTime getTimeOut() { return timeOut; }
    public void setTimeOut(LocalTime timeOut) { this.timeOut = timeOut; }
}