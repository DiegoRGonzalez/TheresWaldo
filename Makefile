default:
	javac -cp "/usr/share/java/weka.jar" *.java

ready:
	$(RM) SD0To1/Subimage*.jpg
	$(RM) SD1To2/Subimage*.jpg
	$(RM) SD2To3/Subimage*.jpg
	$(RM) AllImages/Subimage*.jpg

clean:
	$(RM) *.class
	$(RM) SD0To1/Subimage*.jpg
	$(RM) SD1To2/Subimage*.jpg
	$(RM) SD2To3/Subimage*.jpg
	$(RM) AllImages/Subimage*.jpg

run0:
	java Main Waldo/* WW0.jpg

run1:
	java Main Waldo/* WW1.jpg

run2:
	java Main Waldo/* WW2.jpg

run3:
	java Main Waldo/* WW3.jpg

run4:
	java Main Waldo/* WW4.jpg

run5:
	java Main Waldo/* WW5.jpg

runP:
	java Main Waldo/* WWP.jpg

runNet:
	java -cp /usr/share/java/weka.jar: Trainer
