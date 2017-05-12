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
	/*
	float bitAmountf = 15.0f;

      BufferedImage waldoImage = deepCopy(wIm);
      BufferedImage writeImage = new BufferedImage(waldoImage.getWidth(), waldoImage.getHeight(), waldoImage.getType());

      ColorCorrection colCorrector = new ColorCorrection();

      waldoImage = colCorrector.normalize(waldoImage);

      for( int x = 0; x < waldoImage.getWidth(); x++){
        for( int y = 0; y < waldoImage.getHeight(); y++){

          // Get the RGB value of the image
          Integer rgbVal = waldoImage.getRGB(x,y);

          // Separate the Red, Green and Blue values.
          Color col = new Color(rgbVal);
          col = colCorrector.make12Bit(col);

          boolean wCheck = Util.isWhite12(col);
          boolean rCheck = Util.isRed(col);

          // Check red and white
          if(!(wCheck || rCheck)){
            waldoImage.setRGB(x, y, Color.BLACK.getRGB());
          } else if(wCheck) {
            waldoImage.setRGB(x,y, Color.BLUE.getRGB());
          } else if(rCheck) {
            waldoImage.setRGB(x,y, Color.RED.getRGB());
          }
        }
      }

      for( int x = 0; x < waldoImage.getWidth(); x++) {
        for( int y = 0; y < waldoImage.getHeight(); y++) {
          int maxChannel = 255;
          Color curColor = new Color(waldoImage.getRGB(x,y));

          Integer sameCol = 1;
          Integer otherCol = 0;
          float pixNum = 0.0f;

          for(int i = x - 1; i <= (x+1) && i >= 0 && i < waldoImage.getWidth(); i++){
            for(int j = y - 1; j <= (y+1) && j >= 0 && j < waldoImage.getHeight(); j++){
              pixNum += 1.0f;
              Color col2 = new Color(waldoImage.getRGB(i,j));

              if (col2.getRGB() != Color.BLACK.getRGB() && curColor.getRGB() == col2.getRGB()) sameCol += 1;
              else if(col2.getRGB() != Color.BLACK.getRGB()) otherCol += 1;
            }
          }

          Integer prob = 0;
          if(curColor.equals(Color.RED) && otherCol > sameCol/4) {
            float redProb = (float) sameCol;
            redProb /= pixNum;
            redProb *= 75.0f;
            float whiteProb = (float) otherCol;
            whiteProb /= pixNum;
            whiteProb *= 25.0f;

             prob = (int) (redProb + whiteProb);
          } else if(otherCol > 0 && sameCol > otherCol/4){
            float redProb = (float) otherCol;
            redProb /= pixNum;
            redProb *= 75.0f;
            float whiteProb = (float) sameCol;
            whiteProb /= pixNum;
            whiteProb *= 25.0f;

             prob = (int) (redProb + whiteProb);
          }

          prob = (prob < 0) ? 0 : prob;
          Color newCol = new Color(prob, prob, prob);
          writeImage.setRGB(x,y,newCol.getRGB());
        }
      }
	
      */
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

		boolean wCheck = rbDiff <= 3 && rbDiff >= -2 && rgDiff >= -2 && rgDiff <= 3 && bgDiff <= 2;
		boolean rCheck = !wCheck && rbDiff >= 4 && rgDiff >= 4 && bgDiff <= 2;
		
		// Check red and white
		if(!(wCheck || rCheck)){
		    
		    waldoImage.setRGB(x, y, Color.BLACK.getRGB());
		   
		} else {
		    int maxChannel = 255;
		    
		    Color pixCol = (rCheck) ? new Color(maxChannel, 0, 0) : new Color(0, 0, maxChannel);
		    waldoImage.setRGB(x,y,pixCol.getRGB());
		    
		    for(int i = x - 2; i <= x; i++){
			if(i >= 0){
			    for(int j = y - 2; j <= y; j++){
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

	for( int x = 0; x < writeImage.getWidth(); x++){
	    for( int y = 0; y < writeImage.getHeight(); y++){
		Color color = new Color(writeImage.getRGB(x,y));
		if (color.getRed() > 20){
		    waldoConfidence += 1.0f;
		}
	    }
	}
		
	waldoConfidence /= waldoPixels;
	
	
    }
}
