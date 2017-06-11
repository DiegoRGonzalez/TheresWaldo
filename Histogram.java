// (c) 2017 Jose Rivas-Garcia, Diego Gonzalez and John Freeman
import java.io.File;
import java.io.IOException;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.awt.Color;
import java.util.Hashtable;
import java.util.*;
import java.lang.*;
import java.awt.image.WritableRaster;
import java.awt.image.ColorModel;

/* A class to develop a color histogram of images to help filter for Waldo. This class determines how much
 * red and white appears in an image, which in turn is used to determine if Waldo truly is in that picture
 * in other classes.
 */

public class Histogram {
    
    private float redWhiteProp = 0.0f;

    public Histogram () {}    

    public Histogram (BufferedImage image) {
	generateHistogram(image);
    }
    
    /* Currently creates a color histogram based on how much red and white appear together in the image. It loops
     * through the image editing out locations that are not red or white, and then determines where red and white
     * congregate the most.
     */
    private void generateHistogram(BufferedImage wIm) {
	int width = wIm.getWidth();
	int height = wIm.getHeight();
	
	BufferedImage waldoImage = Util.deepCopy(wIm);
	BufferedImage writeImage = new BufferedImage(width, height, waldoImage.getType());
	
	// The number of pixels in the image to find frequencies of colors
	int waldoPixels = width * height;

	// Loop through the image to find locations with red and white pixel congreations.
	for( int x = 0; x < width; x++){
	    for( int y = 0; y < height; y++){

		// Get the color from the given position.
		Color col = new Color(waldoImage.getRGB(x,y));

		boolean wCheck = Util.isWhite(col);
		boolean rCheck = Util.isRed(col);
		
		// If it is neither red nor white, then ignore the pixel and set it to BLACK
		if(!(wCheck || rCheck)){
		    
		    waldoImage.setRGB(x, y, Color.BLACK.getRGB());
		   
		} else {
		    
		    // Set the pixel at the current location to be either RED if it is red or 
		    // BLUE if it is white.
		    int maxChannel = 255;
		    Color pixCol = (rCheck) ? new Color(maxChannel, 0, 0) : new Color(0, 0, maxChannel);
		    waldoImage.setRGB(x,y,pixCol.getRGB());
		    
		    // From the current position, look at the square (size 5*5) before your current location
		    // to figure out if a lot of red and white are congregating together.
		    for(int i = x - 4; i <= x; i++){
			if(i >= 0){
			    for(int j = y - 4; j <= y; j++){
				if (j >= 0) {
				    Color col2 = new Color(waldoImage.getRGB(i,j));
				    
				    if (col2.getRGB() != Color.BLACK.getRGB()) {
					Color iterRGB = new Color(waldoImage.getRGB(i,j));

					if(!col2.equals(pixCol) || iterRGB.getRGB() == Color.WHITE.getRGB()) { 
					    writeImage.setRGB(x, y, Color.WHITE.getRGB());
					    writeImage.setRGB(i, j, Color.WHITE.getRGB());
					}
				    }
				}
			    }
			}   
		    }
		}		
	    }
	}

	// Find the total proportion of red and white pixels that appeared in the image in groups and save it
	// to the global variable.
	for( int x = 0; x < width; x++){
	    for( int y = 0; y < height; y++){
		Color color = new Color(writeImage.getRGB(x,y));
		if (color.getRed() > 20){
		    redWhiteProp += 1.0f;
		}
	    }
	}
		
	redWhiteProp /= waldoPixels;
	
	
    }

    // Accessor method   
    public float getRedWhiteProp(){
	return redWhiteProp;
    }
}
