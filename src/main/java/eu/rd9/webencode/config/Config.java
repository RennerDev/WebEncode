package eu.rd9.webencode.config;

import eu.rd9.webencode.Constants;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Created by renne on 16.12.2016.
 */
public class Config {

    private static Config config = null;
    private Properties properties;

    public Config() {

        this.properties = new Properties();
        try {
            this.properties.load(new FileInputStream(Constants.CONFIG_FILE_NAME));
        } catch (IOException e) {
            this.loadDefaultValues();
        }

    }

    public static Config getInstance() {
        if (config == null)
            config = new Config();

        return config;
    }

    private void loadDefaultValues() {
        for (Settings setting : Settings.values()) {
            this.properties.setProperty(setting.getPropName(), setting.getDefaultVal());
        }
        this.saveConfig();
    }

    public void saveConfig() {
        try {
            this.properties.store(new FileOutputStream(Constants.CONFIG_FILE_NAME), "Auto-Generated");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getSetting(Settings setting) {
        return this.properties.getProperty(setting.getPropName(), setting.getDefaultVal());
    }

    public void setSetting(Settings setting, String value) {
        this.properties.setProperty(setting.getPropName(), value);
        this.saveConfig();
    }

}
