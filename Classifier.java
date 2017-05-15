// (c) 2017 Jose Rivas-Garcia, Diego Gonzales and John Freeman

import java.io.File;
import java.io.IOException;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.awt.Color;
import java.util.Hashtable;
import java.util.*;
import java.lang.*;

public class Classifier {

    private float waldoConfidence = 0.0f;
    private float waldoStandardErr = 0.0f;
    
    public Classifier () {}

    /* Takes a vector of Subimages and classifies them as either Waldo or Not Waldo.
     * The method calls "classify image" which classifies according to how much
     * red and white appears in the image.
     */
    public Vector<Subimage> classify(Vector<Subimage> subimages) {
   
	Vector<Subimage> filter = new Vector<Subimage>();
	final int numImages = subimages.size();

	for(int i = 0; i < numImages; i++){
	    Subimage sub = subimages.get(i);
	    if(classifyImage(sub)){
		filter.add(sub);
	    }	    
	}

	System.out.println(numImages + " " + filter.size());
	
	return filter;
    }

    public Vector<Subimage> classifyByStandardDev(Vector<Subimage> subimages, float minSD, float maxSD) {
	
	if (minSD > maxSD) {
	    minSD = maxSD;
	    maxSD = minSD;
	}

	// A Vector holding only those images that belong to Waldo
	Vector<Subimage> filter = new Vector<Subimage>();
	final int numImages = subimages.size();

	for(int i = 0; i < numImages; i++){    
	    Subimage sub = subimages.get(i);
	    if(classifyImageByStandardDev(sub, minSD, maxSD)){
		filter.add(sub);
	    }
	    
	}

	System.out.println(numImages + " " + filter.size());
	
	return filter;
    }

    public float getImageStandardDev(Subimage subimage){
	float conf = subimage.getConfLevel();
	
	return Math.abs(conf - waldoConfidence) / waldoStandardErr;
    }

    public boolean classifyImageByStandardDev(Subimage waldoImage, Float minSD, Float maxSD){
	float sd = getImageStandardDev(waldoImage);
	return (sd >= minSD && sd <= maxSD); 
    }

    public boolean classifyImage(Subimage waldoImage) {
	float sd = getImageStandardDev(waldoImage);
	return (sd <= 3.0f);
    }

    public boolean setStandard(Vector<Subimage> waldoImages){
	float mean = 0.0f;

	Vector<Float> means = new Vector<Float>();
	for (int i = 0; i < waldoImages.size(); i++){
	    Float conf = waldoImages.get(i).getConfLevel();
	    
	    mean += conf;
	    means.add(conf);
	}

	mean /= (float) waldoImages.size();
	
	waldoConfidence = mean;

	float sd = 0.0f;
	for (int i = 0; i < means.size(); i++){
	    sd += Math.pow((means.get(i) - mean), 2.0f);	   
	}
	
	sd /= (float) means.size();

	sd = (float) Math.sqrt(sd);

	waldoStandardErr = sd;
	
	return true;
    }
}
