package BasicRMP;

import java.util.ArrayList;

public class RMP extends AlgotithmLUTM{
	
	
	public static boolean checkLTNull(){
		
		for(int i = 0 ; i < LT.size() ; i ++){
			for(int j = 0 ; j < LT.get(0).size() ; j++){
				if(LT.get(i).get(j) == 1){
					return false;
				}
			}
		}
		return true;
	}
	
	public static void updateUPATilingMatrix(){
		
		for(int i = 0 ; i < upaTilingMatrix.size() ; i++){
			for(int j = 0 ; j < upaTilingMatrix.get(0).size() ; j++){
				int res = 0;
				if(upaTilingMatrix.get(i).get(j) == 1){
					res = 1;
				}else if(LT.get(i).get(j) == 1){
					res = 1;
				}
				upaTilingMatrix.get(i).set(j, res);
			}
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
		for(int i = 0 ; i < LT.size() ; i++){
			for(int j = 0 ; j < LT.get(0).size() ; j++){
				if(LT.get(i).get(j) == 1){
					TilingPermissions.get(indexOfTile).set(j,1);
				}
			}
		}
		
		//store permissions for tile
		for(int i = 0 ; i < LT.get(0).size()  ; i++){
			for(int j = 0 ; j < LT.size(); j++){
				if(LT.get(j).get(i) == 1){
					TilingUsers.get(indexOfTile).set(j,1);
				}
			}
		}
		
	}
	
	//check if all permissions are covered
	public static boolean checkPAll(){
		
		for(int i = 0 ; i < P.size() ; i++){
			if(P.get(i) == 0){
				return false;
			}
		}
		return true;
	}

	public static void RMPExecute() {
		// TODO Auto-generated method stub
		
		//check which permissions are never assigned to any app and add them to P - ???
		//checkPermissions();
		
		//initialize the UPA matrix and P - the covered permissions set
		initializeUPAmmAndP();
		
		//check which permissions are never assigned to any users
		checkPermissions();
		//**Print Commands**System.out.println("P: " + P);
		
		int tileNum = 0;
		
		do{
			//call the LUTM algorithm to execute on the UPA
			//**Print Commands**System.out.println("++++++++++INSIDE RMP+++++++++++++");
			//**Print Commands**System.out.println(" LUTM count:" + countLUTM);
			LUTM();
			
			updateUPATilingMatrix();
			
			//update the P - set of permissions fully covered or not assigned to any users
			//**Print Commands**System.out.println("P: " + P);
			checkPermissions();
			//**Print Commands**System.out.println("P: " + P);
			
			storeTile(tileNum);
			
			tileNum++;
			
		}while(checkLTNull() == false && checkPAll() == false);
	}
}
