import java.awt.image.BufferedImage;

public class Subimage {

    private BufferedImage image;
    private int x;
    private int y;

    public Subimage(BufferedImage image, int x, int y) {
	this.image = image;
	this.x = x;
	this.y = y;
    }

    public BufferedImage getImage() {
	return image;
    }

    public int[] getLocation() {
	return new int[]{x, y};
    }

    public int getWidth() {
	return image.getWidth();
    }

    public int getHeight() {
	return image.getHeight();
    }
}