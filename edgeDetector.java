import java.util.Iterator;
import java.lang.Math;
import java.io.File;
import java.io.IOException;
import java.awt.image.BufferedImage;
import java.awt.Color;
import javax.imageio.ImageIO;
import java.util.ArrayList;
import java.util.Collections;

public class edgeDetector {


    public edgeDetector() { }
    
    public int[] getSpliceSize(BufferedImage image){

	// To be used for edge detection
	int previousSum = -1;
	int currentSum = -1;


	
	// To be used to find largest edge width
	int currentWidth = 0;
	int maxWidth = 0;

	for( int i = 0; i < image.getWidth(); i++){
	    for( int j = 0; j < image.getHeight(); j++){
		Color mycolor = new Color(image.getRGB(i, j));
		currentSum = mycolor.getRed() + mycolor.getBlue() + mycolor.getGreen();
		     
		if (previousSum == -1){
		    previousSum = currentSum;
		}
		
		if(Math.abs(previousSum - currentSum) > 80){
		    currentWidth += 1;
		}else if (currentWidth > 0 && currentWidth > maxWidth){
		    maxWidth = currentWidth;
		    currentWidth = 0;
		}else{
		    currentWidth = 0;
		}
		previousSum = currentSum;
		
		    
	    }
	}
	//Reset the values
	previousSum = -1;
	currentSum = -1;
	
	int currentHeight = 0;
	int maxHeight = 0;
	    
	    
	for( int j = 0; j < image.getHeight(); j++){
	    for( int i = 0; i < image.getWidth(); i++){
		Color mycolor = new Color(image.getRGB(i, j));
		currentSum = mycolor.getRed() + mycolor.getBlue() + mycolor.getGreen();
		     
		if (previousSum == -1){
		    previousSum = currentSum;
		}

		if(Math.abs(previousSum - currentSum) > 80){
		    currentHeight += 1;
		}else if (currentHeight > 0 && currentHeight > maxHeight){
		    maxHeight = currentHeight;
		    currentHeight = 0;
		}else{
		    currentHeight = 0;
		}
		previousSum = currentSum;
		    
		    
	    }
	}


	return new int[]{2*maxWidth, 2*maxHeight};


    }



}
