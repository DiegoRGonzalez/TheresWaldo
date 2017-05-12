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

    //Constructor
    public TheresWaldo(BufferedImage image) {
	this.image = image;
    }

    public void blackOutWaldoIm(BufferedImage waldoImage, int xPos, int yPos, int width, int height){
	
	int imWidth = waldoImage.getWidth();
	int imHeight = waldoImage.getHeight();	
	
	for(int x = xPos; x < xPos + width; x++) {
	    for(int y = yPos; y < yPos + height; y++) {
		waldoImage.setRGB(x, y, Color.BLACK.getRGB());
	    }   
	}
    }
    
    public void createFirstQuartIm(Node node, float conf, int x, int y, int width, int height, BufferedImage histImage, BufferedImage waldoImage){
	int halfWidth = width/2;
	int halfHeight = height/2;

	int imWidth = waldoImage.getWidth();
	int imHeight = waldoImage.getHeight();	
       
	int xPos = x;
	int yPos = y - height;
	
	if(!(xPos < 0) && !(yPos <0)){
	
	    xPos = (xPos + width > imWidth) ? imWidth - width - 1: xPos;
	    yPos = (yPos + height > imHeight) ? imHeight - height - 1: yPos;
	   
	    BufferedImage subimage = image.getSubimage(xPos, yPos, width, height);
	    
	    Subimage newImage = new Subimage(subimage, xPos, yPos);

	    blackOutWaldoIm(waldoImage, xPos, yPos, width, height);
	    
	    node.insert(newImage, conf);
	}
    }

    public void createSecondQuartIm(Node node, float conf, int x, int y, int width, int height, BufferedImage histImage, BufferedImage waldoImage){
	int halfWidth = width/2;
	int halfHeight = height/2;

	int imWidth = waldoImage.getWidth();
	int imHeight = waldoImage.getHeight();	
       
	int xPos = x - width;
	int yPos = y - height;
	
	if(!(xPos < 0) && ! (yPos <0)){
	
	    xPos = (xPos + width > imWidth) ? imWidth - width - 1: xPos;
	    yPos = (yPos + height > imHeight) ? imHeight - height - 1: yPos;
	   
	    BufferedImage subimage = image.getSubimage(xPos, yPos, width, height);
	    
	    Subimage newImage = new Subimage(subimage, xPos, yPos);
	    
	    blackOutWaldoIm(waldoImage, xPos, yPos, width, height);
	    
	    node.insert(newImage, conf);
	}
    }

    public void createThirdQuartIm(Node node, float conf, int x, int y, int width, int height, BufferedImage histImage, BufferedImage waldoImage){
	int halfWidth = width/2;
	int halfHeight = height/2;

	int imWidth = waldoImage.getWidth();
	int imHeight = waldoImage.getHeight();	
       
	int xPos = x;
	int yPos = y;
	   
	xPos = (xPos + width > imWidth) ? imWidth - width - 1: xPos;
	yPos = (yPos + height > imHeight) ? imHeight - height - 1: yPos;
	   
	BufferedImage subimage = image.getSubimage(xPos, yPos, width, height);
	   
	Subimage newImage = new Subimage(subimage, xPos, yPos);
	   
	blackOutWaldoIm(waldoImage, xPos, yPos, width, height);

	node.insert(newImage, conf);
    }

    public boolean createCenterIm(Node node, int x, int y, int width, int height, BufferedImage histImage, BufferedImage waldoImage){
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
		
       if(totalProb >= 0.1f){	
	   int xPos = x - halfWidth;
	   int yPos = y - halfHeight;
	   
	   xPos = (xPos + width > imWidth) ? imWidth - width - 1: xPos;
	   yPos = (yPos + height > imHeight) ? imHeight - height - 1: yPos;
	   
	   BufferedImage subimage = image.getSubimage(xPos, yPos, width, height);
	   
	   Subimage newImage = new Subimage(subimage, xPos, yPos);
	   
	   blackOutWaldoIm(waldoImage, xPos, yPos, width, height);
	   
	   node.insert(newImage, totalProb);	
	   createFirstQuartIm(node, totalProb, x, y, width, height, histImage, waldoImage);
	   createSecondQuartIm(node, totalProb, x, y, width, height, histImage, waldoImage);
	   createThirdQuartIm(node, totalProb, x, y, width, height, histImage, waldoImage);

	   return true;
       }

       return false;
    }
    
    public void processImage(Node node, int x, int y, int width, int height, BufferedImage histImage, BufferedImage waldoImage){
	int halfWidth = width/2;
	int halfHeight = height/2;

	int imWidth = waldoImage.getWidth();
	int imHeight = waldoImage.getHeight();	

	for(int i = 0; i < imWidth; i++){
	    for(int j = 0; j < imHeight; j++){
		Color prob = new Color(histImage.getRGB(i,j));
		if(prob.getRGB() != Color.BLACK.getRGB()){
		    createCenterIm(node, i, j, width, height, histImage, waldoImage);
		}
	    }
	}
	
    }
    
    //Generates a vector of Subimages of the original image where each subimage is of
    //the dimensions given and does a 50% overlap to avoid cutting something off
    public Vector<Subimage> createSubimages(int width, int height, BufferedImage histImage, BufferedImage waldoImage) {
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
	Node node = new Node();

	int halfWidth = width/2;
	int halfHeight = height/2;

	int imWidth = waldoImage.getWidth();
	int imHeight = waldoImage.getHeight();	

	for(int i = 0; i < imWidth; i++){
	    for(int j = 0; j < imHeight; j++){
		Color prob = new Color(histImage.getRGB(i,j));
		if(prob.getRGB() != Color.BLACK.getRGB()){
		    createCenterIm(node, i, j, width, height, histImage, waldoImage);
			// createFirstQuartIm(node, i, j, width, height, histImage, waldoImage);
			// createSecondQuartIm(node, i, j, width, height, histImage, waldoImage);
			// createThirdQuartIm(node, i, j, width, height, histImage, waldoImage);
		}
	    }
	}
	

	// //Go down both sides of the image and create subimages and put them in the Vector
	// for(int i = 0; i < totalWidth; i = i + widthStep){
	//     for(int j = 0; j < totalHeight; j = j + heightStep){
		
	// 	float totalProb = 0;
	// 	float numPixels = 0;
	// 	float numColored = 0;
		
	// 	for(int x = i - widthStep; x < i + widthStep && x < totalWidth && i > widthStep; x++) {
	// 	    for(int y = j - heightStep; y < j + heightStep && y < totalHeight && j > heightStep; y++) {
	// 		numPixels += 1.0f;
	// 		Color probColor = new Color(histImage.getRGB(x, y));
	// 		int prob = probColor.getRed();
	// 		if(prob > 0) {
	// 		    totalProb += (float) prob;
	// 		    numColored += 1.0f;
	// 		}
			
	// 	    }
		    
	// 	}

		
	// 	totalProb /= (numColored > 0) ? numColored : 0.0f;
	// 	totalProb /= 255.0f;
		
	// 	if(totalProb >= 0.1f && numColored/numPixels >= 0.01f){		  
	// 	    int x = (width + i > totalWidth) ? totalWidth - 1 - i : i;
	// 	    int y = (height + j > totalHeight) ? totalHeight - 1 - j : j;		    
	// 	    x = x - widthStep;
	// 	    y = y - heightStep;

	// 	    BufferedImage subimage = image.getSubimage(x, y , width, height);
		    
	// 	    Subimage newImage = new Subimage(subimage, x, y);

	// 	    for(int r = i-widthStep; r < i + widthStep && r < totalWidth; r++) {
	// 		for(int s = j-heightStep; s < j + heightStep && s < totalHeight; s++) {
	// 		    waldoImage.setRGB(r,s, Color.BLACK.getRGB());
	// 		}
			
	// 	    }

	// 	    node.insert(newImage, newImage.getConfLevel());
	// 	}
	//     }
	// }

    node = node.getNext();
	while (node != null){
	    subimages.add(node.getSubimage());
	    node = node.getNext();
	}
	
	Subimage.writeImage("SubbedImages", waldoImage);
	return subimages;
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
		subimages.add(new Subimage(subimage, x, y));
	    }
	}

	//If there is extra space on the width and the height of the image not included in any subimage,
	//create a subimage to include thast corner and add it to the Vector
	if(extraWidth != 0 && extraHeight != 0){
	    int x = totalWidth - (2 * extraWidth);
	    int y = totalHeight - (2 * extraHeight);
	    BufferedImage subimage = image.getSubimage(x, y, 2 * extraWidth, 2 * extraHeight);
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
