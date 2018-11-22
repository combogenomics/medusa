package graphs;

import java.util.ArrayList;

public class MyEdge  {
	private String id;
	private MyNode source;
	private MyNode target;
	private double weight;
	private double lenght;
	public ArrayList<int[]> orientations;

	public MyEdge(String id, MyNode source, MyNode target) {
		this.setId(id);
		this.weight = 0;
		this.lenght=1;
		this.source = source;
		this.target = target;
		int[] first;

		first =new int[2];
		first[0]=1;
		first[1]=1;
		this.orientations= new ArrayList<>();
		this.orientations.add(first);
	}

	public ArrayList<int[]> getOrientations() {
		return orientations;
	}

	public void setOrientations(ArrayList<int[]> orientations) {
		this.orientations = orientations;
	}

	public String toStringVerbose() {
		String s = id + "<" + source.getLabel() + ";" + target.getLabel() + ">"
				+ "[" + weight + "]";
		return s;
	}

        @Override
	public String toString() {
		return id;
	}

	public MyNode getSource() {
		return source;
	}

	public void setSource(MyNode source) {
		this.source = source;
	}

	public MyNode getTarget() {
		return target;
	}

	public void setTarget(MyNode target) {
		this.target = target;
	}

	public double getWeight() {
		return weight;
	}

	public void setWeight(double weight) {
		this.weight = weight;
	}

	public String getId() {
		return this.id;
	}

	public void setId(String id) {
		this.id = id;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MyEdge other = (MyEdge) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		/*
		 * if (source == null) { if (other.source != null) return false; } else
		 * if (!source.equals(other.source)) return false; if (target == null) {
		 * if (other.target != null) return false; } else if
		 * (!target.equals(other.target)) return false;
		 */
		return true;
	}

	public double getLenght() {
		return lenght;
	}

	public void setLenght(double d) {
		this.lenght = d;
	}

	public String orientationString() {
		String s;
		if(orientations.size()==1){
		String sourceId = this.getSource().getId();
		String targetId = this.getTarget().getId();
		String oS=  String.valueOf(orientations.get(0)[0]);
		String oT=  String.valueOf(orientations.get(0)[1]);
		s= sourceId+":"+oS+"_"+targetId+":"+oT;
		} else{
			String sourceId = this.getSource().getId();
			String targetId = this.getTarget().getId();
			String oS=  String.valueOf(orientations.get(0)[0]);
			String oT=  String.valueOf(orientations.get(0)[1]);
			String s1= sourceId+":"+oS+"_"+targetId+":"+oT;
			String sourceId1 = this.getSource().getId();
			String targetId1 = this.getTarget().getId();
			String oS1=  String.valueOf(orientations.get(1)[0]);
			String oT1=  String.valueOf(orientations.get(1)[1]);
			String s2= sourceId1+":"+oS1+"_"+targetId1+":"+oT1;
			s= s1+"__"+s2;
		}

		return s;
	}
}
