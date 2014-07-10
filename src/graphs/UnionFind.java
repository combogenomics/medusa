package graphs;

import java.util.ArrayList;
import java.util.HashMap;

public class UnionFind {

	private HashMap<String, Integer> structure;

	public HashMap<String, Integer> getStructure() {
		return structure;
	}

	public UnionFind(ArrayList<MyNode> nodes) {
		structure = new HashMap<String, Integer>();
		for (int i = 0; i < nodes.size(); i++) {
			structure.put(nodes.get(i).getId(), i);
		}
	}

	public boolean find(String Id1, String Id2) {
		if (structure.get(Id1) == structure.get(Id2)) {
			return true;
		} else
			return false;

	}

	public void unite(String Id1, String Id2) {
		int sourceIdCom = structure.get(Id1);
		int targetIdCom = structure.get(Id2);
		ArrayList<String> targetComponent = getComponent(targetIdCom);
		for (String id : targetComponent) {
			structure.put(id, sourceIdCom);
		}
	}

	private ArrayList<String> getComponent(int i) {
		ArrayList<String> component = new ArrayList<String>();
		for (String id : structure.keySet()) {
			if (structure.get(id) == i) {
				component.add(id);
			}

		}

		return component;

	}

}
