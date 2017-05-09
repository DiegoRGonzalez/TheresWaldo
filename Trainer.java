import java.io.*;
import weka.core.*;
import weka.core.Instances;
import weka.classifiers.Evaluation;
import weka.classifiers.*;
import weka.classifiers.functions.MultilayerPerceptron;

public class Trainer {

    private MultilayerPerceptron mlp;
    private Instances train;
    private Instances test;

    public Trainer() {}
    
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
	    System.out.println("Error");
	    ex.printStackTrace();
	}

	System.out.println("HERE "+(mlp==null));
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
    
    public void createNet() {
	try {
	    mlp = new MultilayerPerceptron();
	    mlp.setOptions(Utils.splitOptions("-L 0.3 -M 0.2 -N 500 -V 0 -S 0 -E 20 -H 4"));
	} catch(Exception ex) {
	    ex.printStackTrace();
	}
    }

    public static void main(String args[]) {	
	Trainer t = new Trainer();
	String training_set = "";
	String testing_set = "";
	String savefile = "";
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
	    }
	}

	System.out.println(training_set+" "+testing_set+" "+savefile);

	if(!load) {
	    t.createNet();
	}
	if(!training_set.equals("")) {
	    t.trainNet(training_set);
	}
	if(!testing_set.equals("")) {
	    t.testNet(testing_set);
	}
	if(!savefile.equals("")) {
	    t.saveNet(savefile);
	}
    } 
}