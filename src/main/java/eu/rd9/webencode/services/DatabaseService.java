package eu.rd9.webencode.services;

import eu.rd9.webencode.config.Config;
import eu.rd9.webencode.config.Settings;
import eu.rd9.webencode.data.Preset;
import eu.rd9.webencode.data.PresetParameter;
import eu.rd9.webencode.data.Rule;
import eu.rd9.webencode.util.ObjectHelper;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by renne on 16.12.2016.
 */
public class DatabaseService {

    private static DatabaseService databaseService = null;
    private String connectionStr;
    private Connection connection = null;
    public DatabaseService(String server, String port, String database, String username, String password) {
        this.connectionStr = "jdbc:mysql://" + server + ":" + port + "/" + database + "?" + "user=" + username + "&password=" + password + "&serverTimezone=UTC";
        this.connect();
    }

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
            ResultSet rs = statement.executeQuery("SELECT id,name, ffmpegParams FROM presets");
            while (rs.next()) {

                Preset preset = new Preset();
                preset.setUuid(rs.getString("id"));
                preset.Preset_Name = rs.getString("name");

                try {
                    preset.presetParameter = (PresetParameter) ObjectHelper.fromString(rs.getString("ffmpegParams"));
                } catch (Exception e)
                {
                    preset.presetParameter = new PresetParameter();
                }

                presets.add(preset);

            }
            rs.close();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return presets;
    }

    public void addWatchFolder(String path) {
        try {
            PreparedStatement preparedStatement = this.connection.prepareStatement("INSERT INTO watch_folders (path) VALUES (?);");
            preparedStatement.setString(1, path);
            preparedStatement.execute();
            preparedStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<String> getWatchFolders() {
        List<String> result = new ArrayList<>();

        try {
            Statement statement = this.connection.createStatement();
            ResultSet rs = statement.executeQuery("SELECT path FROM watch_folders");
            while (rs.next()) {
                result.add(rs.getString("path"));
            }
            rs.close();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }


    public List<Rule> getRules() {
        List<Rule> presets = new ArrayList<>();

        try {
            Statement statement = this.connection.createStatement();
            ResultSet rs = statement.executeQuery("SELECT r.id AS rId , r.name AS rName, r.wildcard AS rWildCard, p.id AS pId, p.name AS pName, p.ffmpegParams AS ffmpegParams FROM rules r JOIN presets p ON r.preset = p.id;");
            while (rs.next()) {

                Preset preset = new Preset();
                preset.setUuid(rs.getString("pId"));
                preset.Preset_Name = rs.getString("pName");
                try {
                    preset.presetParameter = (PresetParameter) ObjectHelper.fromString(rs.getString("ffmpegParams"));
                } catch (Exception e)
                {
                    System.out.println("Could not get preset parameters for " + preset.Preset_Name + " from db! Creating default one...");
                    preset.presetParameter = new PresetParameter();
                    e.printStackTrace();
                }

                Rule rule = new Rule();
                rule.setUuid(rs.getString("rId"));
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

    public void removeWatchFolder(String watchFolderPath) {
        try {
            PreparedStatement preparedStatement = this.connection.prepareStatement("DELETE FROM watch_folders WHERE path = ?;");
            preparedStatement.setString(1, watchFolderPath);
            preparedStatement.execute();
            preparedStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void addRule(Rule rule) {
        try {
            PreparedStatement preparedStatement = this.connection.prepareStatement("INSERT INTO rules (id,name,wildcard,preset) VALUES (?,?,?,?);");
            preparedStatement.setString(1, rule.getUUIDStr());
            preparedStatement.setString(2, rule.Rule_Name);
            preparedStatement.setString(3, rule.Wirldcard);
            preparedStatement.setString(4, rule.Preset.getUUIDStr());
            preparedStatement.execute();
            preparedStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void removeRule(Rule rule) {
        try {
            PreparedStatement preparedStatement = this.connection.prepareStatement("DELETE FROM rules WHERE id = ?;");
            preparedStatement.setString(1, rule.getUUIDStr());
            preparedStatement.execute();
            preparedStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void addPreset(Preset preset) {
        try {
            PreparedStatement preparedStatement = this.connection.prepareStatement("INSERT INTO presets (id,name,ffmpegParams) VALUES (?,?,?);");
            preparedStatement.setString(1, preset.getUUIDStr());
            preparedStatement.setString(2, preset.Preset_Name);
            try {
                preparedStatement.setString(3, ObjectHelper.toString(preset.presetParameter));
            } catch (IOException e) {
                preparedStatement.setString(3, "");
            }
        preparedStatement.execute();
            preparedStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void removePreset(Preset preset) {
        try {
            PreparedStatement preparedStatement = this.connection.prepareStatement("DELETE FROM presets WHERE id = ?;");
            preparedStatement.setString(1, preset.getUUIDStr());
            preparedStatement.execute();
            preparedStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateRule(Rule rule) {
        try {
            PreparedStatement preparedStatement = this.connection.prepareStatement("UPDATE rules SET name = ?, wildcard = ?, preset = ? WHERE id = ?;");
            preparedStatement.setString(1, rule.Rule_Name);
            preparedStatement.setString(2, rule.Wirldcard);
            preparedStatement.setString(3, rule.Preset.getUUIDStr());
            preparedStatement.setString(4, rule.getUUIDStr());
            preparedStatement.execute();
            preparedStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updatePreset(Preset preset) {
        try {
            PreparedStatement preparedStatement = this.connection.prepareStatement("UPDATE presets SET name = ?, ffmpegParams = ? WHERE id = ?;");
            preparedStatement.setString(1, preset.Preset_Name);
            try {
                preparedStatement.setString(2, ObjectHelper.toString(preset.presetParameter));
            } catch (IOException e) {
                preparedStatement.setString(2, "");
            }
            preparedStatement.setString(3, preset.getUUIDStr());
            preparedStatement.execute();
            preparedStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
