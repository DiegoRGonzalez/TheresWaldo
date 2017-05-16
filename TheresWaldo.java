//(c) John Freeman, Diego Gonzalez, Jose Rivas
import java.io.File;
import java.io.IOException;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.util.Vector;
import java.awt.Color;

/* Stores and creates subimages from a "Where's Waldo" image given a second image of probabilities. 
 * This stores an original and a copy of the image, in order to allow the program to black out possible Waldo
 * locations, without interfering with the rest of the program.
 */
public class TheresWaldo {

    // Variables to store the "Where's Waldo" Image.
    private BufferedImage blackedOutImage;
    private BufferedImage waldoImage;
    private BufferedImage histImage;
    
    public TheresWaldo(BufferedImage image, BufferedImage histImage) {
	this.waldoImage = image;
	this.blackedOutImage = Util.deepCopy(image);
	this.histImage = histImage;
    }

    // Black out a rectangle of the waldo image from the x and y positions given for the given
    // width and height.
    private void blackOutWaldoIm(int xPos, int yPos, int width, int height){
	
	int imWidth = waldoImage.getWidth();
	int imHeight = waldoImage.getHeight();	
	
	for(int x = xPos; x < xPos + width; x++) {
	    for(int y = yPos; y < yPos + height; y++) {
		blackedOutImage.setRGB(x, y, Color.BLACK.getRGB());
	    }   
	}
    }
    
    // Create a subimage and insert it into the sorted linked list (node) based on its confidence level.
    // The subimage created will of size width * height with the x and y positions being the 
    // bottom left corner.
    private void createTopRightIm(Node node, int x, int y, int width, int height){
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

    // Create a subimage and insert it into the sorted linked list (node) based on its confidence level.
    // The subimage created will of size width * height with the x and y positions being the 
    // Bottom Right corner.
    private void createTopLeftIm(Node node, int x, int y, int width, int height){

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

    // Create a subimage and insert it into the sorted linked list (node) based on its confidence level.
    // The subimage created will of size width * height with the x and y positions being the 
    // Top Left corner.
    private void createBottomRightIm(Node node, int x, int y, int width, int height){
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

    // Create a subimage and insert it into the sorted linked list (node) based on its confidence level.
    // The subimage created will of size width * height with the x and y positions being the 
    // center of the image.
    private void createCenterIm(Node node, int x, int y, int width, int height){
	int halfWidth = width/2;
	int halfHeight = height/2;

	int imWidth = waldoImage.getWidth();
	int imHeight = waldoImage.getHeight();	
       
	int xPos = x - halfWidth;
	int yPos = y - halfHeight;
	   
	xPos = (xPos + width > imWidth) ? imWidth - width - 1: xPos;
	yPos = (yPos + height > imHeight) ? imHeight - height - 1: yPos;
	   
	BufferedImage subimage = waldoImage.getSubimage(xPos, yPos, width, height);
	   
	Subimage newImage = new Subimage(subimage, xPos, yPos);
	   
	blackOutWaldoIm(xPos, yPos, width, height);
	   
	node.insert(newImage,  newImage.getConfLevel());	

    }
    
    /* Determines if a subimage should be created at location (x,y) by looking at the probabilities from the histogram image.
     * This will return true if subimages should be created, or false otherwise.
     */
    private boolean shouldCreateSubimage(int x, int y, int width, int height){
	int halfWidth = width/2;
	int halfHeight = height/2;
       
	int imWidth = waldoImage.getWidth();
	int imHeight = waldoImage.getHeight();	

	// Make sure that a center image can be created from this location. If not, then we have reached the edges of the image, 
	// and do not need to go further.
	if((x - halfWidth < 0 || y - halfHeight < 0) || (x + halfWidth > imWidth || y + halfHeight > imHeight)) return false;
       
	// Go through the image and find the average probability of Waldo being there from a window of size (width/2 + 1) * (height/2 + 1).
	float totalProb = 0;
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

	// Get the average from all of the pixels that were colored. 
	totalProb = (numColored > 0) ? totalProb/numColored : 0.0f;
	totalProb /= 255.0f;
		
	return totalProb >= 0.1f;	
    }
    
    /* Generates a vector of Subimages of the original image where each subimage is of
     * the dimensions given and are sorted into a Sorted Linked List (Node). Four images
     * are created for each position, in order to ensure that we grab all possible locations
     * that Waldo could appear. For example, in case we only grabbed his shirt, we want to find
     * his face from that position, which could be above the pixel spotted.
     */ 
    public Vector<Subimage> createSubimages(int width, int height) {
	Vector<Subimage> subimages = new Vector<Subimage>();

	Node node = new Node();

	int halfWidth = width/2;
	int halfHeight = height/2;

	int imWidth = waldoImage.getWidth();
	int imHeight = waldoImage.getHeight();	

	// For each pixel in the image, determine if we should create a subimage and then create the four discussed above.
	for(int i = 0; i < imWidth; i++){
	    for(int j = 0; j < imHeight; j++){
		Color prob = new Color(histImage.getRGB(i,j));
		if(prob.getRGB() != Color.BLACK.getRGB() && shouldCreateSubimage(i, j, width, height)){
		    createCenterIm(node, i, j, width, height);
		    createTopRightIm(node, i, j, width, height);
		    createTopLeftIm(node, i, j, width, height);
		    createBottomRightIm(node, i, j, width, height);
		}
	    }
	}
	
	// Convert the sorted linked list back into a vector to return.
	node = node.getNext();
	while (node != null){
	    subimages.add(node.getSubimage());
	    node = node.getNext();
	}
	
	// Print out the locations where Waldo may be by blacking them out from the original image.
	Subimage.writeImage("SubbedImages.jpg", blackedOutImage);
	return subimages;
    }

    //Write a Vector of subimages to the home directory
    public void writeSubimages(Vector<Subimage> subimages) {
	for(int i = 0; i < subimages.size(); i++){
	    subimages.get(i).writeImage("Subimage" + i + ".jpg");
	}
    }

    //Write a Vector of subimages to the given path
    public void writeSubimages(Vector<Subimage> subimages, String path) {
	for(int i = 0; i < subimages.size(); i++){
	    subimages.get(i).writeImage(path + i + ".jpg");
	}
    }
}
