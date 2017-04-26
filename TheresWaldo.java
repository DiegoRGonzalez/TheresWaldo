import java.io.File;
import java.io.IOException;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.util.Vector;

public class TheresWaldo {
    
    private BufferedImage image;

    public TheresWaldo(BufferedImage image) {
	this.image = image;
    }

    public Vector<BufferedImage> createSubimages(int width, int height) {
	Vector<BufferedImage> subimages = new Vector<BufferedImage>();
	int widthNum = (2 * (image.getWidth() / width)) - 1;
	int heightNum = (2 * (image.getHeight() / height)) - 1;
	int widthStep = width / 2;
	int heightStep = height / 2;
	
	for(int i = 0; i < widthNum; i++){
	    for(int j = 0; j < heightNum; j++){
		BufferedImage subimage = image.getSubimage(i * widthStep, j * heightStep, width, height);
		subimages.add(subimage);
	    }
	}

	for(int i = 0; i < subimages.size(); i++){
	    try{
		File outputfile = new File("subimage"+i+".jpg");
		ImageIO.write(subimages.get(i), "jpg", outputfile);
	    } catch(IOException e) {
		System.out.println("There was a problem");
	    }	
	}

	return subimages;
    }
}