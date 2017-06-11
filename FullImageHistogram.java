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

/* This class scans a "Where's Waldo" image for hotspots where red and white congregate together. 
 * The intuition here is that Waldo is always colored red and white, and therefore looking for
 * these hotspots will help to significantly narrow down where Waldo can be in a "Where's
 * Waldo" image.
 */
public class FullImageHistogram {

    public FullImageHistogram() {}

    /* Takes a "Where's Waldo" image and finds all of the pixels that are red or white.
     * This method alters each pixel of the input, "waldoImage," according to each pixel's color.
     * Red pixels become RED, White pixels become BLUE, and all other pixels become BLACK.
     * This alteration helps subsequent passes determine the color of each pixel, without having
     * to change it themselves.
     *
     * Note: the definition of a Red or White pixel can be found in Util.isRed and Util.isWhite respectively.
     * Also, all colors are converted to 12 bit in Util.
     */
    public void setRedWhiteImg(BufferedImage waldoImage) {
	int width = waldoImage.getWidth();
	int height = waldoImage.getHeight();
	
	ColorCorrection colCorrector = new ColorCorrection();

	// Go through the image altering the pixels according to whether they are red, white, or other.
	for( int x = 0; x < width; x++){
	    for( int y = 0; y < height; y++){

		Integer rgbVal = waldoImage.getRGB(x,y);
		Color col = new Color(rgbVal);

		boolean wCheck = Util.isWhite(col);
		boolean rCheck = Util.isRed(col);

		
		if(!(wCheck || rCheck)){
		    waldoImage.setRGB(x, y, Color.BLACK.getRGB());
		} else if(wCheck) {
		    waldoImage.setRGB(x,y, Color.BLUE.getRGB());
		} else if(rCheck) {
		    waldoImage.setRGB(x,y, Color.RED.getRGB());
		}
	    }
	}
	
    }

    /* Returns a triple containing the proportion of red and white pixels in a given area,
     * and the minimum between red pixels/white pixels or white pixels/red pixels. 
     * The former allows the program to decide whether there are enough red and white pixels
     * in a given area to say that Waldo could potentially be at that location. 
     * The latter allows the program to determine that there is not too much white or too much red
     * and that the other color is also present.
     */
    public float[] getRedWhiteProp(float redCount, float whiteCount, float numPxls){
	
	float redProp = (redCount)/numPxls;
	float whiteProp = (whiteCount)/numPxls;
	
	float wOverR = (whiteCount > 0.0f) ? redCount/whiteCount : 0.0f;
	float rOverW = (redCount > 0.0f) ? whiteCount/redCount : 0.0f;
	float min = (wOverR < rOverW) ? wOverR : rOverW;
	
	float[] probs = {redProp * 255.0f,  whiteProp * 255.0f, min * 255.0f};
	return probs;
    }

    /* The initial pass through the image. For each pixel in the "Where's Waldo" image, this method
     * creates a 3 * 3 pixel window, and determines the proportion of red and white pixels that appear
     * in that window. If enough red and white pixels appear, then that pixel is adjusted in write image to
     * save the porportions found. 
     * In essence, then, this works basically like a red or white edge detector. Places with a lot of red or a
     * lot of white become blacked out, except for the edges, and places where red and white meet become 
     * flagged higher. 
     */
    public void firstPass(BufferedImage waldoImage, BufferedImage writeImage){
	int height = waldoImage.getHeight();
	int width = waldoImage.getWidth();

	for( int x = 0; x < width; x++) {
	    for( int y = 0; y < height; y++) {

		Color curColor = new Color(waldoImage.getRGB(x,y));

		// Create a window around the current pixel (located at (x,y)). The window is 3 * 3 in size.
		// Count the number of pixels around (x,y) that have the same color as the current pixel
		// and the number that have a different color (but not black).
		Integer numSameCol = 1;
		Integer numOtherCol = 0;
		float pixNum = 0.0f; 		
		for(int i = x - 1; i <= (x+1); i++){
		    for(int j = y - 1; j <= (y+1); j++){
			
			// Check that they are in bounds and that the color is not black before adjusting 
			// color counts
			if( i != x && j != y && i >= 0 && j >= 0 && i < width && j < height){
			    pixNum += 1.0f;
			    Color col2 = new Color(waldoImage.getRGB(i,j));
			    if(col2.getRGB() != Color.BLACK.getRGB()){
				if (curColor.getRGB() == col2.getRGB()) numSameCol += 1;
				else numOtherCol += 1;
			    }
			}
		    }
		}
		
		// Get the proportion of red and white that appear in the image. If not enough white appears,
		// then keep the proportion at 0.0f for all.
		float[] props = {0.0f, 0.0f, 0.0f};
		if(curColor.equals(Color.RED) && numOtherCol > numSameCol/4) {
		    props = getRedWhiteProp(numSameCol, numOtherCol, pixNum);
		} else if(numOtherCol > 0 && numSameCol > numOtherCol/4){
		    props = getRedWhiteProp(numOtherCol, numSameCol, pixNum);
		}
		
		props = Util.adjustRGB(props);
		
		Color newCol = new Color((int) props[0], (int) (props[1]), (int) (props[2]));
		writeImage.setRGB(x,y,newCol.getRGB());
	    }
	}
    }

