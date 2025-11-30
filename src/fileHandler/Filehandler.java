package fileHandler;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Filehandler {
    private static final Path file = Path.of("data", "config_PLAYER.txt");
    private static final Map<String, String> data = new HashMap<>();

    public static void load() throws IOException {
        try {

            if (!Files.exists(file)) {
                Files.createDirectories(file.getParent());
                Files.writeString(file,
                        "playerHealth = 100\n" +
                                "playerName = Hero\n" +
                                "playerLevel = 1",
                        StandardCharsets.UTF_8);
                System.out.println("File created.");
            }

            List<String> lines = Files.readAllLines(file, StandardCharsets.UTF_8);
            data.clear();

            for (String line : lines) {
                line = line.trim();
                if (line.isEmpty() || !line.contains("=")) continue;

                String[] parts = line.split("=", 2);
                data.put(parts[0].trim(), parts[1].trim());
            }

            System.out.println("Loaded: " + data);
        } catch (IOException e) {
            System.err.println("Error loading file: " + e.getMessage());
            throw e;
        }
    }

    public static void save() throws IOException {
        StringBuilder content = new StringBuilder();
        for (Map.Entry<String, String> entry : data.entrySet()) {
            content.append(entry.getKey()).append(" = ").append(entry.getValue()).append("\n");
        }

        Files.writeString(file, content.toString(), StandardCharsets.UTF_8);
        System.out.println("Saved: " + data);
    }

    public static void save(String key, String value) throws IOException {
        data.put(key, value);
        save();
    }

    public static String getString(String key) {
        return data.get(key);
    }

    public static int getInt(String key) {
        return Integer.parseInt(data.get(key));
    }

    public static boolean getBoolean(String key) {
        return Boolean.parseBoolean(data.get(key));
    }

    public static void update(String key, String value) throws IOException {
        data.put(key, value);
        save();
    }
}
