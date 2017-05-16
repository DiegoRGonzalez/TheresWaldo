//(c) 2017 John Freeman, Diego Gonzalez, Jose Rivas
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.IOException;
import java.io.File;

//Simple class to save a buffered image and x and y value that corresponds to
//a small portion of the original image
public class Subimage {

    //Global variables
    private BufferedImage image;
    private Histogram hist;

    //Confidence level given by histogram classifier and 
    //corresponding standard deviation
    private float confLevel;
    private float sd;

    //Confidence level given by neuralnet
    private double neuralConfidence;

    //radius of circle
    private int radius;


    //x and y correspond to the coordinate of top left corner of the subimage
    //in the original image. We can get the rest of the pixels this subimage
    //covers in the original image from the radius.
    //
    //Later is treated as center of potential waldo circle.
    private int x;
    private int y;
    
    public Subimage(BufferedImage image, int x, int y) {
	this.image = image;
	this.x = x + 10;
	this.y = y + 10;
	this.hist = new Histogram(image);
	this.confLevel = hist.getWaldoConfidence();
	this.sd = 0.0f;
	this.radius = 20;
    }
    
    public BufferedImage getImage() {
	return image;
    }

    public int getRadius(){

	return radius;
    }
    
    public void setImage(BufferedImage newImage){
	this.image = newImage;
    }
    
    public void setNeuralConfidence(double confidence){
	this.neuralConfidence = confidence;
	
    }

    public double getNeuralConfidence(){
	return this.neuralConfidence;

    }
    
    
    public int[] getLocation() {
	return new int[]{x, y};
    }
    
    public int getX(){
	return x;
    }
    
    public int getY(){
	return y;
    }
    
    public float getConfLevel(){
	return confLevel;
    }

    public float getSD(){
	return sd;
    }

    //Converts the SD into a percentage to be applied to the histogram confidence
    //level to weight lower SD's as better
    public float getSDConfLevel(){
	float absSD = Math.abs(sd);
	if(absSD <= 0.5f){
	    return 1.0f;
	} else if(absSD <= 1.0f){
	    return 0.85f;
	} else if(absSD <= 1.5f){
	    return 0.65f;
	} else if(absSD <= 2.0f){
	    return 0.4f;
	} else if(absSD <= 2.5f){
	    return 0.25f;
	} else if(absSD <= 3f){
	    return 0.1f;
	}

	return 0.0f;
    }


    //Combine the two confidence levels
    public float getCombConfLevel(){
	double nConf = this.getNeuralConfidence();
	float pConf = this.getConfLevel();

	return 0.85f*(float)nConf + 1.0f*this.getSDConfLevel()*pConf;
	

    }


    //Combine two subimages and generates a new x and y position and radius.
    //Takes the higher of the two confidence levels for both nerual and histogram confidences
    public void addSubimage(Subimage add){
	
	this.setNeuralConfidence(Math.max(getNeuralConfidence() ,  add.getNeuralConfidence()));
	this.setConfLevel(Math.max(getConfLevel() , add.getConfLevel()));
	sd = Math.min(Math.abs(sd), Math.abs(add.getSD()));
	
	double dist = Util.dist(this, add); 

	//If the addition subimages is not contained within this subimage, adjust
	//the radius and x/y accordingly
	if((dist + add.getRadius()) > radius){

	    Subimage a = add;
	    Subimage b = this;

	    if(x < add.getX()){
		a = this;
		b = add;
	    }

	    int xRadius = ((b.getX() + b.getRadius()) - (a.getX() - a.getRadius()))/2;
	    int newX = ((a.getX() - a.getRadius()) + xRadius);

	    
	    a = add;
	    b = this;

	    if(y < add.getY()){
		a = this;
		b = add;
	    }


	    int yRadius = ((b.getY() + b.getRadius()) - (a.getY() - a.getRadius()))/2;
	    int newY = ((a.getY() - a.getRadius()) + yRadius);

	    
	    radius = (xRadius + yRadius)/2;

	    x = newX;
	    y = newY;
	  
	}
	

    }
    
    public int getWidth() {
	return image.getWidth();
    }
    
    public int getHeight() {
	return image.getHeight();
    }
    
    public boolean writeImage(String path){
	return writeImage(path, getImage());
    }
    
    public static boolean writeImage(String path, BufferedImage image){
	try{
	    File outputfile = new File(path);
	    ImageIO.write(image, "jpg", outputfile);
	    return true;
	} catch(IOException e) {
	    return false;
	}
    }
    
    public void setConfLevel(){
	this.confLevel = hist.getWaldoConfidence();
    }

    public void setConfLevel(float newConfLevel){
	confLevel = newConfLevel;
    }

    public void setSDLevel(float sd){
	this.sd = sd;
    }

    public Histogram getHistogram(){
	return hist;
    }
}
