//(c) 2017 John Freeman, Diego Gonzalez, Jose Rivas
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.IOException;
import java.io.File;

//Simple class to save a buffered image and x and y value that corresponds to
//a small portion of the original image
public class Subimage {

    //Global variables
    private BufferedImage image;
    
    //x and y correspond to the coordinate of top left corner of the subimage
    //in the original image. We can get the rest of the pixels this subimage
    //covers in the original image from the height and width of image.
    private int x;
    private int y;

    public Subimage(BufferedImage image, int x, int y) {
	this.image = image;
	this.x = x;
	this.y = y;
    }
    
    public BufferedImage getImage() {
	return image;
    }

    public int[] getLocation() {
	return new int[]{x, y};
    }

    public int getWidth() {
	return image.getWidth();
    }

    public int getHeight() {
	return image.getHeight();
    }

    public boolean writeImage(String path){
	try{
	    File outputfile = new File(path);
	    ImageIO.write(getImage(), "jpg", outputfile);
	    return true;
	} catch(IOException e) {
	    return false;
	}	
    }
}