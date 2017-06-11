import java.util.Iterator;
import java.lang.Math;
import java.io.File;
import java.io.IOException;
import java.awt.image.BufferedImage;
import java.awt.Color;
import javax.imageio.ImageIO;
import java.util.*;
import java.awt.image.WritableRaster;
import java.awt.image.ColorModel;
import java.awt.Graphics;
import java.util.Vector;
import java.util.Random;


public class Util {


  public Util() { }

  // copies a bufferedImage with no connection to the original
  public static BufferedImage deepCopy(BufferedImage bi) {

    BufferedImage newImage = new BufferedImage(bi.getWidth(), bi.getHeight(),bi.getType());
    for (int x = 0; x < bi.getWidth(); x++){
      for(int y = 0; y < bi.getHeight(); y++){
        newImage.setRGB(x,y,bi.getRGB(x,y));
      }
    }

    return newImage;
  }
    
    //Scales the subimages to the correct size in order to be proccessed by the neural net
    public static void scaleImages(Vector<Subimage> images){
	final int numImages = images.size();
	Vector<Subimage> scaledImages = new Vector<Subimage>(numImages);

	for(int i = 0; i < numImages; i++){
	    BufferedImage before = images.get(i).getImage();
	    BufferedImage newImage = new BufferedImage(20,20,BufferedImage.TYPE_INT_RGB);

	    Graphics g = newImage.createGraphics();
	    g.drawImage(before,0,0,20,20,null);
	    //g.dispose();
		
	    images.get(i).setImage(newImage);
	}
	
	
		
    }

    //Combines potential waldos which are close together into one potential waldo
    public static void consolidateCircles(Vector<Subimage> subimages){

	boolean added = false;
	int count = 0;
	int numPass = 2;

	//Do some number of passes to ensure all circles are considered to be consilidated
	for (int k = 0; k < numPass; k++){
	    for(int i = 0; i < subimages.size();){
		added = false;
		
		for(int j = 0; j < subimages.size(); j++){
		    Subimage img1 = subimages.get(i);
		    Subimage img2 = subimages.get(j);
		    
		    //Add subimages together if they are close together
		    if(i != j && Util.dist(img1,img2) < img1.getRadius() + img2.getRadius()){
			count++;
			img2.addSubimage(img1);
			subimages.remove(i);
			added = true;
			break;
		    }
		    
		}
		
		if(!added){
		    i++;
		}
		
	    }
	}
	System.out.println("Consolidated " + count + " circles");
	
    }


    //Compares two colors and determines if they are close enough together given a threshold
    //Assumes 28 bit Colors
    public static boolean closeEnough(Color one, Color two, int threshhold){
	int rDiff = Math.abs(one.getRed() - two.getRed());
	int gDiff = Math.abs(one.getGreen() - two.getGreen());
	int bDiff = Math.abs(one.getBlue() - two.getBlue());
	//return false;
	return (rDiff < threshhold && gDiff < threshhold && bDiff < threshhold);

    }

    
    //Writes a bufferedimage to a given filepath
    public static void writeImage(BufferedImage img, String path){
	try{
	    File outputfile = new File(path);
	    ImageIO.write(img, "jpg", outputfile);
	}catch(Exception E){
	    System.out.println("didn't work");
	}
	
    }

    //Returns true if color is whit
    //Assumes 28bit color
    public static boolean isWhite(Color test){
	ColorCorrection colCorrector = new ColorCorrection();
	test = colCorrector.make12Bit(test);
	return isWhite12(test.getRed(), test.getGreen(), test.getBlue());
	
    }

    //Returns true if color is red
    //Assumes 28bit color
    public static boolean isRed(Color test){
	ColorCorrection colCorrector = new ColorCorrection();
	test = colCorrector.make12Bit(test);
	return closeEnough(test, Color.RED, 150) || closeEnough(test, Color.PINK, 150) || closeEnough(test, Color.MAGENTA, 150) || isRed12(test.getRed(), test.getGreen(), test.getBlue());
	
    }


    //Returns number of red pixels in image
    public static int getRedPixelCount(BufferedImage image) {
	int count = 0;
	for(int i = 0; i < image.getWidth(); i++) {
	    for(int j = 0; j < image.getHeight(); j++) {
		Color color = new Color(image.getRGB(i,j));
		if(isRed(color)) {
		    count++;
		}
	    }
	}
	return count;
    }
    
