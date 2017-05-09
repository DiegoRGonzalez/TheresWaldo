// (c) 2017 Jose Rivas-Garcia, Diego Gonzales and John Freeman

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

/* A class to develop a color histogram of images to filter for Waldo
 * 
 * This is meant to be a very simple algorithm that can be used to find 
 * Waldo. It follows from the very ultra common strategy of looking for 
 * locations in the images where red and white appear most, since Waldo 
 * will most likely be there. 
 */

public class Histogram {
    
    private float waldoConfidence = 0.0f;

    public Histogram () {}    

    public Histogram (BufferedImage image) {
	generateHistogram(image);
    }

    public float getWaldoConfidence(){
	return waldoConfidence;
    }
    
    /* Classify the image according to whether or not Waldo may be there
     * Currently classifies based on how much red appears in the image.
     * More robust classifications to follow, especially to include white
     * and pink (white-red bleed through).
     */
    private void generateHistogram(BufferedImage wIm) {
	float bitAmountf = 15.0f;
	Util util = new Util();
	BufferedImage waldoImage = util.deepCopy(wIm);
	BufferedImage writeImage = new BufferedImage(waldoImage.getWidth(), waldoImage.getHeight(), waldoImage.getType());

	ColorCorrection colCorrector = new ColorCorrection();

	waldoImage = colCorrector.normalize(waldoImage);
	
	// The number of pixels in the image to find frequencies of colors
	int waldoPixels = waldoImage.getWidth() * waldoImage.getHeight();

	for( int x = 0; x < waldoImage.getWidth(); x++){
	    for( int y = 0; y < waldoImage.getHeight(); y++){
		
		// Get the RGB value of the image
		Integer rgbVal = waldoImage.getRGB(x,y);

		// Separate the Red, Green and Blue values.
		Color col = new Color(rgbVal);
		col = colCorrector.make12Bit(col);
		
		Integer red = col.getRed();
		Integer green = col.getGreen();
		Integer blue = col.getBlue();
		
		Integer rbDiff = red-blue;
		Integer rgDiff = red-green;
		Integer bgDiff = Math.abs(blue-green);

		boolean wCheck = Math.abs(rbDiff) <= 3 && Math.abs(rgDiff) <= 3 && bgDiff <= 2;
		boolean rCheck = !wCheck && rbDiff >= 4 && rgDiff >= 4 && bgDiff <= 2;
		
		// Check red and white
		if(!(wCheck || rCheck)){
		    
		    waldoImage.setRGB(x, y, Color.BLACK.getRGB());
		   
		} else {
		    int maxChannel = 255;
		    
		    Color pixCol = (rCheck) ? new Color(maxChannel, 0, 0) : new Color(0, maxChannel, maxChannel);
		    waldoImage.setRGB(x,y,pixCol.getRGB());
		    
		    for(int i = x - 2; i <= x; i++){
			if(i >= 0){
			    for(int j = y - 2; j <= y; j++){
				if (j >= 0) {
				    Color col2 = new Color(waldoImage.getRGB(i,j));
				    
				    if (col2.getRGB() != Color.BLACK.getRGB()) {
					Color iterRGB = new Color(waldoImage.getRGB(i,j));

					if(!col2.equals(pixCol) || iterRGB.equals(Color.WHITE)) { 
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

	for( int x = 0; x < writeImage.getWidth(); x++){
	    for( int y = 0; y < writeImage.getHeight(); y++){
		Color color = new Color(writeImage.getRGB(x,y));
		if (!color.equals(Color.WHITE)){
		    waldoConfidence += 1.0f;
		}
	    }
	}
		
	waldoConfidence /= waldoPixels;
	
	
    }
}
