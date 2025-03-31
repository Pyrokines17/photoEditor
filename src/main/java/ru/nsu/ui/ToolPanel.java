package ru.nsu.ui;

import ru.nsu.filters.FilterList;
import ru.nsu.filters.FilterSwitch;
import ru.nsu.filters.Parameters;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class ToolPanel extends JToolBar {
    private final JFileChooser fileChooser = new JFileChooser();
    private final MenuPanel menuPanel;
    private final FrameWork parent;

    private final ButtonGroup toggleGroup = new ButtonGroup();
    private final List<JToggleButton> toggleButtons = new ArrayList<>();

    private BiConsumer<File, String> savingStrategy;
    private Consumer<File> loadStrategy;

    private final static String ICON_PATH = "/icons/";
    private final static Integer ICON_SIZE = 20;

    private final HashMap<FilterList, Parameters> parametersHashMap = new HashMap<>();
    private int lastSelected = -1;

    public ToolPanel(FrameWork parent, MenuPanel menuPanel) {
        super();

        this.menuPanel = menuPanel;
        this.parent = parent;

        addFileButton(() -> parent.onEvent(ToolPanelEventListener.EventType.FIT_SCREEN_BUTTON_CLICKED), "Fit", "Fit image to screen");
        addFileButton(() -> parent.onEvent(ToolPanelEventListener.EventType.REAL_SIZE_BUTTON_CLICKED), "Real", "Show image in real size");

        addFileButton(getLoadAct(), "Open", "Open image");
        addFileButton(getSaveAct(), "Save", "Save image");

        addFilterButton(FilterList.GRAYSCALE, "Grayscale");
        addFilterButton(FilterList.NEGATIVE, "Negative");
        addFilterButton(FilterList.GAMMA, "Gamma");
        addFilterButton(FilterList.ORDERED_DITHERING, "OrderedDithering");
        addFilterButton(FilterList.FSDITHERING, "FSDithering");
        addFilterButton(FilterList.GAUSSIAN_FILTER, "Gaussian filter");
        addFilterButton(FilterList.SHARPENING, "Sharpen");
        addFilterButton(FilterList.EMBOSS, "Emboss");
        addFilterButton(FilterList.SOBEL_HIGHLIGHTING, "Sobel highlighting");
        addFilterButton(FilterList.ROBERTS_HIGHLIGHTING, "Robert's highlighting");
        addFilterButton(FilterList.FSDITHERING_I, "Floyd-Steinberg dither");
        addFilterButton(FilterList.ORDERED_DITHER_I, "Ordered dither");
    }

    private void addFileButton(Runnable onPressAction, String name, String desc) {
        String lowerName = name.toLowerCase();
        String path = ICON_PATH + lowerName + ".png";
        JButton button = getButton(name, path);

        button.addActionListener(e -> onPressAction.run());
        button.setToolTipText(desc);
        this.add(button);

        menuPanel.addMenuItem(name, onPressAction, desc);
    }

    private void addFilterButton(FilterList filter, String name) {
        String lowerName = name.toLowerCase();
        String path = ICON_PATH + lowerName + ".png";
        JToggleButton button = getToggleButton(name, path);
        int index = toggleButtons.size();

        toggleGroup.add(button);
        toggleButtons.add(button);
        this.add(button);

        BiConsumer<FilterList, Integer> filterAction = getFilterAction();

        button.addActionListener(e -> {
            menuPanel.getRadioButton(index).setSelected(true);
            filterAction.accept(filter, index);
        });

        button.setToolTipText("Apply " + lowerName + " filter");

        menuPanel.addRadioButton(filter, name, this, filterAction);
    }

    private JButton getButton(String label, String iconPath) {
        URL url = getClass().getResource(iconPath);

        if (url == null) {
            return new JButton(label);
        }

        ImageIcon icon = new ImageIcon(url);
        Image image = icon.getImage().getScaledInstance(ICON_SIZE, ICON_SIZE, Image.SCALE_SMOOTH);

        return new JButton(new ImageIcon(image));
    }

    private JToggleButton getToggleButton(String name, String iconPath) {
        URL url = getClass().getResource(iconPath);

        if (url == null) {
            return new JToggleButton(name);
        }

        ImageIcon icon = new ImageIcon(url);
        Image image = icon.getImage().getScaledInstance(ICON_SIZE, ICON_SIZE, Image.SCALE_SMOOTH);

        return new JToggleButton(new ImageIcon(image));
    }

    private BiConsumer<FilterList, Integer> getFilterAction() {
        return (filter, index) -> {
            if (index == lastSelected) {
                toggleGroup.clearSelection();
                menuPanel.clearRadioButtons();
                parent.setFilter(null);
                parent.delFiltered();
                lastSelected = -1;
                return;
            }

            Parameters parameters = FilterSwitch.getParameters(filter);
            HashMap<String, String> types = parameters.getTypes();
            HashMap<String, String> borders = parameters.getBorders();
            HashMap<String, JTextField> fields = new HashMap<>();

            if (types != null) {
                JPanel dialog = new JPanel();
                dialog.setLayout(new BoxLayout(dialog, BoxLayout.Y_AXIS));

                Parameters oldParameters = parametersHashMap.get(filter);

                for (String name : types.keySet()) {
                    JPanel panel = new JPanel();
                    panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
                    panel.add(new JLabel("Enter "+name+": "));
                    String type = types.get(name);

                    if (type.equals("int") || type.equals("double")) {
                        JTextField field = new JTextField();

                        if (oldParameters != null) {
                            if (type.equals("int")) {
                                field.setText(Integer.toString(oldParameters.getIntParam(name)));
                            } else {
                                field.setText(Double.toString(oldParameters.getDoubleParam(name)));
                            }
                        }

                        panel.add(field);
                        fields.put(name, field);
                    }

                    dialog.add(panel);
                }

                boolean lastTry = false;

                while (!lastTry) {
                    lastTry = true;

                    int result = JOptionPane.showConfirmDialog(parent, dialog, "Enter parameters", JOptionPane.OK_CANCEL_OPTION);

                    if (result != JOptionPane.OK_OPTION) {
                        if (lastSelected == -1) {
                            toggleGroup.clearSelection();
                            menuPanel.clearRadioButtons();
                        } else {
                            toggleButtons.get(lastSelected).setSelected(true);
                            menuPanel.getRadioButton(lastSelected).setSelected(true);
                        }

                        return;
                    }

                    for (String name : fields.keySet()) {
                        JTextField field = fields.get(name);
                        String text = field.getText();

                        if (text.isEmpty()) {
                            JOptionPane.showMessageDialog(parent, "Empty field", "Error", JOptionPane.ERROR_MESSAGE);
                            lastTry = false;
                            break;
                        }

                        if (types.get(name).equals("int")) {
                            try {
                                int value = Integer.parseInt(text);
                                if (!checkIntBorders(value, borders.get(name))) {
                                    throw new RuntimeException();
                                }
                                parameters.setIntParam(name, value);
                            } catch (NumberFormatException e) {
                                JOptionPane.showMessageDialog(parent, "Invalid integer: "+text, "Error", JOptionPane.ERROR_MESSAGE);
                                lastTry = false;
                                break;
                            } catch (RuntimeException e) {
                                JOptionPane.showMessageDialog(parent, "Value is out of borders: "+borders.get(name), "Info", JOptionPane.INFORMATION_MESSAGE);
                                lastTry = false;
                                break;
                            }
                        } else {
                            try {
                                double value = Double.parseDouble(text);
                                if (!checkDoubleBorders(value, borders.get(name))) {
                                    throw new RuntimeException();
                                }
                                parameters.setDoubleParam(name, value);
                            } catch (NumberFormatException e) {
                                JOptionPane.showMessageDialog(parent, "Invalid double: "+text, "Error", JOptionPane.ERROR_MESSAGE);
                                lastTry = false;
                                break;
                            } catch (RuntimeException e) {
                                JOptionPane.showMessageDialog(parent, "Value is out of borders: "+borders.get(name), "Info", JOptionPane.INFORMATION_MESSAGE);
                                lastTry = false;
                                break;
                            }
                        }
                    }
                }

                parametersHashMap.put(filter, parameters);
            }

            parent.setFilter(FilterSwitch.getFilter(filter, parameters));
            parent.delFiltered();
            lastSelected = index;
        };
    }

    private boolean checkIntBorders(Integer value, String borders) {
        if (borders == null) {
            return true;
        }

        int min = Integer.parseInt(borders.split("\\|")[0]);
        int max = Integer.parseInt(borders.split("\\|")[1]);
        return value >= min && value <= max;
    }

    private boolean checkDoubleBorders(Double value, String borders) {
        if (borders == null) {
            return true;
        }

        double min = Double.parseDouble(borders.split("\\|")[0]);
        double max = Double.parseDouble(borders.split("\\|")[1]);
        return value >= min && value <= max;
    }

    private Runnable getSaveAct() {
        return () -> {
            if (savingStrategy == null) {
                JOptionPane.showMessageDialog(parent, "Saving error", "Internal error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            int result = fileChooser.showSaveDialog(parent);

            if (result == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();
                savingStrategy.accept(selectedFile, "PNG");
            }
        };
    }

    private Runnable getLoadAct() {
        return () -> {
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
        };
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
