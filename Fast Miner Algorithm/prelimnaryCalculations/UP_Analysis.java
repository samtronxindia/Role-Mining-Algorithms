package prelimnaryCalculations;

public class UP_Analysis extends InitAndDataCollection {
	
	public static void AnalyseUP(){
			
		for(int w = 0 ; w < uP.get(0).size(); w ++){
			int eachUpPermissionsTemp = 0;
			for(int pp = 0 ; pp < uP.size(); pp++){
				if(uP.get(pp).get(w) == 1){
					eachUpPermissionsTemp++;
				}
			}
			eachUpVerticalPermissions.add(w, eachUpPermissionsTemp);
		}
	}
	
	public UP_Analysis() {
		super();
	}

}
