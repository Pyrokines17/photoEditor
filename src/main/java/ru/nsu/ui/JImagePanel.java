package ru.nsu.ui;

import ru.nsu.filters.scales.Bilinear;
import ru.nsu.filters.scales.NearestNeighbor;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.image.BufferedImage;
import java.io.Serial;

import javax.swing.JPanel;
import javax.swing.JScrollPane;

/**
 * Image-viewer created in some JScrollPane
 *
 * @author Serge
 */
public class JImagePanel extends JPanel implements MouseListener, MouseMotionListener, MouseWheelListener {
    @Serial
    private static final long serialVersionUID = 3L;

    private final double zoomCoefficient = 0.05;        // scroll zoom coefficient

    private final JScrollPane imageScrollPane;
    private final FrameWork parentComponent;

    private Dimension panelSize;    // visible image size
    private BufferedImage image;    // image to view
    private Dimension imageSize;    // real image size
    private BufferedImage originalImage; // original image
    private Dimension originalImageSize; // original image size

    private Point lastViewPoint = null;
    private Scale scale = Scale.BILINEAR; // scale of the image

    private int lastX = 0, lastY = 0;        // last captured mouse coordinates

    /**
     * Creates default Image-viewer in the given JScrollPane.
     * Visible space will be painted in black.
     *
     * @param scrollPane - JScrollPane to add a new Image-viewer
     */
    public JImagePanel(JScrollPane scrollPane, FrameWork parentComponent) {
        if (scrollPane == null) {
            throw new RuntimeException("Scroll pane must be not null.");
        }

        imageScrollPane = scrollPane;
        imageScrollPane.setWheelScrollingEnabled(false);
        imageScrollPane.setDoubleBuffered(true);
        imageScrollPane.setViewportView(this);

        this.parentComponent = parentComponent;

        panelSize = getVisibleRectSize();    // adjust panel size to maximum visible in scrollPane
        imageScrollPane.validate();          // added panel to scrollPane
        setMaxVisibleRectSize();

        addMouseListener(this);
        addMouseMotionListener(this);
        addMouseWheelListener(this);
    }

    public JScrollPane getScrollPane() {
        return imageScrollPane;
    }

    public void setOriginalImage(BufferedImage originalImage) {
        this.originalImage = originalImage;
        this.originalImageSize = new Dimension(originalImage.getWidth(), originalImage.getHeight());
    }

    /**
     * Creates new Image-viewer of the given image in the given JScrollPane
     *
     * @param scrollPane - JScrollPane to add a new Image-viewer
     * @param newIm      - image to view
     */
    public JImagePanel(JScrollPane scrollPane, FrameWork parentComponent, BufferedImage newIm) {
        this(scrollPane, parentComponent);
        setImage(newIm, true);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (image == null) {
            g.setColor(Color.LIGHT_GRAY);
            g.fillRect(0, 0, getWidth(), getHeight());
        } else {
            g.drawImage(image, 0, 0, panelSize.width, panelSize.height, null);
        }
    }

    public void setScale(Scale scale) {
        this.scale = scale;
    }

    public BufferedImage getCurrentImage() {
        return image;
    }

    /**
     * Sets a new image to view.
     * If the given image is null, visible space will be painted in black.
     *
     * @param newIm       - new image to view
     * @param setPreviousViewPosition - view image from part witch previous image was viewed.
     */
    public void setImage(BufferedImage newIm, boolean setPreviousViewPosition) {

        Rectangle previousVisibleRect = null;
        Dimension previousPanelSize = null;

        imageScrollPane.getViewport().setViewPosition(new Point(0, 0));

        if (image != null && setPreviousViewPosition) {
            previousPanelSize = new Dimension(panelSize);
            if (lastViewPoint == null) {
                lastViewPoint = new Point(0, 0);
            }
            Point viewPoint = lastViewPoint;

            previousVisibleRect = new Rectangle(viewPoint.x, viewPoint.y, getVisibleRectSize().width, getVisibleRectSize().height);
        }

        // Draw black screen for no image
        image = newIm;

        if (image == null) {
            setMaxVisibleRectSize();
            repaint();
            revalidate();
            return;
        }

        Dimension newImSize = new Dimension(image.getWidth(), image.getHeight());

        imageSize = newImSize;
        panelSize = getVisibleRectSize();

        if (setPreviousViewPosition && canSetDefaultView(newImSize, previousVisibleRect)) {
            panelSize = previousPanelSize;
            imageScrollPane.getViewport().setViewPosition(new Point(previousVisibleRect.x, previousVisibleRect.y));
        }

        imageScrollPane.paintAll(imageScrollPane.getGraphics());
    }

