package utilities;

public class JsonEvaluation {
	String target;
	int scaffolds;
	int joints;
	int lenght;
	double N50;
	
	public JsonEvaluation() {
		 target="";
		 scaffolds=0;
		 joints=0;
		 lenght=0;
		 N50=0;
	}

	public String getTarget() {
		return target;
	}

	public void setTarget(String target) {
		this.target = target;
	}

	public int getScaffolds() {
		return scaffolds;
	}

	public void setScaffolds(int scaffolds) {
		this.scaffolds = scaffolds;
	}

	public int getJoints() {
		return joints;
	}

	public void setJoints(int joints) {
		this.joints = joints;
	}

	public int getLenght() {
		return lenght;
	}

	public void setLenght(int lenght) {
		this.lenght = lenght;
	}

	public double getN50() {
		return N50;
	}

	public void setN50(int n50) {
		N50 = n50;
	}
}
