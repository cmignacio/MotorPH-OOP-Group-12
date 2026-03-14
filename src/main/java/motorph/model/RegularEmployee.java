package motorph.model;

import java.util.Map;

/**
 * Represents a regular employee with full payroll computation logic.
 */
public class RegularEmployee extends Employee {

    public RegularEmployee() {
    super();
}

    public RegularEmployee(Map<String, String> data) {
        super(data);
    }

    @Override
    public double computePay(
            double regularHours,
            double overtimeRegularHours,
            double overtimeRestHours,
            int lateMinutes
    ) {
        double hourlyRate = getHourlyRate();

        double regularPay = regularHours * hourlyRate;
        double regularDayOTPay = overtimeRegularHours * hourlyRate * 1.25;
        double restDayOTPay = overtimeRestHours * hourlyRate * 1.30;
        double lateDeduction = (lateMinutes / 60.0) * hourlyRate;

        double allowance = computeAllowance();

        return regularPay + regularDayOTPay + restDayOTPay + allowance - lateDeduction;
    }

    @Override
    public double computeAllowance() {
        return getRiceSubsidy() + getPhoneAllowance() + getClothingAllowance();
    }
}