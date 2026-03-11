package motorph.service;

import motorph.domain.Role;

public class PermissionService {

    public static boolean canViewAllEmployees(Role role) {
        return role == Role.HR_MANAGER
                || role == Role.HR_STAFF
                || role == Role.ADMIN;
    }

    public static boolean canAddEmployee(Role role) {
        return role == Role.HR_MANAGER
                || role == Role.HR_STAFF
                || role == Role.ADMIN;
    }

    public static boolean canDeleteEmployee(Role role) {
        return role == Role.HR_MANAGER
                || role == Role.ADMIN;
    }

    public static boolean canGeneratePayroll(Role role) {
        return role == Role.FINANCE_MANAGER
                || role == Role.FINANCE_STAFF
                || role == Role.ADMIN;
    }

    public static boolean canApproveFinalPayroll(Role role) {
        return role == Role.FINANCE_MANAGER;
    }

    public static boolean canApproveLeave(Role role) {
        return role == Role.HR_MANAGER;
    }

    public static boolean canManageUsers(Role role) {
        return role == Role.IT_MANAGER
                || role == Role.IT_STAFF
                || role == Role.ADMIN;
    }
}