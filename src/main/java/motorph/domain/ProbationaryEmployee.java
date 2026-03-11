package motorph.domain;

import motorph.domain.Employee;
import java.time.LocalDate;
import java.util.Map;

public class ProbationaryEmployee extends Employee {

    // Simple contract fields (optional; safe even if not in CSV yet)
    private LocalDate contractStartDate;
    private int contractDurationDays; // e.g., 180 days

    public ProbationaryEmployee() {
        super();
    }

    public ProbationaryEmployee(Map<String, String> data) {
        super(data);

        // Optional: only works if your CSV has these headers
        // Example headers: "ContractStartDate", "ContractDurationDays"
        try {
            String start = data.getOrDefault("ContractStartDate", "").trim();
            if (!start.isEmpty()) contractStartDate = LocalDate.parse(start);

            String dur = data.getOrDefault("ContractDurationDays", "").trim();
            if (!dur.isEmpty()) contractDurationDays = Integer.parseInt(dur);

        } catch (Exception ignored) {
            // If fields are absent or formatted differently, we just leave them null/0
        }
    }

    @Override
    public double computePay(double regularHours,
                             double overtimeRegularHours,
                             double overtimeRestHours,
                             int lateMinutes) {

        double hourlyRate = getHourlyRate();

        double regularPay = regularHours * hourlyRate;
        double regularDayOTPay = overtimeRegularHours * hourlyRate * 1.25;
        double restDayOTPay = overtimeRestHours * hourlyRate * 1.30;

        double lateDeduction = (lateMinutes / 60.0) * hourlyRate;

        // Contractual rule: no allowances (simple difference vs RegularEmployee)
        return regularPay + regularDayOTPay + restDayOTPay - lateDeduction;
    }

    // calculateContractEndDate()
    public LocalDate calculateContractEndDate() {
        if (contractStartDate == null || contractDurationDays <= 0) return null;
        return contractStartDate.plusDays(contractDurationDays);
    }

    // isContractActive()
    public boolean isContractActive() {
        LocalDate end = calculateContractEndDate();
        if (end == null) return true; // if no contract info, assume active
        return !LocalDate.now().isAfter(end);
    }

    // Optional getters/setters (if you want to set contract info manually)
    public LocalDate getContractStartDate() { return contractStartDate; }
    public void setContractStartDate(LocalDate contractStartDate) { this.contractStartDate = contractStartDate; }

    public int getContractDurationDays() { return contractDurationDays; }
    public void setContractDurationDays(int contractDurationDays) { this.contractDurationDays = contractDurationDays; }
}
