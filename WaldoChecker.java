// (c) 2017 Jose Rivas-Garcia, Diego Gonzales and John Freeman

import java.io.File;
import java.io.IOException;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.awt.Color;
import java.util.Hashtable;
import java.util.*;
import java.lang.*;

/* A class to develop a color histogram of images to filter for Waldo
 * 
 * This is meant to be a very simple algorithm that can be used to find 
 * Waldo. It follows from the very ultra common strategy of looking for 
 * locations in the images where red and white appear most, since Waldo 
 * will most likely be there. 
 */

public class WaldoChecker {

    private Vector<Tuple<Integer, Integer>> positions = new Vector<Tuple<Integer, Integer>>();

    private Vector<Histogram> waldograms;
    
    public WaldoChecker () {}
    
    public WaldoChecker (Vector<Subimage> waldoImages) {
	for (int i = 0; i < waldoImages.size(); i++){
	    Histogram hist = waldoImages.get(i).getHistogram();
	    waldograms.add(hist);
	}
    }
    
    private boolean checkHistogram(Histogram hist){
	for (int i = 0; i < waldograms.size(); i++){
	    Histogram toCheck = waldograms.get(i);
	    
	    
	}
	return false;

    }
    
    public boolean isWaldo(Subimage image){
	Histogram toCheck = image.getHistogram();
	
	

	return true;
    }
}