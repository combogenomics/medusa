package graphs;

import java.util.ArrayList;

public class MyNode {
	private String id;
	private String label;
	private ArrayList<MyNode> adj;
	private int contiglength;
	private int orientation;


	public void setContiglength(int contiglength) {
		this.contiglength = contiglength;
	}

	public int getContiglength() {
		return contiglength;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public void setAdj(ArrayList<MyNode> adj) {
		this.adj = adj;
	}

	public MyNode(String id, String label) {
		this.adj = new ArrayList<MyNode>();
		this.id = id;
		this.label = label;
		this.contiglength = 1;
		this.orientation=100;
	}

	public MyNode(MyNode n) {
		this.adj = new ArrayList<MyNode>();
		this.id = n.getId();
		this.label = n.getLabel();
		this.contiglength = n.getContiglength();
		this.orientation=n.getOrientation();
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
		MyNode other = (MyNode) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	public String toStringVErbose() {
		return this.label + "[" + this.adj.size() + "]";
	}

	public String toString() {
		return this.id;
	}

	public String getId() {
		return id;
	}

	public String getLabel() {
		return label;
	}

	public int getDegree() {
		return this.adj.size();
		//return g.outEdges(this).size();
	}

	public ArrayList<MyNode> getAdj() {
		return adj;
	}

	public void addAdjacentNode(MyNode adNode) {
		boolean r = false;
		for (MyNode n : this.adj) {
			if (adNode.getId() == n.getId()) {
				r = true;
			}
		}
		if (!r) {
			adj.add(adNode);
		}

	}

	public int getOrientation() {
		return orientation;
	}

	public void setOrientation(int orientation) {
		this.orientation = orientation;
	}

	

}
