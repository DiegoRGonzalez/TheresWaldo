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
    
    //Returns an int array with the width and height of a subimage size
    public static void scaleImages(Vector<Subimage> images){
	final int numImages = images.size();
	Vector<Subimage> scaledImages = new Vector<Subimage>(numImages);

	for(int i = 0; i < numImages; i++){
	    BufferedImage before = images.get(i).getImage();
	    BufferedImage newImage = new BufferedImage(20,20,BufferedImage.TYPE_INT_RGB);

	    Graphics g = newImage.createGraphics();
	    g.drawImage(before,0,0,20,20,null);
	    g.dispose();
		
	    images.get(i).setImage(newImage);
	}
	
	
		
    }

    public static boolean closeEnough(Color one, Color two, int threshhold){
	int rDiff = Math.abs(one.getRed() - two.getRed());
	int gDiff = Math.abs(one.getGreen() - two.getGreen());
	int bDiff = Math.abs(one.getBlue() - two.getBlue());
	//return false;
	return (rDiff < threshhold && gDiff < threshhold && bDiff < threshhold);

    }



    public static BufferedImage getRedWhiteImg(BufferedImage input){
	for(int i = 0; i < input.getWidth(); i++){
	    for(int j = 0; j < input.getHeight(); j++){
		Color color = new Color(input.getRGB(i,j));
		if((isRed(color) || isWhite(color))){
		    //input.setRGB(i,j,Color.WHITE.getRGB());
		}else{
		    input.setRGB(i,j,Color.BLACK.getRGB());
		}
		
	       	    
	    }
	}

	return input;
	

    }

    public static BufferedImage removeAllButColors(ArrayList<Color> colors, BufferedImage input, int threshhold){
	for(int i = 0; i < input.getWidth(); i++){
	    for(int j = 0; j < input.getHeight(); j++){
		boolean keepColor = false;
		for (int k = 0; k < colors.size() && !keepColor; k++){
		    Color cur = new Color(input.getRGB(i,j));
		    Color test = colors.get(k);
		    keepColor = closeEnough(test, cur, threshhold);
		}	
		
		if(!keepColor){
		    input.setRGB(i,j,Color.BLACK.getRGB());
		}
	       	    
	    }
	}

	return input;
	

    }

    public static BufferedImage getRedWhiteImgOLD(BufferedImage input, int threshhold){
	ArrayList<Color> colors = new ArrayList<Color>();
	colors.add(Color.RED);
	colors.add(Color.WHITE);
	colors.add(Color.PINK);
	return removeAllButColors(colors, input, threshhold);
	
    }

    public static void writeImage(BufferedImage img, String path){
	try{
	    File outputfile = new File(path);
	    ImageIO.write(img, "jpg", outputfile);
	}catch(Exception E){
	    System.out.println("didn't work");
	}
	
    }

    public static boolean isWhite(Color test){
	ColorCorrection colCorrector = new ColorCorrection();
	test = colCorrector.make12Bit(test);
	return isWhite12(test.getRed(), test.getGreen(), test.getBlue());
	
    }

    
    public static boolean isRed(Color test){
	ColorCorrection colCorrector = new ColorCorrection();
	test = colCorrector.make12Bit(test);
	return closeEnough(test, Color.RED, 150) || closeEnough(test, Color.PINK, 150) || closeEnough(test, Color.MAGENTA, 150) || isRed12(test.getRed(), test.getGreen(), test.getBlue());
	
    }

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

    public static boolean isWhite12(int red, int green, int blue){
	Integer rbDiff = red-blue;
	Integer rgDiff = red-green;
	Integer bgDiff = Math.abs(blue-green);
	return rbDiff >= -2 && rbDiff <= 3 && rgDiff >= -2 && rgDiff <= 3 && bgDiff <= 2 && red >= 2;
    }

    public static boolean isRed12(int red, int green, int blue){
	Integer rbDiff =  red-blue;
	Integer rgDiff = red-green;
	Integer bgDiff = Math.abs(blue-green);
	return rbDiff >= 4 && rgDiff >= 4 && bgDiff <= 2;
    }


   
 

    public static int randomRGB(){
	Random rand = new Random();
	int r = rand.nextInt(255);
	int g = rand.nextInt(255);
	int b = rand.nextInt(255);
	
	Color col = new Color(r,g,b);
	
	return col.getRGB();


    }

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
    
    /*
    public static void addCircle(int x, int y, int r, BufferedImage img){


	for (int j = y-r; j < y+r; j++) {
	    for (int i = x; Math.pow((i-x),2) + Math.pow((j-y),2) <= Math.pow(r,2); i--) {
		
		if(i > 0 && i < img.getWidth() && j > 0 && j < img.getHeight()){
		    img.setRGB(i,j,Color.BLACK.getRGB());
		    
		}


	    }
	    for (int i = x+1; (i-x)*(i-x) + (j-y)*(j-y) <= r*r; i++) {
		
		if(i > 0 && i < img.getWidth() && j > 0 && j < img.getHeight()){
		    img.setRGB(i,j,Color.BLACK.getRGB());

		}


	    }
	}


	


    }*/
    
    public static void addCircle(int x, int y, int r, BufferedImage img){
	Graphics g = img.createGraphics();
	g.setColor(Color.GREEN);
	g.drawOval(x-r/2, y-r/2, r, r);
	int thickness = 6;
	for(int i = 0; i < thickness; i++){
	    r++;
	    g.drawOval(x-r/2, y-r/2, r, r);
	}

    }


    public static double dist(Subimage a, Subimage b){

	return Math.sqrt(Math.pow(Math.abs(a.getX() - b.getX()),2) + Math.pow(Math.abs(a.getY() - b.getY()),2));

    }

    
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
}
