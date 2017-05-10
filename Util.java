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
	    BufferedImage newImage = new BufferedImage(25,25,BufferedImage.TYPE_INT_RGB);

	    Graphics g = newImage.createGraphics();
	    g.drawImage(before,0,0,25,25,null);
	    g.dispose();
		
	    images.get(i).setImage(newImage);
	}
		
    }

    public boolean closeEnough(Color one, Color two, int threshhold){
	int rDiff = Math.abs(one.getRed() - two.getRed());
	int gDiff = Math.abs(one.getGreen() - two.getGreen());
	int bDiff = Math.abs(one.getBlue() - two.getBlue());
	//return false;
	return (rDiff < threshhold && gDiff < threshhold && bDiff < threshhold);

    }

    /*    public int bucketFill(int x, int y, BufferedImage input, BufferedImage buffer){
	Color original = new Color(input.getRGB(x,y));
	int numFilled = 0;
	

	if(x+1 < input.getWidth()){
	    Color right = new Color(input.getRGB(x+1,y));
	    Color test = new Color(buffer.getRGB(x+1,y));
	    if(test != Color.BLACK && closeEnough(original,right,50)){
		buffer.setRGB(x,y,Color.BLACK.getRGB());
		numFilled += 1 + bucketFill(x+1,y,input,buffer);
	    }
	    
	}

	if(x-1 >= 0){
	    Color left = new Color(input.getRGB(x-1,y));
	    Color test = new Color(buffer.getRGB(x-1,y));
	    if(test != Color.BLACK && closeEnough(original,left,50)){
		buffer.setRGB(x,y,Color.BLACK.getRGB());
		numFilled += 1 + bucketFill(x-1,y,input,buffer);
	    }
	}

	if(y+1 < input.getHeight()){
	    Color up = new Color(input.getRGB(x,y+1));
	    Color test = new Color(buffer.getRGB(x,y+1));
	    if(test != Color.BLACK && closeEnough(original,up,50)){
		buffer.setRGB(x,y,Color.BLACK.getRGB());
		numFilled += 1 + bucketFill(x,y+1,input,buffer);
	    }

	}

	if(y-1 >= 0){
	    Color down = new Color(input.getRGB(x,y-1));
	    Color test = new Color(buffer.getRGB(x,y-1));
	    if(test != Color.BLACK && closeEnough(original,down,50)){
		buffer.setRGB(x,y,Color.BLACK.getRGB());
		numFilled += 1 + bucketFill(x,y-1,input,buffer);
	    }
	}


	System.out.println(numFilled);
	return numFilled;

    }
    */


    public int bucketFillNoSE(int x,int y,BufferedImage image, int threshhold){
	ArrayList<ArrayList<Boolean>> buffer = new ArrayList<ArrayList<Boolean>>();
	for(int i = 0; i < image.getWidth(); i++){

	    ArrayList<Boolean> innerBuffer = new ArrayList<Boolean>();
	    for(int j = 0; j < image.getHeight(); j++){
		innerBuffer.add(false);
	    }    
	    buffer.add(innerBuffer);

	}
	buffer.get(x).set(y, true);
	Color originalColor = new Color(image.getRGB(x,y));
	int numFilled = 0;
	for(int i = x; i > 0; i--){
	    for(int j = y; j > 0; j--){
		Color test = new Color(image.getRGB(i,j));
		if(!buffer.get(i).get(j) && closeEnough(originalColor, test, threshhold)){
			if(buffer.get(i+1).get(j) || buffer.get(i-1).get(j) ||
			   buffer.get(i).get(j+1) || buffer.get(i).get(j-1)){
			    buffer.get(i).set(j,true);
			    numFilled++;
		
			}
		} 
	    }
	}

	for(int i = 1; i < (image.getWidth() - 1); i++){
	    for(int j = 1; j < (image.getHeight() - 1); j++){
		Color test = new Color(image.getRGB(i,j));
		if(!buffer.get(i).get(j) && closeEnough(originalColor, test, threshhold)){
			if(buffer.get(i+1).get(j) || buffer.get(i-1).get(j) ||
			   buffer.get(i).get(j+1) || buffer.get(i).get(j-1)){
			    buffer.get(i).set(j,true);
			    numFilled++;
		
			}
		} 
	    }
	}	


	for(int i = (image.getWidth() -5); i > 5; i--){
	    for(int j = (image.getHeight() -5); j > 5; j--){
		Color test = new Color(image.getRGB(i,j));
		if(!buffer.get(i).get(j) && closeEnough(originalColor, test, threshhold)){
			if(buffer.get(i+1).get(j) || buffer.get(i-1).get(j) ||
			   buffer.get(i).get(j+1) || buffer.get(i).get(j-1)){
			    buffer.get(i).set(j,true);
			    numFilled++;

			}
		} 
	    }
	}


	return numFilled;
	
    }


    public int bucketFill(int x,int y,BufferedImage image, int threshhold){
	ArrayList<ArrayList<Boolean>> buffer = new ArrayList<ArrayList<Boolean>>();
	for(int i = 0; i < image.getWidth(); i++){

	    ArrayList<Boolean> innerBuffer = new ArrayList<Boolean>();
	    for(int j = 0; j < image.getHeight(); j++){
		innerBuffer.add(false);
	    }    
	    buffer.add(innerBuffer);

	}
	buffer.get(x).set(y, true);
	Color originalColor = new Color(image.getRGB(x,y));
	int numFilled = 0;
	for(int i = x; i > 0; i--){
	    for(int j = y; j > 0; j--){
		Color test = new Color(image.getRGB(i,j));
		if(!buffer.get(i).get(j) && closeEnough(originalColor, test, threshhold)){
			if(buffer.get(i+1).get(j) || buffer.get(i-1).get(j) ||
			   buffer.get(i).get(j+1) || buffer.get(i).get(j-1)){
			    buffer.get(i).set(j,true);
			    numFilled++;
			    image.setRGB(i,j,Color.BLACK.getRGB());
			}
		} 
	    }
	}

	for(int i = 1; i < (image.getWidth() - 5); i++){
	    for(int j = 1; j < (image.getHeight() - 5); j++){
		Color test = new Color(image.getRGB(i,j));
		if(!buffer.get(i).get(j) && closeEnough(originalColor, test, threshhold)){
			if(buffer.get(i+1).get(j) || buffer.get(i-1).get(j) ||
			   buffer.get(i).get(j+1) || buffer.get(i).get(j-1)){
			    buffer.get(i).set(j,true);
			    numFilled++;
			    image.setRGB(i,j,Color.BLACK.getRGB());
			}
		} 
	    }
	} 	



	for(int i = (image.getWidth() -5); i > 5; i--){
	    for(int j = (image.getHeight() -5); j > 5; j--){
		Color test = new Color(image.getRGB(i,j));
		if(!buffer.get(i).get(j) && closeEnough(originalColor, test, threshhold)){
			if(buffer.get(i+1).get(j) || buffer.get(i-1).get(j) ||
			   buffer.get(i).get(j+1) || buffer.get(i).get(j-1)){
			    buffer.get(i).set(j,true);
			    numFilled++;
			    image.setRGB(i,j,Color.BLACK.getRGB());
			}
		} 
	    }
	}


 
	return numFilled;
	
    }



    public BufferedImage getRedWhiteImg(BufferedImage input){
	for(int i = 0; i < input.getWidth(); i++){
	    for(int j = 0; j < input.getHeight(); j++){
		Color color = new Color(input.getRGB(i,j));
		if(!(isRed(color) || isWhite(color))){
		    input.setRGB(i,j,Color.BLACK.getRGB());
		}
		
	       	    
	    }
	}

	return input;
	

    }

    public BufferedImage removeAllButColors(ArrayList<Color> colors, BufferedImage input, int threshhold){
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

    public BufferedImage getRedWhiteImgOLD(BufferedImage input, int threshhold){
	ArrayList<Color> colors = new ArrayList<Color>();
	colors.add(Color.RED);
	colors.add(Color.WHITE);
	colors.add(Color.PINK);
	return removeAllButColors(colors, input, threshhold);
	
    }

    public void writeImage(BufferedImage img, String path){
	try{
	    File outputfile = new File(path);
	    ImageIO.write(img, "jpg", outputfile);
	}catch(Exception E){
	    System.out.println("didn't work");
	}
	
    }

    public boolean isWhite(Color test){
	ColorCorrection colCorrector = new ColorCorrection();
	test = colCorrector.make12Bit(test);
	return isWhite12(test.getRed(), test.getGreen(), test.getBlue());
	
    }

    
    public boolean isRed(Color test){
	ColorCorrection colCorrector = new ColorCorrection();
	test = colCorrector.make12Bit(test);
	return isRed12(test.getRed(), test.getGreen(), test.getBlue());
	
    }
    

    public boolean isWhite12(int red, int green, int blue){
	Integer rbDiff = red-blue;
	Integer rgDiff = red-green;
	Integer bgDiff = Math.abs(blue-green);
	return Math.abs(rbDiff) <= 3 && Math.abs(rgDiff) <= 3 && bgDiff <= 2;
    }

    public boolean isRed12(int red, int green, int blue){
	Integer rbDiff = red-blue;
	Integer rgDiff = red-green;
	Integer bgDiff = Math.abs(blue-green);
	return rbDiff >= 4 && rgDiff >= 4 && bgDiff <= 2;
    }


    

    // public BufferedImage removeBackground(BufferedImage input){
	    
    // 	int mostFilled = 0;
    // 	BufferedImage noBG = this.deepCopy(input);
    // 	int x = 0;
    // 	int y = 0;
    // 	for(int i = 0; i < input.getWidth()/4; i+=10){
    // 	    for(int j = 0; j < input.getHeight()/4; j+=10){
		
		
    // 		int newFilled = bucketFillNoSE(i,j,noBG,70);
    // 		if(newFilled > mostFilled){
    // 			System.out.println(newFilled);
    // 			mostFilled = newFilled;
    // 			x = i; y = j;
    // 		}
    // 	    }
    // 	}
    // 	System.out.println("DONE");
    // 	Color bg = new Color(input.getRGB(x,y));
    // 	bucketFill(x,y,noBG,70);    
    // 	/*for(int i = 0; i < input.getWidth(); i++){
    // 	     for(int j = 0; j < input.getHeight(); j++){
    // 		 noBG.setRGB(i,j,bg.getRGB());
		 
    // 	     }
    // 	     }*/
    
    // 	return input;
    // }
    
    
}