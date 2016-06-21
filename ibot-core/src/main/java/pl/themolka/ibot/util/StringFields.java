package pl.themolka.ibot.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class StringFields {
    public static final String DEFAULT_LAYOUT = "%[/FIELD/]";

    private final Map<String, Object> fields = new HashMap<>();
    private String layout;

    public StringFields() {
        this.resetLayout();
    }

    public StringFields append(String field, Object value) {
        this.fields.put(field, value);
        return this;
    }

    public String format(String message) {
        return this.format(message, new String());
    }

    public String format(String message, String defaults) {
        for (String field : this.getFields()) {
            message = message.replace(this.parseField(field), this.getValueString(field, defaults));
        }
        return message;
    }

    public Set<String> getFields() {
        return this.getFieldValues().keySet();
    }

    public Map<String, Object> getFieldValues() {
        return this.fields;
    }

    public String getLayout() {
        return this.layout;
    }

    public Object getValue(String field) {
        return this.getFieldValues().get(field);
    }

    public String getValueString(String field) {
        return this.getValueString(field, null);
    }

    public String getValueString(String field, String defaults) {
        Object value = this.getValue(field);
        if (value != null) {
            return value.toString();
        }
        return defaults;
    }

    public StringFields layout(String layout) {
        this.layout = layout;
        return this;
    }

    public StringFields resetLayout() {
        this.layout = DEFAULT_LAYOUT;
        return this;
    }

    private String parseField(String field) {
        return this.getLayout().replace("/FIELD/", field);
    }
}
