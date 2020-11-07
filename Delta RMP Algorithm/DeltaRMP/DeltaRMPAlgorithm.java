package DeltaRMP;

import java.io.PrintWriter;
import java.util.ArrayList;

public class DeltaRMPAlgorithm extends Updaters{
	
	//has the static_sortedCR and static_areaOfSortedRole variables
	public void initializeFinalCR(){
		
		//initialize the tiling matrix
		initializeUPATilingMatrix();
		
		//init tile for the role
		initializeTileForRole();
		
		//initialize the flag variable "added" to 0s
		initializeAdded();
		
		//initialize flag variable "addedPerm" to 0s
		initializeAddedPerm();
		
		//init variable for uncovered area list
		initUncoveredAreaOfRoles();
		
		//init delta
		delta = 0;
		
		//end of initialization
		
		finalCR.add(new ArrayList<Integer>(static_sortedCR.get(0)));
		getTileForRole(0);
		
		/*System.out.println("Tile");
		for(int i = 0 ; i < tileForRole.size() ; i++){
			System.out.println(tileForRole.get(i));
		}*/
		
		/*for(int i = 0 ; i < upaTilingMatrix.size() ; i++){
			System.out.println(upaTilingMatrix.get(i));
		}*/
		updateUPATilingMatrix();
		//System.out.println("Final static_CR: " + finalCR + "\n");
		/*for(int i = 0 ; i < upaTilingMatrix.size() ; i++){
			System.out.println(upaTilingMatrix.get(i));
		}*/
		added.set(0, 1);
		
		updateUncoveredArea();
		updateDelta();
		
		/*System.out.println("delta: init" + delta);

		
		System.out.println("Unc area after 1 iter:" + uncoveredAreaOfSortedRole);*/
	}
	
	public void calcFinalCR(int tempDelta){
		
		int maxArea = 0;
		int maxAreaIndex = 0;
		 
		for(int i = 0 ; i < static_sortedCR.size() ; i++){
			
			//break if delta
			if(delta < tempDelta){
				break;
			}
			
			if(delta  < 4000)
			{
				callPrinter();
			}
			
			
			maxArea = 0;
			maxAreaIndex = 0;
			
			//skip the role if any common perms found between it, and the exclusion list for perms "addedPerm"
			/*int skipRole = 0;
			for(int b = 0 ; b < addedPerm.size() ; b++) {
				if(static_sortedCR.get(i).get(b) == 1) {
					if(addedPerm.get(b) == 1) {
						skipRole = 1;
					}
				}
			}
			if(skipRole == 1) {
				continue;
			}*/
			
			for(int j = 0 ; j < uncoveredAreaOfSortedRole.size() ; j++){
				if(added.get(j) == 1){
					continue;
				}
				if(uncoveredAreaOfSortedRole.get(j) > maxArea){
					maxArea = uncoveredAreaOfSortedRole.get(j);
					maxAreaIndex = j;
				}
			}
			//System.out.println("Max unc area index " + maxAreaIndex + " max unc area: " + maxArea);
			
			//add all perm elements from the best role obtained to the exception list "addedPerm"
			/*for(int a = 0 ; a < addedPerm.size() ; a++) {
				if(static_sortedCR.get(maxAreaIndex).get(a) == 1) {
					addedPerm.set(a, 1);
				}
			}*/
			
			finalCR.add(new ArrayList<Integer>(static_sortedCR.get(maxAreaIndex)));
			
			/*System.out.println("FinCR");
			for(int a = 0 ; a < finalCR.size() ; a++){
				System.out.println(finalCR.get(a));
			}*/
			
			//init tile for the role
			clearTileForRole();

			//get the tile corresponding to current role i
			getTileForRole(maxAreaIndex);
			
			//update tiling matrix
			updateUPATilingMatrix();
			
			//update exclusion list "added"
			added.set(maxAreaIndex, 1);
			
			//update uncovered area of remaining roles
			updateUncoveredArea();
			//System.out.println("unc area updated: " + uncoveredAreaOfSortedRole);
			
			//update the delta value
			updateDelta();
			
			//callPrinter();
			//System.out.println("delta: " + delta);
			
			//System.out.println(uncoveredAreaOfSortedRole);
		}
		
		//post processing on the finalCR based on "non-overlapping roles/groups"
		for(int i = 0 ; i < finalCR.size() ; i++) {
			for(int j = 0 ; j < finalCR.get(i).size() ; j++) {
				if(finalCR.get(i).get(j) == 1) {
					for(int k = i + 1 ; k < finalCR.size() ; k++) {
						finalCR.get(k).set(j, 0);
					}
				}
			}
		}
		
		callPrinter();
	}
	
	
	
	public void callPrinter(){
		printPA();
		getPrivilege();
		System.out.println("Delta= " + delta + "\n Over Privilege Percentage: " + overPrivilegeTotal + "\n Under Privilege Percentage: " + underPrivilegeTotal + "\n Number of Roles: " + finalCR.size() + "\n Permission Coverage (out of 161 permissions): " + getStaticPermissionCoverage());
	}
	
	public void printPA(){
		
		static_roleSetCardinality.add(finalCR.size());
		try{
			PrintWriter outputPA = new PrintWriter("C:\\Users\\Samir\\Desktop\\workspace\\DeltaRMP\\src\\output\\PA_Delta_" + delta + ".txt");
		
			for(int i = 0 ; i < finalCR.size() ; i ++){
				for(int j = 0 ; j < finalCR.get(0).size() ; j ++){
					if(finalCR.get(i).get(j) == 1){
						outputPA.print(static_perms1.get(j) + ",");
					}
				}
				outputPA.print("\n");
			}
			outputPA.close();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}
