package eu.rd9.webencode.data;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by dominicrenner on 18.12.16.
 */
public class PresetParameter implements Serializable {

    private Map<PresetOption, String> presetOptionsMap = new HashMap<>();

    public void addOption ( PresetOption option, String value )
    {
        this.presetOptionsMap.put(option, value);
    }

    public void removeOption (PresetOption option)
    {
        this.presetOptionsMap.remove(option);
    }

    public String getOptionValue (PresetOption option)
    {
        return this.presetOptionsMap.get(option);
    }

}
