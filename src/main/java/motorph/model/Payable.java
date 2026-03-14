package motorph.model;

public interface Payable {

    double computePay(double regularHours,
                      double overtimeRegularHours,
                      double overtimeRestHours,
                      int lateMinutes);

    double computeAllowance();
}