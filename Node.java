// (c) Jose Rivas and John Freeman and Diego Gonzales
import java.io.File;
import java.io.IOException;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.util.Vector;
import java.awt.Color;

/* A sorted linked list used only for subimages. This node class allows for easy 
 * insertion of subimages based on their confidence level. Furthermore, there is a dummy
 * node at the very beginning, in order to allow for easier insertion.
 */
public class Node{
    private Node next;
    private Subimage image;
    private float conf;
    private int size;

    public Node(){
	this.next = null;
	this.image = null;
	this.conf = 0;
	this.size = 0;
    }

    public Node(Subimage image, float confLevel){
	this.next = null;
	this.image = image;
	this.conf = confLevel;
	this.size = 0;
    }

    // Inserts an image based on its confidence level into the linked list. The first 
    // node is a dummy node, and therefore can be skipped when checking to see where an
    // element is to be inserted.
    public void insert(Subimage image, float confLevel){
	if(next == null) {
	    next = new Node(image, confLevel);
	} else {
	    Node temp = this;
	    Node tempNxt = temp.getNext();
	    while (tempNxt != null){
		if(tempNxt.getConf() < confLevel) break;		
		temp = temp.getNext();
		tempNxt = temp.getNext();
	    }
	    
	    Node newN = new Node(image, confLevel);
	    temp.setNext(newN);
	    newN.setNext(tempNxt);
	} 
	size++;
    }

    /********Acessor methods for private state.****************/
    public int getSize(){
	return size;
    }

    public Node getNext () {
	return next;
    }
	
    public float getConf() {
	return conf;	      
    }

    public Subimage getSubimage(){
	return image;
    }

    private void setNext(Node next){
	this.next = next;
    }
}
