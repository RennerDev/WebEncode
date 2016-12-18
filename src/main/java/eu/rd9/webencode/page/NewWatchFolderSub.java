package eu.rd9.webencode.page;

import com.vaadin.server.Page;
import com.vaadin.ui.*;
import eu.rd9.webencode.services.DatabaseService;
import eu.rd9.webencode.services.WatchFolderService;

import java.io.File;

/**
 * Created by renne on 18.12.2016.
 */
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
        for (File root : File.listRoots()) {
            if (root.isDirectory())
                tree.addItem(root);
        }

        TextField textField = new TextField();
        content.addComponent(textField);

        tree.addValueChangeListener(e -> {

            String value = "";
            try {
                value = tree.getParent(tree.getValue()).toString() + "\\" + e.getProperty().getValue() + "\\";
            } catch (Exception ex) {
                if (tree.getValue() != null)
                    value = tree.getValue().toString();
            }
            Notification.show("Value changed:",
                    value,
                    Notification.Type.TRAY_NOTIFICATION);

            textField.setValue(value);

            File path = new File(value);
            String[] subPaths = path.list((current, name) -> new File(current, name).isDirectory());
            if (subPaths == null || subPaths.length == 0) {
                tree.setChildrenAllowed(tree.getValue(), false);
                return;
            }

            tree.setChildrenAllowed(tree.getValue(), true);
            for (String p : subPaths) {
                File subFile = new File(value + "/" + p);
                if (subFile.isDirectory()) {
                    tree.addItem(subFile.getName());
                    tree.setParent(subFile.getName(), tree.getValue());
                }
            }
        });


        HorizontalLayout horizontalLayout = new HorizontalLayout();
        content.addComponent(horizontalLayout);

        Button addBtn = new Button("Add");
        addBtn.addClickListener((Button.ClickListener) event -> {
            String value = textField.getValue();
            DatabaseService.getInstance().addWatchFolder(value);
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
}
