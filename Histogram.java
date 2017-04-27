import java.io.File;
import java.io.IOException;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.awt.Color;
import java.util.Hashtable;
import java.util.*;
import java.lang.*;

public class Histogram {
    /*public Histogram(Vector<BufferedImage> images) {
	try {
	    
	    
	    Hashtable<Color, int> histogram = new Hashtable<int, int>();
	    Set<Color> colorSet = new Set<Color>();

	    if (images.isEmpty()){
		System.out.println("Need images.");
		exit(1);
	    }
	    
	    for( int i = 0; i < image.getWidth(); i++){
		for( int j = 0; j < image.getHeight(); j++){
		    Color color = new Color(image.getRGB(x,y));
		    if (histogram.contains(color)) 
			histogram[color] += 1;
		    else {
			histogram[color] = 0;
			colorSet.add(color);
		    }
		}
	    }
	    
	    System.out.println(colorSet);
	    System.out.println("Waldo is somewhere in there");
	    
	    
	}
	catch (IOException e){
	    System.out.println("Waldo is not in there");
	}
    }
    */
    public Histogram() {}

    public Vector<Subimage> classify(Vector<Subimage> subimages) {
	Vector<Subimage> filter = new Vector<Subimage>();
	
	for(int i = 0; i < subimages.size(); i++) {
	    Subimage image = subimages.get(i);
	    if(classifyImage(image.getImage())){
		filter.add(image);
	    }
	}
	return filter;
    }

    public boolean classifyImage(BufferedImage waldoImage) {
	Hashtable<Integer, Integer> waldoHist = new Hashtable<Integer, Integer>();
	
	int waldoPixels = waldoImage.getWidth() * waldoImage.getHeight();
	
	int countRed = 0;
	int countWhite = 0;
	for( int x = 0; x < waldoImage.getWidth(); x++){
	    for( int y = 0; y < waldoImage.getHeight(); y++){
		
		Integer color = waldoImage.getRGB(x,y);
		Color col = new Color(color);
		
		Float red = (float) col.getRed()/255.0f;
		Float blue = (float) col.getBlue()/ 255.0f;
		Float green = (float) col.getGreen()/255.0f;

		Float rgDiff = Math.abs(red - green);
		    Float rbDiff = Math.abs(red - blue);
		    Float bgDiff = Math.abs(blue - green);
		    
		    if (red >= blue && red >= green && blue <= 0.2f && green <= 0.2f){			
			countRed += 1;
		    }
		    else if (red >= 0.7f  && blue >= 0.4f && green >= 0.4f){
			countWhite += 1;
		    }			
	    }
	}
	
	//	System.out.println(countRed);
	float whiteProp = (float) countWhite/ (float) waldoPixels;
	float redProp = (float) countRed/ (float) waldoPixels;
	
	//System.out.println(whiteProp + " " + redProp);
	if(redProp >= 0.07 && whiteProp >= 0.01f){
	    //  System.out.println("WALDO IS HERE");
	    return true;
	} else {
	    //System.out.println("WALDO IS NOT HERE");
	    return false;
	}	
	
    }
}