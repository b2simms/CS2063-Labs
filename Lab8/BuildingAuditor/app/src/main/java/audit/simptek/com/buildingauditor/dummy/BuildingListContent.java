package audit.simptek.com.buildingauditor.dummy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Helper class for providing sample address for user interfaces created by
 * Android template wizards.
 * <p>
 * TODO: Replace all uses of this class before publishing your app.
 */
public class BuildingListContent {

    /**
     * An array of sample (dummy) items.
     */
    public static final List<PropertyItem> ITEMS = new ArrayList<PropertyItem>();

    /**
     * A map of sample (dummy) items, by ID.
     */
    public static final Map<String, PropertyItem> ITEM_MAP = new HashMap<String, PropertyItem>();

    static {
        // Add some sample items.
        addItem("Building Name", "Fredericton", 3);
        addItem("Kentsteam Apartments", "790 Regent St, Fredericton Canada E3B 5K8", 5);
        addItem("Redwood Apartments", "990 King St, Fredericton Canada E3B 8P9", 2);
        addItem("Charles Homestead", "11 Craft Rd, Fredericton Canada E3B 5R5", 5);
        addItem("Steam Housing", "790 Regent St, Fredericton Canada E3B 5K8", 5);
        addItem("Kentsteam Apartments", "790 Regent St, Fredericton Canada E3B 5K8", 7);
        addItem("Kentsteam Apartments", "790 Regent St, Fredericton Canada E3B 5K8", 2);
        addItem("Kentsteam Apartments", "790 Regent St, Fredericton Canada E3B 5K8", 5);
        addItem("Kentsteam Apartments", "790 Regent St, Fredericton Canada E3B 5K8", 5);

    }

    private static void addItem(String name, String address, int floors) {
        PropertyItem item = new PropertyItem(name, address, floors);
        ITEMS.add(item);
        ITEM_MAP.put(item.name, item);
    }

    /**
     * A dummy item representing a piece of address.
     */
    public static class PropertyItem {
        public final String name;
        public final String address;
        public final int floors;

        public PropertyItem(String name, String address, int floors) {
            this.name = name;
            this.address = address;
            this.floors = floors;
        }

        @Override
        public String toString() {
            return address;
        }
    }
}