    private boolean canSetDefaultView(Dimension newImSize, Rectangle previousVisibleRect) {
        if (imageSize == null) {
            return true;
        }
        return previousVisibleRect != null && (newImSize.height == imageSize.height) || (newImSize.width == imageSize.width);
    }

    /**
     * Sets "fit-screen" view.
     */
    public void fitScreen() {
        if (scale == Scale.COMMON) {
            commonResize();
        } else if (scale == Scale.NEAREST_NEIGHBOR) {
            nearestNeighborResize();
        } else if (scale == Scale.BILINEAR) {
            bilinearResize();
        }
    }

    private void commonResize() {
        setMaxVisibleRectSize();

        double kh = (double) imageSize.height / panelSize.height;
        double kw = (double) imageSize.width / panelSize.width;
        double k = Math.max(kh, kw);

        panelSize.width = (int) (imageSize.width / k);
        panelSize.height = (int) (imageSize.height / k);

        this.setPreferredSize(panelSize);

        repaint();
        imageScrollPane.getViewport().setViewPosition(new Point(0, 0));
        revalidate();
        imageScrollPane.repaint();    // wipe off the old picture in "spare" space
        imageScrollPane.revalidate();
    }

    private void nearestNeighborResize() {
        if (image == null) {
            return;
        }

        double scaleX = (double) getVisibleRectSize().width / imageSize.width;
        double scaleY = (double) getVisibleRectSize().height / imageSize.height;
        double scale = Math.min(scaleX, scaleY);

        int newWidth = (int) (imageSize.width * scale);
        int newHeight = (int) (imageSize.height * scale);

        BufferedImage resizedImage = NearestNeighbor.resize(image, newWidth, newHeight);

        panelSize.setSize(newWidth, newHeight);
        image = resizedImage;

        this.setPreferredSize(panelSize);
        revalidate();
        repaint();
        imageScrollPane.repaint();
        imageScrollPane.revalidate();
    }

    private void bilinearResize() {
        if (image == null) {
            return;
        }

        double scaleX = (double) getVisibleRectSize().width / imageSize.width;
        double scaleY = (double) getVisibleRectSize().height / imageSize.height;
        double scale = Math.min(scaleX, scaleY);

        int newWidth = (int) (imageSize.width * scale);
        int newHeight = (int) (imageSize.height * scale);

        BufferedImage resizedImage = Bilinear.resize(image, newWidth, newHeight);

        panelSize.setSize(newWidth, newHeight);
        image = resizedImage;

        this.setPreferredSize(panelSize);
        revalidate();
        repaint();
        imageScrollPane.repaint();
        imageScrollPane.revalidate();
    }

    /**
     * Sets "real-size" view.
     */
    public void realSize() {
        if (imageSize == null)
            return;

        double k = (double) imageSize.width / panelSize.width;
        Point scroll = imageScrollPane.getViewport().getViewPosition();
        scroll.x *= (int) k;
        scroll.y *= (int) k;

        panelSize.setSize(imageSize);

        revalidate();    // imageScrollPane.validate();
        imageScrollPane.getHorizontalScrollBar().setValue(scroll.x);
        imageScrollPane.getVerticalScrollBar().setValue(scroll.y);
        imageScrollPane.paintAll(imageScrollPane.getGraphics());
    }

    /**
     * @return Dimension object with the current view-size
     */
    private Dimension getVisibleRectSize() {
        // maximum size for panel with or without scrolling (inner border of the ScrollPane)
        return imageScrollPane.getSize();
    }

