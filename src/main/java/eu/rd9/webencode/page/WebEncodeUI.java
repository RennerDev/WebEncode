package eu.rd9.webencode.page;

import com.vaadin.annotations.Theme;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.server.Page;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.*;
import eu.rd9.webencode.config.Config;
import eu.rd9.webencode.config.Settings;
import eu.rd9.webencode.data.Preset;
import eu.rd9.webencode.data.Rule;
import eu.rd9.webencode.services.DatabaseService;
import eu.rd9.webencode.services.WatchFolderService;
import eu.rd9.webencode.workers.Worker;
import eu.rd9.webencode.workers.WorkerManager;
import eu.rd9.webencode.workers.WorkerState;
import eu.rd9.webencode.workers.Workers;

import java.io.File;
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
            label.setWidth("20%");
            settingsGrid.addComponent(label, 0, row);

            TextField textField = new TextField();
            textField.setValue(config.getSetting(setting));
            textField.setWidth("20%");
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

        for (Rule rule : DatabaseService.getInstance().getRules())
        {
            rulesTable.addItem(new Object[]{rule,rule.Preset}, rule);
        }


        rulesTable.addValueChangeListener((Property.ValueChangeListener) event -> {

            verticalLayout.removeAllComponents();

            for(java.lang.reflect.Field field : Rule.class.getFields())
            {
                TextField textField = new TextField(field.getName().replace("_", " "));
                try {
                    String val = field.get(rulesTable.getValue()).toString();
                    textField.setValue(val.toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }

                verticalLayout.addComponent(textField);
            }
        });

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

        for ( Preset preset : DatabaseService.getInstance().getPresets())
        {
            presetTable.addItem(new Object[]{preset}, preset);
        }


        presetTable.addValueChangeListener((Property.ValueChangeListener) event -> {

            verticalLayout.removeAllComponents();

            for(java.lang.reflect.Field field : Preset.class.getFields())
            {
                TextField textField = new TextField(field.getName().replace("_", " "));
                try {
                    String val = field.get(presetTable.getValue()).toString();
                    textField.setValue(val.toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }

                verticalLayout.addComponent(textField);
            }
        });



    }

    private void setupWorkers(GridLayout workersGrid) {
        Table workersTable = new Table();
        workersTable.setWidth("25%");
        workersTable.setHeight("25%");
        workersTable.addContainerProperty("Worker", Worker.class, null);
        workersTable.addContainerProperty("State", WorkerState.class, null);
        workersGrid.addComponent(workersTable, 0,0);

        for (Worker worker : WorkerManager.getInstance().getWorkers())
        {
            workersTable.addItem(new Object[]{worker, worker.state}, worker);
        }

        Button addWorkerBtn = new Button("Add Worker");
        addWorkerBtn.addClickListener(clickEvent -> {
            NewWorkerSub sub = new NewWorkerSub();

            // Add it to the root component
            UI.getCurrent().addWindow(sub);
        });

        workersGrid.setRows(workersGrid.getRows() + 1);
        workersGrid.addComponent(addWorkerBtn, 0 ,1);

        Button removeWorkerBtn = new Button("Remove Worker");
        workersGrid.setColumns(workersGrid.getColumns() + 1);
        workersGrid.addComponent(removeWorkerBtn, 1,1);
    }

    private void setupWatchFolder(GridLayout watchFolderGrid) {

        watchFolderGrid.setColumns(watchFolderGrid.getColumns() + 1);
        watchFolderGrid.setRows(watchFolderGrid.getRows() + 1);

        Table watchFolderContentTable = new Table();
        watchFolderContentTable.setWidth("25%");
        watchFolderContentTable.setHeight("25%");
        watchFolderContentTable.addContainerProperty("Files", String.class, null);

        Table watchFolderTable = new Table();
        watchFolderTable.setWidth("75%");
        watchFolderTable.setHeight("75%");
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
            if (watchFolderService != null)
            {
                watchFolderService.wStop();
                WebEncodeUI.watchFolderServices.remove(watchFolderService.getWatchFolderPath(), watchFolderService);
                Page.getCurrent().reload();
            }
        });

        HorizontalLayout horizontalLayout = new HorizontalLayout();
        horizontalLayout.addComponent(addFolderBtn);
        horizontalLayout.addComponent(removeWatchFolderBtn);
        watchFolderGrid.addComponent(horizontalLayout);
    }

    class NewWatchFolderSub extends Window {
        public NewWatchFolderSub() {
            super("Select folder to watch"); // Set window caption
            center();

            // Some basic content for the window
            VerticalLayout content = new VerticalLayout();
            content.setMargin(true);
            setContent(content);

            // Disable the close button
            setClosable(false);

            Tree tree = new Tree();
            content.addComponent(tree);
            createRootFolderTree(tree, File.listRoots(), null);

            TextField textField = new TextField();
            content.addComponent(textField);

            tree.addValueChangeListener(e -> {

                String value = "";
                try {
                    value = tree.getParent(tree.getValue()).toString() + "\\" + e.getProperty().getValue() + "\\";
                } catch (Exception ex) {
                    value = tree.getValue().toString();
                }
                Notification.show("Value changed:",
                        value,
                        Notification.Type.TRAY_NOTIFICATION);

                textField.setValue(value);
            });


            HorizontalLayout horizontalLayout = new HorizontalLayout();
            content.addComponent(horizontalLayout);

            Button addBtn = new Button("Add");
            addBtn.addClickListener((Button.ClickListener) event -> {
                String value = textField.getValue();
                WatchFolderService watchFolderService = new WatchFolderService(value);
                WebEncodeUI.watchFolderServices.put(value, watchFolderService);
                close();
                Page.getCurrent().reload();
            });

            Button closeBtn = new Button("Close");
            closeBtn.addClickListener((Button.ClickListener) event -> {
                close(); // Close the sub-window
            });

            horizontalLayout.addComponent(addBtn);
            horizontalLayout.addComponent(closeBtn);
        }

        private void createRootFolderTree(Tree tree, File[] paths, Object lastRoot) {
            for (File path : paths) {

                if (!path.isDirectory() || !path.canRead())
                    continue;

                Item pathItem = tree.addItem(path);

                if (lastRoot != null)
                    tree.setParent(path, lastRoot);

                String[] subPaths = path.list((current, name) -> new File(current, name).isDirectory());
                if (subPaths == null || subPaths.length == 0) {
                    tree.setChildrenAllowed(path, false);
                    continue;
                }

                List<File> subPathes = new ArrayList<>();
                for (String subPath : subPaths) {
                    File f = new File(path + "/" + subPath);
                    if (!f.isDirectory() || !f.canRead())
                        continue;
                    subPathes.add(f);
                }

                //createRootFolderTree(tree, subPathes.toArray(new File[subPathes.size()]), path);

            }
        }
    }

    class NewWorkerSub extends Window {
        public NewWorkerSub() {
            super("Create a new worker"); // Set window caption
            center();

            // Some basic content for the window
            VerticalLayout content = new VerticalLayout();
            content.setMargin(true);
            setContent(content);

            // Disable the close button
            setClosable(false);

            ComboBox comboBox = new ComboBox("Worker Type");
            for (Workers worker : Workers.values()) {
                comboBox.addItem(worker.getWorker());
            }
            content.addComponent(comboBox);


            HorizontalLayout horizontalLayout = new HorizontalLayout();
            content.addComponent(horizontalLayout);




            Button addBtn = new Button("Add");
            addBtn.addClickListener((Button.ClickListener) event -> {
                WorkerManager.getInstance().startWorker((Worker) comboBox.getValue());
                close();
                Page.getCurrent().reload();
            });

            Button closeBtn = new Button("Close");
            closeBtn.addClickListener((Button.ClickListener) event -> {
                close(); // Close the sub-window
            });

            horizontalLayout.addComponent(addBtn);
            horizontalLayout.addComponent(closeBtn);
        }

    }

}


