package ru.nsu.ui;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.StrokeBorder;
import java.awt.*;
import java.io.IOException;

public class FrameWork extends JFrame {
    public static final int MIN_WINDOW_WIDTH = 640;
    public static final int MIN_WINDOW_HEIGHT = 480;

    public static final int INITIAL_WINDOW_WIDTH = 800;
    public static final int INITIAL_WINDOW_HEIGHT = 600;

    private static FrameWork INSTANCE;

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
        JImagePanel panel = new JImagePanel(scrollPane, this);

        scrollPane.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(4, 4, 4, 4),
                new StrokeBorder(getDashedStroke(), Color.BLACK)
        ));

        add(scrollPane, BorderLayout.CENTER);

        scrollPane.repaint();
        scrollPane.revalidate();

        ToolPanel toolPanel = new ToolPanel(this);

        toolPanel.setLoadStrategy((file) -> {
            try {
                panel.setImage(ImageIO.read(file), true);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        add(toolPanel, BorderLayout.NORTH);

        pack();
        setVisible(true);
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
        // placeholder
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
}
