default:
	javac *.java

clean:
	$(RM) *.class
	$(RM) SD0To1/Subimage*.jpg
	$(RM) SD1To2/Subimage*.jpg
	$(RM) SD2To3/Subimage*.jpg

run0:
	java Main 5 /home/scratch/17jdr3/TestingImages/Waldo/* WW0.jpg

run2: 
	java Main 5 /home/scratch/17jdr3/TestingImages/Waldo/* WW2.jpg
