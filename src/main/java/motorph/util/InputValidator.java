package motorph.util;

public class InputValidator {

    public static boolean isEmpty(String value) {
        return value == null || value.trim().isEmpty();
    }

    public static boolean isValidEmployeeId(String employeeId) {
        return employeeId != null && employeeId.matches("\\d+");
    }

    public static boolean isValidPhone(String phone) {
        return phone != null && phone.matches("09\\d{9}");
    }

    public static boolean isValidName(String name) {
        return name != null && name.matches("[a-zA-Z\\s]+");
    }

    public static boolean isValidNumber(String value) {
        return value != null && value.matches("\\d+(\\.\\d+)?");
    }

    public static boolean isValidSSS(String sss) {
        return sss != null && sss.matches("\\d{2}-\\d{7}-\\d");
    }

    public static boolean isValidPhilhealth(String philhealth) {
        return philhealth != null && philhealth.matches("\\d{4}-\\d{4}-\\d{4}");
    }

    public static boolean isValidPagibig(String pagibig) {
        return pagibig != null && pagibig.matches("\\d{4}-\\d{4}-\\d{4}");
    }

    public static boolean isValidTIN(String tin) {
        return tin != null && tin.matches("\\d{3}-\\d{3}-\\d{3}-\\d{3}");
    }
}
