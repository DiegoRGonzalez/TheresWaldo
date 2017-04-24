import java.io.File;
import java.io.IOException;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;


public class TheresWaldo {


    public TheresWaldo(String path) {
	try {
	    BufferedImage image= ImageIO.read(new File(path));
	    
	    for( int i = 0; i < image.getWidth(); i++){
		for( int j = 0; j < image.getHeight(); j++){
		    image.getRGB(i, j);
		}
	    }
	    System.out.println("Waldo is somewhere in there");
	    

	}
	catch (IOException e){
	    System.out.println("Waldo is not in there");
	}

    }


    public static void main(String[] argv){
	System.out.println(argv[0]);
	new TheresWaldo(argv[0]);
    }






}