scala:
	scalac -d bin -cp jars/scalatest_2.10-1.9.1.jar *.scala -deprecation

run:
	cd bin && scala jp.kenkov.smt.ibmmodel.IBMModel1Test

test:
	cd bin && scala -cp ../jars/scalatest_2.10-1.9.1.jar org.scalatest.run \
		jp.kenkov.smt.test.IBMModel1Test \
		jp.kenkov.smt.test.ViterbiAlignmentTest

.PHONY: clean

clean:
	rm -r bin/*
