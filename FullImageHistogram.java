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
      return Math.abs(rbDiff) <= 3 && Math.abs(rgDiff) <= 3 && bgDiff <= 2;
    }

    public boolean isRed(boolean wCheck, int red, int green, int blue){
      Integer rbDiff = red-blue;
      Integer rgDiff = red-green;
      Integer bgDiff = Math.abs(blue-green);
      return !wCheck && rbDiff >= 4 && rgDiff >= 4 && bgDiff <= 2;
    }

    public BufferedImage generateHistogram(BufferedImage wIm) {
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

      for( int x = 0; x < waldoImage.getWidth(); x++) {
        for( int y = 0; y < waldoImage.getHeight(); y++) {
          int maxChannel = 255;
          Color curColor = new Color(waldoImage.getRGB(x,y));

          Integer sameCol = 0;
          Integer otherCol = 0;

          for(int i = x - 4; i <= x && i >= 0; i++){
            for(int j = y - 4; j <= y && j >= 0; j++){
              Color col2 = new Color(waldoImage.getRGB(i,j));

              if (col2.getRGB() != Color.BLACK.getRGB() && curColor.getRGB() != col2.getRGB()) sameCol += 1;
              else if(col2.getRGB() != Color.BLACK.getRGB()) otherCol += 1;
            }
          }

          Integer prob = 0;
          if(curColor.equals(Color.RED) && otherCol != 0) {
             prob = sameCol * 30 - otherCol * 10 + (int)((float) sameCol/(float)otherCol * 15.0f);
          } else if(otherCol != 0){
             prob = otherCol * 30 - sameCol * 10 + (int)((float) otherCol/(float)sameCol * 15.0f);
          }

          Color newCol = new Color(prob, prob, prob);
          writeImage.setRGB(x,y,newCol.getRGB());
        }
      }

      return writeImage;

    }

  }
