import com.sun.j3d.utils.geometry.ColorCube;

import javax.imageio.ImageIO;
import javax.media.j3d.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class Main {
    public static final boolean ENABLE_BG = true;

    public static void main(String[] args) throws IOException {
        OffScreenRenderer renderer = new OffScreenRenderer();

        OrderedGroup group1 = new OrderedGroup();
        Transform3D t3d = new Transform3D();
        t3d.rotY(Math.PI/8);
        TransformGroup transformGroup = new TransformGroup(t3d);
        transformGroup.addChild(new ColorCube(0.5));
        group1.addChild(transformGroup);

        Transform3D t3d2 = new Transform3D();
        t3d2.rotX(Math.PI/8);
        TransformGroup transformGroup2 = new TransformGroup(t3d2);
        transformGroup2.addChild(new ColorCube(0.5));
        group1.addChild(transformGroup2);

        renderer.addNode(group1);
        ImageComponent2D bg = loadBackground();
        if(bg != null){
            renderer.setBackground(bg);
        }

        BufferedImage image1 = renderer.render();
        ImageIO.write(image1, "png", new FileOutputStream("render-1.png"));

        BufferedImage image2 = renderer.render();
        ImageIO.write(image2, "png", new FileOutputStream("render-2.png"));
        renderer.destroy();
    }


    private static ImageComponent2D loadBackground() throws IOException {
        if(!ENABLE_BG) {
            return null;
        }
        BufferedImage image = ImageIO.read(new File("./bg.jpg"));
        return new ImageComponent2D(ImageComponent.FORMAT_RGB, image);
    }
}
