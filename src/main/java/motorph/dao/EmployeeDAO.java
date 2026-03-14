package motorph.dao;

import motorph.model.Employee;
import motorph.model.RegularEmployee;
import motorph.model.ProbationaryEmployee;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class EmployeeDAO {

    public List<Employee> loadEmployees() {
        List<Employee> employees = new ArrayList<>();

        try {
            InputStream input = getClass()
                    .getClassLoader()
                    .getResourceAsStream("employees.csv");

            if (input == null) {
                System.out.println("employees.csv not found in resources folder.");
                return employees;
            }

            BufferedReader reader = new BufferedReader(new InputStreamReader(input));
            String line;
            boolean firstRow = true;

            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) {
                    continue;
                }

                if (firstRow) {
                    firstRow = false;
                    if (line.toLowerCase().contains("employee")) {
                        continue;
                    }
                }

                String[] parts = line.split(",");

                if (parts.length < 4) {
                    continue;
                }

                String id = parts[0].trim();
                String lastName = parts[1].trim();
                String firstName = parts[2].trim();
                String type = parts[3].trim();

                Employee emp;

                if (type.equalsIgnoreCase("Probationary")) {
                    emp = new ProbationaryEmployee();
                } else {
                    emp = new RegularEmployee();
                }

                emp.setEmployeeId(id);
                emp.setLastName(lastName);
                emp.setFirstName(firstName);
                emp.setStatus(type);

                employees.add(emp);
            }

            reader.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return employees;
    }
}