    /* The second and third pass through the "Where's Waldo" image. These passes perform similarly to the
     * first pass, except they take into account the previous proportions of red and white at each pixel.
     * Furthermore, these passes take a margin, in order to change the window size at each pixel. Instead
     * of 3 * 3 windows, the next two passes use larger windows. This allows the program to determine where
     * large clusters of red and white appear together, in order to get rid of places that have only one color.
     * or places with too little red and white. Again, the idea is that Waldo will have slightly more red than
     * white, but there should still be some of both.
     */
    public void repeatPass(BufferedImage waldoImage, BufferedImage writeImage, int addedMargin){

	int height = waldoImage.getHeight();
	int width = waldoImage.getWidth();
	
	for( int x = 0; x < width; x++) {
	    for( int y = 0; y < height; y++) {
 
		Color curColor = new Color(waldoImage.getRGB(x,y));

		// Create a window around the current pixel (located at (x,y)). The window is 
		// (addedMargin * 2 + 1)^2 in size.
		// Count the number of pixels around (x,y) that have the same color as the current pixel
		// and the number that have a different color (but not black).
		int numSameCol = 0;
		int numOtherCol = 0;
		float pixNum = 0.0f;
		for(int i = x - addedMargin; i <= (x+addedMargin) && i >= 0 && i < width; i++){
		    for(int j = y - addedMargin; j <= (y+addedMargin) && j >= 0 && j < height; j++){
			pixNum += 1.0f;
			Color col2 = new Color(waldoImage.getRGB(i,j));
			
			// Get the previous proportions of red and white found around this location.
			// These proportions should have been found with smaller windows.
			Color probCol = new Color(writeImage.getRGB(i,j));
			float red = probCol.getRed()/255.0f;
			float white = probCol.getGreen()/255.0f;
			float rwProp = probCol.getBlue()/255.0f;
	      	      
			// If there was too little red or white, or too much of one of them, then do not count
			// that location.
			if(red >= 0.2f && white >= 0.1f && rwProp >= 0.1f && rwProp <= 0.9f){
			    if (col2.getRGB() != Color.BLACK.getRGB() && curColor.getRGB() == col2.getRGB()) {
				numSameCol += 1.0f;
			    }
			    else if(col2.getRGB() != Color.BLACK.getRGB()) {
				numOtherCol += 1.0f;
			    }
			}
		    }
		}
	  
		// Recreate the prortions again, and write them back out.
		float[] props = {0.0f, 0.0f, 0.0f};
		if(curColor.equals(Color.RED)) {
		    props = getRedWhiteProp(numSameCol, numOtherCol, pixNum);
		} else if(numOtherCol > 0){
		    props = getRedWhiteProp(numOtherCol, numSameCol, pixNum);
		}

		props = Util.adjustRGB(props);
		
		Color newCol = new Color((int) props[0], (int) (props[1]), (int) (props[2]));
		writeImage.setRGB(x,y,newCol.getRGB());
		
	    }
	}

    }
   
    /* The final pass through the image takes the "writeImage," which is full of red and white color proportions
     * and turns it into a black and white image of probabilities. These are then written back out to the second image
     * and are used to determine the final hotspots where Waldo could be.
     * Much like the previous passes, it takes windows of size 5*5 pixels to check the proportions. This is done by simply
     * adding and averaging all of the proportions.
     */
    public void finalPass(BufferedImage writeImage, BufferedImage write){
	int width = writeImage.getWidth();
	int height = writeImage.getHeight();

	for( int x = 0; x < width; x++) {
	    for( int y = 0; y < height; y++) {
		
		// Go through the image with a sliding 5 * 5 window.
		// Determine the number of pixels that will be checked and that do not have proportions equal to 0.
		// Adds up all of the proportions found.
		int oldPropsTot = 0;
		float numPix = 0.0f;
		float numColored = 0.0f;
		for(int i = x - 2; i < x+2; i++){
		    if(i >= 0 && i < width){
			for (int j = y-2; j < y+2; j++){
			    if(j >= 0 && j < height && i != x && j != y){
				Color oldCol = new Color(writeImage.getRGB(i,j));
				int rProp = oldCol.getRed();
				int wProp = oldCol.getGreen();
				int rwProp = oldCol.getBlue();
				numPix += 1.0f;
			      
				if(rProp > 0 && wProp > 0 && rwProp >= 0.1f) {
				    oldPropsTot += rProp + wProp;
				    numColored += 1.0f;
				}
			    }
			}
		    }
		}

		// Determine the color value of those proportions, getting rid of places where too few pixels are colored. 
		int w = (numColored/numPix >= 0.1f && numColored/numPix <= 0.9f) ? (int) (oldPropsTot/numColored) : 0;
		if(w > 255) w = 255;
		Color newCol = new Color(w,w,w);
	      
		write.setRGB(x,y, newCol.getRGB());
	    }
	}
    }

    /* Take a "Waldo Image," copy it, and figure out the probability that Waldo is in a current location through the different passes defined above. 
     * As an overview: The image is first altered to show only red and white pixel locations. An initial pass is made to determine edges of red and white.
     * The second and third passes get rid of locations where too few red and white pixels meet together, or where only red or only white pixels are together, 
     * for example, a large red train. The final pass takes the proportions found from the previous three passes and determines the probability that Waldo is at
     * a particular pixel location.
     */
    public BufferedImage generateHistogram(BufferedImage wIm) {
	
	BufferedImage waldoImage = Util.deepCopy(wIm);
      
	int height = waldoImage.getHeight();
	int width = waldoImage.getWidth();

	BufferedImage writeImage = new BufferedImage(width, height, waldoImage.getType());

	ColorCorrection colCorrector = new ColorCorrection();

	setRedWhiteImg(waldoImage);

	firstPass(waldoImage, writeImage);

	repeatPass(waldoImage, writeImage, 3);
      
	repeatPass(waldoImage, writeImage, 5);

	BufferedImage write = new BufferedImage(width, height, writeImage.getType());
      
	finalPass(writeImage, write);

	return write;

    }

}
