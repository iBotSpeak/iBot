package pl.themolka.ibot.storage.var;

public class DBVariable {
    private int intValue;

    public DBVariable(int intValue) {
        this.intValue = intValue;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof DBVariable) {
            return ((DBVariable) obj).intValue() == this.intValue();
        } else if (obj instanceof Integer) {
            return obj.equals(this.intValue());
        }

        return false;
    }

    @Override
    public String toString() {
        return String.valueOf(this.intValue());
    }

    public int intValue() {
        return this.intValue;
    }
}