    //returns number of white pixels in image
    public static int getWhitePixelCount(BufferedImage image) {
	int count = 0;
	for(int i = 0; i < image.getWidth(); i++) {
	    for(int j = 0; j < image.getHeight(); j++) {
		Color color = new Color(image.getRGB(i,j));
		if(isWhite(color)) {
		    count++;
		}
	    }
	}
	return count;
    }

    //returns whether color is white or not
    //Assumes 12bit color
    public static boolean isWhite12(int red, int green, int blue){
	Integer rbDiff = red-blue;
	Integer rgDiff = red-green;
	Integer bgDiff = Math.abs(blue-green);
	return rbDiff >= -2 && rbDiff <= 3 && rgDiff >= -2 && rgDiff <= 3 && bgDiff <= 2 && red >= 3;
    }

    //returns whether color is red or not
    //Assumes 12bit color
    public static boolean isRed12(int red, int green, int blue){
	Integer rbDiff =  red-blue;
	Integer rgDiff = red-green;
	Integer bgDiff = Math.abs(blue-green);
	return rbDiff >= 4 && rgDiff >= 4 && bgDiff <= 2;
    }

    //Returns a random RGB
    public static int randomRGB(){
	Random rand = new Random();
	int r = rand.nextInt(255);
	int g = rand.nextInt(255);
	int b = rand.nextInt(255);
	
	Color col = new Color(r,g,b);
	
	return col.getRGB();


    }


    //Generates training images for the classifier and neural net given 
    //A waldo image of size 40x40 with a lime green background
    public static void makeTraining(BufferedImage input){
	Color replace = new Color(0,191,10);

	for(int i = 0; i < 3; i++){
	    for(int j = 0; j < 3; j++){
		    
		    int x = (int)((float)i * 10f);
		    int y = (int)((float)j * 10f);
		    BufferedImage originalSub = input.getSubimage(x,y,20,20);

		    int numBG = 25;
		    for(int k = 0; k < numBG; k++){
			BufferedImage newImage = deepCopy(originalSub);
			replaceColor(newImage, replace, randomRGB());
			writeImage(newImage, "train4/4noiseWaldo" + i + "" + j + "" + k + ".jpg");
		    }		   

		    
	    }
	    
	}
    } 
    
    

    //Draws a circle to the given image with the given attributes
    public static void addCircle(int x, int y, int r, BufferedImage img, String str){
	System.out.println("Adding circle: " + x + ", " + y + ", " + r);
	Graphics g = img.createGraphics();
	g.setColor(Color.GREEN);
	g.drawOval(x-r, y-r, 2*r, 2*r);
	int thickness = 6;
	for(int i = 0; i < thickness; i++){
	    r++;
	    g.drawOval(x-r, y-r, 2*r, 2*r);
	}

	if(str != ""){
	    g.setColor(Color.WHITE);
	    g.fillOval(x-5,y-15,30,30);
	    g.setColor(Color.BLACK);
	    g.drawString(str, x, y);
	    

	}

    }

    //Distance between two subimages
    public static double dist(Subimage a, Subimage b){

	return Math.sqrt(Math.pow(Math.abs(a.getX() - b.getX()),2) + Math.pow(Math.abs(a.getY() - b.getY()),2));

    }

    
    //replaces each pixel of a given color in an image to a random color
    public static void replaceColor(BufferedImage input, Color searchColor, int rgb){
	for(int i = 0; i < input.getWidth(); i++){
	    for(int j = 0; j < input.getHeight(); j++){
		Color test = new Color(input.getRGB(i,j));
		
		if(closeEnough(test, searchColor,75)){
		    input.setRGB(i,j,randomRGB());
		}
		
	    }
	}
    }

    //Makes sure that RGB values stay approriate range [0, 255]
    public static float[] adjustRGB(float[] toChange){
	float[] adjusted = {0.0f, 0.0f, 0.0f};
	adjusted[0] = (toChange[0] < 0.0f) ? 0.0f : toChange[0];
	adjusted[1] = (toChange[1] < 0.0f) ? 0.0f : toChange[1];
	adjusted[2] = (toChange[2] < 0.0f) ? 0.0f : toChange[2];

	adjusted[0] = (toChange[0] > 255.0f) ? 255.0f : toChange[0];
	adjusted[1] = (toChange[1] > 255.0f) ? 255.0f : toChange[1];
	adjusted[2] = (toChange[2] > 255.0f) ? 255.0f : toChange[2];
	return adjusted;
    }
}
