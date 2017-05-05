import java.lang.Math;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import java.io.IOException;
import java.awt.Color;

public class ColorCorrection {


    public ColorCorrection () {}
    
    public BufferedImage normalize(BufferedImage image){
	int baseRed = 255;
	int baseGreen = 255;
	int baseBlue = 255;



	int numPixels = image.getWidth() * image.getHeight();
	float averageRed = 0 ;
	float averageBlue = 0;
	float averageGreen = 0;

	for( int i = 0; i < image.getWidth(); i++){
	    for( int j = 0; j < image.getHeight(); j++){
		Color currentColor = new Color(image.getRGB(i,j));
		
		averageRed += currentColor.getRed();
		averageBlue += currentColor.getBlue();
		averageGreen += currentColor.getGreen();
	    }
	}

	averageRed /= numPixels;
	averageBlue /= numPixels;
	averageGreen /= numPixels;


	float SDRed = 0;
	float SDGreen = 0;
	float SDBlue = 0;

	for( int i = 0; i < image.getWidth(); i++){
	    for( int j = 0; j < image.getHeight(); j++){
		Color currentColor = new Color(image.getRGB(i,j));
		
		SDRed += Math.pow(currentColor.getRed() - averageRed, 2);
		SDGreen += Math.pow(currentColor.getGreen() - averageGreen, 2);
		SDBlue += Math.pow(currentColor.getBlue() - averageBlue, 2);


	    }
	}	

	SDRed /= numPixels;
	SDRed = (float)Math.sqrt(SDRed);
	SDGreen /= numPixels;
	SDGreen = (float)Math.sqrt(SDGreen);
	SDBlue /= numPixels;
	SDBlue = (float)Math.sqrt(SDBlue);

	float maxRed = 0;
	float minRed = 255;
	float maxBlue = 0;
	float minBlue = 255;
	float maxGreen = 0;
	float minGreen = 255;


	for( int i = 0; i < image.getWidth(); i++){
	    for( int j = 0; j < image.getHeight(); j++){
		Color currentColor = new Color(image.getRGB(i,j));
		float r =  ((float)(currentColor.getRed() - averageRed)/SDRed);
		float g = ((float)(currentColor.getGreen() - averageGreen)/SDGreen);
		float b = ((float)(currentColor.getBlue() - averageBlue)/SDBlue);
		
		maxRed = Math.max(r,maxRed);
		minRed = Math.min(r,minRed); 
		maxGreen = Math.max(g,maxGreen);
		minGreen = Math.min(g,minGreen);
		maxBlue = Math.max(b,maxBlue);
		minBlue = Math.min(b,minBlue);	      
	
	    }
	}
	
	maxRed = (minRed < 0) ? maxRed + Math.abs(minRed) : maxRed - minRed;
	maxGreen = (minGreen < 0) ? maxGreen + Math.abs(minGreen) : maxGreen - minGreen;
	maxBlue = (minBlue < 0) ? maxBlue + Math.abs(minBlue) : maxBlue - minBlue;
	
	for( int i = 0; i < image.getWidth(); i++){
	    for( int j = 0; j < image.getHeight(); j++){
		Color currentColor = new Color(image.getRGB(i,j));

		float rN =  ((float)(currentColor.getRed() - averageRed)/SDRed);
		float gN = ((float)(currentColor.getGreen() - averageGreen)/SDGreen);
		float bN = ((float)(currentColor.getBlue() - averageBlue)/SDBlue);


		
		float rF = (minRed < 0) ? rN + Math.abs(minRed) : rN - minRed;
		float gF = (minGreen < 0) ? gN + Math.abs(minGreen) : gN - minGreen;
		float bF = (minBlue < 0) ? bN + Math.abs(minBlue) : bN - minBlue;
		
		


		int r = (int)((rF/maxRed)*255.0f);
		int g = (int)((gF/maxGreen)*255.0f);
		int b = (int)((bF/maxBlue)*255.0f);
		
		/*
		System.out.println("DIFF-R: " + Math.abs(r - currentColor.getRed()));
		System.out.println("DIFF-G: " + Math.abs(g - currentColor.getGreen()));
		System.out.println("DIFF-B: " + Math.abs(b - currentColor.getBlue()));
		*/

		image.setRGB(i ,j , new Color(r,g,b).getRGB());    
		
		
	    }
	}
	
	


	return image;
    }

    public Color make12Bit(Color col){

	Integer r = (Integer) col.getRed();
	Integer g = (Integer) col.getGreen();
	Integer b = (Integer) col.getBlue();
	
	float rP = (float) r/255.0f;
	float gP = (float) g/255.0f;
	float bP = (float) b/255.0f;
	
	r = (int) (rP * 15.0f);		
	g = (int) (gP * 15.0f);
	b = (int) (bP * 15.0f);
	
	return new Color(r, g, b);
	
    }

    public static void main(String[] argv){
	ColorCorrection correct = new ColorCorrection();
	try {
	    BufferedImage image = ImageIO.read(new File(argv[0]));
	    
	    ImageIO.write(correct.normalize(image), "jpg", new File("normalized.jpg"));
	    

	}
	catch( IOException e) {
	    System.out.println("No Image loaded");
	}
	
    }




}