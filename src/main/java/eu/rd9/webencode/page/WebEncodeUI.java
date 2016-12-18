package eu.rd9.webencode.page;

import com.vaadin.annotations.Theme;
import com.vaadin.data.Container;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.server.Page;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.*;
import eu.rd9.webencode.config.Config;
import eu.rd9.webencode.config.Settings;
import eu.rd9.webencode.data.Preset;
import eu.rd9.webencode.data.PresetOption;
import eu.rd9.webencode.data.Rule;
import eu.rd9.webencode.services.DatabaseService;
import eu.rd9.webencode.services.WatchFolderService;
import eu.rd9.webencode.workers.Worker;
import eu.rd9.webencode.workers.WorkerManager;
import eu.rd9.webencode.workers.WorkerState;

import javax.xml.soap.Text;
import java.util.*;

/**
 * This UI is the application entry point. A UI may either represent a browser window
 * (or tab) or some part of a html page where a Vaadin application is embedded.
 * <p>
 * The UI is initialized using {@link #init(VaadinRequest)}. This method is intended to be
 * overridden to add component to the user interface and initialize non-component functionality.
 */
@Theme("valo")
public class WebEncodeUI extends UI {
    public static Map<String, WatchFolderService> watchFolderServices = new HashMap<>();
    public Config config;

    @Override
    protected void init(VaadinRequest vaadinRequest) {
        // Create the content root layout for the UI
        this.config = Config.getInstance();

        VerticalLayout content = new VerticalLayout();
        setContent(content);

        TabSheet tabsheet = new TabSheet();
        content.addComponent(tabsheet);


        GridLayout watchFolderGrid = new GridLayout(1, 1);
        watchFolderGrid.setMargin(true);
        watchFolderGrid.setWidth("100%");
        watchFolderGrid.setHeight("100%");
        tabsheet.addTab(watchFolderGrid).setCaption("Watch Folder");
        setupWatchFolder(watchFolderGrid);

        GridLayout workersGrid = new GridLayout(1, 1);
        workersGrid.setWidth("100%");
        workersGrid.setHeight("100%");
        tabsheet.addTab(workersGrid).setCaption("Workers");
        setupWorkers(workersGrid);

        GridLayout rulesGrid = new GridLayout(1, 1);
        rulesGrid.setWidth("100%");
        rulesGrid.setHeight("100%");
        tabsheet.addTab(rulesGrid).setCaption("Rules");
        setupRules(rulesGrid);

        GridLayout presetsGrid = new GridLayout(1, 1);
        presetsGrid.setWidth("100%");
        presetsGrid.setHeight("100%");
        tabsheet.addTab(presetsGrid).setCaption("Presets");
        setupPresets(presetsGrid);

        GridLayout settingsGrid = new GridLayout(2, 1);
        settingsGrid.setWidth("100%");
        settingsGrid.setHeight("100%");
        tabsheet.addTab(settingsGrid).setCaption("Settings");
        setupSettings(settingsGrid);
    }

    private void setupSettings(GridLayout settingsGrid) {

        Map<Settings, TextField> settingsTextFieldMap = new HashMap<>();

        int row = 0;
        settingsGrid.setRows(settingsGrid.getRows() + Settings.values().length);
        for (Settings setting : Settings.values()) {
            Label label = new Label(setting.getSettingName());
            label.setWidth("50%");
            settingsGrid.addComponent(label, 0, row);

            TextField textField = new TextField();
            textField.setValue(config.getSetting(setting));
            textField.setWidth("50%");
            textField.markAsDirty();
            settingsGrid.addComponent(textField, 1, row);
            row++;

            settingsTextFieldMap.put(setting, textField);
        }


        Button saveBtn = new Button("Save");
        saveBtn.addClickListener(clickEvent -> {
            for (Map.Entry<Settings, TextField> e : settingsTextFieldMap.entrySet()) {
                config.setSetting(e.getKey(), e.getValue().getValue());
            }
            Notification.show("Settings saved!");
        });
        settingsGrid.setRows(settingsGrid.getRows() + 1);
        settingsGrid.addComponent(saveBtn, 0, row);

        Button resetButton = new Button("Reset");
        resetButton.addClickListener(clickEvent -> {
            for (Map.Entry<Settings, TextField> e : settingsTextFieldMap.entrySet()) {
                e.getValue().setValue(config.getSetting(e.getKey()));
            }
            Notification.show("Settings reset!");
        });
        settingsGrid.setRows(settingsGrid.getRows() + 1);
        settingsGrid.addComponent(resetButton, 1, row++);


    }

