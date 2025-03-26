package ru.nsu.ui;

import ru.nsu.filters.FilterList;
import ru.nsu.filters.FilterSwitch;
import ru.nsu.filters.Parameters;

import javax.swing.*;
import java.awt.event.ItemEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class ToolPanel extends JToolBar {
    private final JFileChooser fileChooser = new JFileChooser();
    private final FrameWork parent;

    private final ButtonGroup toggleGroup = new ButtonGroup();
    private final List<JToggleButton> toggleButtons = new ArrayList<>();

    private BiConsumer<File, String> savingStrategy;
    private Consumer<File> loadStrategy;

    public ToolPanel(FrameWork parent, MenuPanel menuPanel) {
        super();

        this.parent = parent;

        JButton saveButton = getSaveButton();
        JButton loadButton = getLoadButton();

        add(saveButton);
        add(loadButton);

        addFilterButton(FilterList.NEGATIVE, "Negative", menuPanel);
    }

    private void addFilterButton(FilterList filter, String name, MenuPanel menuPanel) {
        JToggleButton button = new JToggleButton(name);
        int index = toggleButtons.size();

        toggleGroup.add(button);
        toggleButtons.add(button);
        this.add(button);

        Consumer<FilterList> filterAction = getFilterAction();

        button.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                menuPanel.getRadioButton(index).setSelected(true);
                filterAction.accept(filter);
            }
        });

        button.setToolTipText("Apply " + name + " filter");

        menuPanel.addRadioButton(filter, name, this, filterAction);
    }

    private Consumer<FilterList> getFilterAction() {
        return (filter) -> {
            Parameters parameters = FilterSwitch.getParameters(filter);
            HashMap<String, String> types = parameters.getTypes();

            if (types != null) {

            }

            parent.setFilter(FilterSwitch.getFilter(filter, parameters));
        };
    }

    private JButton getSaveButton() {
        JButton saveButton = new JButton("Save");

        saveButton.addActionListener(notUsed -> {
            if (savingStrategy == null) {
                JOptionPane.showMessageDialog(parent, "Saving error", "Internal error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            int result = fileChooser.showSaveDialog(parent);

            if (result == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();
                savingStrategy.accept(selectedFile, "PNG");
            }
        });

        return saveButton;
    }

    private JButton getLoadButton() {
        JButton loadButton = new JButton("Open");

        loadButton.addActionListener(notUsed -> {
            if (loadStrategy == null) {
                JOptionPane.showMessageDialog(parent, "Loading error", "Internal error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            int result = fileChooser.showOpenDialog(parent);

            if (result == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();
                loadStrategy.accept(selectedFile);
            }
        });

        return loadButton;
    }

    public void setSavingStrategy(BiConsumer<File, String> savingStrategy) {
        this.savingStrategy = savingStrategy;
    }

    public void setLoadStrategy(Consumer<File> loadStrategy) {
        this.loadStrategy = loadStrategy;
    }

    public JToggleButton getToggleButton(int index) {
        return toggleButtons.get(index);
    }
}
