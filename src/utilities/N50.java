package utilities;

import java.util.ArrayList;
import java.util.Collections;

public class N50 {

	public static double n50minimum(ArrayList<Integer> lenghts){
		Collections.sort(lenghts);
		int total = 0;
		int partial =0;
		int index=0;
		for(int i : lenghts){
			total+=i;
		}
		while(index < lenghts.size() && partial+lenghts.get(index) < (total/2)){
			partial += lenghts.get(index);
			index++;
		}
		
		return lenghts.get(index);
	}
	
	
	public static double n50maximum(ArrayList<Integer> lenghts){
		Collections.sort(lenghts);
		Collections.reverse(lenghts);
		int total = 0;
		int partial =0;
		int index=0;
		for(int i : lenghts){
			total+=i;
		}
		while(index < lenghts.size() && partial+lenghts.get(index) < (total/2)){
			partial += lenghts.get(index);
			index++;
		}
		
		return lenghts.get(index);
	}
	
	
	public static double n50mean(ArrayList<Integer> lenghts) {
		Collections.sort(lenghts);
		int total = 0;
		int partial =0;
		int index=0;
		for(int i : lenghts){
			total+=i;
		}
		while(index < lenghts.size() && partial+lenghts.get(index) < (total/2)){
			partial += lenghts.get(index);
			index++;
		}
		
		if(partial+lenghts.get(index)==(total/2)){
			return (lenghts.get(index)+lenghts.get(index+1))/2.0;
		}else{
			return lenghts.get(index);
		}
		
		}

}
