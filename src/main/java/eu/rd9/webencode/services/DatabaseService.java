package eu.rd9.webencode.services;

import eu.rd9.webencode.config.Config;
import eu.rd9.webencode.config.Settings;
import eu.rd9.webencode.data.Preset;
import eu.rd9.webencode.data.Rule;
import javafx.util.Pair;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by renne on 16.12.2016.
 */
public class DatabaseService {

    private static DatabaseService databaseService = null;

    public static DatabaseService getInstance() {
        Config config = Config.getInstance();
        if (databaseService == null)
            databaseService = new DatabaseService(config.getSetting(Settings.MYSQL_SERVER),
                    config.getSetting(Settings.MYSQL_PORT),
                    config.getSetting(Settings.MYSQL_DATABASE),
                    config.getSetting(Settings.MYSQL_USERNAME),
                    config.getSetting(Settings.MYSQL_PASSWORD));

        return databaseService;
    }

    private String connectionStr;
    private Connection connection = null;

    public DatabaseService(String server, String port, String database, String username, String password) {
        this.connectionStr = "jdbc:mysql://" + server + ":" + port + "/" + database + "?" + "user=" + username + "&password=" + password + "&serverTimezone=UTC";
        this.connect();
    }

    public void connect() {
        try {
            connection = DriverManager
                    .getConnection(this.connectionStr);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Preset> getPresets() {
        List<Preset> presets = new ArrayList<>();

        try {
            Statement statement = this.connection.createStatement();
            ResultSet rs = statement.executeQuery("SELECT name, ffmpegParams FROM presets");
            while (rs.next()) {

                Preset preset = new Preset();
                preset.Preset_Name = rs.getString("name");
                preset.FFmpeg_Parameters = rs.getString("ffmpegParams");
                presets.add(preset);

            }
            rs.close();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return presets;
    }

    public List<Rule> getRules() {
        List<Rule> presets = new ArrayList<>();

        try {
            Statement statement = this.connection.createStatement();
            ResultSet rs = statement.executeQuery("SELECT r.name AS rName, r.wildcard AS rWildCard, p.name AS pName, p.ffmpegParams AS ffmpeg FROM rules r JOIN presets p ON r.preset = p.id");
            while (rs.next()) {

                Preset preset = new Preset();
                preset.Preset_Name = rs.getString("pName");
                preset.FFmpeg_Parameters = rs.getString("ffmpeg");

                Rule rule = new Rule();
                rule.Rule_Name = rs.getString("rName");
                rule.Wirldcard = rs.getString("rWildCard");
                rule.Preset = preset;

                presets.add(rule);

            }
            rs.close();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return presets;
    }

}
