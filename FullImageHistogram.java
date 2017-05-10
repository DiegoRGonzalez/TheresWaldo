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

public class FullImageHistogram {

  public FullImageHistogram() {}

    // copies a bufferedImage with no connection to the original
    private static BufferedImage deepCopy(BufferedImage bi) {

      BufferedImage newImage = new BufferedImage(bi.getWidth(), bi.getHeight(),bi.getType());
      for (int x = 0; x < bi.getWidth(); x++){
        for(int y = 0; y < bi.getHeight(); y++){
          newImage.setRGB(x,y,bi.getRGB(x,y));
        }
      }

      return newImage;
    }

    public boolean isWhite(int red, int green, int blue){
      Integer rbDiff = red-blue;
      Integer rgDiff = red-green;
      Integer bgDiff = Math.abs(blue-green);
      return Math.abs(rbDiff) <= 2 && Math.abs(rgDiff) <= 2 && bgDiff <= 1;
    }

    public boolean isRed(boolean wCheck, int red, int green, int blue){
      Integer rbDiff = red-blue;
      Integer rgDiff = red-green;
      Integer bgDiff = Math.abs(blue-green);
      return !wCheck && rbDiff >= 4 && rgDiff >= 4 && bgDiff <= 2;
    }

    private float calcPixelProb(float numRed, float numWhite, float numPixels){
	float redProb = numRed/numPixels * 75f;
	float whiteProb = numWhite/numPixels * 25f;
	float totalProb = redProb + whiteProb;

	if(whiteProb <= 1 || redProb <= 10) return 0.0f;
	return totalProb/100.0f * 255.0f;
    }

    private void firstPass(BufferedImage redImage, BufferedImage whiteImage, BufferedImage writeImage){

	int width = redImage.getWidth();
	int height = redImage.getHeight();

	for( int x = 0; x < width; x++) {
	    for( int y = 0; y < height; y++) {
		boolean isRed = redImage.getRGB(x, y) == Color.WHITE.getRGB();
		
		float numRed = 0.0f;
		float numWhite = 0.0f;
		float numPixels = 0.0f;
		float prop = 0.0f;

		if(isRed) {
		    for(int i = x - 2 ; i < (x + 2); i++) {
			if(i < width && i >= 0) { 
			    for(int j = y - 2; j < (y + 2); j++) {
				numPixels+=1.0f;
				if ( j < height && j >= 0) {
				    if(redImage.getRGB(i, j) == Color.WHITE.getRGB()) {
					numRed += 1.0f;
				    }
				    
				    if(whiteImage.getRGB(i, j) == Color.WHITE.getRGB()) {
					numWhite += 1.0f;
					int half = 255/2;
				       
					writeImage.setRGB(i,j, Color.WHITE.getRGB());
				    } 
				}
			    }
			}
		    }
		    
		    Color setCol = Color.BLACK;
		    if((numPixels - numRed - numWhite)/numPixels < 0.3f && numWhite/numPixels >= 0.1f){
			writeImage.setRGB(x, y, Color.WHITE.getRGB());
		    }
		    else writeImage.setRGB(x,y, Color.BLACK.getRGB());


		}		

	    }
	}

	
    
    }

    private void secondPass(BufferedImage redImage, BufferedImage whiteImage, BufferedImage writeImage){

	int width = writeImage.getWidth();
	int height = writeImage.getHeight();
	
	for( int x = 0; x < width; x++) {
	    for( int y = 0; y < height; y++) {
		Color col = new Color(writeImage.getRGB(x,y));
		if(col.getRGB() == Color.WHITE.getRGB()){
		    float count = 0.0f;
		    float numPxls = 0.0f;
		    for (int i = x - 2; i < width && i < x + 2 && i > 0; i++){
			for (int j = y - 2; j < height && j < y + 2 && j > 0; j++){
			    Color prevCol = new Color(writeImage.getRGB(i,j));
			    numPxls += 1.0f;
			    if (prevCol.getRGB() == Color.WHITE.getRGB()){
				count += 1.0f;
			    }
			}
		    }

		    if(count/numPxls <= 0.2f){
			writeImage.setRGB(x,y, Color.BLACK.getRGB());
		    }
		    
		}
	    }		    
	}
	

    }


