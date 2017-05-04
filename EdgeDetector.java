import java.util.Iterator;
import java.lang.Math;
import java.io.File;
import java.io.IOException;
import java.awt.image.BufferedImage;
import java.awt.Color;
import javax.imageio.ImageIO;
import java.util.ArrayList;
import java.util.Collections;
import java.awt.image.WritableRaster;
import java.awt.image.ColorModel;

public class EdgeDetector {


    public EdgeDetector() { }
    
    // copies a bufferedImage with no connection to the original 
    static BufferedImage deepCopy(BufferedImage bi) {
	
	ColorModel cm = bi.getColorModel();
	boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
	WritableRaster raster = bi.copyData(null);
	return new BufferedImage(cm, raster, isAlphaPremultiplied, null);
	
    }
    
    //Returns an int array with the width and height of a subimage size
    public int[] getSpliceSize(BufferedImage image){
	ArrayList widthList = new ArrayList();
	ArrayList heightList= new ArrayList();
	BufferedImage result = deepCopy(image);
	int threshold = 150;
	// To be used for edge detection
	int borderSum1 = -1;
	int borderSum2 = -1;
	int currentSum = -1;


	
	//Edges will be measured and stored here
	int currentHeight= 0;

	//Loop over the pixels and measure vertical edges
	for( int i = 0; i < image.getWidth(); i++){
	    for( int j = 0; j < image.getHeight(); j++){
		if(i != 0 && i != image.getWidth()-1){
		    Color mycolor = new Color(image.getRGB(i, j));
		    Color border1 = new Color(image.getRGB(i-1,j));
		    Color border2 = new Color(image.getRGB(i+1,j));
		    currentSum = mycolor.getRed() + mycolor.getBlue() + mycolor.getGreen();
		    borderSum1 = border1.getRed() + border1.getBlue() + border1.getGreen();
		    borderSum2 = border2.getRed() + border2.getBlue() + border2.getGreen();
							   
		
		    //Check to see if the pixel values between current pixel and adjacent pixels
		    //are different enough to indicate an edge
		    if(Math.abs(borderSum1 - currentSum) > threshold || Math.abs(borderSum2 - currentSum) > threshold){
			currentHeight += 1;
		
			result.setRGB( i,j,new Color(0,0,0).getRGB());
		
			
		    }else if (currentHeight > 0){
			heightList.add(currentHeight);
			currentHeight = 0;		   
		    }else{
			currentHeight = 0;
		    }
		    
		}
	    }
	}

	//Edges will be measured and stored here
	int currentWidth = 0;

	
	//Loop over the pixels and measure horizontal edges
	for( int j = 0; j < image.getHeight(); j++){
	    for( int i = 0; i < image.getWidth(); i++){
	    
		if(j != 0 && j != image.getHeight()-1){
		    Color mycolor = new Color(image.getRGB(i, j));
		    Color border1 = new Color(image.getRGB(i,j+1));
		    Color border2 = new Color(image.getRGB(i,j-1));
		    currentSum = mycolor.getRed() + mycolor.getBlue() + mycolor.getGreen();
		    borderSum1 = border1.getRed() + border1.getBlue() + border1.getGreen();
		    borderSum2 = border2.getRed() + border2.getBlue() + border2.getGreen();
					
		   
		    //Check to see if the pixel values between current pixel and adjacent pixels
		    //are different enough to indicate an edge
		    if(Math.abs(borderSum1 - currentSum) > threshold || Math.abs(borderSum2 - currentSum) > threshold){
			currentWidth += 1;
		    }else if (currentWidth > 5){
			widthList.add(currentWidth);
			currentWidth = 0;
		    }else{
			currentWidth = 0;
		    }
		    
		}
	    }
	}
	
       
	//Sort our ArrayLists of edges
	Collections.sort(widthList);
	Collections.sort(heightList);


	//Test code
	/*Iterator it = widthList.iterator();
	while(it.hasNext()){
	    System.out.println(it.next());
	    }*/
	/*try{
	    ImageIO.write(result, "jpg", new File("result.jpg"));

	}catch(IOException e){
	    System.out.println("Nope");
	}*/


	//In order to avoid outliers, we will get the 99th percentile size of edges and
	//use this as our rough estimate as to the size of a person.
	Float percentage = 99.0f/100.0f;
	int width = (int)widthList.get((int)Math.floor(percentage*widthList.size()));
	int height = (int)heightList.get((int)Math.floor(percentage*heightList.size()));
	
	//This is to ensure a square subimage size
	int size = Math.max(height,width);

	//Double the size of the person to make sure we get people inside our subimages
	return new int[]{2*size,2*size};
	//return new int[]{25, 25};

    }

    /*public static void main(String[] argv){
	edgeDetector detect = new edgeDetector();
	try {
	    BufferedImage image = ImageIO.read(new File(argv[0]));
	    int[] sizes = detect.getSpliceSize(image);
	    System.out.println(sizes[0] + " " + sizes[1]);
	}
	catch( IOException e) {
	    System.out.println("No Image loaded");
	}
	
    }*/

}
