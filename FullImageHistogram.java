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
      return rbDiff <= 3 && rbDiff >= -2 && rgDiff >= -2 && rgDiff <= 3 && bgDiff <= 2;
    }

    public boolean isRed(boolean wCheck, int red, int green, int blue){
      Integer rbDiff = red-blue;
      Integer rgDiff = red-green;
      Integer bgDiff = Math.abs(blue-green);
      return !wCheck && rbDiff >= 4 && rgDiff >= 4 && bgDiff <= 2;
    }

    public void setRedWhiteImg(BufferedImage waldoImage) {
	int width = waldoImage.getWidth();
	int height = waldoImage.getHeight();
	
	ColorCorrection colCorrector = new ColorCorrection();

	for( int x = 0; x < width; x++){
	    for( int y = 0; y < height; y++){

		// Get the RGB value of the image
		Integer rgbVal = waldoImage.getRGB(x,y);

		// Separate the Red, Green and Blue values.
		Color col = new Color(rgbVal);
		col = colCorrector.make12Bit(col);

		Integer red = col.getRed();
		Integer green = col.getGreen();
		Integer blue = col.getBlue();

		boolean wCheck = isWhite(red, green, blue);
		boolean rCheck = isRed(wCheck, red, green, blue);

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
	
    }

    public float[] getRedWhiteProb(float redCount, float whiteCount, float numPxls){
	
	float redProp = (redCount)/numPxls;
	float whiteProp = (whiteCount)/numPxls;
	
	float wOverR = (whiteCount > 0.0f) ? redCount/whiteCount : 0.0f;
	float rOverW = (redCount > 0.0f) ? whiteCount/redCount : 0.0f;
	float min = (wOverR < rOverW) ? wOverR : rOverW;
	
	float[] probs = {redProp * 255.0f,  whiteProp * 255.0f, min * 255.0f};
	return probs;
    }

    public void firstPass(BufferedImage waldoImage, BufferedImage writeImage){
	int height = waldoImage.getHeight();
	int width = waldoImage.getWidth();

	for( int x = 0; x < width; x++) {
	    for( int y = 0; y < height; y++) {
		int maxChannel = 255;
		Color curColor = new Color(waldoImage.getRGB(x,y));

		Integer sameCol = 1;
		Integer otherCol = 0;
		float pixNum = 0.0f;

		for(int i = x - 1; i <= (x+1); i++){
		    for(int j = y - 1; j <= (y+1); j++){
			if( i != x && j != y && i >= 0 && j >= 0 && i < width && j < height){
			    pixNum += 1.0f;
			    Color col2 = new Color(waldoImage.getRGB(i,j));
			    
			    if (col2.getRGB() != Color.BLACK.getRGB() && curColor.getRGB() == col2.getRGB()) sameCol += 1;
			    else if(col2.getRGB() != Color.BLACK.getRGB()) otherCol += 1;
			}
		    }
		}
		
		float[] probs = {0.0f, 0.0f, 0.0f};
		if(curColor.equals(Color.RED) && otherCol > sameCol/4) {
		    probs = getRedWhiteProb(sameCol, otherCol, pixNum);
		} else if(otherCol > 0 && sameCol > otherCol/4){
		    probs = getRedWhiteProb(otherCol, sameCol, pixNum);
		}
		
		probs[0] = (probs[0] < 0.0f) ? 0.0f : probs[0];
		probs[1] = (probs[1] < 0.0f) ? 0.0f : probs[1];
		probs[2] = (probs[2] < 0.0f) ? 0.0f : probs[2];

		probs[0] = (probs[0] > 255.0f) ? 255.0f : probs[0];
		probs[1] = (probs[1] > 255.0f) ? 255.0f : probs[1];
		probs[2] = (probs[2] > 255.0f) ? 255.0f : probs[2];
		
		Color newCol = new Color((int) probs[0], (int) (probs[1]), (int) (probs[2]));
		writeImage.setRGB(x,y,newCol.getRGB());
	    }
	}
    }

    public void repeatPass(BufferedImage waldoImage, BufferedImage writeImage, int addedMargin){

	int height = waldoImage.getHeight();
	int width = waldoImage.getWidth();
	for( int x = 0; x < width; x++) {
        for( int y = 0; y < height; y++) {
          int maxChannel = 255;
          int sameCol = 0;
          int otherCol = 0;

          Color curColor = new Color(waldoImage.getRGB(x,y));
          float pixNum = 0.0f;
          for(int i = x - addedMargin; i <= (x+addedMargin) && i >= 0 && i < width; i++){
            for(int j = y - addedMargin; j <= (y+addedMargin) && j >= 0 && j < height; j++){
              pixNum += 1.0f;
              Color col2 = new Color(waldoImage.getRGB(i,j));

              Color probCol = new Color(writeImage.getRGB(i,j));

	      float red = probCol.getRed()/255.0f;
	      float white = probCol.getGreen()/255.0f;
	      float rwProp = probCol.getBlue()/255.0f;
	      	      
	      if(red >= 0.2f && white >= 0.1f && rwProp >= 0.1f && rwProp <= 0.9f){
		  if (col2.getRGB() != Color.BLACK.getRGB() && curColor.getRGB() == col2.getRGB()) {
		      sameCol += 1.0f;
		  }
		  else if(col2.getRGB() != Color.BLACK.getRGB()) {
		      otherCol += 1.0f;
		  }
	      }
	    }
	  }
	  
	  float[] probs = {0.0f, 0.0f, 0.0f};
	  if(curColor.equals(Color.RED)) {
	      probs = getRedWhiteProb(sameCol, otherCol, pixNum);
	  } else if(otherCol > 0){
	      probs = getRedWhiteProb(otherCol, sameCol, pixNum);
	  }


	  probs[0] = (probs[0] < 0.0f) ? 0.0f : probs[0];
	  probs[1] = (probs[1] < 0.0f) ? 0.0f : probs[1];
	  probs[2] = (probs[2] < 0.0f) ? 0.0f : probs[2];

	  probs[0] = (probs[0] > 255.0f) ? 255.0f : probs[0];
	  probs[1] = (probs[1] > 255.0f) ? 255.0f : probs[1];
	  probs[2] = (probs[2] > 255.0f) ? 255.0f : probs[2];
		
	  Color newCol = new Color((int) probs[0], (int) (probs[1]), (int) (probs[2]));
	  writeImage.setRGB(x,y,newCol.getRGB());
		
        }
      }

    }
   

    public void finalPass(BufferedImage writeImage, BufferedImage write){
	int width = writeImage.getWidth();
	int height = writeImage.getHeight();

	for( int x = 0; x < width; x++) {
	  for( int y = 0; y < height; y++) {
	      Color writeCol = new Color(writeImage.getRGB(x,y));
	      int oldProbsTot = 0;
	      float numPix = 0.0f;
	      float numColored = 0.0f;
	      for(int i = x - 2; i < x+2; i++){
		  if(i >= 0 && i < width){
		      for (int j = y-2; j < y+2; j++){
			  if(j >= 0 && j < height && i != x && j != y){
			      Color oldCol = new Color(writeImage.getRGB(i,j));
			      int rProp = oldCol.getRed();
			      int wProp = oldCol.getGreen();
			      numPix += 1.0f;
			      
			      if(rProp > 0 && wProp > 0) {
				  oldProbsTot += rProp + wProp;
				  numColored += 1.0f;
			      }				  
			      
			  }
		      }
		  }
	      }

	      int w = (numColored/numPix >= 0.0f && numColored/numPix <= 0.9f) ? (int) (oldProbsTot/numColored) : 0;
	      if(w > 255) w = 255;
	      Color newCol = new Color(w,w,w);
	      
	      write.setRGB(x,y, newCol.getRGB());
	  }
      }
    }

    public BufferedImage generateHistogram(BufferedImage wIm) {
      float bitAmountf = 15.0f;

      BufferedImage waldoImage = deepCopy(wIm);

      int height = waldoImage.getHeight();
      int width = waldoImage.getWidth();

      BufferedImage writeImage = new BufferedImage(width, height, waldoImage.getType());

      ColorCorrection colCorrector = new ColorCorrection();

      setRedWhiteImg(waldoImage);

      Subimage.writeImage("redwhiteset", waldoImage);

      firstPass(waldoImage, writeImage);

      Subimage.writeImage("Here", writeImage);

      repeatPass(waldoImage, writeImage, 3);

      Subimage.writeImage("Here2", writeImage);

      repeatPass(waldoImage, writeImage, 5);

      Subimage.writeImage("Here10", writeImage);

      BufferedImage write = new BufferedImage(writeImage.getWidth(), writeImage.getHeight(), writeImage.getType());
      
      finalPass(writeImage, write);
      
      return write;

    }

  }
