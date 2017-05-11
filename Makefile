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
	java Main 11 Waldo/* WW0.jpg

run1:
	java Main 11 Waldo/* WW1.jpg

run2:
	java Main 11 Waldo/* WW2.jpg

run3:
	java Main 11 Waldo/* WW3.jpg

run4:
	java Main 11 Waldo/* WW4.jpg

runP:
	java Main 11 Waldo/* WWP.jpg

runNet:
	java -cp /usr/share/java/weka.jar: Trainer
