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
	int totalHeight = image.getHeight();
	int totalWidth = image.getWidth();
	int widthNum = (2 * (totalWidth / width)) - 1;
	int heightNum = (2 * (totalHeight / height)) - 1;
	int widthStep = width / 2;
	int heightStep = height / 2;
	
	for(int i = 0; i < widthNum; i++){
	    for(int j = 0; j < heightNum; j++){
		BufferedImage subimage = image.getSubimage(i * widthStep, j * heightStep, width, height);
		subimages.add(subimage);
	    }
	}

	int extraWidth = (int)((float) totalWidth % (float) width);
	int extraHeight = (int)((float) totalHeight % (float) height);
	
	if(extraWidth != 0){
	    for(int i = 0; i < heightNum; i++){
		BufferedImage subimage = image.getSubimage(totalWidth - (2 * extraWidth), i * heightStep, extraWidth * 2, height);
		subimages.add(subimage);
	    }   
	}

	if(extraHeight != 0){
	    for(int i = 0; i < widthNum; i++){
		BufferedImage subimage = image.getSubimage(i * widthStep, totalHeight - (2 * extraHeight), width, 2 * extraHeight);
		subimages.add(subimage);
	    }
	}

	if(extraWidth != 0 && extraHeight != 0){
	    BufferedImage subimage = image.getSubimage(totalWidth - (2 * extraWidth), totalHeight - (2 * extraHeight), 2 * extraWidth, 2 * extraHeight);
	    subimages.add(subimage);
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