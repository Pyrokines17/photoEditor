package ru.nsu.ui;

import ru.nsu.filters.FilterList;
import ru.nsu.filters.FilterSwitch;
import ru.nsu.filters.Parameters;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.io.File;
import java.net.URL;
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

    private final static String ICON_PATH = "icons/";
    private final static Integer ICON_SIZE = 20;

    public ToolPanel(FrameWork parent, MenuPanel menuPanel) {
        super();

        this.parent = parent;

        JButton saveButton = getSaveButton();
        JButton loadButton = getLoadButton();

        add(saveButton);
        add(loadButton);

        addFilterButton(FilterList.GRAYSCALE, "Grayscale", menuPanel);
        addFilterButton(FilterList.NEGATIVE, "Negative", menuPanel);
    }

    private void addFilterButton(FilterList filter, String name, MenuPanel menuPanel) {
        String lowerName = name.toLowerCase();
        String path = ICON_PATH + lowerName + ".png";
        JToggleButton button = getToggleButton(name, path);
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

        button.setToolTipText("Apply " + lowerName + " filter");

        menuPanel.addRadioButton(filter, name, this, filterAction);
    }

    private JToggleButton getToggleButton(String name, String iconPath) {
        URL url = getClass().getClassLoader().getResource(iconPath);

        if (url == null) {
            return new JToggleButton(name);
        }

        ImageIcon icon = new ImageIcon(url);
        Image image = icon.getImage().getScaledInstance(ICON_SIZE, ICON_SIZE, Image.SCALE_SMOOTH);

        return new JToggleButton(new ImageIcon(image));
    }

    private Consumer<FilterList> getFilterAction() {
        return (filter) -> {
            Parameters parameters = FilterSwitch.getParameters(filter);
            HashMap<String, String> types = parameters.getTypes();

            if (types != null) {

            }

            parent.setFilter(FilterSwitch.getFilter(filter, parameters));
            parent.delFiltered();
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

            FileNameExtensionFilter filter = new FileNameExtensionFilter("Images",
                    "bmp", "jpeg", "png", "gif", "jpg");
            fileChooser.setFileFilter(filter);

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
