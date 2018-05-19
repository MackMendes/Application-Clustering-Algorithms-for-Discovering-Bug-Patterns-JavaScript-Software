package ca.uab.ece.salt.pangor.learn.eval;

import ca.ubc.ece.salt.pangor.original.learn.EvaluationResult;

interface Data {
	String[] getData(EvaluationResult[] dataSetResult);
	String[] getLim();
	String[] getAxp();
	String getLabel();
}