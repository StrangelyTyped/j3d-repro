import com.sun.j3d.utils.universe.SimpleUniverse;

import javax.media.j3d.*;
import javax.swing.*;
import javax.vecmath.Point3d;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class OffScreenRenderer {

    private final Canvas3D canvas;
    private final SimpleUniverse universe;
    private final BranchGroup group;
    private final ImageComponent2D buffer;
    private final BranchGroup bgGroup;
    private final Background background;

    private static final int RENDER_WIDTH = 336;
    private static final int RENDER_HEIGHT = 336;

    public static boolean ON_SCREEN = false;

    public OffScreenRenderer(){
        BufferedImage bImage = new BufferedImage(RENDER_WIDTH, RENDER_HEIGHT, BufferedImage.TYPE_INT_ARGB);
        buffer = new ImageComponent2D(ImageComponent.FORMAT_RGBA, bImage);
        buffer.setCapability(ImageComponent2D.ALLOW_IMAGE_WRITE);
        buffer.setCapability(ImageComponent2D.ALLOW_IMAGE_READ);

        canvas = new Canvas3D(SimpleUniverse.getPreferredConfiguration(), !ON_SCREEN);
        canvas.setSize(RENDER_WIDTH, RENDER_HEIGHT);
        canvas.getScreen3D().setPhysicalScreenHeight(1);
        canvas.getScreen3D().setPhysicalScreenWidth(1);
        if(!ON_SCREEN) {
            canvas.getScreen3D().setSize(RENDER_WIDTH, RENDER_HEIGHT);
            canvas.setOffScreenBuffer(buffer);
        }

        universe = new SimpleUniverse(canvas);

        group = new BranchGroup();
        group.setCapability(BranchGroup.ALLOW_DETACH);

        BoundingSphere bounds = new BoundingSphere(new Point3d(0.0, 0.0, 0.0), 100.0);

        bgGroup = new BranchGroup();
        bgGroup.setCapability(BranchGroup.ALLOW_DETACH);
        background = new Background();
        background.setImageScaleMode(Background.SCALE_FIT_MAX);
        background.setApplicationBounds(bounds);
        background.setCapability(Background.ALLOW_IMAGE_WRITE);
        bgGroup.addChild(background);

        universe.getViewingPlatform().setNominalViewingTransform();
    }

    public void addNode(Node child){
        group.addChild(child);
        universe.addBranchGraph(group);
    }

    public void reset(){
        universe.getLocale().removeBranchGraph(group);
        universe.getLocale().removeBranchGraph(bgGroup);
        removeChildren(group);
    }

    public void destroy(){
        reset();
        universe.cleanup();
    }

    public void setBackground(ImageComponent2D bg){
        background.setImage(bg);
        universe.addBranchGraph(bgGroup);
    }

    private void removeChildren(Group g){
        g.removeAllChildren();
    }

    public BufferedImage render() throws IOException {
        long start = System.nanoTime();


        if(ON_SCREEN){
            JFrame frame = new JFrame("Render");
            frame.setSize(RENDER_WIDTH, RENDER_HEIGHT);
            frame.getContentPane().setLayout(new BorderLayout());
            frame.getContentPane().add("Center", canvas);
            frame.setVisible(true);
        }else {
            canvas.renderOffScreenBuffer();
            canvas.waitForOffScreenRendering();
        }

        System.out.println("Rendering took " + ((System.nanoTime() - start) / 1000000) + "ms");
        return buffer.getImage();
    }

}
