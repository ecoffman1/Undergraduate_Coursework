
default: 
	javac *.java
	jar -cvf PageRank.jar ./*.class
	hadoop jar PageRank.jar PageRank -N 5 -alpha 0.1 -i 10

clean:
	rm -rf output-graph* *.jar *.class

clobber:
	rm -rf output-graph* *.jar *.class *.java
