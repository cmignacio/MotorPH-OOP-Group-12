package motorph.service;

import motorph.domain.LeaveRequest;
import motorph.domain.LeaveType;
import motorph.repository.LeaveRepository;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public class LeaveService {

    private final LeaveRepository leaveRepository;

    public LeaveService(LeaveRepository leaveRepository) {
        this.leaveRepository = leaveRepository;
    }

    public LeaveRequest fileLeave(String employeeId,
                                  LeaveType type,
                                  LocalDate startDate,
                                  LocalDate endDate) throws IOException {

        String leaveId = UUID.randomUUID().toString();

        LeaveRequest leave = new LeaveRequest(
                leaveId,
                employeeId,
                type,
                startDate,
                endDate
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
