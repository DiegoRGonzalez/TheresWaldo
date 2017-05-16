//(c) 2017 John Freeman, Jose Rivas, and Diego Gonzalez
import java.io.*;
import weka.core.*;
import weka.core.Instances;
import weka.classifiers.Evaluation;
import weka.classifiers.*;
import weka.classifiers.functions.MultilayerPerceptron;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.util.Vector;

//Class that creates, trains, tests, svaes, and reads Neural Nets
public class NeuralNetCreater {

    //Instance variables
    private MultilayerPerceptron mlp;
    private Instances train;
    private Instances test;

    //Constructor
    public NeuralNetCreater() {}
    
    //Saves the MultilayerPerceptron to a file
    public void saveNet(String path) {
	try{
	    weka.core.SerializationHelper.write(path, mlp);
	} catch(Exception ex) {
	    ex.printStackTrace();
	}
    }

    //Reads the MultilayerPerceptron from a file
    public void reloadNet(String path) {
	try {
	    mlp = (MultilayerPerceptron) weka.core.SerializationHelper.read(path);
    	} catch(Exception ex) {
	    ex.printStackTrace();
	}
    }

    //Generates Instances from a given .arff file and trains the Neural Net on it
    public void trainNet(String training_set) {
	try {
	    FileReader trainreader = new FileReader(training_set);
	    train = new Instances(trainreader);
	    train.setClassIndex(train.numAttributes() - 1);
	    mlp.buildClassifier(train);
	    trainreader.close();
	} catch(Exception ex) {
	    ex.printStackTrace();
	}
    }

    //Generates Instances from a given .arff file and tests the Neural Net on it
    public void testNet(String testing_set) {
	try{
	    FileReader testreader = new FileReader(testing_set);
	    test = new Instances(testreader);
	    test.setClassIndex(test.numAttributes() - 1);
	    
	    Evaluation eval = new Evaluation(train);
	    eval.evaluateModel(mlp, test);
	    System.out.println(eval.toSummaryString("\nResults\n======\n", false));
	    testreader.close();
	} catch(Exception ex){
	    ex.printStackTrace();            
	}
    }

    //Takes an image, generates an Instance from it for the Neural Net,
    //and returns the result of distribution of that instance
    public void classify(BufferedImage image) {
	try {
	    Util util = new Util();
	    BWArffGenerator ag = new BWArffGenerator();
	    Subimage s = new Subimage(image,0,0);
	    Vector<Subimage> vec = new Vector<Subimage>();
	    vec.add(s);
	    util.scaleImages(vec);
	    Instance inst = ag.createInstance(vec.get(0).getImage());
	    double[] result = mlp.distributionForInstance(inst);
	    System.out.println("Yes: "+result[0]);
	    System.out.println("No: "+result[1]);
	} catch(Exception ex) {
	    ex.printStackTrace();
	}
    }

    //Creates a new MultilayerPerceptron using the given parameters
    public void createNet() {
	try {
	    mlp = new MultilayerPerceptron();
	    mlp.setOptions(Utils.splitOptions("-L 0.15 -M 0.2 -N 1000 -V 0 -S 0 -E 1 -H 160,8 -D"));
	} catch(Exception ex) {
	    ex.printStackTrace();
	}
    }

    //Generates, tests, saves, or reads a neural net based on command line switches
    public static void main(String args[]) {	
	NeuralNetCreater t = new NeuralNetCreater();
	String training_set = "";
	String testing_set = "";
	String savefile = "";
	BufferedImage image = null;
	boolean load = false;

	for(int i = 0; (i + 1) < args.length; i++) {
	    String s = args[i++];
	    if(s.equals("-tr")) {
		training_set = args[i];
	    } else if(s.equals("-te")) {
		testing_set = args[i];
	    } else if(s.equals("-ote")) {
		training_set = args[i++];
		testing_set = args[i];
	    } else if(s.equals("-s")) {
		savefile = args[i];
	    } else if(s.equals("-l")) {
		load = true;
		t.reloadNet(args[i]);
	    } else if(s.equals("-c")) {
		try {
		    image = ImageIO.read(new File(args[i]));
		} catch(Exception ex) {
		    ex.printStackTrace();
		}
	    } else {
		System.out.println("Incorrect parameters");
		return;
	    }
	}

	if(!load) {
	    t.createNet();
	}
	if(!training_set.equals("")) {
	    t.trainNet(training_set);
	}
	if(!testing_set.equals("")) {
	    t.testNet(testing_set);
	}
	if(image != null) {
	    t.classify(image);
	}
	if(!savefile.equals("")) {
	    t.saveNet(savefile);
	}
    } 
}