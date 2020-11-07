package MinNoiseRMP;

import java.util.ArrayList;

public class Initialize extends CalcAreaAndSortRoles{
	
	public static void initializeAdded() {
		//init flag veriable "added" to 0s
		for(int i = 0 ; i < sortedCR.size() ; i++){
			added.add(0);
		}
	}

	public static void initUncoveredAreaOfRoles() {
		//init flag veriable "added" to 0s
		for(int i = 0 ; i < sortedCR.size() ; i++){
			uncoveredAreaOfSortedRole.add(0);
		}
	}

	public static void initializeUPATilingMatrix() {
		//init coverage matrix upaTilingMatrix
		for(int i = 0 ; i < UPA.size() ; i++){
			ArrayList<Integer> temp = new ArrayList<Integer>();
			for(int j = 0 ; j < UPA.get(0).size(); j++){
					temp.add(j, 0);
			}
			upaTilingMatrix.add(i, new ArrayList<Integer>(temp));
		}
	}

	public static void initializeTileForRole() {
		//init coverage matrix upaTilingMatrix
		for(int i = 0 ; i < UPA.size() ; i++){
			ArrayList<Integer> temp = new ArrayList<Integer>();
			for(int j = 0 ; j < UPA.get(0).size(); j++){
					temp.add(j, 0);
			}
			tileForRole.add(i, new ArrayList<Integer>(temp));
		}
	}

	public static void clearTileForRole() {
		//init coverage matrix upaTilingMatrix
		for(int i = 0 ; i < UPA.size() ; i++){
			ArrayList<Integer> temp = new ArrayList<Integer>();
			for(int j = 0 ; j < UPA.get(0).size(); j++){
					temp.add(j, 0);
			}
			tileForRole.set(i, new ArrayList<Integer>(temp));
		}
	}

}
