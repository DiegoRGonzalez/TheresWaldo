import java.io.*;
import weka.core.*;
import weka.core.Instances;
import weka.classifiers.Evaluation;
import weka.classifiers.*;
import weka.classifiers.functions.MultilayerPerceptron;

public class Trainer {

    public Trainer() {}
    /*
    public void saveNet(String path) {
	Debug.saveToFile(path, mlp);
    }

    public MultilayerPerceptron reloadNet(String path) {
	mlp = new MultilayerPerceptron();
	mlp.setModelFile(new File(path));
    }
    */
    public MultilayerPerceptron createNet(String training_set, String testing_set) {
	MultilayerPerceptron mlp = new MultilayerPerceptron();
	try{
	    FileReader trainreader = new FileReader(training_set);
	    FileReader testreader = new FileReader(testing_set);
	    
	    Instances train = new Instances(trainreader);
	    Instances test = new Instances(testreader);
	    train.setClassIndex(train.numAttributes() - 1);
	    test.setClassIndex(test.numAttributes() - 1);
	    
	    mlp.setOptions(Utils.splitOptions("-L 0.3 -M 0.2 -N 500 -V 0 -S 0 -E 20 -H 4"));
	    mlp.buildClassifier(train);
	    
	    Evaluation eval = new Evaluation(train);
	    eval.evaluateModel(mlp, test);
	    System.out.println(eval.toSummaryString("\nResults\n======\n", false));
	    trainreader.close();
	    testreader.close();
	} catch(Exception ex){
	    ex.printStackTrace();            
	}
	return mlp;
    } 
    
    public static void main(String args[]) {	
	Trainer t = new Trainer();
	if(args.length < 2 || args.length > 3) {
	    System.out.println("Need to pass in Training arff and Testing arff files.\nCan also add an extra file to save the multilayer perceptron to.");
	    return;
	} else {
	    t.createNet(args[0], args[1]);
	    //	    if(args.length == 3) {
	    //	t.saveNet(args[2]);
	    //}
	}
	return;
    } 
}