    private void repeatPass(BufferedImage waldoImage, BufferedImage writeImage, int margin, float percentThreshold){
	int threshold = (int) (percentThreshold * 255.0f);

	for( int x = 0; x < waldoImage.getWidth(); x++) {
	    for( int y = 0; y < waldoImage.getHeight(); y++) {
		int maxChannel = 255;
		int numSameCol = 0;
		int numOtherCol = 0;

		Color curColor = new Color(waldoImage.getRGB(x,y));
		Float pixNum = 0.0f;
		
		for(int i = x - margin; i <= (x+margin) && i >= 0 && i < waldoImage.getWidth(); i++){
		    for(int j = y - margin; j <= (y+margin) && j >= 0 && j < waldoImage.getHeight(); j++){
			pixNum += 1.0f;
			Color col2 = new Color(waldoImage.getRGB(i,j));

			Color probCol = new Color(writeImage.getRGB(i,j));
			Integer prevProb = probCol.getRed();
			if (prevProb >= threshold && col2.getRGB() != Color.BLACK.getRGB() && curColor.getRGB() == col2.getRGB()) {
			    numSameCol += 1;
			}
			else if(prevProb >= threshold && col2.getRGB() != Color.BLACK.getRGB()) {
			    numOtherCol += 1;
			}
		    }
		}
	
		Integer prob = 0;
		if(curColor.equals(Color.RED) && numOtherCol > numSameCol/4) {
		    prob = (int) calcPixelProb((float) numSameCol, (float) numOtherCol, (float) pixNum);
		} else if(numOtherCol != 0 && numSameCol > 1 && numSameCol > numOtherCol/4){
		    prob = (int) calcPixelProb((float) numOtherCol, (float) numSameCol, (float) pixNum);
		}

		Color newCol = new Color(prob, prob, prob);

		writeImage.setRGB(x,y, newCol.getRGB());
	    }
	}

    }

    public void generateRedWhiteImage(BufferedImage wIm, BufferedImage redImage, BufferedImage whiteImage) {
      float bitAmountf = 15.0f;

      BufferedImage waldoImage = wIm;

      ColorCorrection colCorrector = new ColorCorrection();

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

          boolean wCheck = isWhite(red, green, blue);
          boolean rCheck =  isRed(wCheck, red, green, blue);
	  
	  
          // Check red and white
	  if(rCheck) {

	      redImage.setRGB(x, y, Color.WHITE.getRGB());
	      whiteImage.setRGB(x, y, Color.BLACK.getRGB());
          } else if (wCheck) {
	      redImage.setRGB(x, y, Color.BLACK.getRGB());
	      whiteImage.setRGB(x, y, Color.WHITE.getRGB());
	  } else {
	      whiteImage.setRGB(x, y, Color.BLACK.getRGB());
	      redImage.setRGB(x, y, Color.BLACK.getRGB());
	  }
        }
      }
    }
    

    public BufferedImage generateHistogram(BufferedImage wIm) {
      float bitAmountf = 15.0f;

      BufferedImage waldoImage = deepCopy(wIm);
      BufferedImage redImage = new BufferedImage(wIm.getWidth(), wIm.getHeight(), wIm.getType());
      BufferedImage whiteImage = new BufferedImage(wIm.getWidth(), wIm.getHeight(), wIm.getType());

      generateRedWhiteImage(waldoImage, redImage, whiteImage);
      Subimage.writeImage("white", whiteImage);
      Subimage.writeImage("red", redImage);
      

      BufferedImage writeImage = new BufferedImage(wIm.getWidth(), wIm.getHeight(), wIm.getType());

      firstPass(redImage, whiteImage, writeImage);
      Subimage.writeImage("first", writeImage);

      secondPass(redImage, whiteImage, writeImage);

      Subimage.writeImage("second", writeImage);

      repeatPass(waldoImage, writeImage, 2, 0.1f);

      //thirdPass(writeImage);
      //

      //      repeatPass(waldoImage, writeImage, 3, 0.25f);
      Subimage.writeImage("Here2", writeImage);
      return writeImage;

    }

  }
