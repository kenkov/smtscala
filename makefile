scala:
	scalac IBMModel.scala

.PHONY: clean

run:
	time scala IBMModelTest

clean:
	rm *.class
