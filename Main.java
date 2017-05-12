//(c) 2017 John Freeman, Diego Gonzalez, Jose Rivas
import java.io.File;
import java.io.IOException;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.util.Scanner;
import java.util.Vector;
import java.util.Comparator;
import java.util.PriorityQueue;

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

      BufferedImage wIm = Util.deepCopy(image);

      BufferedImage histImage = fullImHist.generateHistogram(image);
      Subimage.writeImage("WWWFullHist", histImage);

      TheresWaldo theresWaldo = new TheresWaldo(image);
      
      //BufferedImage redWhite = Util.getRedWhiteImg(image);
      //Subimage.writeImage("REDWHITE", redWhite);
      
      int[] window = ed.getSpliceSize(image);

      Vector<Subimage> subimages = theresWaldo.createSubimages(25, 25, histImage, wIm);      
      //n.removeBackground(image);

      //Util.scaleImages(subimages);

      theresWaldo.writeSubimages(subimages, "AllImages/Subimage");

      subimages = classifier.classify(subimages);

      Vector<Subimage> sd0To1 = classifier.classifyByStandardDev(subimages, 0.0f, 1.0f);
      Vector<Subimage> sd1To2 = classifier.classifyByStandardDev(subimages, 1.0f, 2.0f);
      Vector<Subimage> sd2To3 = classifier.classifyByStandardDev(subimages, 2.0f, 3.0f);

      theresWaldo.writeSubimages(sd0To1, "SD0To1/Subimage");
      theresWaldo.writeSubimages(sd1To2, "SD1To2/Subimage");
      theresWaldo.writeSubimages(sd2To3, "SD2To3/Subimage");
     
    } catch (Exception e){
	System.out.println(e);
	e.printStackTrace();
	usage();
    }
  }
}
