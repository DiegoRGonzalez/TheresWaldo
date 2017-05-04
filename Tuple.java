// (c) 2017 Jose Rivas-Garcia, Diego Gonzales and John Freeman

import java.io.File;
import java.io.IOException;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.awt.Color;
import java.util.Hashtable;
import java.util.*;
import java.lang.*;

public class Tuple<F, S> {

    private F first;
    private S second;
	
    public Tuple(F first, S second) {
	this.first = first;
	this.second = second;
    }
	
    public Tuple() {
	}

    @Override public boolean equals(Object o) {
	if (!(o instanceof Tuple)) {
	    return false;
	}
	Tuple<?, ?> p = (Tuple<?, ?>) o;
	return first.equals(p.first) && second.equals(p.second);
    }
    
}