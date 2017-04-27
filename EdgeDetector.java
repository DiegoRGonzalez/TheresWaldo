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
    
    static BufferedImage deepCopy(BufferedImage bi) {
	
	ColorModel cm = bi.getColorModel();
	boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
	WritableRaster raster = bi.copyData(null);
	return new BufferedImage(cm, raster, isAlphaPremultiplied, null);
	
    }
    
    public int[] getSpliceSize(BufferedImage image){
	ArrayList widthList = new ArrayList();
	ArrayList heightList= new ArrayList();
	BufferedImage result = deepCopy(image);
	int threshold = 150;
	// To be used for edge detection
	int borderSum1 = -1;
	int borderSum2 = -1;
	int currentSum = -1;


	
	// To be used to find largest edge width
	int currentHeight= 0;

	
	for( int i = 0; i < image.getWidth(); i++){
	    for( int j = 0; j < image.getHeight(); j++){
		if(i != 0 && i != image.getWidth()-1){
		    Color mycolor = new Color(image.getRGB(i, j));
		    Color border1 = new Color(image.getRGB(i-1,j));
		    Color border2 = new Color(image.getRGB(i+1,j));
		    currentSum = mycolor.getRed() + mycolor.getBlue() + mycolor.getGreen();
		    borderSum1 = border1.getRed() + border1.getBlue() + border1.getGreen();
		    borderSum2 = border2.getRed() + border2.getBlue() + border2.getGreen();
							   
		
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

	// To be used to find largest edge width
	int currentWidth = 0;
	int sum = 0;
	for( int j = 0; j < image.getHeight(); j++){
	    for( int i = 0; i < image.getWidth(); i++){
	    
		if(j != 0 && j != image.getHeight()-1){
		    Color mycolor = new Color(image.getRGB(i, j));
		    Color border1 = new Color(image.getRGB(i,j+1));
		    Color border2 = new Color(image.getRGB(i,j-1));
		    currentSum = mycolor.getRed() + mycolor.getBlue() + mycolor.getGreen();
		    borderSum1 = border1.getRed() + border1.getBlue() + border1.getGreen();
		    borderSum2 = border2.getRed() + border2.getBlue() + border2.getGreen();
							   
		
		    if(Math.abs(borderSum1 - currentSum) > threshold || Math.abs(borderSum2 - currentSum) > threshold){
			currentWidth += 1;
		    }else if (currentWidth > 5){
			widthList.add(currentWidth);
			sum += currentWidth;
			currentWidth = 0;
		    }else{
			currentWidth = 0;
		    }
		    
		}
	    }
	}
	
       
	Collections.sort(widthList);
	Collections.sort(heightList);

	/*Iterator it = widthList.iterator();
	while(it.hasNext()){
	    System.out.println(it.next());
	    }*/
	/*try{
	    ImageIO.write(result, "jpg", new File("result.jpg"));

	}catch(IOException e){
	    System.out.println("Nope");
	}*/

	Float percentage = 99.0f/100.0f;
	int width = (int)widthList.get((int)Math.floor(percentage*widthList.size()));
	int height = (int)heightList.get((int)Math.floor(percentage*heightList.size()));
	return new int[]{2*width,2*height};


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
