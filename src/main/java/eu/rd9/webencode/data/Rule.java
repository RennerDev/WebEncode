package eu.rd9.webencode.data;

import java.util.UUID;

/**
 * Created by renne on 16.12.2016.
 */
public class Rule {

    public String Rule_Name;
    public String Wirldcard;
    public Preset Preset;
    private UUID uuid = UUID.randomUUID();

    public void setUuid(String uuidStr) {
        this.uuid = UUID.fromString(uuidStr);
    }


    public String getUUIDStr() {
        return this.uuid.toString();
    }

    @Override
    public String toString() {
        return this.Rule_Name;
    }
}
