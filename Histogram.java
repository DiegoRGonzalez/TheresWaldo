// (c) 2017 Jose Rivas-Garcia, Diego Gonzales and John Freeman

import java.io.File;
import java.io.IOException;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.awt.Color;
import java.util.Hashtable;
import java.util.*;
import java.lang.*;

/* A class to develop a color histogram of images to filter for Waldo
 * 
 * This is meant to be a very simple algorithm that can be used to find 
 * Waldo. It follows from the very ultra common strategy of looking for 
 * locations in the images where red and white appear most, since Waldo 
 * will most likely be there. 
 */

public class Histogram {

    public Histogram() {}

    /* Takes a vector of Subimages and classifies them as either Waldo or Not Waldo.
     * The method calls "classify image" which classifies according to how much
     * red and white appears in the image.
     *
     * Right now, we really need to think about maybe looping through this multiple times
     * with adjustable parameters for finding red and white. This way, we can continue to 
     * filter until only half (at most) of the images are left. 
     */
    public Vector<Subimage> classify(Vector<Subimage> subimages) {
	
	// A Vector holding only those images that belong to Waldo
	Vector<Subimage> filter = new Vector<Subimage>();
	
	for(int i = 0; i < subimages.size(); i++) {
	    Subimage image = subimages.get(i);
	    
	    // Classify a single image. If Waldo may be there, add it to filter.
	    if(classifyImage(image.getImage())){
		filter.add(image);
	    }
	}

	// Returned the filtered vector
	return filter;
    }

    /* Classify the image according to whether or not Waldo may be there
     * Currently classifies based on how much red appears in the image.
     * More robust classifications to follow, especially to include white
     * and pink (white-red bleed through).
     */
    public boolean classifyImage(BufferedImage waldoImage) {

	// Hashtable to keep count of Colors that appear (will be useful when doing more
	// intense work with Colors). Will change to keep frequencies, also, not counts.
	// Hashtable<Integer, Integer> waldoHist = new Hashtable<Integer, Integer>();
	
	// The number of pixels in the image to find frequencies of colors
	int waldoPixels = waldoImage.getWidth() * waldoImage.getHeight();
	
	// Count the red and white pixels
	int countRed = 0;
	int countWhite = 0;
	for( int x = 0; x < waldoImage.getWidth(); x++){
	    for( int y = 0; y < waldoImage.getHeight(); y++){
		
		// Get the RGB value of the image
		Integer color = waldoImage.getRGB(x,y);

		// Separate the Red, Green and Blue values.
		Color col = new Color(color);		
		Float red = (float) col.getRed()/255.0f;
		Float blue = (float) col.getBlue()/ 255.0f;
		Float green = (float) col.getGreen()/255.0f;

		// Perhaps will be useful later to test just how much more of a color there
		// is compared to another color.
		Float rgDiff = Math.abs(red - green);
		Float rbDiff = Math.abs(red - blue);
		Float bgDiff = Math.abs(blue - green);
		    
		// If the color has more red than any of the others, and the blue and green colors
		// are low, then it is definitely red. The constants need work, as they do not
		// capture all possible red values. We really need to fine tune this and make it
		// so the constants change according to how the picture is working.
		// One possible thing is to find the average hue of the color and work it in.
		if (red >= blue && red >= green && blue <= 0.2f && green <= 0.2f){ 
		    countRed += 1;
		}
		// In this case, white includes those colors with a pink hue. This helps with
		// bleed through, when the red pixels and the white pixels become one, so the
		// pixel is pink, not white.
		else if (red >= 0.7f  && blue >= 0.4f && green >= 0.4f){
		    countWhite += 1;
		}			
	    }
	}
	
	//	System.out.println(countRed);
	
	// Get the proportion of whtie and red pixels in the image.
	float whiteProp = (float) countWhite/ (float) waldoPixels;
	float redProp = (float) countRed/ (float) waldoPixels;
	
	
	//System.out.println(whiteProp + " " + redProp);
	
	// If more than 7% of the image is red and 1% is "white", then there is a chance
	// that Waldo is there. These numbers should be adjustable. However, for now they
	// seem to be working. This of course does not help to stop those images that 
	// have a lot of distractions. However, it does help get rid of those images that
	// we should never even try to look at. 
	if(redProp >= 0.07 && whiteProp >= 0.01f){
	    //  System.out.println("WALDO IS HERE");
	    return true;
	} else {
	    //System.out.println("WALDO IS NOT HERE");
	    return false;
	}	
	
    }

}