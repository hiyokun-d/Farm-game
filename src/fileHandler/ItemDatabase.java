package fileHandler;

import java.util.HashMap;

public class ItemDatabase {
    public static HashMap<String, ItemData> items = new HashMap<>();

    public static void register(ItemData data) {
        items.put(data.id, data);
    }

    public static ItemData get(String id) {
        return items.get(id);
    }

    public static void save() {
        // Implement saving logic if needed
    }
}
