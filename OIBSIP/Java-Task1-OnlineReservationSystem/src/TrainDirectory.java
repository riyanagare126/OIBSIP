import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Lookup directory that maps numeric train numbers to train names for auto-population.
 */
public class TrainDirectory {
    private static final Map<Integer, String> TRAIN_MAP = new LinkedHashMap<>();

    static {
        TRAIN_MAP.put(12951, "Mumbai Rajdhani Express");
        TRAIN_MAP.put(12002, "New Delhi Bhopal Shatabdi Express");
        TRAIN_MAP.put(12301, "Howrah Rajdhani Express");
        TRAIN_MAP.put(12627, "Karnataka Superfast Express");
        TRAIN_MAP.put(22436, "Vande Bharat Express (NDLS - BSB)");
        TRAIN_MAP.put(12259, "Sealdah Duronto Express");
        TRAIN_MAP.put(12430, "New Delhi Lucknow AC Superfast");
        TRAIN_MAP.put(12801, "Purushottam Express");
        TRAIN_MAP.put(12622, "Tamil Nadu Express");
        TRAIN_MAP.put(12137, "Punjab Mail");
    }

    public static String lookupTrainName(String trainNumberInput) {
        if (trainNumberInput == null || trainNumberInput.trim().isEmpty()) {
            return "";
        }
        try {
            int trainNo = Integer.parseInt(trainNumberInput.trim());
            if (trainNo <= 0) {
                return "";
            }
            return TRAIN_MAP.getOrDefault(trainNo, "Oasis Intercity Express #" + trainNo);
        } catch (NumberFormatException e) {
            return "";
        }
    }

    public static Map<Integer, String> getSampleTrains() {
        return TRAIN_MAP;
    }
}

