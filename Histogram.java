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
    
    private Hashtable<Integer, Float> hist = new Hashtable<Integer, Float>();

    public Histogram () {}

    

    public Histogram (Subimage image) {
	generateHistogram(image);
    }

    public Hashtable<Integer, Float> getHistogram() {
	return hist;
    }
    
    /* Classify the image according to whether or not Waldo may be there
     * Currently classifies based on how much red appears in the image.
     * More robust classifications to follow, especially to include white
     * and pink (white-red bleed through).
     */
    private void generateHistogram(Subimage waldoSubimage) {
	int bitAmount = 15;
	float bitAmountf = 15.0f;

	BufferedImage waldoImage = waldoSubimage.getImage();
	BufferedImage writeImage = new BufferedImage(waldoImage.getWidth(), waldoImage.getHeight(), waldoImage.getType());
	int[] loc = waldoSubimage.getLocation();
	Subimage writeSubimage = new Subimage(writeImage, loc[0], loc[1]);

	BufferedImage bit12Image = new BufferedImage(waldoImage.getWidth(), waldoImage.getHeight(), waldoImage.getType());
	Subimage bit12Subimage = new Subimage(bit12Image, loc[0], loc[1]);

	ColorCorrection colCorrector = new ColorCorrection();

	waldoImage = colCorrector.normalize(waldoImage);
	
	// The number of pixels in the image to find frequencies of colors
	int waldoPixels = waldoImage.getWidth() * waldoImage.getHeight();
	
	Hashtable<Integer, Float> counts = new Hashtable<Integer, Float>();

	for( int x = 0; x < waldoImage.getWidth(); x++){
	    for( int y = 0; y < waldoImage.getHeight(); y++){
		
		// Get the RGB value of the image
		Integer rgbVal = waldoImage.getRGB(x,y);

		// Separate the Red, Green and Blue values.
		Color col = new Color(rgbVal);

		Integer red = (Integer) col.getRed();
		Integer blue = (Integer) col.getBlue();
		Integer green = (Integer) col.getGreen();	      
		
		float rP = (float) red/255.0f;
		float gP = (float) green/255.0f;
		float bP = (float) blue/255.0f;

		red =  (int) (rP * bitAmountf);		
		blue =  (int) (bP * bitAmountf);
		green = (int) (gP * bitAmountf);
				           
		Integer rbDiff = red-blue;
		Integer rgDiff = red-green;
		Integer bgDiff = Math.abs(blue-green);

		boolean wCheck = Math.abs(rbDiff) <= 2 && Math.abs(rgDiff) <= 2 && bgDiff <= 2;
		boolean rCheck = !wCheck && rbDiff >= 3 && rgDiff >= 3 && bgDiff <= 1;
		
		
		// Check red and white
		if(!(wCheck || rCheck)){
		    
		    waldoImage.setRGB(x, y, Color.BLACK.getRGB());
		   
		} else {
		    int maxChannel = 255;
		    
		    Color pixCol = (rCheck) ? new Color(maxChannel, 0, 0) : new Color(0, maxChannel, maxChannel);
		    waldoImage.setRGB(x,y,pixCol.getRGB());
		    
		    for(int i = x - 2; i <= x; i++){
			if(i >= 0) {
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
	/*
	for( int x = 0; x < writeImage.getWidth(); x++){
	    for( int y = 0; y < writeImage.getHeight(); y++){
		Color color = new Color(writeImage.getRGB(x,y));
		if (!color.equals(Color.WHITE)){
		    writeImage.setRGB(x,y, 0);
		}
	    }
	}
	*/
	
	bit12Subimage.writeImage("12bitImage.jpg");
	writeSubimage.writeImage("WaldoHist.jpg");
	waldoSubimage.writeImage("test.jpg");
    }
}




    /* Classify the image according to whether or not Waldo may be there
     * Currently classifies based on how much red appears in the image.
     * More robust classifications to follow, especially to include white
     * and pink (white-red bleed through).
     */
    /*
    private void generateHistogram(Subimage waldoSubimage) {
	
	BufferedImage waldoImage = waldoSubimage.getImage();
	// The number of pixels in the image to find frequencies of colors
	int waldoPixels = waldoImage.getWidth() * waldoImage.getHeight();
	
	Hashtable<Integer, Float> counts = new Hashtable<Integer, Float>();

	for( int x = 0; x < waldoImage.getWidth(); x++){
	    for( int y = 0; y < waldoImage.getHeight(); y++){
		
		// Get the RGB value of the image
		Integer color = waldoImage.getRGB(x,y);

		// Separate the Red, Green and Blue values.
		Color col = new Color(color);

		Integer red = (Integer) col.getRed();
		Integer blue = (Integer) col.getBlue();
		Integer green = (Integer) col.getGreen();	      
		
		Integer rbDiff = red-blue;
		Integer rgDiff = red-green;

		// Check red and white
		if((rbDiff > 10 && rgDiff > 10) || (rbDiff > -15 && rbDiff < 15 && rgDiff > -15 && rgDiff < 15)){
		    
		    Float rPercent = red/255.0f;
		    Float gPercent = green/255.0f;
		    Float bPercent = blue/255.0f;
		
		    Integer newR = (Integer) Math.round(rPercent * 15.0f);
		    Integer newG = (Integer) Math.round(gPercent * 15.0f);
		    Integer newB = (Integer) Math.round(bPercent * 15.0f);
		    
		    color = (newR & 0xf) << 8 + (newG & 0xf) << 4 + newB & 0xf;
		    
		    Float count = counts.get(color);

		    if (count == null) {
			counts.put(color, count + 1.0f);
		    } else {
			counts.put(color, 0.0f);
		    }
		}
	    }
	}
	
	Set<Integer> keys = counts.keySet();
        for(Integer key: keys){
            hist.put(key, counts.get(key)/(float) waldoPixels);
        }
    }
*/


		// rP = red/bitAmountf;
		// bP = blue/bitAmountf;
		// gP = green/bitAmountf;
		
		// Integer r = (int) (rP * 255.0f);
		// Integer b = (int) (bP * 255.0f);
		// Integer g = (int) (gP * 255.0f);

		// Color tbit = new Color(r,b,g);
		// bit12Image.setRGB(x, y, tbit.getRGB());
