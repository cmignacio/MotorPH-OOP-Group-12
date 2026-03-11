package motorph.domain;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

public class LeaveRequest {

    private String leaveId;
    private String employeeId;
    private LeaveType leaveType;
    private LocalDate startDate;
    private LocalDate endDate;
    private LeaveStatus status;

    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("MM/dd/yyyy");

    public LeaveRequest(String leaveId,
                        String employeeId,
                        LeaveType leaveType,
                        LocalDate startDate,
                        LocalDate endDate) {
        this.leaveId = leaveId;
        this.employeeId = employeeId;
        this.leaveType = leaveType;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = LeaveStatus.PENDING;
    }

    public LeaveRequest(Map<String, String> data) {
        this.leaveId = data.get("Leave ID");
        this.employeeId = data.get("Employee ID");
        this.leaveType = LeaveType.valueOf(data.get("Type").trim().toUpperCase());
        this.startDate = LocalDate.parse(data.get("Start Date"), FORMATTER);
        this.endDate = LocalDate.parse(data.get("End Date"), FORMATTER);
        this.status = LeaveStatus.valueOf(data.get("Status").trim().toUpperCase());
    }

    public String getLeaveId() {
        return leaveId;
    }

    public String getEmployeeId() {
        return employeeId;
    }

    public LeaveType getLeaveType() {
        return leaveType;
    }

    public LocalDate getStartDateValue() {
        return startDate;
    }

    public LocalDate getEndDateValue() {
        return endDate;
    }

    public String getStartDate() {
        return startDate.format(FORMATTER);
    }

    public String getEndDate() {
        return endDate.format(FORMATTER);
    }

    public LeaveStatus getStatus() {
        return status;
    }

    public void approve() {
        this.status = LeaveStatus.APPROVED;
    }

    public void reject() {
        this.status = LeaveStatus.REJECTED;
    }

    public Map<String, String> toMap() {
        Map<String, String> map = new HashMap<>();
        map.put("Leave ID", leaveId);
        map.put("Employee ID", employeeId);
        map.put("Type", leaveType.name());
        map.put("Start Date", getStartDate());
        map.put("End Date", getEndDate());
        map.put("Status", status.name());
        return map;
    }
}
