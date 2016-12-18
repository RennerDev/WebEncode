package eu.rd9.webencode.page;

import com.vaadin.server.Page;
import com.vaadin.ui.*;
import eu.rd9.webencode.data.Preset;
import eu.rd9.webencode.services.DatabaseService;

/**
 * Created by renne on 18.12.2016.
 */
class NewPresetSub extends Window {
    public NewPresetSub() {
        super("Select folder to watch"); // Set window caption
        center();

        // Some basic content for the window
        VerticalLayout content = new VerticalLayout();
        content.setMargin(true);
        setContent(content);

        // Disable the close button
        setClosable(false);

        Preset preset = new Preset();
        TextField uidTextField = new TextField("UUID");
        uidTextField.setEnabled(false);
        uidTextField.setValue(preset.getUUIDStr());
        content.addComponent(uidTextField);


        TextField presetNameTextField = new TextField("Name");
        content.addComponent(presetNameTextField);

        TextField presetFFmpgedParamsTextField = new TextField("FFmpeg Parameters");
        content.addComponent(presetFFmpgedParamsTextField);

        HorizontalLayout horizontalLayout = new HorizontalLayout();
        content.addComponent(horizontalLayout);

        Button addBtn = new Button("Add");
        addBtn.addClickListener((Button.ClickListener) event -> {
            preset.Preset_Name = presetNameTextField.getValue();
            preset.FFmpeg_Parameters = presetFFmpgedParamsTextField.getValue();
            DatabaseService.getInstance().addPreset(preset);
            close(); // Close the sub-window
            Page.getCurrent().reload();
        });
        horizontalLayout.addComponent(addBtn);

        Button closeBtn = new Button("Close");
        closeBtn.addClickListener((Button.ClickListener) event -> {
            close(); // Close the sub-window
        });
        horizontalLayout.addComponent(closeBtn);

    }


}
