package ru.nsu.ui;

import ru.nsu.filters.FilterList;

import javax.swing.*;
import java.awt.event.ItemEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class MenuPanel extends JMenuBar {
    private final ButtonGroup radioGroup = new ButtonGroup();
    private final List<JRadioButtonMenuItem> radioButtons = new ArrayList<>();

    private final JMenu filtersMenu;

    public MenuPanel(FrameWork parent) {
        super();

        JMenu fileMenu = new JMenu("File");
        JMenuItem exitItem = new JMenuItem("Exit");

        exitItem.addActionListener(e -> System.exit(0));
        fileMenu.add(exitItem);

        filtersMenu = new JMenu("Filters");

        JMenu helpMenu = new JMenu("Help");
        JMenuItem aboutItem = new JMenuItem("About");

        aboutItem.addActionListener(e -> JOptionPane.showMessageDialog(parent, "This is a photo editor", "About", JOptionPane.INFORMATION_MESSAGE));
        helpMenu.add(aboutItem);

        add(fileMenu);
        add(filtersMenu);
        add(helpMenu);
    }

    public void addRadioButton(FilterList filter, String name, ToolPanel toolPanel, Consumer<FilterList> filterAction) {
        JRadioButtonMenuItem radioButton = new JRadioButtonMenuItem(name);
        int index = radioButtons.size();

        radioGroup.add(radioButton);
        radioButtons.add(radioButton);
        filtersMenu.add(radioButton);

        radioButton.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                toolPanel.getToggleButton(index).setSelected(true);
                filterAction.accept(filter);
            }
        });

        radioButton.setToolTipText("Apply " + name + " filter");
    }

    public JRadioButtonMenuItem getRadioButton(int index) {
        return radioButtons.get(index);
    }
}
