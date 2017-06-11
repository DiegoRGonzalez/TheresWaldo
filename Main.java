//(c) 2017 John Freeman, Diego Gonzalez, Jose Rivas
import java.io.File;
import java.io.IOException;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.util.Scanner;
import java.util.Vector;
import java.util.Comparator;
import java.util.PriorityQueue;
import weka.core.*;
import weka.core.Instances;
import weka.classifiers.Evaluation;
import java.awt.Graphics;
import weka.classifiers.*;
import weka.classifiers.functions.MultilayerPerceptron;


public class Main {

  private static void usage(){
    System.out.println("ERROR");
    System.out.println("java Main #WaldoImages WaldoImageFolderFiles WheresWaldoImage");
    System.exit(1);
  }

  //Main method of our whole program
  public static void main(String[] argv){
    Scanner scan = new Scanner(System.in);
    EdgeDetector ed = new EdgeDetector();
    Classifier classifier = new Classifier();
    ColorCorrection corrector = new ColorCorrection();
    FullImageHistogram fullImHist = new FullImageHistogram();
    int argLen = argv.length;
    
    try{
	int num = argv.length;


	//Build our vector of training images for the classifier
	int i = 1;
	Vector<Subimage> waldoImages = new Vector<Subimage>();
	for(; i <= num - 2; i++){
	    BufferedImage im = ImageIO.read(new File(argv[i]));
	    Subimage sub = new Subimage(im, 0, 0);
	    waldoImages.add(sub);
	    
	}

	//Train the classifer
	classifier.setStandard(waldoImages);
	
	//Grab the main image and perform preproccessing for TheresWaldo
	BufferedImage image = ImageIO.read(new File(argv[i]));

	//Preproccessing 
	image = corrector.normalize(image);	
	BufferedImage wIm = Util.deepCopy(image);
	BufferedImage histImage = fullImHist.generateHistogram(image);

	//Create our vector of subimages to be checked for Waldonitiy
	TheresWaldo theresWaldo = new TheresWaldo(image, histImage);
	Vector<Subimage> subimages = theresWaldo.createSubimages(25, 25);      
	
	//Assign histogram confidence level to subimages and cut down potentional waldos
	subimages = classifier.classify(subimages);
	

	//Scale and preproccess subimages for the neural net
	Util.scaleImages(subimages);
	Util.consolidateCircles(subimages);
	
	
	MultilayerPerceptron mlp = (MultilayerPerceptron) weka.core.SerializationHelper.read("WaldoFinderNoise.model");	    
	ArffGenerator ag = new ArffGenerator();
	
	//Number of potential waldos to be found
	int maxSize = 50;
	Comparator<Subimage> comp = new SubimageComparator();
	PriorityQueue<Subimage> pq = new PriorityQueue<Subimage>(maxSize, comp);
	

	//Iterate over the subimages, grab the top (maxSize) images ranked by the classifier
	//and the neural net
	for(i = 0; i < subimages.size(); i++){
	    Subimage subimage = subimages.get(i);
		Instance inst = ag.createInstance(subimage.getImage());
		double[] result = mlp.distributionForInstance(inst);
		subimage.setNeuralConfidence(result[0]);
		
		
		if(pq.size() < maxSize){
		    pq.add(subimage);
		}else{
		    Subimage top = pq.remove();
		    if(top.getNeuralConfidence() > subimage.getNeuralConfidence()){
			pq.add(top);
		    }else{
			pq.add(subimage);
		    }
		    
		}
	}
	

	//Draw circles around the top potential waldos and print out the resulting image
	while(pq.size() > 0){
	    Subimage img = pq.remove();
	    //img.writeImage("PotentialWaldos/potWaldo" +pq.size() + "__" + img.getCombConfLevel() + ".jpg");
	    
	    Util.addCircle(img.getX() + 13, img.getY() +13, img.getRadius(), image, "" + pq.size());
	}
	
	Util.writeImage(image, "circleTest.jpg");
	
	
	
	
	
	

	
	//Debug code
	
	//Vector<Subimage> sd0To1 = classifier.classifyByStandardDev(subimages, 0.0f, 1.0f);
	// Vector<Subimage> sd1To2 = classifier.classifyByStandardDev(subimages, 1.0f, 2.0f);
	// Vector<Subimage> sd2To3 = classifier.classifyByStandardDev(subimages, 2.0f, 3.0f);
	
	// theresWaldo.writeSubimages(sd0To1, "SD0To1/Subimage");
	// theresWaldo.writeSubimages(sd1To2, "SD1To2/Subimage");
	// theresWaldo.writeSubimages(sd2To3, "SD2To3/Subimage");
	
    } catch (Exception e){
	System.out.println(e);
	e.printStackTrace();
	usage();
    }
  }
}
