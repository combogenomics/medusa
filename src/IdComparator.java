
import graphs.MyEdge;

import java.util.Comparator;
public class IdComparator implements Comparator<MyEdge> {
	@Override
	public int compare(MyEdge o1, MyEdge o2) {			
		return o1.getId().compareTo(o2.getId());
	}
}