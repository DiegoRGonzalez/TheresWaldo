import java.io.File;
import java.io.IOException;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.util.Scanner;


public class Main {
    public static void main(String[] argv){
	Scanner scan = new Scanner(System.in);
	while(true){
	    System.out.println("Please input an image file that contains a Waldo:");
	    String path = scan.next();
	    try {
		BufferedImage image= ImageIO.read(new File(path));
		TheresWaldo theresWaldo = new TheresWaldo(image);
		theresWaldo.createSubimages(38,38);
	    } catch(IOException e){
		System.out.println("Not a valid image file!");
		continue;
	    }
	}
    }
}