    private void setupRules(GridLayout rulesGrid) {

        Table rulesTable = new Table();
        rulesTable.setWidth("40%");
        rulesTable.setHeight("40%");
        rulesTable.addContainerProperty("Rule", Rule.class, null);
        rulesTable.addContainerProperty("Preset", Preset.class, null);
        rulesTable.setSelectable(true);
        rulesTable.setImmediate(true);
        rulesGrid.addComponent(rulesTable, 0, 0);


        VerticalLayout verticalLayout = new VerticalLayout();
        rulesGrid.setColumns(rulesGrid.getColumns() + 1);
        rulesGrid.addComponent(verticalLayout, 1, 0);

        for (Rule rule : DatabaseService.getInstance().getRules()) {
            rulesTable.addItem(new Object[]{rule, rule.Preset}, rule);
        }


        rulesTable.addValueChangeListener((Property.ValueChangeListener) event -> {

            verticalLayout.removeAllComponents();

            Rule rule = (Rule) rulesTable.getValue();
            if ( rule == null )
                return;


            TextField rueUUIDTextField = new TextField("UUID");
            rueUUIDTextField.setValue(rule.getUUIDStr());
            rueUUIDTextField.setEnabled(false);
            verticalLayout.addComponent(rueUUIDTextField);


            TextField ruleNameTextField = new TextField("Rule Name");
            ruleNameTextField.setValue(rule.Rule_Name);
            verticalLayout.addComponent(ruleNameTextField);


            TextField ruleWildCardTextField = new TextField("Wildcard (RegEx)");
            ruleWildCardTextField.setValue(rule.Wirldcard);
            verticalLayout.addComponent(ruleWildCardTextField);

            ComboBox presetSelectionComboBox = new ComboBox("Preset");
            presetSelectionComboBox.addItems(DatabaseService.getInstance().getPresets());
            presetSelectionComboBox.select(rule.Preset);
            verticalLayout.addComponent(presetSelectionComboBox);


            HorizontalLayout horizontalLayout = new HorizontalLayout();
            verticalLayout.addComponent(horizontalLayout);

            Button saveBtn = new Button("Save");
            saveBtn.addClickListener(event1 -> {
                rule.Rule_Name = ruleNameTextField.getValue();
                rule.Wirldcard = ruleWildCardTextField.getValue();
                rule.Preset = (Preset) presetSelectionComboBox.getValue();
                DatabaseService.getInstance().updateRule((Rule) rulesTable.getValue());
            });
            horizontalLayout.addComponent(saveBtn);

            Button resetBtn = new Button("Reset");
            resetBtn.addClickListener(event1 -> {
                ruleNameTextField.setValue(rule.Rule_Name);
                ruleWildCardTextField.setValue(rule.Wirldcard);
                presetSelectionComboBox.select(rule.Preset);
            });
            horizontalLayout.addComponent(resetBtn);
        });


        Button addFolderBtn = new Button("Add Rule");
        addFolderBtn.addClickListener(clickEvent -> {
            NewRuleSub sub = new NewRuleSub();

            // Add it to the root component
            UI.getCurrent().addWindow(sub);
        });

        Button removeRuleBtn = new Button("Remove Rule");
        removeRuleBtn.addClickListener(clickEvent -> {
            Rule rule = (Rule) rulesTable.getValue();
            if (rule != null) {
                DatabaseService.getInstance().removeRule(rule);
                Page.getCurrent().reload();
            }
        });

        HorizontalLayout horizontalLayout = new HorizontalLayout();
        horizontalLayout.addComponent(addFolderBtn);
        horizontalLayout.addComponent(removeRuleBtn);
        rulesGrid.setRows(rulesGrid.getRows() + 1);
        rulesGrid.addComponent(horizontalLayout);


    }

