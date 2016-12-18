package eu.rd9.webencode.config;

/**
 * Created by renne on 16.12.2016.
 */
public enum Settings {

    MYSQL_SERVER("MySQL-Server", "mysql.server", "localhost"),
    MYSQL_PORT("MySQL-Port", "mysql.port", "3306"),
    MYSQL_DATABASE("MySQL_Database", "mysql.db", "web_encode"),
    MYSQL_USERNAME("MySQL-Username", "mysql.username", "root"),
    MYSQL_PASSWORD("MySQL-Password", "mysql.password", ""),
    FFMPEG_PATH("FFmpeg-Path", "ffmpeg.path", ""),
    CONVERTED_FILES_OUTPUT_PATH("Output Path for converted files", "converted.output.path", ""),
    THREAD_COUNT("Thread Count (default value based on your cpu count)", "thread.count", "" + Runtime.getRuntime().availableProcessors());

    private String settingName;
    private String propName;
    private String defaultVal;

    Settings(String name, String propName, String defaultVal) {
        this.settingName = name;
        this.propName = propName;
        this.defaultVal = defaultVal;
    }

    public String getSettingName() {
        return this.settingName;
    }

    public String getPropName() {
        return this.propName;
    }

    public String getDefaultVal() {
        return this.defaultVal;
    }
}
