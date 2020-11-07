package MinNoiseRMP;

import java.io.PrintWriter;
import java.util.ArrayList;

public class MinNoiseRMPAlgorithm extends Updaters {

	// has the static_sortedCR and static_areaOfSortedRole variables
	public static void initializeFinalCR() {

		tileNum = 0;
		// initialize the tiling matrix
		initializeUPATilingMatrix();

		// init tile for the role
		initializeTileForRole();

		// initialize the flag variable "added" to 0s
		initializeAdded();

		// init variable for uncovered area list
		initUncoveredAreaOfRoles();

		// init delta
		delta = 0;

		// end of initialization

		finalCR.add(new ArrayList<Integer>(sortedCR.get(0)));
		getTileForRole(0);

		/*
		 * System.out.println("Tile"); for(int i = 0 ; i < tileForRole.size() ; i++){
		 * System.out.println(tileForRole.get(i)); }
		 */

		/*
		 * for(int i = 0 ; i < upaTilingMatrix.size() ; i++){
		 * System.out.println(upaTilingMatrix.get(i)); }
		 */
		updateUPATilingMatrix();

		// store tile as role in UA and PA
		storeTile(tileNum);
		tileNum++;

		// System.out.println("Final static_CR: " + finalCR + "\n");
		/*
		 * for(int i = 0 ; i < upaTilingMatrix.size() ; i++){
		 * System.out.println(upaTilingMatrix.get(i)); }
		 */
		added.set(0, 1);

		updateUncoveredArea();
		updateDelta();

		/*
		 * System.out.println("delta: init" + delta);
		 * 
		 * 
		 * System.out.println("Unc area after 1 iter:" + uncoveredAreaOfSortedRole);
		 */
	}

	public static void calcFinalCR(int tempRoleSetCardinality) {

		int maxArea = 0;
		int maxAreaIndex = 0;

		boolean loop1 = false;
		boolean loop2 = false;
		boolean loop3 = false;
		for (int i = 0; i < sortedCR.size(); i++) {

			// break if delta
			if (finalCR.size() >= tempRoleSetCardinality) {
				break;
			}
			
			if(finalCR.size() >= 120 && loop1 == false) {
				associateAdditionalRoles();
				loop1 = true;
			}
			if(finalCR.size() >= 130 && loop2 == false) {
				associateAdditionalRoles();
				loop2 = true;
			}
			if(finalCR.size() >= 134 && loop3 == false) {
				associateAdditionalRoles();
				loop3 = true;
				break;
			}
			

			/*
			 * if (getStaticPermissionCoverage2() >= 161) { System.out.println("Cov Perms: "
			 * + getStaticPermissionCoverage2());
			 * 
			 * associateAdditionalRoles(); System.out.println("UA: \n"); for (int i2 = 0; i2
			 * < TilingUsers.size(); i2++) { for (int j2 = 0; j2 <
			 * TilingUsers.get(0).size(); j2++) {
			 * System.out.print(TilingUsers.get(i2).get(j2) + ","); }
			 * System.out.print("\n"); } System.out.println("PA: \n"); for (int i2 = 0; i2 <
			 * TilingPermissions.size(); i2++) { for (int j2 = 0; j2 <
			 * TilingPermissions.get(0).size(); j2++) {
			 * System.out.print(TilingPermissions.get(i2).get(j2) + ","); }
			 * System.out.print("\n"); }
			 * 
			 * break; }
			 */
			/*
			 * if(getStaticPermissionCoverage2() >= 33 && loop1 == false) {
			 * System.out.println("Cov Perms: " + getStaticPermissionCoverage2());
			 * 
			 * associateAdditionalRoles();
			 * 
			 * System.out.println("UA: \n"); for(int i2=0; i2 < TilingUsers.size(); i2++) {
			 * for(int j2=0; j2 < TilingUsers.get(0).size(); j2++) {
			 * System.out.print(TilingUsers.get(i2).get(j2) + ","); }
			 * System.out.print("\n"); } System.out.println("PA: \n"); for(int i2=0; i2 <
			 * TilingPermissions.size(); i2++) { for(int j2=0; j2 <
			 * TilingPermissions.get(0).size(); j2++) {
			 * System.out.print(TilingPermissions.get(i2).get(j2) + ","); }
			 * System.out.print("\n"); }
			 * 
			 * loop1 = true;
			 * 
			 * }
			 */
			/*
			 * if(getStaticPermissionCoverage2() >= 97 && loop2 == false) {
			 * System.out.println("Cov Perms: " + getStaticPermissionCoverage2());
			 * 
			 * associateAdditionalRoles(); System.out.println("UA: \n"); for(int i2=0; i2 <
			 * TilingUsers.size(); i2++) { for(int j2=0; j2 < TilingUsers.get(0).size();
			 * j2++) { System.out.print(TilingUsers.get(i2).get(j2) + ","); }
			 * System.out.print("\n"); } System.out.println("PA: \n"); for(int i2=0; i2 <
			 * TilingPermissions.size(); i2++) { for(int j2=0; j2 <
			 * TilingPermissions.get(0).size(); j2++) {
			 * System.out.print(TilingPermissions.get(i2).get(j2) + ","); }
			 * System.out.print("\n"); } loop2 = true;
			 * 
			 * }
			 */

			if (delta < 1) {
				break;
			}

			if (finalCR.size() % 10 == 0) {
				System.out.print(finalCR.size() + "Roles calc.ed");
			}

			maxArea = 0;
			maxAreaIndex = 0;
			for (int j = 0; j < uncoveredAreaOfSortedRole.size(); j++) {
				if (added.get(j) == 1) {
					continue;
				}
				if (uncoveredAreaOfSortedRole.get(j) > maxArea) {
					maxArea = uncoveredAreaOfSortedRole.get(j);
					maxAreaIndex = j;
				}
			}
			// System.out.println("Max unc area index " + maxAreaIndex + " max unc area: " +
			// maxArea);

			finalCR.add(new ArrayList<Integer>(sortedCR.get(maxAreaIndex)));

			/*
			 * System.out.println("FinCR"); for(int a = 0 ; a < finalCR.size() ; a++){
			 * System.out.println(finalCR.get(a)); }
			 */

			// init tile for the role
			clearTileForRole();

			// get the tile corresponding to current role i
			getTileForRole(maxAreaIndex);

			// update tiling matrix
			updateUPATilingMatrix();
			storeTile(tileNum);
			tileNum++;

			// update exclusion list "added"
			added.set(maxAreaIndex, 1);

			// update uncovered area of remaining roles
			updateUncoveredArea();
			// System.out.println("unc area updated: " + uncoveredAreaOfSortedRole);

			// update the delta value
			updateDelta();
			// System.out.println("delta: " + delta);

			// System.out.println(uncoveredAreaOfSortedRole);
		}
	}

