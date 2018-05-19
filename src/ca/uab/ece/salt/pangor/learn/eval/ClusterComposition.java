package ca.uab.ece.salt.pangor.learn.eval;

import ca.ubc.ece.salt.pangor.original.learn.EvaluationResult;

class ClusterComposition implements Data {

	/* The class to get data for. */
	private String classID;

	public ClusterComposition(String classID) {
		this.classID = classID;
	}

	@Override
	public String[] getData(EvaluationResult[] dataSetResult) {
		String[] data = new String[dataSetResult.length];
		for(int j = 0; j < dataSetResult.length; j++) {
			Double val = dataSetResult[j].clusterComposition.get(this.classID);
			data[j] = val == null ? "NA" : String.valueOf(val);
		}
		return data;
	}

	@Override
	public String[] getLim() {
		return new String[]{ "0.0", "1.0" };
	}

	@Override
	public String[] getAxp() {
		return new String[]{ "0.0", "1.0", "5.0" };
	}

	@Override
	public String getLabel() {
		return "Cluster Composition " + this.classID;
	}

}