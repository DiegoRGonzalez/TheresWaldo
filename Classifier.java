// (c) 2017 Jose Rivas-Garcia, Diego Gonzalez and John Freeman

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

    /* Takes a vector of Subimages and classifies them according to how close they appear to be Waldo.
     * The method calls "classify image" which classifies according to how much red and white appear.
     */
    public Vector<Subimage> classify(Vector<Subimage> subimages) {
	
	Vector<Subimage> filter = new Vector<Subimage>();
	int numImages = subimages.size();
	for(int i = 0; i < numImages; i++){
	    Subimage sub = subimages.get(i);
	    if(classifyImage(sub)){
		filter.add(sub);
	    }
	}
	return filter;
    }

    /* Classifies a vector of images based on whether the images are within a certain range of standard 
     * deviations away from the learned mean of red and white color proportion for a regular Waldo.
     * Calls classifyImageByStandardDev to help classify an image. 
     */
    public Vector<Subimage> classifyByStandardDev(Vector<Subimage> subimages, float minSD, float maxSD) {
       
	// Make sure that the minSD is truly the minimum of minSD and maxSD
	if (minSD > maxSD) {
	    minSD = maxSD;
	    maxSD = minSD;
	}

	Vector<Subimage> filter = new Vector<Subimage>();

	final int numImages = subimages.size();

	for(int i = 0; i < numImages; i++){
	    
	    Subimage sub = subimages.get(i);
	    if(classifyImageByStandardDev(sub, minSD, maxSD)){
		filter.add(sub);
	    }
	    
	}
	
	return filter;
    }

    /* Calculates how many standard devations an image falls from a regular Waldo image. 
     * The mean is the learned mean from a set of real Waldo images.
     */
    public float getImageStandardDev(Subimage subimage){
	float conf = subimage.getConfLevel();
	
	return Math.abs(conf - waldoConfidence) / waldoStandardErr;
    }

    /* Determines whether an image falls within a given range of standard deviations from the mean.
     */
    public boolean classifyImageByStandardDev(Subimage waldoImage, Float minSD, Float maxSD){
	float sd = getImageStandardDev(waldoImage);
	return (sd >= minSD && sd <= maxSD); 
    }

    /* Determins whether the image falls within 3 standard deviations from the mean.
     */
    public boolean classifyImage(Subimage waldoImage) {
	float sd = getImageStandardDev(waldoImage);
	return (sd <= 3.0f);
    }

    /* Takes a vector of real Waldo images and determines the mean and standard deviation. This allows
     * the program to determine whether or not another image is similar to a "real" Waldo image.
     */
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
