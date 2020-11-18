package MinNoiseRMP;

import java.util.ArrayList;

public class Updaters extends Getters{
	
	public static void updateUPATilingMatrix() {
		for(int i = 0 ; i < upaTilingMatrix.size() ; i++){
			ArrayList<Integer> tempList = new ArrayList<Integer>();
			for(int j = 0 ; j < upaTilingMatrix.get(0).size() ; j++){
				if(tileForRole.get(i).get(j) == 1){
					tempList.add(1);
				}
				else if(upaTilingMatrix.get(i).get(j) == 1){
					tempList.add(1);
				}
				else{
					tempList.add(0);
				}
			}
			upaTilingMatrix.set(i, new ArrayList<Integer>(tempList));
		}
	}
	
	public static void updateUPATilingMatrixEnhanced(int role, int user){
		
		for(int j = 0 ; j < upaTilingMatrix.get(0).size() ; j++){
			if(finalCR.get(role).get(j) == 1){
				upaTilingMatrix.get(user).set(j, 1);
			}
		}
	}

	public static void updateDelta() {
		delta = 0;
		for(int i = 0 ; i < UPA.size() ; i++){
			for(int j = 0 ; j < sortedCR.get(0).size() ; j++){
				if(UPA.get(i).get(j) == 1){
					if(upaTilingMatrix.get(i).get(j) == 0){
						delta++;
					}
				}
				if(UPA.get(i).get(j) == 0){
					if(upaTilingMatrix.get(i).get(j) == 1){
						delta++;
					}
				}
			}
		}
		System.out.println(delta);
	}
	
	public static void updateUncoveredArea() {
		for(int i = 0 ; i < sortedCR.size() ; i++){
			
			if(added.get(i) == 1){
				uncoveredAreaOfSortedRole.set(i, 0);
				continue;
			}
			
			//init tile for the role
			clearTileForRole();
			
			//get the tile corresponding to current role i
			getTileForRole(i);
			
			//get uncovered area for role
			uncoveredAreaOfSortedRole.set(i, getUncoveredArea());
		}
	}
	
	public static void storeTile(int indexOfTile){
		
		//add 0 tile to tile-permission matrix
		ArrayList<Integer> temp1 = new ArrayList<Integer>();
		for(int j = 0 ; j < UPA.get(0).size(); j++){
				temp1.add(j, 0);
		}
		TilingPermissions.add(new ArrayList<Integer>(temp1));
		
		//add 0 tile to tile-user matrix
		ArrayList<Integer> temp2 = new ArrayList<Integer>();
		for(int j = 0 ; j < UPA.size(); j++){
				temp2.add(j, 0);
		}
		TilingUsers.add(new ArrayList<Integer>(temp2));
		
		//store permissions for tile
		for(int i = 0 ; i < tileForRole.size() ; i++){
			for(int j = 0 ; j < tileForRole.get(0).size() ; j++){
				if(tileForRole.get(i).get(j) == 1){
					TilingPermissions.get(indexOfTile).set(j,1);
				}
			}
		}
		
		//store permissions for tile
		for(int i = 0 ; i < tileForRole.get(0).size()  ; i++){
			for(int j = 0 ; j < tileForRole.size(); j++){
				if(tileForRole.get(j).get(i) == 1){
					TilingUsers.get(indexOfTile).set(j,1);
				}
			}
		}
	}
	
	public static void storeAdditionalUA(int roleToAdd, int userToAdd){
		
		TilingUsers.get(roleToAdd).set(userToAdd,1);
	}
}
