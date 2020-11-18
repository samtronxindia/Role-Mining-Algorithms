package MinNoiseRMP;

import java.util.ArrayList;

public class Getters extends Initialize{
	
public static void getTileForRole(int roleTemp) {
		
		int i = 0;
		int j = 0;
		for(i = 0 ; i < UPA.size() ; i++){
			for(j = (sortedCR.get(0).size() - 1) ; j >= 0 ; j--){
				if(sortedCR.get(roleTemp).get(j) == 0){
					continue;
				}
				if(sortedCR.get(roleTemp).get(j) == 1){
					if(UPA.get(i).get(j) == 0){
						break;
					}
				}
			}
			
			if(j == -1){
				tileForRole.set(i, new ArrayList<Integer>(sortedCR.get(roleTemp)));
			}
		}
	}
 
	public static int getUncoveredArea() {
		
		int areaOfCurrentTile = 0;
		for(int i = 0 ; i < UPA.size(); i++){
			for(int j = 0; j < UPA.get(0).size() ; j++){
				if(tileForRole.get(i).get(j) == 1){
					if(upaTilingMatrix.get(i).get(j) == 0){
						areaOfCurrentTile++;
					}
				}
			}
		}
		return areaOfCurrentTile;
	}
	
	public static void getPrivilege(){
		
		underPrivilegeTotal = 0.0;
		overPrivilegeTotal = 0.0;
	
		for(int i = 0 ; i < UPA.size() ; i++){
			int underPrivilege = 0;
			int overPrivilege = 0;
			int numPermAppAssigned = 0;
			for(int j = 0 ; j < sortedCR.get(0).size() ; j++){
				if(UPA.get(i).get(j) == 0){
					if(upaTilingMatrix.get(i).get(j) == 0){
						continue;
					}
				}
				if(UPA.get(i).get(j) == 1){
					if(upaTilingMatrix.get(i).get(j) == 1){
						numPermAppAssigned++;
						continue;
					}
				}
				if(UPA.get(i).get(j) == 1){
					if(upaTilingMatrix.get(i).get(j) == 0){
						underPrivilege++;
						continue;
					}
				}
				if(UPA.get(i).get(j) == 0){
					if(upaTilingMatrix.get(i).get(j) == 1){
						overPrivilege++;
						continue;
					}
				}
			}
			underPrivilegeTotal = underPrivilegeTotal + ((double)underPrivilege / (underPrivilege + numPermAppAssigned));
			overPrivilegeTotal = overPrivilegeTotal + ((double)overPrivilege / (overPrivilege + numPermAppAssigned));
		}
		underPrivilegeTotal = (underPrivilegeTotal / UPA.size()) * 100;
		overPrivilegeTotal = (overPrivilegeTotal / UPA.size()) * 100;
	}
	
	public static int getStaticPermissionCoverage(){
		
		int coveredPermTemp = 0 ;
		for(int i = 0 ; i < upaTilingMatrix.get(0).size() ; i ++){
			for(int j = 0 ; j < upaTilingMatrix.size() ; j++){
				if(upaTilingMatrix.get(j).get(i) == 1){
					coveredPermTemp++;
					break;
				}
			}
		}
		return coveredPermTemp;
	}
	public static int getStaticPermissionCoverage2(){
		
		int coveredPermTemp = 0 ;
		for(int i = 0 ; i < TilingPermissions.get(0).size() ; i ++){
			for(int j = 0 ; j < TilingPermissions.size() ; j++){
				if(TilingPermissions.get(j).get(i) == 1){
					coveredPermTemp++;
					break;
				}
			}
		}
		return coveredPermTemp;
	}

}
