package ru.nsu.ui;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.File;
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
        super("ICG Photo editor");

        configureWindow();

        JScrollPane scrollPane = new JScrollPane();
        JImagePanel panel = new JImagePanel(scrollPane, this);
        add(scrollPane, BorderLayout.CENTER);

        scrollPane.repaint();
        scrollPane.revalidate();

        ToolPanel toolPanel = new ToolPanel();
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

        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                Dimension size = scrollPane.getSize();
                System.out.println(size);
            }
        });

        try {
            panel.setImage(ImageIO.read(new File("./test_images/_test_image.jpg")), true);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    private void configureWindow() {
        setPreferredSize(new Dimension(INITIAL_WINDOW_WIDTH, INITIAL_WINDOW_HEIGHT));
        setMinimumSize(new Dimension(MIN_WINDOW_WIDTH, MIN_WINDOW_HEIGHT));
        setResizable(true);
        setLocation(100, 100);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        setLayout(new BorderLayout());
    }

    public void changeViewedImage() {
        // placeholder
    }

    public void clickImage(int x, int y) {
        // placeholder
    }
}
