package fileHandler;

import types.CropType;

import java.io.BufferedReader;
import java.io.FileReader;
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
    private static final Path itemData = Path.of("data", "itemData.dat");

    public static void loadPlayerData() throws IOException {

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

    public static void loadItemData() {
        try (BufferedReader br = new BufferedReader(new FileReader(itemData.toFile()))) {
            String line;
            ItemData current = null;

            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;

                // Start of block: [TYPE]
                if (line.startsWith("[") && line.endsWith("]")) {
                    if (current != null) {
                        ItemDatabase.register(current);
                    }
                    current = new ItemData();
                    current.type = line.substring(1, line.length() - 1);
                    continue;
                }

                // Key-value parsing
                if (line.contains("=")) {
                    String[] p = line.split("=");
                    String key = p[0].trim();
                    String value = p[1].trim();

                    switch (key) {
                        case "id" -> current.id = value;
                        case "name" -> current.name = value;
                        case "stackable" -> current.stackable = Boolean.parseBoolean(value);
//                        case "startIndex" -> current.startIndex = Integer.parseInt(value);

                        // CROP
//                        case "stages" -> current.stages = Integer.parseInt(value);

                        case "growthTimes" -> {
                            String[] split = value.split(",");
                            current.growthTimes = new int[split.length];
                            for (int i = 0; i < split.length; i++)
                                current.growthTimes[i] = Integer.parseInt(split[i]);
                        }
                        case "seedId" -> current.seedId = value;
                        case "harvestId" -> current.harvestId = value;

                        // SEED
                        case "plantId" -> {
                            current.plantId = value;
                            try {
                                current.cropType = CropType.valueOf(value);
                            } catch (Exception e) {
                                System.err.println("Invalid cropType: " + value);
                            }
                        }
                    }
                }
            }

            // register last block
            if (current != null) ItemDatabase.register(current);

            System.out.println("Loaded " + ItemDatabase.items.size() + " items.");

        } catch (IOException e) {
            System.err.println("Error loading item data: " + e.getMessage());
        }
    }

    public static void load() throws IOException {
        loadItemData();
        loadPlayerData();
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
