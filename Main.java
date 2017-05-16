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

      int i = 1;
      Vector<Subimage> waldoImages = new Vector<Subimage>();
      for(; i <= num - 2; i++){
        BufferedImage im = ImageIO.read(new File(argv[i]));
        Subimage sub = new Subimage(im, 0, 0);
        waldoImages.add(sub);

      }
      classifier.setStandard(waldoImages);

      BufferedImage image = ImageIO.read(new File(argv[i]));
      
      image = corrector.normalize(image);
      /*
      BufferedImage img5 = ImageIO.read(new File("waldoGreen4.jpg"));
      BufferedImage newImage = new BufferedImage(40,40,BufferedImage.TYPE_INT_RGB);
      
      Graphics g = newImage.createGraphics();
      g.drawImage(img5,0,0,40,40,null);
      g.dispose();
      Util.makeTraining(newImage); 
      */

      BufferedImage wIm = Util.deepCopy(image);

      BufferedImage histImage = fullImHist.generateHistogram(image);
      Subimage.writeImage("WWWFullHist", histImage);

      TheresWaldo theresWaldo = new TheresWaldo(image);
      
      //BufferedImage redWhite = Util.getRedWhiteImg(image);
      //Subimage.writeImage("REDWHITE", redWhite);
      
      //int[] window = ed.getSpliceSize(image);

      Vector<Subimage> subimages = theresWaldo.createSubimages(25, 25, histImage, wIm);      
      //n.removeBackground(image);

      //Util.scaleImages(subimages);

      //theresWaldo.writeSubimages(subimages, "AllImages/Subimage");


      subimages = classifier.classify(subimages);
      Util.scaleImages(subimages);
      
      try {
	  MultilayerPerceptron mlp = (MultilayerPerceptron) weka.core.SerializationHelper.read("WaldoFinderNoise.model");
	  
	  ArffGenerator ag = new ArffGenerator();
	  Subimage waldo = new Subimage(image, 0, 0);
	  double highestWaldo = 0;





	  int maxSize = 50;
	  Comparator<Subimage> comp = new SubimageComparator();
	  PriorityQueue<Subimage> pq = new PriorityQueue<Subimage>(maxSize, comp);
	    
	  boolean added = false;
	  int count = 0;
	  for(i = 0; i < subimages.size();){
	      added = false;

	      for(int j = 0; j < subimages.size(); j++){
		  Subimage img1 = subimages.get(i);
		  Subimage img2 = subimages.get(j);
		  
		  if(i != j && Util.dist(img1,img2) < img1.getRadius() + img2.getRadius()){
		      count++;
		      img2.addSubimage(img1);
		      subimages.remove(i);
		      added = true;
		      break;
		  }

	      }

	      if(!added){
		  i++;
	      }

	  }

 for(i = 0; i < subimages.size();){
	      added = false;

	      for(int j = 0; j < subimages.size(); j++){
		  Subimage img1 = subimages.get(i);
		  Subimage img2 = subimages.get(j);
		  
		  if(i != j && Util.dist(img1,img2) < img1.getRadius() + img2.getRadius()){
		      count++;
		      img2.addSubimage(img1);
		      subimages.remove(i);
		      added = true;
		      break;
		  }

	      }

	      if(!added){
		  i++;
	      }

	  }

	  System.out.println("Consolidated " + count + " circles");
	  

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
	  
	  while(pq.size() > 0){
	      Subimage img = pq.remove();
	      //img.writeImage("PotentialWaldos/potWaldo" +pq.size() + "__" + img.getCombConfLevel() + ".jpg");

	      Util.addCircle(img.getX() + 13, img.getY() +13, img.getRadius(), image, "" + pq.size());
	  }
	  
	  Util.writeImage(image, "circleTest.jpg");
	  
	  

      } catch(Exception ex) {
	  ex.printStackTrace();
      }

      

      

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
