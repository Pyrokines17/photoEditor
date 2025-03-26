package ru.nsu.ui;

import ru.nsu.filters.Negative;
import ru.nsu.filters.Parameters;

import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class ToolPanel extends JToolBar {
    private final JFileChooser fileChooser = new JFileChooser();

    private BiConsumer<File, String> savingStrategy;
    private Consumer<File> loadStrategy;

    private final FrameWork parent;

    public ToolPanel(FrameWork parent) {
        super();

        this.parent = parent;

        JButton saveButton = getSaveButton();
        JButton loadButton = getLoadButton();

        JButton negativeButton = new JButton("Negative");

        negativeButton.addActionListener(notUsed -> {
            BufferedImage originalImage = parent.getOriginalImage();

            if (originalImage == null) {
                JOptionPane.showMessageDialog(parent, "No image to apply filter", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            parent.setFilter(new Negative(new Parameters(parent.getOriginalImage())));
        });

        JButton fitScreenButton = getButton("Fit screen", () -> {
            parent.onEvent(ToolPanelEventListener.EventType.FIT_SCREEN_BUTTON_CLICKED);
        });
        JButton realSizeButton = getButton("Real size", () -> {
            parent.onEvent(ToolPanelEventListener.EventType.REAL_SIZE_BUTTON_CLICKED);
        });

        add(saveButton);
        add(loadButton);

        add(negativeButton);
        add(fitScreenButton);
        add(realSizeButton);
    }

    private JButton getButton(String label, Runnable onPressAction) {
        JButton button = new JButton(label);
        button.addActionListener(e -> {
            onPressAction.run();
        });
        return button;
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

}
