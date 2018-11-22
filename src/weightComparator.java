
import graphs.MyEdge;
import java.util.Comparator;

public class weightComparator implements Comparator<MyEdge> {
	@Override
	public int compare(MyEdge o1, MyEdge o2) {
		int i = 0;
		if(o1.getWeight()==o2.getWeight()){
			i= 0;
		} else if(o1.getWeight()<o2.getWeight()){
			i=-1;
		} else if (o1.getWeight()>o2.getWeight()){
			i=1;
		}
		return i;
	}
}



