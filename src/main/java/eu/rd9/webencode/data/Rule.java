package eu.rd9.webencode.data;

/**
 * Created by renne on 16.12.2016.
 */
public class Rule {

    public String Rule_Name;
    public String Wirldcard;
    public Preset Preset;

    @Override
    public String toString()
    {
        return this.Rule_Name;
    }
}
