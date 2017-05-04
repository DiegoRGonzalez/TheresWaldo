//(c) John Freeman, Diego Gonzalez, Jose Rivas
import java.io.File;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.util.Scanner;
import java.io.PrintWriter;
import java.awt.Color;

//Currently, a class that does the creation and writing of subimages of the original image
public class ArffGenerator {

    public ArffGenerator() {}
    
    private String generateAttributes() {
	String attr = "";
	for(int i = 0; i < 625; i++) {
	    attr += "@ATTRIBUTE pixel" + i + "red real\n";
	    attr += "@ATTRIBUTE pixel" + i + "green real\n";
	    attr += "@ATTRIBUTE pixel" + i + "blue real\n";
	}
	attr += "@ATTRIBUTE waldo {yes, no}\n";
	
	return attr;
    }
    
    public static void main(String[] args) {
	ArffGenerator ag = new ArffGenerator();

	if(args.length < 1) {
	    System.out.println("Need to give a file name for .arff file");
	    return;
	}
	try {
	        PrintWriter out = new PrintWriter(args[0]);
		out.print("@RELATION Waldo\n\n"+ag.generateAttributes()+"\n@DATA\n");
		
		Scanner in = new Scanner(System.in);
		System.out.println("Give file of images to make an .arff file");
		String foldername = in.next();
		File folder = new File(foldername);
		String[] files = folder.list();
		try {
		    for(int i = 0; i < files.length; i++) {
			String filename = files[i];
			BufferedImage image = ImageIO.read(new File(foldername + "/" + filename));
			String data = "";
			for(int x = 0; x < image.getWidth(); x++) {
			    for(int y = 0; y < image.getHeight(); y++) {
				Color color = new Color(image.getRGB(x, y));
				data += color.getRed() + ",";
				data += color.getGreen() + ",";
				data += color.getBlue() + ",";
			    }
			}
			data += (filename.toLowerCase().contains("waldo")) ? "yes\n" : "no\n";

			if((filename.toLowerCase().contains("waldo"))){
			    //System.out.println(data);
			}

			
			out.print(data);
			
		    }
		    out.close();
		} catch(IOException e) {
		    System.out.println("Only images in the folder");
		}
	} catch(FileNotFoundException e) {
	    System.out.println("Problem occured");
	    return;
	}
    }
}