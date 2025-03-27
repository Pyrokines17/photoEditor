package ru.nsu.ui;

import ru.nsu.filters.Filter;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.StrokeBorder;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class FrameWork extends JFrame implements ToolPanelEventListener {
    public static final int MIN_WINDOW_WIDTH = 640;
    public static final int MIN_WINDOW_HEIGHT = 480;

    public static final int INITIAL_WINDOW_WIDTH = 800;
    public static final int INITIAL_WINDOW_HEIGHT = 600;

    private static FrameWork INSTANCE;

    private BufferedImage originalImage = null;
    private BufferedImage filteredImage = null;

    private boolean filterApplied = false;
    private final JImagePanel panel;
    private Filter filter;

    public static FrameWork getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new FrameWork();
        }

        return INSTANCE;
    }

    FrameWork() {
        super("Photo editor");

        configureWindow();

        JScrollPane scrollPane = new JScrollPane();
        panel = new JImagePanel(scrollPane, this);

        scrollPane.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(4, 4, 4, 4),
                new StrokeBorder(getDashedStroke(), Color.BLACK)
        ));

        add(scrollPane, BorderLayout.CENTER);

        scrollPane.repaint();
        scrollPane.revalidate();

        MenuPanel menuPanel = new MenuPanel(this);
        setJMenuBar(menuPanel);

        ToolPanel toolPanel = getToolPanel(menuPanel);
        add(toolPanel, BorderLayout.NORTH);

        pack();
        setVisible(true);
    }

    private ToolPanel getToolPanel(MenuPanel menuPanel) {
        ToolPanel toolPanel = new ToolPanel(this, menuPanel);

        toolPanel.setSavingStrategy((file, format) -> {
            BufferedImage curImage = panel.getCurrentImage();

            if (curImage == null) {
                JOptionPane.showMessageDialog(this, "No image to save", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                ImageIO.write(panel.getCurrentImage(), format, file);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        toolPanel.setLoadStrategy((file) -> {
            try {
                originalImage = ImageIO.read(file);
                panel.setImage(originalImage, true);
                panel.realSize();
                delFiltered();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        return toolPanel;
    }

    private void configureWindow() {
        setPreferredSize(new Dimension(INITIAL_WINDOW_WIDTH, INITIAL_WINDOW_HEIGHT));
        setMinimumSize(new Dimension(MIN_WINDOW_WIDTH, MIN_WINDOW_HEIGHT));
        setResizable(true);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
    }

    public void changeViewedImage() {
        // placeholder
    }

    public void clickImage(int x, int y) {
        if (originalImage == null || filter == null) {
            return;
        }

        if (filterApplied) {
            panel.setImage(originalImage, true);
            filterApplied = false;
        } else {
            this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            filteredImage = filter.apply(originalImage, x, y);
            panel.setImage(filteredImage, true);
            this.setCursor(Cursor.getDefaultCursor());
            filterApplied = true;
        }
    }

    public void setFilter(Filter filter) {
        this.filter = filter;
    }

    public void delFiltered() {
        if (originalImage != null && filterApplied) {
            panel.setImage(originalImage, true);
            filterApplied = false;
        }

        filteredImage = null;
    }

    private BasicStroke getDashedStroke() {
        float[] dashPattern = {5f, 5f};
        return new BasicStroke(
                1,
                BasicStroke.CAP_BUTT,
                BasicStroke.JOIN_MITER,
                10f,
                dashPattern,
                0f
        );
    }

    @Override
    public void onEvent(EventType eventType) {
        switch (eventType) {
            case REAL_SIZE_BUTTON_CLICKED -> {
                panel.realSize();
            }
            case FIT_SCREEN_BUTTON_CLICKED -> {
                panel.fitScreen();
            }
        }
    }
}