	public static void associateAdditionalRoles() {

		// i'th role
		for (int i = 0; i < finalCR.size(); i++) {

			int numPerm = 0;
			// calc num of permissions the role has
			for (int a = 0; a < finalCR.get(0).size(); a++) {
				if (finalCR.get(i).get(a) == 1) {
					numPerm++;
				}
			}
			// j'th user
			for (int j = 0; j < UPA.size(); j++) {

				int common = 0;

				// find out number of common permissions to i'th role and j'th user
				for (int k = 0; k < finalCR.get(0).size(); k++) {
					if (finalCR.get(i).get(k) == 1) {
						if (UPA.get(j).get(k) == 1) {
							common++;
						}
					} else if (UPA.get(j).get(k) == 1) {
						if (finalCR.get(i).get(k) == 1) {
							common++;
						}
					}
				}

				// if common permissions are > |i'th role|, add this role to the j'th user
				if (common > (0.5 * (double) numPerm)) {
					storeAdditionalUA(i, j);
					updateUPATilingMatrixEnhanced(i, j);
				}
			}
		}
		updateDelta();
		callPrinter();
	}

	public static void callPrinter() {
		printPA();
		getPrivilege();
		System.out.println("Delta= " + delta + "\n Over Privilege Percentage: " + overPrivilegeTotal
				+ "\n Under Privilege Percentage: " + underPrivilegeTotal + "\n Number of Roles: " + finalCR.size()
				+ "\n Permission Coverage (out of 161 permissions): " + getStaticPermissionCoverage()
				+ "\n Perm Cov. #2: " + getStaticPermissionCoverage2());
	}

	public static void printPA() {

		roleSetCardinality.add(finalCR.size());
		try {
			PrintWriter outputPA = new PrintWriter(
					"C:\\Users\\Samir\\Desktop\\workspace\\MinNoiseRMP\\src\\output\\PA_R_" + finalCR.size() + "_Delta_"
							+ delta + ".txt");

			for (int i = 0; i < finalCR.size(); i++) {
				for (int j = 0; j < finalCR.get(0).size(); j++) {
					if (finalCR.get(i).get(j) == 1) {
						outputPA.print(perms1.get(j) + ",");
					}
				}
				outputPA.print("\n");
			}
			outputPA.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