    private void setupPresets(GridLayout presetsGrid) {

        Table presetTable = new Table();
        presetTable.setWidth("20%");
        presetTable.setHeight("20%");
        presetTable.addContainerProperty("Preset", Preset.class, null);
        presetTable.setSelectable(true);
        presetTable.setImmediate(true);
        presetsGrid.addComponent(presetTable, 0, 0);


        VerticalLayout verticalLayout = new VerticalLayout();
        presetsGrid.setColumns(presetsGrid.getColumns() + 1);
        presetsGrid.addComponent(verticalLayout, 1, 0);

        for (Preset preset : DatabaseService.getInstance().getPresets()) {
            presetTable.addItem(new Object[]{preset}, preset);
        }


        presetTable.addValueChangeListener((Property.ValueChangeListener) event -> {
            verticalLayout.removeAllComponents();

            Preset preset = (Preset) presetTable.getValue();
            if (preset == null)
                return;

            TextField presetNameTextField = new TextField("Preset Name");
            presetNameTextField.setValue(preset.Preset_Name);
            verticalLayout.addComponent(presetNameTextField);

            Table optionsTable = new Table("Options");
            optionsTable.setImmediate(true);
            optionsTable.setHeight("30%");
            optionsTable.setWidth("80%");
            optionsTable.setEditable(true);
            optionsTable.addContainerProperty("Option", String.class, null);
            optionsTable.addContainerProperty("Value", String.class, null);

            for (PresetOption presetOption : PresetOption.values())
            {
                String value = preset.presetParameter.getOptionValue(presetOption);
                if (value == null)
                   continue;

                optionsTable.addItem(new Object[]{presetOption.getUiName(), value}, presetOption);
            }
            verticalLayout.addComponent(optionsTable);

            HorizontalLayout horizontalLayout = new HorizontalLayout();
            verticalLayout.addComponent(horizontalLayout);

            Button saveBtn = new Button("Save");
            saveBtn.addClickListener(event1 -> {

                Map<String, String> map = new HashMap<>();
                for (Object itemID : optionsTable.getItemIds()) {

                    System.out.println(itemID);
                    String[] rows = optionsTable.getItem(itemID).toString().split(" ");
                    map.put(rows[0], rows[1]);
                }

                for(PresetOption option : PresetOption.values())
                {
                    String val = map.get(option.getUiName());
                   if (val == null)
                       continue;

                   preset.presetParameter.addOption(option, val);
                }

                preset.Preset_Name = presetNameTextField.getValue();
                DatabaseService.getInstance().updatePreset((Preset) presetTable.getValue());

            });
            horizontalLayout.addComponent(saveBtn);

            Button resetBtn = new Button("Reset");
            resetBtn.addClickListener(event1 -> {

                presetNameTextField.setValue(preset.Preset_Name);

            });
            horizontalLayout.addComponent(resetBtn);
        });


        presetsGrid.setRows(presetsGrid.getRows() + 1);
        Button addFolderBtn = new Button("Add Preset");
        addFolderBtn.addClickListener(clickEvent -> {
            NewPresetSub sub = new NewPresetSub();

            // Add it to the root component
            UI.getCurrent().addWindow(sub);
        });

        Button removePresetBtn = new Button("Remove Preset");
        removePresetBtn.addClickListener(clickEvent -> {
            Preset preset = (Preset) presetTable.getValue();
            if (preset != null) {
                DatabaseService.getInstance().removePreset(preset);
                Page.getCurrent().reload();
            }
        });

        HorizontalLayout horizontalLayout = new HorizontalLayout();
        horizontalLayout.addComponent(addFolderBtn);
        horizontalLayout.addComponent(removePresetBtn);
        presetsGrid.addComponent(horizontalLayout);


    }

