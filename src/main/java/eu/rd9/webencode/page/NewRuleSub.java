package eu.rd9.webencode.page;

import com.vaadin.server.Page;
import com.vaadin.ui.*;
import eu.rd9.webencode.data.Preset;
import eu.rd9.webencode.data.Rule;
import eu.rd9.webencode.services.DatabaseService;

import java.util.List;

/**
 * Created by renne on 18.12.2016.
 */
class NewRuleSub extends Window {
    public NewRuleSub() {
        super("Select folder to watch"); // Set window caption
        center();

        // Some basic content for the window
        VerticalLayout content = new VerticalLayout();
        content.setMargin(true);
        setContent(content);

        // Disable the close button
        setClosable(false);

        Rule rule = new Rule();
        TextField uidTextField = new TextField("UUID");
        uidTextField.setEnabled(false);
        uidTextField.setValue(rule.getUUIDStr());
        content.addComponent(uidTextField);


        TextField ruleNameTextField = new TextField("Name");
        content.addComponent(ruleNameTextField);

        TextField wildcardTextField = new TextField("Wildcard (RegEx)");
        content.addComponent(wildcardTextField);

        ComboBox presetsComboBox = new ComboBox("Preset");
        List<Preset> presets = DatabaseService.getInstance().getPresets();
        for (Preset preset : presets) {
            presetsComboBox.addItem(preset);
            presetsComboBox.select(preset);
        }
        content.addComponent(presetsComboBox);


        HorizontalLayout horizontalLayout = new HorizontalLayout();
        content.addComponent(horizontalLayout);

        Button addBtn = new Button("Add");
        addBtn.addClickListener((Button.ClickListener) event -> {
            rule.Rule_Name = ruleNameTextField.getValue();
            rule.Wirldcard = wildcardTextField.getValue();
            rule.Preset = (Preset) presetsComboBox.getValue();
            DatabaseService.getInstance().addRule(rule);
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
