package motorph.repository;

import motorph.domain.LeaveRequest;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LeaveRepository {

    private final String filePath;

    public LeaveRepository(String filePath) {
        this.filePath = filePath;
    }

    public void saveLeave(LeaveRequest leave) throws IOException {
        boolean fileExists = Files.exists(Paths.get(filePath));

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, true))) {
            if (!fileExists) {
                writer.write("Leave ID,Employee ID,Type,Start Date,End Date,Status");
                writer.newLine();
            }

            Map<String, String> map = leave.toMap();

            writer.write(String.join(",",
                    map.get("Leave ID"),
                    map.get("Employee ID"),
                    map.get("Type"),
                    map.get("Start Date"),
                    map.get("End Date"),
                    map.get("Status")
            ));
            writer.newLine();
        }
    }

    public List<LeaveRequest> findAll() throws IOException {
        List<LeaveRequest> leaves = new ArrayList<>();

        if (!Files.exists(Paths.get(filePath))) {
            return leaves;
        }

        List<String> lines = Files.readAllLines(Paths.get(filePath));
        for (int i = 1; i < lines.size(); i++) {
            String line = lines.get(i).trim();
            if (line.isEmpty()) continue;

            String[] parts = line.split(",");

            Map<String, String> data = new HashMap<>();
            data.put("Leave ID", parts[0]);
            data.put("Employee ID", parts[1]);
            data.put("Type", parts[2]);
            data.put("Start Date", parts[3]);
            data.put("End Date", parts[4]);
            data.put("Status", parts[5]);

            leaves.add(new LeaveRequest(data));
        }

        return leaves;
    }

    public void overwriteAll(List<LeaveRequest> leaves) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            writer.write("Leave ID,Employee ID,Type,Start Date,End Date,Status");
            writer.newLine();

            for (LeaveRequest leave : leaves) {
                Map<String, String> map = leave.toMap();

                writer.write(String.join(",",
                        map.get("Leave ID"),
                        map.get("Employee ID"),
                        map.get("Type"),
                        map.get("Start Date"),
                        map.get("End Date"),
                        map.get("Status")
                ));
                writer.newLine();
            }
        }
    }
}
