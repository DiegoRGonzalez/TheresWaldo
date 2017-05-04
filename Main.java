//(c) 2017 John Freeman, Diego Gonzalez, Jose Rivas
import java.io.File;
import java.io.IOException;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.util.Scanner;
import java.util.Vector;

public class Main {

    //Main method of our whole program
    public static void main(String[] argv){
	Scanner scan = new Scanner(System.in);
	EdgeDetector ed = new EdgeDetector();
	Classifier classifier = new Classifier();

	while(true){
	    System.out.println("Please input an image file that contains a Waldo:");
	    String path = scan.next();
	    try {
		//Save the Waldo image given to us
		//BufferedImage image= ImageIO.read(new File(path));
		//TheresWaldo theresWaldo = new TheresWaldo(image);

		//Runs the edge detector and creates a list of subimages of the original image
		//based on the result of that edge detector
		//int[] window = ed.getSpliceSize(image);
		//Vector<Subimage> subimages = theresWaldo.createSubimages(window[0],window[1]);
		
		BufferedImage im = ImageIO.read(new File(path));
		Subimage sub = new Subimage(im, 0, 0);
		Histogram hist = new Histogram(sub);
		
		/*
		//Classify the Vector of Subimages and prune them to the ones we believe might contain
		//Waldo based on the histogram of that Subimage
		System.out.println("Original # subimages: "+subimages.size());
		subimages = classifier.classify(subimages);
		System.out.println("Trimmed # subimages: "+subimages.size());
		*/
		//Write the subimages to the directory for debugging purposes
		//theresWaldo.writeSubimages(subimages);
	    } catch(IOException e){
		System.out.println("Not a valid image file!");
		continue;
	    }
	}
    }
}