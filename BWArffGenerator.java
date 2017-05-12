//(c) 2017 John Freeman, Diego Gonzalez, Jose Rivas
import java.io.File;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.util.Scanner;
import java.io.PrintWriter;
import java.awt.Color;
import weka.core.Instance;

public class BWArffGenerator {

    public BWArffGenerator() {}

    public Instance createInstance(BufferedImage image) {
	BufferedImage test = image;
	Util util = new Util();
	//Instance inst = new Instance(3);
	Instance inst = new Instance((image.getWidth() * image.getHeight()));
	int i = 0;
	int average = 0;
	for(int x = 0; x < image.getWidth(); x++) {
	    for(int y = 0; y < image.getHeight(); y++) {
		Color color = new Color(image.getRGB(x, y));
		average = (color.getRed() + color.getGreen() + color.getBlue()) / 3;
		if(util.isRed(color)) {
		    average = (average * 2) / 3;
		} else if (!util.isWhite(color)) {
		    average /= 3;
		}
		inst.setValue(i++, average);
		Color grey = new Color(average, average, average);
		test.setRGB(x,y,grey.getRGB());
	    }
	}
	return inst;
    }
    
    private String generateAttributes() {
	String attr = "";
	for(int i = 0; i < 400; i++) {
	    attr += "@ATTRIBUTE pixel" + i + " Real\n";
	}
	attr += "@ATTRIBUTE waldo {yes, no}\n";
	return attr;
    }

    public String imageToArff(BufferedImage image) {
	String data = "";
	Util util = new Util();
	for(int x = 0; x < image.getWidth(); x++) {
	    for(int y = 0; y < image.getHeight(); y++) {
		Color color = new Color(image.getRGB(x, y));
		int average = (color.getRed() + color.getGreen() + color.getBlue()) / 3;
		if(util.isRed(color)) {
		    average = (average * 2) / 3;
		} else if (!util.isWhite(color)) {
		    average /= 3;
		}
		data += average + ",";
	    }
	}
	return data;
    }

    public void iterateFolders(String path, PrintWriter out) {
	File folder = new File(path);
	String[] files = folder.list();
	try {
	    for(int i = 0; i < files.length; i++) {
		String filename = files[i];
		File file = new File(path + "/" + filename);
		if(file.isDirectory()) {
		    iterateFolders(path + "/" + filename, out);
		} else {
		    BufferedImage image = ImageIO.read(file);
		    String data = imageToArff(image);
		    data += (filename.toLowerCase().contains("waldo")) ? ",yes\n" : ",no\n";
		    out.print(data);
		}
	    }
	} catch(IOException e) {
	    System.out.println("A problem occured.");
	    return;
	}
    }
    
    public static void main(String[] args) {
	BWArffGenerator ag = new BWArffGenerator();

	if(args.length < 2) {
	    System.out.println("Need to give a file name for .arff file and folder with test images");
	    return;
	}
	try {
	        PrintWriter out = new PrintWriter(args[0]);
		out.print("@RELATION Waldo\n\n"+ag.generateAttributes()+"\n@DATA\n");
		String foldername = args[1];
		ag.iterateFolders(foldername, out);
		out.close();
	} catch(FileNotFoundException e) {
	    System.out.println("Problem occured");
	    return;
	}
    }
}
