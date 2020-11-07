package prelimnaryCalculations;
import java.util.ArrayList;


public class CR_Analysis extends CR_Analysis_New {

	public static void updateCr() throws Exception {
		
		int i = 0;
		int j = 0;
		int k = 0;
		double permissionCommonality = 0;
	
		boolean brokenJ = false;
		ArrayList<Integer> numPermC2R = new ArrayList<Integer>();
		ArrayList<Integer> disableC2R1 = new ArrayList<Integer>();
		ArrayList<Integer> disableC2R2 = new ArrayList<Integer>();
	
		int numPermC2RTemp = 0;
		
		for(int w = 0 ; w < getcR().size(); w++){
			disableC2R1.add(w, 0);
		}
		for(int w = 0 ; w < getcR().size(); w++){
			disableC2R2.add(w, 0);
		}
		
		System.out.println("Size of eachCrPermission: " + eachCrPermissions.size() + "\n");
		
		//loop for calculating role commonality
		for(i = 0 ; i < getcR().size(); i++){
			double roleCommonality = 0;
			int commonality = 0;
			for(j = 0 ; j < getcR().size() ; j++){				
				for(k = (getcR().get(0).size() - 1) ; k >= 0 ; k--){
					if(getcR().get(i).get(k) != getcR().get(j).get(k)){
						if(getcR().get(i).get(k) == 1){
							break;
						}
					}
					/*//"April27,2018" if(commonality > 2){
						break;
					}*/
				}//end k for loop
				if(k == -1){
					commonality++;
				}
				
				/*//"April27,2018" if(commonality > 2){
					disableC2R2.set(j , 1);
					brokenJ = true;
					continue;//changed from break. See how to eliminate elements via their ratings not arbitrarily
				}*/
				//"April27,2018" commonality = 0;
			}//end j for loop
			
			justCommonalityFactor.add(commonality);
			
			roleCommonality = ((double)commonality/(double)getcR().size());
			commonalityFactor.add(roleCommonality);
			
			commonality = 0;
			 
			if(brokenJ == true){
				disableC2R1.set(i , 1);
			}
			
			brokenJ = false;
	
		}//end i , loop for calculating commonality
		
		//enable when calc C2R
		/*for(i = 0 ; i < cR.get(0).size(); i++){
			for(j = 0 ; j < cR.size() ; j++){
				if(cR.get(j).get(i) == 1){
					permissionCommonality++;
				}
			}
			permissionCommonalityFactor.add(i, (permissionCommonality/cR.size()));
			permissionCommonality = 0;
		}
		
		for(int w = 0 ; w < cR.size() ; w++){
			if(disableC2R1.get(w) == 0 && disableC2R2.get(w) == 0){
				c2R.add(new ArrayList<Integer>(cR.get(w)));
				rating2.add(new ArrayList<Integer>(rating.get(w)));
			}
		}
		
		ArrayList<ArrayList<Integer>> c3Temp = new ArrayList<ArrayList<Integer>>(cR);
		ArrayList<ArrayList<Integer>> rating3Temp = new ArrayList<ArrayList<Integer>>(rating);
		
		for(int w = 0 ; w < c3Temp.size(); w ++){
			int eachC2RPermissionsTemp = 0;
			for(int pp = 0 ; pp < c3Temp.get(0).size(); pp++){
				if(c3Temp.get(w).get(pp) == 1){
					eachC2RPermissionsTemp++;
				}
			}
			eachC2RPermissions.add(w, eachC2RPermissionsTemp);
		}
		
		for(int w = 0 ; w < c3Temp.get(0).size(); w ++){
			int eachC2RPermissionsTemp = 0;
			for(int pp = 0 ; pp < c3Temp.size(); pp++){
				if(c3Temp.get(pp).get(w) == 1){
					eachC2RPermissionsTemp++;
				}
			}
			C2RVerticalPermissions.add(w, eachC2RPermissionsTemp);
		}*/
		
		/*for(int w = 0 ; w < C2RVerticalPermissions.size() ; w++){
			if(C2RVerticalPermissions.get(w) <= 3){
				continue;
			}
			if(C2RVerticalPermissions.get(w) > 3){
				int maxElement = eachC2RPermissions.get(0);
				for(int a = 0 ; a <= 3 ; a++){
					int maxElementIndex = 0;
					for(int pp = 0 ; pp < rating3Temp.size() ; pp++){
						if(maxElement < rating3Temp.get(pp)){//fix this
							maxElement = eachC2RPermissions.get(pp);
							maxElementIndex = pp;
						}
					}
					c3R.add(new ArrayList<Integer>(c3Temp.get(maxElementIndex)));
					rating3.add(new ArrayList<Integer>(rating3Temp.get(maxElementIndex)));
					c3Temp.remove(maxElementIndex);
					rating3Temp.remove(maxElementIndex);
				}
			}
		}*/
		
	}//end method updateCr()

	public CR_Analysis() {
		super();
	}

}