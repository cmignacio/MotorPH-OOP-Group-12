package motorph.model;

import java.time.LocalDate;
import java.util.Map;

public class ProbationaryEmployee extends Employee {

    private LocalDate contractStartDate;
    private int contractDurationDays; // example: 180 days

    public ProbationaryEmployee() {
        super();
    }

    public ProbationaryEmployee(Map<String, String> data) {
        super(data);

        try {
            String start = data.getOrDefault("ContractStartDate", "").trim();
            if (!start.isEmpty()) {
                contractStartDate = LocalDate.parse(start);
            }

            String dur = data.getOrDefault("ContractDurationDays", "").trim();
            if (!dur.isEmpty()) {
                contractDurationDays = Integer.parseInt(dur);
            }

        } catch (Exception ignored) {
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

        return regularPay + regularDayOTPay + restDayOTPay - lateDeduction;
    }

    @Override
    public double computeAllowance() {
        return 0;
    }

    public LocalDate calculateContractEndDate() {
        if (contractStartDate == null || contractDurationDays <= 0) return null;
        return contractStartDate.plusDays(contractDurationDays);
    }

    public boolean isContractActive() {
        LocalDate end = calculateContractEndDate();
        if (end == null) return true;
        return !LocalDate.now().isAfter(end);
    }

    public LocalDate getContractStartDate() {
        return contractStartDate;
    }

    public void setContractStartDate(LocalDate contractStartDate) {
        this.contractStartDate = contractStartDate;
    }

    public int getContractDurationDays() {
        return contractDurationDays;
    }

    public void setContractDurationDays(int contractDurationDays) {
        this.contractDurationDays = contractDurationDays;
    }
}