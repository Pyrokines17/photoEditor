package ru.nsu.ui;

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

    /**
     * Sets a new image to view.
     * If the given image is null, visible space will be painted in black.
     * Default view causes to "fit-screen" view.
     * If defaultView is set to false, viewer will show the last viewed part of the previous image.
     * But only if both of the images have the same size.
     *
     * @param newIm       - new image to view
     * @param defaultView - view the image in the default view, or save the view on the image
     */
    public void setImage(BufferedImage newIm, boolean defaultView) {
        // defaultView means "fit screen (panel)"

        // Draw black screen for no image
        image = newIm;

        if (image == null) {
            // make full defaultView
            setMaxVisibleRectSize();    // panelSize = getVisibleRectSize();
            repaint();
            revalidate();    // imageScrollPane.validate();
            return;
        }

        // Check if it is possible to use defaultView
        Dimension newImSize = new Dimension(image.getWidth(), image.getHeight());

        if (imageSize == null) {
            defaultView = true;
        } else if ((newImSize.height != imageSize.height) || (newImSize.width != imageSize.width)) {
            defaultView = true;
        }

        imageSize = newImSize;

        panelSize = getVisibleRectSize();

        if (defaultView) {
            setMaxVisibleRectSize();    // panelSize = getVisibleRectSize();

            double kh = (double) imageSize.height / panelSize.height;
            double kw = (double) imageSize.width / panelSize.width;
            double k = Math.max(kh, kw);

            panelSize.width = (int) (imageSize.width / k);
            panelSize.height = (int) (imageSize.height / k);

            this.setPreferredSize(panelSize);

            repaint();
            imageScrollPane.getViewport().setViewPosition(new Point(0, 0));
            revalidate();    // imageScrollPane.validate();
            imageScrollPane.repaint();    // wipe off the old picture in "spare" space
            imageScrollPane.revalidate();
        } else {
            // just change image
            imageScrollPane.paintAll(imageScrollPane.getGraphics());
        }

    }

    /**
     * Sets "fit-screen" view.
     */
    public void fitScreen() {
        setImage(image, true);
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

    public void setView(Rectangle rect) {
        setView(rect, 10);
    }

    private boolean setView(Rectangle rect, int minSize) {
        // should also take into account ScrollBars size
        if (image == null) {
            return false;
        }

        if (imageSize.width < minSize || imageSize.height < minSize) {
            return false;
        }

        if (minSize <= 0) {
            minSize = 10;
        }

        if (rect.width < minSize) {
            rect.width = minSize;
        }

        if (rect.height < minSize) {
            rect.height = minSize;
        }

        if (rect.x < 0) {
            rect.x = 0;
        }

        if (rect.y < 0) {
            rect.y = 0;
        }

        if (rect.x > imageSize.width - minSize) {
            rect.x = imageSize.width - minSize;
        }

        if (rect.y > imageSize.height - minSize) {
            rect.y = imageSize.height - minSize;
        }

        if ((rect.x + rect.width) > imageSize.width) {
            rect.width = imageSize.width - rect.x;
        }

        if ((rect.y + rect.height) > imageSize.height) {
            rect.height = imageSize.height - rect.y;
        }

        Dimension viewSize = getVisibleRectSize();

        double kw = (double) rect.width / viewSize.width;
        double kh = (double) rect.height / viewSize.height;
        double k = Math.max(kh, kw);

        int newPW = (int) (imageSize.width / k);
        int newPH = (int) (imageSize.height / k);
        // Check for size whether we can still zoom out
        if (newPW == (int) (newPW * (1 - 2 * zoomCoefficient))) {
            return setView(rect, minSize * 2);
        }

        panelSize.width = newPW;
        panelSize.height = newPH;

        revalidate();
        imageScrollPane.validate();

        int xc = rect.x + rect.width / 2, yc = rect.y + rect.height / 2;

        xc = (int) (xc / k);
        yc = (int) (yc / k);    // we need to center new view
        imageScrollPane.getViewport().setViewPosition(new Point(xc - viewSize.width / 2, yc - viewSize.height / 2));
        revalidate();    // imageScrollPane.validate();
        imageScrollPane.paintAll(imageScrollPane.getGraphics());

        return true;
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
        // Move image with mouse

        if (e.getModifiersEx() == InputEvent.BUTTON3_DOWN_MASK)        // ( (e.getModifiers() & MouseEvent.BUTTON3_MASK) == 0)
            return;

        // move picture using scroll
        Point scroll = imageScrollPane.getViewport().getViewPosition();
        scroll.x += (lastX - e.getX());
        scroll.y += (lastY - e.getY());

        //imageScrollPane.getViewport().setViewPosition(scroll);
        imageScrollPane.getHorizontalScrollBar().setValue(scroll.x);
        imageScrollPane.getVerticalScrollBar().setValue(scroll.y);
        imageScrollPane.repaint();

        // We changed the position of the underlying picture, take it into account
        lastX = e.getX() + (lastX - e.getX());	// lastX = lastX
        lastY = e.getY() + (lastY - e.getY());	// lastY = lastY
    }

    /**
     * When a rectangle is selected with pressed right button,
     * we zoom image to that rectangle
     */
    @Override
    public void mouseReleased(MouseEvent e) {
        if (e.getModifiersEx() != InputEvent.BUTTON3_DOWN_MASK)
            return;

        int x1 = e.getX();
        int y1 = e.getY();

        if (Math.abs(x1 - lastX) < 5 && Math.abs(y1 - lastY) < 5)
            return;

        double k = (double) imageSize.width / panelSize.width;

        int x0 = (int) (k * lastX);
        int y0 = (int) (k * lastY);

        x1 = (int) (k * x1);
        y1 = (int) (k * y1);

        int w = Math.abs(x1 - x0);
        int h = Math.abs(y1 - y0);

        if (x1 < x0) x0 = x1;
        if (y1 < y0) y0 = y1;

        Rectangle rect = new Rectangle(x0, y0, w, h);
        setView(rect);
    }


    /**
     * Process image click and call parent's methods
     */
    @Override
    public void mouseClicked(MouseEvent e) {
        if ((e.getModifiersEx() == InputEvent.BUTTON2_DOWN_MASK) || (e.getModifiersEx() == InputEvent.BUTTON3_DOWN_MASK))
            parentComponent.changeViewedImage();

        if (e.getModifiersEx() == InputEvent.BUTTON1_DOWN_MASK) {
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
