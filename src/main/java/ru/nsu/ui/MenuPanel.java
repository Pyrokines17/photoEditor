package ru.nsu.ui;

import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;
import ru.nsu.filters.FilterList;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

public class MenuPanel extends JMenuBar {
    private final ButtonGroup radioGroup = new ButtonGroup();
    private final List<JRadioButtonMenuItem> radioButtons = new ArrayList<>();

    private final Parser parser = Parser.builder().build();
    private final HtmlRenderer renderer = HtmlRenderer.builder().build();

    private final JMenu fileMenu;
    private final JMenu filtersMenu;

    public MenuPanel(FrameWork parent) {
        super();

        fileMenu = new JMenu("File");
        JMenuItem exitItem = new JMenuItem("Exit");

        exitItem.addActionListener(e -> System.exit(0));
        fileMenu.add(exitItem);

        fileMenu.addSeparator();

        filtersMenu = new JMenu("Filters");

        JMenu helpMenu = new JMenu("Help");
        JMenuItem aboutItem = new JMenuItem("About");

        aboutItem.addActionListener(e -> {
            JDialog aboutDialog = getAboutDialog(parent);
            aboutDialog.setVisible(true);
        });
        helpMenu.add(aboutItem);

        add(fileMenu);
        add(filtersMenu);
        add(helpMenu);
    }

    public void addRadioButton(FilterList filter, String name, ToolPanel toolPanel, BiConsumer<FilterList, Integer> filterAction) {
        String lowerName = name.toLowerCase();
        JRadioButtonMenuItem radioButton = new JRadioButtonMenuItem(name);
        int index = radioButtons.size();

        radioGroup.add(radioButton);
        radioButtons.add(radioButton);
        filtersMenu.add(radioButton);

        radioButton.addActionListener(e -> {
            toolPanel.getToggleButton(index).setSelected(true);
            filterAction.accept(filter, index);
        });

        radioButton.setToolTipText("Apply " + lowerName + " filter");
    }

    public void addMenuItem(String name, Runnable action, String desc) {
        JMenuItem menuItem = new JMenuItem(name);
        menuItem.addActionListener(e -> action.run());
        menuItem.setToolTipText(desc);
        fileMenu.add(menuItem);
    }

    private JDialog getAboutDialog(FrameWork parent) {
        JDialog dialog = new JDialog(parent, "About", true);
        dialog.setSize(400, 300);
        dialog.setLocationRelativeTo(parent);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        JEditorPane textArea = new JEditorPane();
        textArea.setContentType("text/html");
        textArea.setEditable(false);

        try {
            String readmeContent = readmeMarkdown();
            Node document = parser.parse(readmeContent);
            String htmlContent = renderer.render(document);
            textArea.setText(htmlContent);
        } catch (IOException e) {
            textArea.setText("<html><body>Ошибка при загрузке файла: " +
                    e.getMessage() + "</body></html>");
        }

        JScrollPane scrollPane = new JScrollPane(textArea);
        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(e -> dialog.dispose());

        dialog.setLayout(new BorderLayout());

        dialog.add(scrollPane, BorderLayout.CENTER);
        dialog.add(closeButton, BorderLayout.SOUTH);

        return dialog;
    }

    private String readmeMarkdown() throws IOException {
        StringBuilder content = new StringBuilder();

        try (InputStream inputStream = getClass().getResourceAsStream("/about.md")) {
            if (inputStream == null) {
                throw new IOException("File not found: " + "/about.md");
            }

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
                String line;

                while ((line = reader.readLine()) != null) {
                    content.append(line).append("\n");
                }
            }
        }

        return content.toString();
    }

    public JRadioButtonMenuItem getRadioButton(int index) {
        return radioButtons.get(index);
    }

    public void clearRadioButtons() {
        radioGroup.clearSelection();
    }
}
