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
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

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
        addFilterButton(FilterList.TEST, "test");
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
            HashMap<String, Component> fields = new HashMap<>();

            if (types != null) {
                JPanel dialog = generateParameterSelectionDialog(filter, types, borders, fields);

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
                        boolean parsingSuccess;
                        Function<String, Number> parser;
                        Predicate<Number> checkValue;

                        switch (Parameters.getConstraintType(parameters.getBorders().get(name))) {
                            case BORDERS -> {
                                if (types.get(name).equals("int")) {
                                    checkValue = (val) -> checkIntBorders((Integer) val, parameters.getBorders().get(name));
                                    parser = Integer::parseInt;
                                }
                                else {
                                    checkValue = (val) -> checkDoubleBorders((Double) val, parameters.getBorders().get(name));
                                    parser = Double::parseDouble;
                                }
                            }
                            case ENUM -> {
                                if (types.get(name).equals("int")) {
                                    checkValue = (val) -> checkIntEnumValue((Integer) val, parameters.getBorders().get(name));
                                    parser = Integer::parseInt;
                                }
                                else {
                                    checkValue = (val) -> checkDoubleEnumValue((Double) val, parameters.getBorders().get(name));
                                    parser = Double::parseDouble;
                                }
                            }
                            default -> throw new IllegalArgumentException("Unknown constraint type.");
                        }

                        parsingSuccess = parseParameter(fields, name, parser, checkValue, parameters);
                        if (!parsingSuccess) {
                            lastTry = false;
                            break;
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

    private boolean checkIntEnumValue(Integer val, String s) {
        return Arrays.stream(s.split(",")).map((Integer::parseInt)).anyMatch(enumVal -> enumVal.equals(val));
    }

    private boolean checkDoubleEnumValue(Double val, String s) {
        return Arrays.stream(s.split(",")).map((Double::parseDouble)).anyMatch(enumVal -> enumVal.equals(val));
    }

    private boolean parseParameter(HashMap<String, Component> fields, String name, Function<String, Number> parser,
                                   Predicate<Number> checkValue, Parameters parameters) {
        String text = getTextFromComponent(fields.get(name));

        if (text.isEmpty()) {
            JOptionPane.showMessageDialog(parent, "Empty field", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        try {
            Number value = parser.apply(text);
            if (!checkValue.test(value)) {
                throw new RuntimeException();
            }
            if (value instanceof Integer) {
                parameters.setIntParam(name, (Integer) value);
            }
            else if (value instanceof Double) {
                parameters.setDoubleParam(name, (Double) value);
            }
            else {
                throw new RuntimeException("Value of invalid type.");
            }

            return true;
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(parent, "Invalid value: " + text + " for parameter: " + name, "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        } catch (RuntimeException e) {
            JOptionPane.showMessageDialog(parent, "Value " + name + " is out of borders: "+parameters.getBorders().get(name), "Info", JOptionPane.INFORMATION_MESSAGE);
            return false;
        }
    }

    private String getTextFromComponent(Component component) {
        if (component instanceof JTextField) {
            return ((JTextField)component).getText();
        }
        if (component instanceof JComboBox<?>) {
            return (String)((JComboBox<?>) component).getSelectedItem();
        }
        throw new IllegalArgumentException("Unknown component.");
    }

    private JPanel generateParameterSelectionDialog(FilterList filter, HashMap<String, String> types, HashMap<String, String> borders, HashMap<String, Component> fields) {
        JPanel dialog = new JPanel();
        dialog.setLayout(new BoxLayout(dialog, BoxLayout.Y_AXIS));

        Parameters oldParameters = parametersHashMap.get(filter);

        for (String name : types.keySet()) {
            JPanel panel = new JPanel();
            panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
            panel.add(new JLabel("Enter "+name+": "));
            String type = types.get(name);

            if (type.equals("int") || type.equals("double")) {
                switch (Parameters.getConstraintType(borders.get(name))) {
                    case BORDERS -> {
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
                    case ENUM -> {
                        String[] options = borders.get(name).split(",");
                        JComboBox<String> dropdown = new JComboBox<>(options);
                        panel.add(dropdown);
                        fields.put(name, dropdown);
                    }
                }
            }

            dialog.add(panel);
        }
        return dialog;
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
