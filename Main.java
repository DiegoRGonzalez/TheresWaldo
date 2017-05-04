//(c) 2017 John Freeman, Diego Gonzalez, Jose Rivas
import java.io.File;
import java.io.IOException;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.util.Scanner;
import java.util.Vector;

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
	int argLen = argv.length;

	try{
	    int num = Integer.parseInt(argv[0]);
	
	    int i = 1;
	    Vector<Subimage> waldoImages = new Vector<Subimage>();
	    for(; i <= num; i++){
		BufferedImage im = ImageIO.read(new File(argv[i]));
		Subimage sub = new Subimage(im, 0, 0);
		waldoImages.add(sub);
		
	    }
	    classifier.setStandard(waldoImages);
	    
	    
	    BufferedImage image = ImageIO.read(new File(argv[i]));
	    image = corrector.normalize(image);

	    TheresWaldo theresWaldo = new TheresWaldo(image);

	    int[] window = ed.getSpliceSize(image);

	    Vector<Subimage> subimages = theresWaldo.createSubimages(window[0],window[1]);
	    
	    subimages = classifier.classify(subimages);

	    theresWaldo.writeSubimages(subimages);

	} catch (Exception e){
	    System.out.println(e);
	    e.printStackTrace();
	    usage();
	}
    }
}