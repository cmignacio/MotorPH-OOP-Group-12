package motorph.service;

import motorph.model.LeaveRequest;
import motorph.model.LeaveType;
import motorph.dao.LeaveDAO;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public class LeaveService {

    private final LeaveDAO leaveRepository;

    public LeaveService(LeaveDAO leaveRepository) {
        this.leaveRepository = leaveRepository;
    }

    public LeaveRequest fileLeave(String employeeId,
                                  LeaveType type,
                                  LocalDate startDate,
                                  LocalDate endDate,
                                  String reason) throws IOException {

        String leaveId = UUID.randomUUID().toString();

        LeaveRequest leave = new LeaveRequest(
                leaveId,
                employeeId,
                type,
                startDate,
                endDate,
                reason
        );

        leaveRepository.saveLeave(leave);
        return leave;
    }

    public List<LeaveRequest> getAllLeaves() throws IOException {
        return leaveRepository.findAll();
    }

    public void approveLeave(String leaveId) throws IOException {
        List<LeaveRequest> leaves = leaveRepository.findAll();

        for (LeaveRequest leave : leaves) {
            if (leave.getLeaveId().equals(leaveId)) {
                leave.approve();
                break;
            }
        }

        leaveRepository.overwriteAll(leaves);
    }

    public void rejectLeave(String leaveId) throws IOException {
        List<LeaveRequest> leaves = leaveRepository.findAll();

        for (LeaveRequest leave : leaves) {
            if (leave.getLeaveId().equals(leaveId)) {
                leave.reject();
                break;
            }
        }

        leaveRepository.overwriteAll(leaves);
    }
}