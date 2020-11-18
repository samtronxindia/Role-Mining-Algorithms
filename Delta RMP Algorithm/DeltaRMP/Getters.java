package DeltaRMP;

import java.util.ArrayList;

public class Getters extends Initialize {

	public void getTileForRole(int roleTemp) {
		
		int i = 0;
		int j = 0;
		for(i = 0 ; i < static_UPA.size() ; i++){
			for(j = (static_sortedCR.get(0).size() - 1) ; j >= 0 ; j--){
				if(static_sortedCR.get(roleTemp).get(j) == 0){
					continue;
				}
				if(static_sortedCR.get(roleTemp).get(j) == 1){
					if(static_UPA.get(i).get(j) == 0){
						break;
					}
				}
			}
			
			if(j == -1){
				tileForRole.set(i, new ArrayList<Integer>(static_sortedCR.get(roleTemp)));
			}
		}
	}

	public int getUncoveredArea() {
		
		int areaOfCurrentTile = 0;
		for(int i = 0 ; i < static_UPA.size(); i++){
			for(int j = 0; j < static_UPA.get(0).size() ; j++){
				if(tileForRole.get(i).get(j) == 1){
					if(upaTilingMatrix.get(i).get(j) == 0){
						areaOfCurrentTile++;
					}
				}
			}
		}
		return areaOfCurrentTile;
	}
	
	public void getPrivilege(){
		
		int privilege = 0;
		double privilegePerc = 0.0;
		underPrivilegeTotal = 0.0;
		overPrivilegeTotal = 0.0;
	
		for(int i = 0 ; i < static_UPA.size() ; i++){
			int underPrivilege = 0;
			int overPrivilege = 0;
			int numPermAppAssigned = 0;
			for(int j = 0 ; j < static_sortedCR.get(0).size() ; j++){
				if(static_UPA.get(i).get(j) == 0){
					if(upaTilingMatrix.get(i).get(j) == 0){
						continue;
					}
				}
				if(static_UPA.get(i).get(j) == 1){
					if(upaTilingMatrix.get(i).get(j) == 1){
						numPermAppAssigned++;
						continue;
					}
				}
				if(static_UPA.get(i).get(j) == 1){
					if(upaTilingMatrix.get(i).get(j) == 0){
						underPrivilege++;
						continue;
					}
				}
				if(static_UPA.get(i).get(j) == 0){
					if(upaTilingMatrix.get(i).get(j) == 1){
						overPrivilege++;
						continue;
					}
				}
			}
			underPrivilegeTotal = underPrivilegeTotal + ((double)underPrivilege / (underPrivilege + numPermAppAssigned));
			overPrivilegeTotal = overPrivilegeTotal + ((double)overPrivilege / (overPrivilege + numPermAppAssigned));
		}
		underPrivilegeTotal = (underPrivilegeTotal / static_UPA.size()) * 100;
		overPrivilegeTotal = (overPrivilegeTotal / static_UPA.size()) * 100;
	}
	
	public int getStaticPermissionCoverage(){
		
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
	public Getters() {
		super();
	}
}