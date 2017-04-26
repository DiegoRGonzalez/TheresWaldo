import java.io.File;
import java.io.IOException;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.awt.Color;
import java.util.Hashtable;
import java.util.*;
import java.lang.*;

public class Histogram {
    /*public Histogram(Vector<BufferedImage> images) {
	try {
	    
	    
	    Hashtable<Color, int> histogram = new Hashtable<int, int>();
	    Set<Color> colorSet = new Set<Color>();

	    if (images.isEmpty()){
		System.out.println("Need images.");
		exit(1);
	    }
	    
	    for( int i = 0; i < image.getWidth(); i++){
		for( int j = 0; j < image.getHeight(); j++){
		    Color color = new Color(image.getRGB(x,y));
		    if (histogram.contains(color)) 
			histogram[color] += 1;
		    else {
			histogram[color] = 0;
			colorSet.add(color);
		    }
		}
	    }
	    
	    System.out.println(colorSet);
	    System.out.println("Waldo is somewhere in there");
	    
	    
	}
	catch (IOException e){
	    System.out.println("Waldo is not in there");
	}
    }
    */

    private Integer editColor(Integer color){
	int r = (color >> 16) & 0xff;
	int g = (color >> 8) & 0xff;
	int b = (color & 0xff);
	
	return new Integer(256 * r/16 + 16 * g/16 + b/16);
	    
    }
    
    public Boolean histogramSimilarity(Hashtable<Integer, Integer> waldo, Hashtable<Integer, Integer> compare){
	Set<Integer> waldoKeys = waldo.keySet();

	float topSum = 0;
	float botSum = 0;
	for(Integer integer: waldoKeys){
	    Integer waldoVal = waldo.get(integer);
	    Integer compVal = compare.get(integer);
	    if(compVal != null){
		topSum += Math.min(waldoVal.intValue(), compVal.intValue());
		botSum += compVal.intValue();
		
	    }
	}
	
	return topSum/botSum > 0.6;
	
    }

    public Boolean histogramSimilarity2(Hashtable<Integer, Integer> waldo, Hashtable<Integer, Integer> compare){
	Set<Integer> waldoKeys = waldo.keySet();

	float sum = 0;
	for(Integer integer: waldoKeys){
	    float top = 0;
	    float bot = 0;
	    Integer waldoVal = waldo.get(integer);
	    Integer compVal = compare.get(integer);
	    if(compVal != null || (waldoVal == 0 && compVal == 0)){
		top = waldoVal.intValue() - compVal.intValue();
		top *= top;
		bot = waldoVal.intValue() + compVal.intValue();	
	    }
	    if(bot != 0) sum += top/bot;
	}
	
	sum *= 2;
	System.out.println(sum);
	return sum > 500;
    }

    public Histogram(String[] imagePath) {
	try {
	    BufferedImage image = ImageIO.read(new File(imagePath[0]));
	    
	    Hashtable<Integer, Integer> histogram = new Hashtable<Integer, Integer>();
	    Set<Integer> colorSet = new HashSet<Integer>();
	    int numColors = 0;
	    
	    for( int x = 0; x < image.getWidth(); x++){
		for( int y = 0; y < image.getHeight(); y++){
		    Integer color = image.getRGB(x,y);
		    //color = editColor(color);	    
		    
		    Integer numOccurences = histogram.get(color);

		    if (numOccurences == null){
			histogram.put(color, 1);
		    } else{
			histogram.put(color, numOccurences + 1);
		    } 
		}
	    }
	    
	    BufferedImage otherImage = ImageIO.read(new File(imagePath[1]));
	    
	    Hashtable<Integer, Integer> histogram2 = new Hashtable<Integer, Integer>();

	    for( int x = 0; x < otherImage.getWidth(); x++){
		for( int y = 0; y < otherImage.getHeight(); y++){
		    Integer color = otherImage.getRGB(x,y);
		    color = editColor(color);	    
		    
		    Integer numOccurences = histogram2.get(color);
		    
		    if (numOccurences == null){
			histogram2.put(color, 1);
		    } else{
			histogram2.put(color, numOccurences + 1);
		    } 
		}
	    }
	    
	    System.out.println(histogramSimilarity(histogram, histogram2));
	    System.out.println(histogramSimilarity2(histogram, histogram2));

	}
	catch (IOException e){
	    System.out.println("Waldo is not in there");
	}
    }

    public static void main(String[] argv){
	
	new Histogram(argv);
    }

}