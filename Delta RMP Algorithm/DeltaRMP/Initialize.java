package DeltaRMP;

import java.util.ArrayList;

public class Initialize extends CalculateAreaAndSort {

	public void initializeAdded() {
		//init flag veriable "added" to 0s
		for(int i = 0 ; i < static_sortedCR.size() ; i++){
			added.add(0);
		}
	}
	
	public void initializeAddedPerm() {
		//init flag variable "addedPerm" to 0s
		for(int i = 0; i < static_sortedCR.get(0).size() ; i++) {
			addedPerm.add(0);
		}
	}

	public void initUncoveredAreaOfRoles() {
		//init flag veriable "added" to 0s
		for(int i = 0 ; i < static_sortedCR.size() ; i++){
			uncoveredAreaOfSortedRole.add(0);
		}
	}

	public void initializeUPATilingMatrix() {
		//init coverage matrix upaTilingMatrix
		for(int i = 0 ; i < static_UPA.size() ; i++){
			ArrayList<Integer> temp = new ArrayList<Integer>();
			for(int j = 0 ; j < static_UPA.get(0).size(); j++){
					temp.add(j, 0);
			}
			upaTilingMatrix.add(i, new ArrayList<Integer>(temp));
		}
	}

	public void initializeTileForRole() {
		//init coverage matrix upaTilingMatrix
		for(int i = 0 ; i < static_UPA.size() ; i++){
			ArrayList<Integer> temp = new ArrayList<Integer>();
			for(int j = 0 ; j < static_UPA.get(0).size(); j++){
					temp.add(j, 0);
			}
			tileForRole.add(i, new ArrayList<Integer>(temp));
		}
	}

	public void clearTileForRole() {
		//init coverage matrix upaTilingMatrix
		for(int i = 0 ; i < static_UPA.size() ; i++){
			ArrayList<Integer> temp = new ArrayList<Integer>();
			for(int j = 0 ; j < static_UPA.get(0).size(); j++){
					temp.add(j, 0);
			}
			tileForRole.set(i, new ArrayList<Integer>(temp));
		}
	}

	public Initialize() {
		super();
	}

}