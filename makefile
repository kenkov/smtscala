scala:
	scalac -d src IBMModel.scala package.scala

.PHONY: clean

clean:
	rm -r src/*
