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
    private Histogram hist;
    private float confLevel;

    //x and y correspond to the coordinate of top left corner of the subimage
    //in the original image. We can get the rest of the pixels this subimage
    //covers in the original image from the height and width of image.
    private int x;
    private int y;

    public Subimage(BufferedImage image, int x, int y) {
	this.image = image;
	this.x = x;
	this.y = y;
	this.hist = new Histogram(image);
	this.confLevel = hist.getWaldoConfidence();
    }
    
    public BufferedImage getImage() {
	return image;
    }

    public int[] getLocation() {
	return new int[]{x, y};
    }

    public int getX(){
	return x;
    }

    public int getY(){
	return y;
    }

    public float getConfLevel(){
	return confLevel;
    }

    public int getWidth() {
	return image.getWidth();
    }

    public int getHeight() {
	return image.getHeight();
    }

    public boolean writeImage(String path){
	return writeImage(path, getImage());
    }

    public static boolean writeImage(String path, BufferedImage image){
	try{
	    File outputfile = new File(path);
	    ImageIO.write(image, "jpg", outputfile);
	    return true;
	} catch(IOException e) {
	    return false;
	}	
    }

    public void setConfLevel(float conf){
	this.confLevel = hist.getWaldoConfidence();
    }

    public Histogram getHistogram(){
	return hist;
    }
}