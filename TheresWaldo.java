//(c) John Freeman, Diego Gonzalez, Jose Rivas
import java.io.File;
import java.io.IOException;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.util.Vector;

import java.awt.Color;

//Currently, a class that does the creation and writing of subimages of the original image
public class TheresWaldo {
    
    //Global variables
    private BufferedImage image;
    private BufferedImage histImage;
    
    //Constructor
    public TheresWaldo(BufferedImage image, BufferedImage histImage) {
	this.image = image;
	this.histImage = histImage;
    }
    
    private Boolean possibleWaldo(BufferedImage image, int x, int y, int width, int height){
	BufferedImage hist = histImage.getSubimage(x, y, width, height);
	float prop = 0.0f;
	for(int i = 0; i < width; i++){
	    for(int j = 0; j < height; j++){
		Integer col = hist.getRGB(i,j);
		
		if(col == Color.WHITE.getRGB()){
		    prop += 1.0f;
		}
	    }
	}

	return prop/(float)(width * height) >= 0.25f;
    }

    //Generates a Vector of Subimages of the original image where each subimage is of
    //the dimensions given and does a 50% overlap to avoid cutting something off
    public Vector<Subimage> createSubimages(int width, int height) {
	Vector<Subimage> subimages = new Vector<Subimage>();
	
	//Save the dimensions of the original image
	int totalHeight = image.getHeight();
	int totalWidth = image.getWidth();
	
	//Calculate the number of cus you must make on a given side of the image
	int widthNum = (2 * (totalWidth / width)) - 1;
	int heightNum = (2 * (totalHeight / height)) - 1;
	
	//Calculate how far each step must be down a side to perform a slice with
	//50% overlap
	int widthStep = width / 2;
	int heightStep = height / 2;
	
	//Go down both sides of the image and create subimages and put them in the Vector
	for(int i = 0; i < widthNum; i++){
	    for(int j = 0; j < heightNum; j++){
		int x = i * widthStep;
		int y = j * heightStep;
		BufferedImage subimage = image.getSubimage(x, y, width, height);
		if(possibleWaldo(subimage, x,y,width,height))
		   subimages.add(new Subimage(subimage, x, y));
		
	    }
	}

	//Check if there is extra image on the sides of the image that were not
	//included in the subimages
	int extraWidth = (int)((float) totalWidth % (float) width);
	int extraHeight = (int)((float) totalHeight % (float) height);
	
	//If there is extra space on the width of the image not included in any subimage,
	//create subimages to include it and add it to the Vector
	if(extraWidth != 0){
	    int x = totalWidth - (2 * extraWidth);
	    for(int i = 0; i < heightNum; i++){
		int y = i * heightStep;
		BufferedImage subimage = image.getSubimage(x, y, extraWidth * 2, height);
		if(possibleWaldo(subimage, x, y, extraWidth * 2,height))
		   subimages.add(new Subimage(subimage, x, y));
	    }   
	}

	//If there is extra space on the height of the image not included in any subimage,
	//create subimages to include it and add it to the Vector
	if(extraHeight != 0){
	    int y = totalHeight - (2 * extraHeight);
	    for(int i = 0; i < widthNum; i++){
		int x = i * widthStep;
		BufferedImage subimage = image.getSubimage(x, y, width, 2 * extraHeight);
		if(possibleWaldo(subimage, x,y,width,2 * extraHeight))
		   subimages.add(new Subimage(subimage, x, y));
	    }
	}

	//If there is extra space on the width and the height of the image not included in any subimage,
	//create a subimage to include thast corner and add it to the Vector
	if(extraWidth != 0 && extraHeight != 0){
	    int x = totalWidth - (2 * extraWidth);
	    int y = totalHeight - (2 * extraHeight);
	    BufferedImage subimage = image.getSubimage(x, y, 2 * extraWidth, 2 * extraHeight );
	    if(possibleWaldo(subimage, x,y,extraWidth * 2, extraHeight * 2))
	       subimages.add(new Subimage(subimage, x, y));
	}
    
	return subimages;
    }

    //Write a Vector of subimages to the home directory
    public void writeSubimages(Vector<Subimage> subimages) {
	for(int i = 0; i < subimages.size(); i++){
	    subimages.get(i).writeImage("Subimage" + i + ".jpg");
	}
    }

    //Write a Vector of subimages to the home directory
    public void writeSubimages(Vector<Subimage> subimages, String path) {
	for(int i = 0; i < subimages.size(); i++){
	    subimages.get(i).writeImage(path + i + ".jpg");
	}
    }
}