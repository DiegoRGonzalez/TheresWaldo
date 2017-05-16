How to run our code:

We included a Makefile for many of these commands.

Use "make" to compile

To generate a new .arff file, use "java -cp /usr/share/local/weka.jar:. BWArffGenerator [name of new .arff file] [file of images to create instances of]".

To create a new Neural Net, use "java -cp /usr/share/local/weka.jar:. NeuralNetCreator -tr [Training Set] -te [Testing Set] -s [Where to save the model] -l [Where to read the model from]". It always needs a Training Set, but all other switches are optional.
