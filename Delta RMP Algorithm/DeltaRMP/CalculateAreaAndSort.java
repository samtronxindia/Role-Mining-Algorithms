package DeltaRMP;

import java.util.ArrayList;

public class CalculateAreaAndSort extends InitAndDataCollection{
	
	public static void calcArea(){
		
		//init static_areaOfRole to all 0s
		for(int a = 0 ; a < static_CR.size(); a++){
			static_areaOfRole.add(0);
		}
		
		int i = 0;
		int j = 0;
		int k = 0;
		int count = 0;
		int numPerm = 0;
		
		for(i = 0 ; i < static_CR.size(); i++){//take row from static_CR
			numPerm = 0;
			count = 0;
			for(int a = 0 ; a < static_CR.get(0).size(); a++){
				if(static_CR.get(i).get(a) == 1){
					numPerm++;
				}
			}
			for(j = 0 ; j < static_UPA.size(); j++){//take row from static_UPA
				for(k = (static_CR.get(0).size() - 1) ; k >= 0  ; k--){//compare individual permission-elements
					if(static_CR.get(i).get(k) == 0){
						continue;
					}
					if(static_CR.get(i).get(k) == 1){
						if(static_UPA.get(j).get(k) == 0){
							break;
						}
					}
				}
				
				if(k == -1){
					count++;
				}
			}
			static_areaOfRole.set(i, (count * numPerm));
		}
	}
	
	public static void sortRoles(){
		
		ArrayList<Integer> addedTemp = new ArrayList<Integer>();
		
		//init flag veriable "addedTemp" to 0s
		for(int i = 0 ; i < static_CR.size() ; i++){
			addedTemp.add(0);
		}
		
		//find max area role and add them until all roles have been added to static_sortedCR
		for(int i = 0 ; i < static_CR.size() ; i++){
			int maxArea = 0;
			int maxAreaIndex = 0;
			
			//find role with maximum area which hasnt been added to static_sortedCR
			for(int j = 0 ; j < static_areaOfRole.size() ; j++){
				if(addedTemp.get(j) != 1){
					if(static_areaOfRole.get(j) > maxArea){
						maxArea = static_areaOfRole.get(j);
						maxAreaIndex = j;
					}
				}
			}
			
			static_sortedCR.add(new ArrayList<Integer>(static_CR.get(maxAreaIndex)));
			static_areaOfSortedRole.add(static_areaOfRole.get(maxAreaIndex));
			addedTemp.set(maxAreaIndex, 1);
		}
	}
	
	public static int calcUPACardinality(){
		
		int cardinality = 0;
		for(int i = 0 ; i < static_UPA.size() ; i++){
			for(int j = 0 ; j < static_UPA.get(0).size() ; j++){
				if(static_UPA.get(i).get(j) == 1){
					cardinality++;
				}
			}
		}
		return cardinality;
	}
}
