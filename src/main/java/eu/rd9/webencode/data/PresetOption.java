package eu.rd9.webencode.data;

/**
 * Created by dominicrenner on 18.12.16.
 */
public enum PresetOption {

    VIDEO_OUTPUT_FORMAT ( "Video-Format", "mp4"),
    VIDEO_CODEC("Video-Codec", "libx264");

    private String uiName;
    private String defaultValue;

    PresetOption(String uiName, String defaultValue) {
        this.uiName = uiName;
        this.defaultValue = defaultValue;
    }

    public String getUiName()
    {
        return this.uiName;
    }

    public String getDefaultValue()
    {
        return this.defaultValue;
    }
}
