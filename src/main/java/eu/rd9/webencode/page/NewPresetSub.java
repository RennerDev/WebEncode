package eu.rd9.webencode.page;

import com.vaadin.data.Item;
import com.vaadin.server.Page;
import com.vaadin.ui.*;
import eu.rd9.webencode.data.Preset;
import eu.rd9.webencode.data.PresetOption;
import eu.rd9.webencode.data.PresetParameter;
import eu.rd9.webencode.services.DatabaseService;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by renne on 18.12.2016.
 */
class NewPresetSub extends Window {
    public NewPresetSub() {
        super("Select folder to watch"); // Set window caption
        center();

        setWidth("30%");
        setHeight("60%");

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

        Table optionsTable = new Table("Options");
        optionsTable.setWidth("100%");
        optionsTable.setHeight("100%");
        optionsTable.setEditable(true);
        optionsTable.addContainerProperty("Option", String.class, null);
        optionsTable.addContainerProperty("Value", String.class, null);

        for (PresetOption presetOption : PresetOption.values())
        {
            String value = preset.presetParameter.getOptionValue(presetOption);
            if (value == null)
                optionsTable.addItem(new Object[]{presetOption.getUiName(), presetOption.getDefaultValue()}, presetOption);

            optionsTable.addItem(new Object[]{presetOption.getUiName(), presetOption.getDefaultValue()}, presetOption);
        }

        content.addComponent(optionsTable);

        HorizontalLayout horizontalLayout = new HorizontalLayout();
        content.addComponent(horizontalLayout);

        Button addBtn = new Button("Add");
        addBtn.addClickListener((Button.ClickListener) event -> {

            Map<String, String> map = new HashMap<>();
            for (Object itemID : optionsTable.getItemIds()) {

                System.out.println(itemID);
                String[] rows = optionsTable.getItem(itemID).toString().split(" ");

                try {
                    map.put(rows[0], rows[1]);
                } catch (Exception e)
                {

                }

            }

            for(PresetOption option : PresetOption.values())
            {
                String val = map.get(option.getUiName());
                if (val == null)
                    continue;

                preset.presetParameter.addOption(option, val);
            }

            preset.Preset_Name = presetNameTextField.getValue();
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
