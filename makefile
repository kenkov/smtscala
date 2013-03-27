scala:
	scalac -d bin -cp jars/scalatest_2.10-1.9.1.jar *.scala

test:
	cd bin && scala -cp ../jars/scalatest_2.10-1.9.1.jar org.scalatest.run jp.kenkov.smt.test.IBMModel1Test

.PHONY: clean

clean:
	rm -r bin/*
