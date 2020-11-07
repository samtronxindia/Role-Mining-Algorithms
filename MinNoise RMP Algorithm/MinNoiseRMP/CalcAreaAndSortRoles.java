package MinNoiseRMP;

import java.util.ArrayList;

public class CalcAreaAndSortRoles extends UpdateCR{
	
public static void calcArea(){
		
		//init static_areaOfRole to all 0s
		for(int a = 0 ; a < CR.size(); a++){
			areaOfRole.add(0);
		}
		
		int i = 0;
		int j = 0;
		int k = 0;
		int count = 0;
		int numPerm = 0;
		
		for(i = 0 ; i < CR.size(); i++){//take row from static_CR
			numPerm = 0;
			count = 0;
			for(int a = 0 ; a < CR.get(0).size(); a++){
				if(CR.get(i).get(a) == 1){
					numPerm++;
				}
			}
			for(j = 0 ; j < UPA.size(); j++){//take row from static_UPA
				//System.out.print("static_CR:" + static_CR.get(i) + "static_UPA:" + static_UPA.get(j));
				for(k = (CR.get(0).size() - 1) ; k >= 0  ; k--){//compare individual permission-elements
					if(CR.get(i).get(k) == 0){
						continue;
					}
					if(CR.get(i).get(k) == 1){
						if(UPA.get(j).get(k) == 0){
							//System.out.print("No Match!" + "\n");
							break;
						}
					}
				}
				
				if(k == -1){
					//System.out.print("Match!" + "\n");
					count++;
				}
			}
			//System.out.println("\n" + count + "," + numPerm + "\n");
			//traversed the static_UPA for 1 Role in static_CR, calc the area of role and add it to static_areaOfRole
			areaOfRole.set(i, (count * numPerm));
		}
	}
	
	public static void sortRoles(){
		
		ArrayList<Integer> addedTemp = new ArrayList<Integer>();
		
		//init flag veriable "addedTemp" to 0s
		for(int i = 0 ; i < CR.size() ; i++){
			addedTemp.add(0);
		}
		
		//find max area role and add them until all roles have been added to static_sortedCR
		for(int i = 0 ; i < CR.size() ; i++){
			int maxArea = 0;
			int maxAreaIndex = 0;
			
			//find role with maximum area which hasnt been added to static_sortedCR
			for(int j = 0 ; j < areaOfRole.size() ; j++){
				if(addedTemp.get(j) != 1){
					if(areaOfRole.get(j) > maxArea){
						maxArea = areaOfRole.get(j);
						maxAreaIndex = j;
					}
				}
			}
			/*System.out.print("Index: " + maxAreaIndex + " & ");
			System.out.print(" Area: " + static_areaOfRole.get(maxAreaIndex));
			System.out.println(" static_CR: " + static_CR.get(maxAreaIndex) + "\n");*/
			
			sortedCR.add(new ArrayList<Integer>(CR.get(maxAreaIndex)));
			areaOfSortedRole.add(areaOfRole.get(maxAreaIndex));
			addedTemp.set(maxAreaIndex, 1);
			
			/*System.out.println(static_sortedCR);
			System.out.println(static_areaOfSortedRole);*/
		}
	}

}
