package DeltaRMP;

import java.util.ArrayList;

public class Updaters extends Getters {
	
	public void updateUPATilingMatrix() {
		
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
	
	public void updateDelta() {
		
		delta = 0;
		for(int i = 0 ; i < static_UPA.size() ; i++){
			for(int j = 0 ; j < static_sortedCR.get(0).size() ; j++){
				if(static_UPA.get(i).get(j) == 1){
					if(upaTilingMatrix.get(i).get(j) == 0){
						delta++;
					}
				}
				if(static_UPA.get(i).get(j) == 0){
					if(upaTilingMatrix.get(i).get(j) == 1){
						delta++;
					}
				}
			}
		}
		System.out.println(delta);
	}
	
	public void updateUncoveredArea() {
		
		for(int i = 0 ; i < static_sortedCR.size() ; i++){
			
			if(added.get(i) == 1){
				uncoveredAreaOfSortedRole.set(i, 0);
				continue;
			}
			
			//init tile for the role
			clearTileForRole();
			
			//get the tile corresponding to current role i
			getTileForRole(i);
			
			/*System.out.println("TileforRole");
			for(int a = 0 ; a < tileForRole.size(); a++){
				System.out.println(tileForRole.get(a));
			}*/
			//System.out.println("Tile size: " + tileForRole.size());
			
			//get uncovered area for role
			uncoveredAreaOfSortedRole.set(i, getUncoveredArea());
			//System.out.println("UNC Area: " + uncoveredAreaOfSortedRole.get(i));
		}
	}
}
