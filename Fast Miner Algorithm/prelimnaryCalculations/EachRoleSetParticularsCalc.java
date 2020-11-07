package prelimnaryCalculations;

public class EachRoleSetParticularsCalc extends FewChecks {

	public static void eachPermCalc() {
		//count # permissions associated with each CR
		for(int w = 0 ; w < getcR().size(); w ++){
			int eachCrPermissionsTemp = 0;
			for(int pp = 0 ; pp < getcR().get(0).size(); pp++){
				if(getcR().get(w).get(pp) == 1){
					eachCrPermissionsTemp++;
				}
			}
			eachCrPermissions.add(w, eachCrPermissionsTemp);
		}
		
		for(int w = 0 ; w < getcR().get(0).size(); w ++){
			int eachCrPermissionsTemp = 0;
			for(int pp = 0 ; pp < getcR().size(); pp++){
				if(getcR().get(pp).get(w) == 1){
					eachCrPermissionsTemp++;
				}
			}
			eachCrVerticalPermissions.add(w, eachCrPermissionsTemp);
		}
		System.out.println(eachCrVerticalPermissions);
	}

	public EachRoleSetParticularsCalc() {
		super();
	}

}