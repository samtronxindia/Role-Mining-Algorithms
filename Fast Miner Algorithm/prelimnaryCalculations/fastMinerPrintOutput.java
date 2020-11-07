package prelimnaryCalculations;

import java.io.PrintWriter;
import java.util.ArrayList;


public class fastMinerPrintOutput extends PrintAnalysedOutputForCR {

	public static void printOutput() {
		
		//print some prelimnary info
		System.out.println("UP SIZE:" + uP.size() + "\n");
		System.out.println("IR SIZE:" + iR.size() + "\n" );
		System.out.println("GR SIZE:" + gR.size() + "\n" );
		System.out.println("Perms size" + perms1.size() + "\n" );
		System.out.println("Orig count:" + origCount + "\n");
		System.out.println("Orig count size:" + origCount.size() + "\n");
		System.out.println("Count:" + count + "\n");
		System.out.println("Count Size:" + count.size() + "\n");
		
		ArrayList<Integer> verticalCount = new ArrayList<Integer>();
		ArrayList<Integer> horizontalCount = new ArrayList<Integer>();
		ArrayList<Integer> verticalCountIR = new ArrayList<Integer>();
		ArrayList<Integer> verticalCountGR = new ArrayList<Integer>();

		
		//for getting number of perms assoc. w/each IR
		for(int w = 0 ; w < iR.size(); w ++){
			int eachIrPermissionsTemp = 0;
			for(int pp = 0 ; pp < iR.get(0).size(); pp++){
				if(iR.get(w).get(pp) == 1){
					eachIrPermissionsTemp++;
				}
			}
			eachIrPermissions.add(w , eachIrPermissionsTemp);
		}
			 	
		//for getting number of perms assoc. w/each GR
		for(int w = 0 ; w < gR.size(); w ++){
			int eachGrPermissionsTemp = 0;
			for(int pp = 0 ; pp < gR.get(0).size(); pp++){
				if(gR.get(w).get(pp) == 1){
				eachGrPermissionsTemp++;
				}
			}
			eachGrPermissions.add(w , eachGrPermissionsTemp);
		}
		
		//for getting number of IR's each perm assoc to
		for(int w = 0 ; w < iR.get(0).size(); w ++){
			int eachIrPermissionsTemp = 0;
			for(int pp = 0 ; pp < iR.size(); pp++){
				if(iR.get(pp).get(w) == 1){
					eachIrPermissionsTemp++;
				}
			}
			eachIrVerticalPermissions.add(w, eachIrPermissionsTemp);
		}
		
		//for getting number of GR's each perm assoc to
		for(int w = 0 ; w < gR.get(0).size(); w ++){
			int eachGrPermissionsTemp = 0;
			for(int pp = 0 ; pp < gR.size(); pp++){
				if(gR.get(pp).get(w) == 1){
					eachGrPermissionsTemp++;
				}
			}
			eachGrVerticalPermissions.add(w, eachGrPermissionsTemp);
		}
		
		//vertical & horizontal number of X for UP
		for(int w = 0 ; w < uP.get(0).size(); w++){
			int tempcounting = 0;
			for(int pp = 0 ; pp < uP.size() ; pp++){
				tempcounting = tempcounting + uP.get(pp).get(w);
			}
			verticalCount.add(w, tempcounting);
		}
		for(int w = 0 ; w < uP.size(); w++){
			int tempcounting = 0;
			for(int pp = 0 ; pp < uP.get(0).size() ; pp++){
				tempcounting = tempcounting + uP.get(w).get(pp);
			}
			horizontalCount.add(w, tempcounting);
		}
		
		//vertical count IR
		for(int w = 0 ; w < iR.get(0).size(); w++){
			int tempcounting = 0;
			for(int pp = 0 ; pp < iR.size() ; pp++){
				tempcounting = tempcounting + iR.get(pp).get(w);
			}
			verticalCountIR.add(w, tempcounting);
		}
		
		//vertical count GR
		for(int w = 0 ; w < gR.get(0).size(); w++){
			int tempcounting = 0;
			for(int pp = 0 ; pp < gR.size() ; pp++){
				tempcounting = tempcounting + gR.get(pp).get(w);
			}
			verticalCountGR.add(w, tempcounting);
		}
			
		try {
			
			PrintWriter writer = new PrintWriter("C:\\Users\\Samir\\Desktop\\workspace\\fastMinerTwo\\src\\outputs\\output"/*, "UTF-8"*/);
			writer.println("Perm distribution: " + verticalCount);
			writer.println("Total number of Perms: " + verticalCount.size());
			writer.println("App distribution: " + horizontalCount);
			writer.println("Total number of Apps: " + horizontalCount.size());
				
			for (int w = 0 ; w < verticalCount.size(); w++){
				if (verticalCount.get(w) != 0){
					writer.print(perms1.get(w) + ",");
				}
			}
				
			int appsWithZeroPerms= 0;
			int appsWithNonZeroPerms = 0;
			for(int w = 0 ; w < verticalCount.size(); w++){
				if(verticalCount.get(w) != 0){
					appsWithNonZeroPerms++;
					//writer.println("Permission: " + perms1.get(w) + "Perm Count: " + verticalCount.get(w) + "\n");
				}
			}
			//writer.println("============================================== \n");
			//writer.println("Non zero permissions: " + temp9 + "\n");
			for(int w = 0 ; w < verticalCount.size(); w++){
				if(verticalCount.get(w) == 0){
					appsWithZeroPerms++;
					//writer.println("Permission: " + perms1.get(w) + "Perm Count: " + verticalCount.get(w) + "\n");
				}
			}
			
			int usedIrPerms = 0 ;
			int usedGrPerms = 0 ;
			for(int w = 0 ; w < verticalCountIR.size(); w++){
				if(verticalCountIR.get(w) != 0){
					usedIrPerms++;
					//writer.println("Permission: " + perms1.get(w) + "Perm Count: " + verticalCount.get(w) + "\n");
				}
			}
			
			for(int w = 0 ; w < verticalCountGR.size(); w++){
				if(verticalCountGR.get(w) != 0){
					usedGrPerms++;
					//writer.println("Permission: " + perms1.get(w) + "Perm Count: " + verticalCount.get(w) + "\n");
				}
			}
				
			writer.println("\n Used permissions/Total Permissions: " + appsWithNonZeroPerms + "/" + (appsWithNonZeroPerms + appsWithZeroPerms));
			writer.println("\n Used permissions IR: " + usedIrPerms);
			writer.println("\n Used permissions GR: " + usedGrPerms);
			writer.println("\n Number of Perms associated with each IR: " + eachIrPermissions + "\n");
			
			//for loop for outputing the permission names for every IRs
			/*for(int w = 0 ; w < iR.size() ; w++){
				writer.print("IR#"+ w + "-");
				for(int pp = 0 ; pp < iR.get(0).size() ; pp++){
					if(iR.get(w).get(pp) == 1){
						writer.print(perms1.get(pp) + ",");
					}
				}
				writer.print("\n");
			}*/
			
			writer.println("Orig count:" + origCount + "\n");
			writer.println("Orig count size:" + origCount.size() + "\n");
			writer.println("\n Number of Perms associated with each GR: " + eachGrPermissions);
			writer.println("Count:" + count + "\n");
			writer.println("GR SIZE:" + gR.size() + "\n" );
			//writer.println("\n Number of Perms associated with each CR: " + eachCrPermissions + "\n");
			//writer.println("1st GR" + gR.get(0) + "\n" );
			//writer.println("Perms size" + perms1.size() + "\n" );
			writer.println("Count Size:" + count.size() + "\n");
			writer.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public fastMinerPrintOutput() {
		super();
	}

}