    /**
     * Sets panelSize to the maximum available view-size with hidden scroll bars.
     */
    private void setMaxVisibleRectSize() {
        // maximum size for panel without scrolling (inner border of the ScrollPane)
        panelSize = getVisibleRectSize();    // max size, but possibly with enabled scroll-bars
        revalidate();
        imageScrollPane.validate();
        panelSize = getVisibleRectSize();    // max size, without enabled scroll-bars
        revalidate();
    }

    @Override
    public Dimension getPreferredSize() {
        return panelSize;
    }

    /**
     * Change zoom when scrolling
     */
    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        if (image == null) {
            return;
        }

        if (!e.isControlDown()) {
            return;
        }

        // Zoom
        double k = 1 - e.getWheelRotation() * zoomCoefficient;

        // Check for minimum size where we can still increase size
        int newPW = (int) (panelSize.width * k);

        if (newPW == (int) (newPW * (1 + zoomCoefficient))) {
            return;
        }

        if (k > 1) {
            int newPH = (int) (panelSize.height * k);
            Dimension viewSize = getVisibleRectSize();
            int pixSizeX = newPW / imageSize.width;
            int pixSizeY = newPH / imageSize.height;

            if (pixSizeX > 0 && pixSizeY > 0) {
                int pixNumX = viewSize.width / pixSizeX;
                int pixNumY = viewSize.height / pixSizeY;
                if (pixNumX < 2 || pixNumY < 2)
                    return;
            }
        }

        panelSize.width = newPW;
        panelSize.height = (int) ((long) panelSize.width * imageSize.height / imageSize.width); // not to lose ratio

        // Move so that mouse position doesn't visibly change
        int x = (int) (e.getX() * k);
        int y = (int) (e.getY() * k);

        Point scroll = imageScrollPane.getViewport().getViewPosition();

        scroll.x -= e.getX();
        scroll.y -= e.getY();
        scroll.x += x;
        scroll.y += y;

        repaint();
        revalidate();
        imageScrollPane.validate();

        imageScrollPane.getHorizontalScrollBar().setValue(scroll.x);
        imageScrollPane.getVerticalScrollBar().setValue(scroll.y);

        imageScrollPane.repaint();

        lastViewPoint = new Point(imageScrollPane.getViewport().getViewPosition());

    }

    @Override
    public void mousePressed(MouseEvent e) {
        lastX = e.getX();
        lastY = e.getY();
    }

    /**
     * Move visible image part when dragging
     */
    @Override
    public void mouseDragged(MouseEvent e) {
        if (!e.isControlDown()) {
            return;
        }

        // move picture using scroll
        Point scroll = imageScrollPane.getViewport().getViewPosition();
        scroll.x += (lastX - e.getX());
        scroll.y += (lastY - e.getY());

        imageScrollPane.getHorizontalScrollBar().setValue(scroll.x);
        imageScrollPane.getVerticalScrollBar().setValue(scroll.y);
        imageScrollPane.repaint();

        lastViewPoint = new Point(imageScrollPane.getViewport().getViewPosition());
    }

    @Override
    public void mouseReleased(MouseEvent e) {
    }

    /**
     * Process image click and call parent's methods
     */
    @Override
    public void mouseClicked(MouseEvent e) {
        if ((e.getModifiersEx() == InputEvent.BUTTON2_DOWN_MASK) || (e.getModifiersEx() == InputEvent.BUTTON3_DOWN_MASK)) {
            parentComponent.changeViewedImage();
        }

        if (e.getModifiersEx() == InputEvent.SHIFT_DOWN_MASK) {
            if (imageSize == null) {
                parentComponent.clickImage(e.getX(), e.getY());
                return;
            }

            double k = (double) imageSize.width / panelSize.width;
            int x = (int) (k * e.getX());
            int y = (int) (k * e.getY());

            if ((x < imageSize.width) && (y < imageSize.height)) {
                parentComponent.clickImage(x, y);
            }
        }
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

    @Override
    public void mouseMoved(MouseEvent e) {
    }
}
