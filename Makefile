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
	java -cp /usr/share/java/weka.jar:. Main Waldo/* WW0.jpg

run1:
	java -cp /usr/share/java/weka.jar:. Main Waldo/* WW1.jpg

run2:
	java -cp /usr/share/java/weka.jar:. Main Waldo/* WW2.jpg

run3:
	java -cp /usr/share/java/weka.jar:. Main Waldo/* WW3.jpg

run4:
	java -cp /usr/share/java/weka.jar:. Main Waldo/* WW4.jpg

run5:
	java -cp /usr/share/java/weka.jar:. Main Waldo/* WW5.jpg

runP:
	java -cp /usr/share/java/weka.jar:. Main Waldo/* WWP.jpg

runNet:
	java -cp /usr/share/java/weka.jar: Trainer
