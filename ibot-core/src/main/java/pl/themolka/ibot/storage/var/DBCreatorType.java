package pl.themolka.ibot.storage.var;

import org.bson.types.ObjectId;

public class DBCreatorType extends DBVariable {
    DBCreatorType(int intValue) {
        super(intValue);
    }

    /** stores iBots ObjectId (iBot) */
    public static final DBCreatorType GLOBAL = new DBCreatorType(1);

    /** stores servers ObjectId (server) */
    public static final DBCreatorType SERVER = new DBCreatorType(11);
    /** stores clients ObjectId (server) */
    public static final DBCreatorType SERVER_PROFILE = new DBCreatorType(12);

    /** stores websites ObjectId (website) @see #VALUE_WEBSITE_ID */
    public static final DBCreatorType WEBSITE = new DBCreatorType(21);
    /** stores users ObjectId (website) */
    public static final DBCreatorType WEBSITE_PROFILE = new DBCreatorType(22);
    /** stores users ObjectId (website) */
    public static final DBCreatorType WEBSITE_SUPPORT = new DBCreatorType(23);

    /** stores API keys ObjectId */
    public static final DBCreatorType API = new DBCreatorType(31);

    // static values
    public static final ObjectId VALUE_GLOBAL_ID = new ObjectId();
    public static final ObjectId VALUE_WEBSITE_ID = new ObjectId();
}
