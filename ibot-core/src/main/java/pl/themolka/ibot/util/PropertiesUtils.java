package pl.themolka.ibot.util;

import java.util.Properties;

public class PropertiesUtils {
    public static boolean contains(Properties properties, String key) {
        return properties.getProperty(key) != null;
    }

    public static boolean containsValue(Properties properties, String key) {
        return contains(properties, key) && !properties.getProperty(key).equals("");
    }

    public static Object setOrNot(Properties properties, String key, Object defaultValue) {
        if (properties.getProperty(key) != null) {
            return properties.getProperty(key);
        } else {
            return defaultValue;
        }
    }

    public static Object setOrNot(Properties properties, String key, Object trueValue, Object falseValue) {
        if (properties.getProperty(key) != null) {
            return trueValue;
        } else {
            return falseValue;
        }
    }

    public static boolean setOrNotBoolean(Properties properties, String key, boolean defaultValue) {
        Object value = setOrNot(properties, key, defaultValue);
        if (value != null) {
            return Boolean.parseBoolean(value.toString());
        } else {
            return defaultValue;
        }
    }

    public static String setOrNotString(Properties properties, String key, String defaultValue) {
        Object value = setOrNot(properties, key, defaultValue);
        if (value != null) {
            return String.valueOf(value);
        } else {
            return defaultValue;
        }
    }
}
