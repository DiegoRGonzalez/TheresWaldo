// (c) 2017 Jose Rivas-Garcia, Diego Gonzales and John Freeman

import java.io.File;
import java.io.IOException;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.awt.Color;
import java.util.Hashtable;
import java.util.*;
import java.lang.*;

public class Classifier {
    
    public Classifier () {}

    
    public class Triple<F, S, T> {

	private F first;
	private S second;
	private T third;
	
	public Triple(F first, S second, T third) {
	    this.first = first;
	    this.second = second;
	    this.third = third;
	}
	
	public Triple() {
	}

	@Override
	public boolean equals(Object o) {
	    if (!(o instanceof Triple)) {
		return false;
	    }
	    Triple<?, ?, ?> p = (Triple<?, ?, ?>) o;
	    return first.equals(p.first) && second.equals(p.second) && third.equals(p.third);
	}    
    }

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
	//if(true) return subimages;
	final int numImages = subimages.size();
	int numLeft = subimages.size();
	
	Float redProp = 0.02f;
	Float whtProp = 0.01f;
	
	Float rRThresh = 0.25f;
	Float rGThresh = 0.2f;
	Float rBThresh = 0.2f;
	
	Float wRThresh = 0.4f;
	Float wGThresh = 0.2f;
	Float wBThresh = 0.2f;

	System.out.println(numLeft + " " + numImages/2);
	while( numLeft > numImages/2 || numLeft < 10){
	    
	    Triple<Float, Float, Float> redRGBThresholds = new Triple<Float, Float, Float>(rRThresh, rGThresh, rBThresh);
	    Triple<Float, Float, Float> whtRGBThresholds = new Triple<Float, Float, Float>(wRThresh, wGThresh, wBThresh);

	    for(int i = 0; i < subimages.size(); i++) {
		Subimage image = subimages.get(i);
		
		// Classify a single image. If Waldo may be there, add it to filter.
		if(classifyImage(image.getImage(), redRGBThresholds, whtRGBThresholds, redProp, whtProp)){
		    filter.add(image);
		}
	    }
	    
	    numLeft = filter.size();
	    
	    //redProp += 0.01f;
	    //whtProp += 0.01f;
	    
	    if(numLeft < 10) {
		rRThresh -= 0.1f;
		rGThresh -= 0.1f;
		rBThresh -= 0.1f;
		
		wRThresh -= 0.1f;
		wGThresh -= 0.1f;
		wBThresh -= 0.1f;
	    } else{
		subimages = new Vector<Subimage>(filter);
		filter = new Vector<Subimage>();
		rRThresh += 0.05f;
		rGThresh += 0.05f;
		rBThresh += 0.05f;
	    
		wRThresh += 0.05f;
		wGThresh += 0.05f;
		wBThresh += 0.05f;
	    }
	    System.out.println(numLeft + " " + numImages/2);
	}

	// Returned the filtered vector
	return subimages;
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
		// Note: Add check to be sure red is greater than blue or green.
		else if (red >= 0.7f && blue >= 0.4f && green >= 0.4f){
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

    /* Classify the image according to whether or not Waldo may be there
     * Currently classifies based on how much red appears in the image.
     * More robust classifications to follow, especially to include white
     * and pink (white-red bleed through).
     */
    public boolean classifyImage(BufferedImage waldoImage, Triple<Float, Float, Float> redThresholds, Triple<Float, Float, Float> whiteThresholds, Float redProp, Float whiteProp) {

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
		    
		Triple<Float, Float, Float> colorTriple = new Triple<Float, Float, Float>(red, green, blue);

		// If the color has more red than any of the others, and the blue and green colors
		// are low, then it is definitely red. The constants need work, as they do not
		// capture all possible red values. We really need to fine tune this and make it
		// so the constants change according to how the picture is working.
		// One possible thing is to find the average hue of the color and work it in.
		if (red >= blue && red >= green && red >= redThresholds.first && blue <= redThresholds.third && green <= redThresholds.second){ 
		    countRed += 1;
		}
		// In this case, white includes those colors with a pink hue. This helps with
		// bleed through, when the red pixels and the white pixels become one, so the
		// pixel is pink, not white.
		// Note: Add check to be sure red is greater than blue or green.
		else if (red >= whiteThresholds.first && blue >= whiteThresholds.third && green >= whiteThresholds.second){
		    countWhite += 1;
		}			
	    }
	}
	
	//	System.out.println(countRed);
	
	// Get the proportion of whtie and red pixels in the image.
	float wProp = (float) countWhite/ (float) waldoPixels;
	float rProp = (float) countRed/ (float) waldoPixels;
	
	
	//System.out.println(whiteProp + " " + redProp);
	
	// If more than 7% of the image is red and 1% is "white", then there is a chance
	// that Waldo is there. These numbers should be adjustable. However, for now they
	// seem to be working. This of course does not help to stop those images that 
	// have a lot of distractions. However, it does help get rid of those images that
	// we should never even try to look at. 
	if(rProp >= redProp && wProp >= whiteProp){
	    //  System.out.println("WALDO IS HERE");
	    return true;
	} else {
	    //System.out.println("WALDO IS NOT HERE");
	    return false;
	}	
	
    }
}