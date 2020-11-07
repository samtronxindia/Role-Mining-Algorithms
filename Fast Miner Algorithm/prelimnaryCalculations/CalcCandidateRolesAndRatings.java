package prelimnaryCalculations;
import java.util.ArrayList;

public class CalcCandidateRolesAndRatings extends
		PhaseTwo {

	public static void calcRating(int maxPriority) {
		 
		int i = 0;
		int j = 0;
		int k = 0;
		for(i = 0 ; i < iR.size(); i++){
			for(j = 0 ; j < gR.size() ; j++){
				for(k = (gR.get(0).size() - 1) ; k >= 0 ; k--){
					if(iR.get(i).get(k) != gR.get(j).get(k)){
						break;
					}
				}
				if(k == -1){
					getcR().add(new ArrayList<Integer>(iR.get(i)));
					ArrayList<Integer> tempCR = new ArrayList<Integer>(); 
					int priority = 10;
					while(priority <= maxPriority){
						tempCR.add(origCount.get(i)*priority + count.get(j));
						priority = priority + 10;
					}
					rating.add(new ArrayList<Integer>(tempCR));
					iR.remove(i);
					origCount.remove(i);
					gR.remove(j);
					count.remove(j);
					commonElements++;
					break;
				}
			}
		}
		for(i = 0 ; i < iR.size(); i++){
				getcR().add(new ArrayList<Integer>(iR.get(i)));
				ArrayList<Integer> tempCR = new ArrayList<Integer>(); 
				int priority = 10;
				while(priority <= maxPriority){
					tempCR.add(origCount.get(i)*priority);
					priority = priority + 10;
				}
				rating.add(new ArrayList<Integer>(tempCR));
		}
		for(j = 0 ; j < gR.size(); j++){
			getcR().add(new ArrayList<Integer>(gR.get(j)));
			ArrayList<Integer> tempCR = new ArrayList<Integer>(); 
			int priority = 10;
			while(priority <= maxPriority){
				tempCR.add(count.get(j));
				priority = priority + 10;
			}
			rating.add(new ArrayList<Integer>(tempCR));
		}
		
	}

	public CalcCandidateRolesAndRatings() {
		super();
	}

}