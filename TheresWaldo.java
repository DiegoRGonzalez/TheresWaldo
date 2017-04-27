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

    public Vector<Subimage> createSubimages(int width, int height) {
	Vector<Subimage> subimages = new Vector<Subimage>();
	int totalHeight = image.getHeight();
	int totalWidth = image.getWidth();
	int widthNum = (2 * (totalWidth / width)) - 1;
	int heightNum = (2 * (totalHeight / height)) - 1;
	int widthStep = width / 2;
	int heightStep = height / 2;
	
	for(int i = 0; i < widthNum; i++){
	    for(int j = 0; j < heightNum; j++){
		int x = i * widthStep;
		int y = j * heightStep;
		BufferedImage subimage = image.getSubimage(x, y, width, height);
		subimages.add(new Subimage(subimage, x, y));
	    }
	}

	int extraWidth = (int)((float) totalWidth % (float) width);
	int extraHeight = (int)((float) totalHeight % (float) height);
	
	if(extraWidth != 0){
	    int x = totalWidth - (2 * extraWidth);
	    for(int i = 0; i < heightNum; i++){
		int y = i * heightStep;
		BufferedImage subimage = image.getSubimage(x, y, extraWidth * 2, height);
		subimages.add(new Subimage(subimage, x, y));
	    }   
	}

	if(extraHeight != 0){
	    int y = totalHeight - (2 * extraHeight);
	    for(int i = 0; i < widthNum; i++){
		int x = i * widthStep;
		BufferedImage subimage = image.getSubimage(x, y, width, 2 * extraHeight);
		subimages.add(new Subimage(subimage, x, y));
	    }
	}

	if(extraWidth != 0 && extraHeight != 0){
	    int x = totalWidth - (2 * extraWidth);
	    int y = totalHeight - (2 * extraHeight);
	    BufferedImage subimage = image.getSubimage(x, y, 2 * extraWidth, 2 * extraHeight);
	    subimages.add(new Subimage(subimage, x, y));
	}
    
	return subimages;
}

    public void writeSubimages(Vector<Subimage> subimages) {
	for(int i = 0; i < subimages.size(); i++){
	    try{
		File outputfile = new File("subimage"+i+".jpg");
		ImageIO.write(subimages.get(i).getImage(), "jpg", outputfile);
	    } catch(IOException e) {
		System.out.println("There was a problem");
	    }	
	}
    }
}