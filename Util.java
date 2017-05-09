import java.util.Iterator;
import java.lang.Math;
import java.io.File;
import java.io.IOException;
import java.awt.image.BufferedImage;
import java.awt.Color;
import javax.imageio.ImageIO;
import java.util.*;
import java.awt.image.WritableRaster;
import java.awt.image.ColorModel;
import java.awt.Graphics;
import java.util.Vector;

public class Util {


    public Util() { }

    // copies a bufferedImage with no connection to the original 
    public static BufferedImage deepCopy(BufferedImage bi) {

	BufferedImage newImage = new BufferedImage(bi.getWidth(), bi.getHeight(),bi.getType());
	for (int x = 0; x < bi.getWidth(); x++){
	    for(int y = 0; y < bi.getHeight(); y++){
		newImage.setRGB(x,y,bi.getRGB(x,y));
	    }
	}

	return newImage;
    }

    
    //Returns an int array with the width and height of a subimage size
    public static void scaleImages(Vector<Subimage> images){
	final int numImages = images.size();
	Vector<Subimage> scaledImages = new Vector<Subimage>(numImages);

	for(int i = 0; i < numImages; i++){
	    BufferedImage before = images.get(i).getImage();
	    BufferedImage newImage = new BufferedImage(25,25,BufferedImage.TYPE_INT_RGB);

	    Graphics g = newImage.createGraphics();
	    g.drawImage(before,0,0,25,25,null);
	    g.dispose();
		
	    images.get(i).setImage(newImage);
	}

	
    }


}