    private void setupWorkers(GridLayout workersGrid) {
        final Table workersTable = new Table();
        workersTable.setWidth("100%");
        workersTable.setHeight("100%");
        workersTable.addContainerProperty("Worker", Worker.class, null);
        workersTable.addContainerProperty("State", WorkerState.class, null);
        workersGrid.addComponent(workersTable, 0, 0);


        workersTable.markAsDirty();
        this.access(() -> new Timer().scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                workersTable.clear();
                for (Worker worker : WorkerManager.getInstance().getWorkers()) {
                    workersTable.addItem(new Object[]{worker, worker.state}, worker);
                }
                workersTable.refreshRowCache();
            }
        }, 1000, 1000));


    }

    private void setupWatchFolder(GridLayout watchFolderGrid) {

        watchFolderGrid.setColumns(watchFolderGrid.getColumns() + 1);
        watchFolderGrid.setRows(watchFolderGrid.getRows() + 1);

        Table watchFolderContentTable = new Table();
        watchFolderContentTable.setWidth("50%");
        watchFolderContentTable.setHeight("100%");
        watchFolderContentTable.addContainerProperty("Files", String.class, null);

        Table watchFolderTable = new Table();
        watchFolderTable.setWidth("50%");
        watchFolderTable.setHeight("100%");
        watchFolderTable.addContainerProperty("Folder", String.class, null);
        watchFolderTable.setSelectable(true);
        watchFolderTable.setImmediate(true);

        {
            int row = 1;
            for (String watchFolderService : watchFolderServices.keySet()) {
                watchFolderTable.addItem(new Object[]{watchFolderService}, row++);
            }
        }

        watchFolderTable.addValueChangeListener((Property.ValueChangeListener) event -> {
            int row = 1;
            System.out.println(watchFolderTable.getValue());
            for (String watchFolderService : watchFolderServices.get(watchFolderTable.getItem(watchFolderTable.getValue()).toString()).getFiles()) {
                watchFolderContentTable.clear();
                watchFolderContentTable.addItem(new Object[]{watchFolderService}, row++);
            }
        });

        watchFolderGrid.addComponent(watchFolderTable, 0, 0);
        watchFolderGrid.addComponent(watchFolderContentTable, 1, 0);

        watchFolderGrid.setRows(watchFolderGrid.getRows() + 1);
        Button addFolderBtn = new Button("Add Folder");
        addFolderBtn.addClickListener(clickEvent -> {
            NewWatchFolderSub sub = new NewWatchFolderSub();

            // Add it to the root component
            UI.getCurrent().addWindow(sub);
        });

        Button removeWatchFolderBtn = new Button("Remove Folder");
        removeWatchFolderBtn.addClickListener(clickEvent -> {
            WatchFolderService watchFolderService = WebEncodeUI.watchFolderServices.get(watchFolderTable.getItem(watchFolderTable.getValue()).toString());
            if (watchFolderService != null) {
                watchFolderService.wStop();
                WebEncodeUI.watchFolderServices.remove(watchFolderService.getWatchFolderPath(), watchFolderService);
                DatabaseService.getInstance().removeWatchFolder(watchFolderService.getWatchFolderPath());
                Page.getCurrent().reload();
            }
        });

        HorizontalLayout horizontalLayout = new HorizontalLayout();
        horizontalLayout.addComponent(addFolderBtn);
        horizontalLayout.addComponent(removeWatchFolderBtn);
        watchFolderGrid.addComponent(horizontalLayout);
    }

}


