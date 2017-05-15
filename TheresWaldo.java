//(c) 2017 John Freeman, Diego Gonzalez, Jose Rivas

import java.io.File;
import java.io.IOException;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.util.Vector;
import java.awt.Color;

/*
 * Takes the 
 */
public class TheresWaldo {

    // The "Where's Waldo" image given to the program.
    private BufferedImage waldoImage;
    private BufferedImage blackedOutImage;

    // Constructor
    public TheresWaldo(BufferedImage image) {
	this.waldoImage = image;
	this.blackedOutImage = Util.deepCopy(image);
    }

    // Black out the "Where's Waldo" image.
    private void blackOutWaldoIm(int xPos, int yPos, int width, int height){	
	int imWidth = blackedOutImage.getWidth();
	int imHeight = blackedOutImage.getHeight();	
	
	for(int x = xPos; x < xPos + width; x++) {
	    for(int y = yPos; y < yPos + height; y++) {
		blackedOutImage.setRGB(x, y, Color.BLACK.getRGB());
	    }   
	}
    }
    
    private void createFirstQuartIm(SortedLinkedList node, int x, int y, int width, int height, BufferedImage histImage){
	int imWidth = waldoImage.getWidth();
	int imHeight = waldoImage.getHeight();	
       
	int xPos = x;
	int yPos = y - height;
	
	if(!(xPos < 0) && !(yPos <0)){
	
	    xPos = (xPos + width > imWidth) ? imWidth - width - 1: xPos;
	    yPos = (yPos + height > imHeight) ? imHeight - height - 1: yPos;
	   
	    BufferedImage subimage = waldoImage.getSubimage(xPos, yPos, width, height);
	    
	    Subimage newImage = new Subimage(subimage, xPos, yPos);

	    blackOutWaldoIm(xPos, yPos, width, height);
	    
	    node.insert(newImage, newImage.getConfLevel());
	}
    }

    private void createSecondQuartIm(SortedLinkedList node, int x, int y, int width, int height, BufferedImage histImage){
	int imWidth = waldoImage.getWidth();
	int imHeight = waldoImage.getHeight();	
       
	int xPos = x - width;
	int yPos = y - height;
	
	if(!(xPos < 0) && ! (yPos <0)){
	
	    xPos = (xPos + width > imWidth) ? imWidth - width - 1: xPos;
	    yPos = (yPos + height > imHeight) ? imHeight - height - 1: yPos;
	   
	    BufferedImage subimage = waldoImage.getSubimage(xPos, yPos, width, height);
	    
	    Subimage newImage = new Subimage(subimage, xPos, yPos);
	    
	    blackOutWaldoIm(xPos, yPos, width, height);
	    
	    node.insert(newImage,  newImage.getConfLevel());
	}
    }

    private void createThirdQuartIm(SortedLinkedList node, int x, int y, int width, int height, BufferedImage histImage){
	int imWidth = waldoImage.getWidth();
	int imHeight = waldoImage.getHeight();	
       
	int xPos = x;
	int yPos = y;
	   
	xPos = (xPos + width > imWidth) ? imWidth - width - 1: xPos;
	yPos = (yPos + height > imHeight) ? imHeight - height - 1: yPos;
	   
	BufferedImage subimage = waldoImage.getSubimage(xPos, yPos, width, height);
	   
	Subimage newImage = new Subimage(subimage, xPos, yPos);
	   
	blackOutWaldoIm(xPos, yPos, width, height);

	node.insert(newImage,  newImage.getConfLevel());
    }

    /*
     * 
     */
    public void createCenterIm(SortedLinkedList node, int x, int y, int width, int height, BufferedImage histImage){

	int imWidth = waldoImage.getWidth();
	int imHeight = waldoImage.getHeight();	

	int halfWidth = width/2;
	int halfHeight = height/2;
	     
	int xPos = x - halfWidth;
	int yPos = y - halfHeight;
	   
	xPos = (xPos + width > imWidth) ? imWidth - width - 1: xPos;
	yPos = (yPos + height > imHeight) ? imHeight - height - 1: yPos;
	   
	BufferedImage subimage = waldoImage.getSubimage(xPos, yPos, width, height);
	   
	Subimage newImage = new Subimage(subimage, xPos, yPos);
	   
	blackOutWaldoIm(xPos, yPos, width, height);
	   
	node.insert(newImage,  newImage.getConfLevel());	

    }
    
    private boolean shouldCreateImage(int x, int y, int width, int height, BufferedImage histImage){
	int halfWidth = width/2;
	int halfHeight = height/2;
       
	if(x - halfWidth < 0 || y - halfHeight < 0) return false;

	int imWidth = waldoImage.getWidth();
	int imHeight = waldoImage.getHeight();	
       
	float totalProb = 0;
	float numPixels = 0;
	float numColored = 0;	

	for(int i = x - halfWidth; i < x + halfWidth && i < imWidth && i >= 0; i++){
	    for(int j = y - halfHeight; j < y + halfHeight && j < imHeight && j >= 0; j++){
		Color probColor = new Color(histImage.getRGB(x, y));
		int prob = probColor.getRed();
		if(prob > 0) {
		    totalProb += (float) prob;
		    numColored += 1.0f;
		}
		histImage.setRGB(i,j, Color.BLACK.getRGB());
	    }
	}

	totalProb = (numColored > 0) ? totalProb/numColored : 0.0f;
	totalProb /= 255.0f;
		
	return totalProb >= 0.1f;
    }
    
    // Generates a sorted linked list of Subimages of the original image where each subimage is of
    // the dimensions given and does a 50% overlap to avoid cutting Waldo off. 
    public SortedLinkedList createSubimages(int width, int height, BufferedImage histImage) {
	
	//Save the dimensions of the original image
	int imWidth = histImage.getWidth();
	int imHeight = histImage.getHeight();	

	SortedLinkedList node = new SortedLinkedList();

	for(int i = 0; i < imWidth; i++){
	    for(int j = 0; j < imHeight; j++){
		Color prob = new Color(histImage.getRGB(i,j));
		if(prob.getRGB() != Color.BLACK.getRGB()){
		    if(shouldCreateImage(i, j, width, height, histImage)) {
			createCenterIm(node, i, j, width, height, histImage);			
			createFirstQuartIm(node, i, j, width, height, histImage);
			createSecondQuartIm(node, i, j, width, height, histImage);
			createThirdQuartIm(node, i, j, width, height, histImage);
		    }
		}
	    }
	}
	
	Subimage.writeImage("SubbedImages", blackedOutImage);
	return node;
    }

    // Write a Vector of subimages to the home directory
    public void writeSubimages(Vector<Subimage> subimages) {
	for(int i = 0; i < subimages.size(); i++){
	    subimages.get(i).writeImage("Subimage" + i + ".jpg");
	}
    }

    //Write a Vector of subimages to the given file path.
    public void writeSubimages(Vector<Subimage> subimages, String path) {
	for(int i = 0; i < subimages.size(); i++){
	    subimages.get(i).writeImage(path + i + ".jpg");
	}
    }
}
