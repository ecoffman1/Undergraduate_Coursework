
default: 
	javac *.java
	jar -cvf SecondarySorting.jar ./*.class
	hadoop jar SecondarySorting.jar SecondarySorting input output

clean:
	rm -rf output *.jar *.class

clobber:
	rm -rf output *.jar *.class *.java
