import java.io.*;
import weka.core.*;
import weka.core.Instances;
import weka.classifiers.Evaluation;
import weka.classifiers.*;
import weka.classifiers.functions.MultilayerPerceptron;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

public class NeuralNetCreater {

    private MultilayerPerceptron mlp;
    private Instances train;
    private Instances test;

    public NeuralNetCreater() {}
    
    public void saveNet(String path) {
	try{
	    weka.core.SerializationHelper.write(path, mlp);
	} catch(Exception ex) {
	    ex.printStackTrace();
	}
    }

    public void reloadNet(String path) {
	try {
	    mlp = (MultilayerPerceptron) weka.core.SerializationHelper.read(path);
    	} catch(Exception ex) {
	    ex.printStackTrace();
	}
    }

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

    public void classify(BufferedImage image) {
	try {
	    BWArffGenerator ag = new BWArffGenerator();
	    Instance inst = ag.createInstance(image);
	    double[] result = mlp.distributionForInstance(inst);
	    System.out.println("Yes: "+result[0]);
	    System.out.println("No: "+result[1]);
	} catch(Exception ex) {
	    ex.printStackTrace();
	}
    }
    
    public void createNet() {
	try {
	    mlp = new MultilayerPerceptron();
	    mlp.setOptions(Utils.splitOptions("-L 0.4 -M 0.1 -N 10000 -V 0 -S 0 -E 1 -H 20,8"));
	} catch(Exception ex) {
	    ex.printStackTrace();
	}
    }

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