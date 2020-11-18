package BasicRMP;

import java.util.ArrayList;

public class AlgotithmLUTM extends Initialization{
	
	protected static int indexOfFirstPermissionLocated = 0 ;
	
	public static void locateFirstUncoveredPermission(){
		
		for(int i = 0 ; i < UPA.get(0).size(); i++){
			
			//check if permission is in the P set, if yes, permissions has been located
			if(P.get(i) == 0){
				indexOfFirstPermissionLocated = i;
				break;
			}
		}
	}
	
	public static void initLT(){
		for(int i = 0 ; i < UPA.size() ; i++){
			ArrayList<Integer> temp = new ArrayList<Integer>();
			for(int j = 0 ; j < UPA.get(0).size(); j++){
					temp.add(j, 0);
			}
			LT.set(i, new ArrayList<Integer>(temp));
		}
	}
	
	public static void initCurrentConsideredTile(){
		
		for(int i = 0 ; i < UPA.size(); i++){
			ArrayList<Integer> templist = new ArrayList<Integer>();
			for(int j = 0 ; j < UPA.get(0).size(); j++){
				templist.add(0);
			}
			currentConsideredTile.set(i, new ArrayList<Integer>(templist));
		}
	}
	
	public static void getCurrentConsideredTile(int firstPermissionIndex, int lastPermissionIndex){
		for(int i = 0 ; i < UPA.size(); i++){
			ArrayList<Integer> templist = new ArrayList<Integer>();
			for(int j = 0 ; j < UPA.get(0).size(); j++){
				templist.add(0);
			}
			currentConsideredTile.set(i, new ArrayList<Integer>(templist));
		}
		for(int i = 0 ; i < UPA.size(); i++){
			
			//init temp1 to '1' because if init to 0, its multiplication will always be 0
			int temp1 = 1;
			
			//create temp array to hold a particular row and init array to 0
			ArrayList<Integer> temparray = new ArrayList<Integer>();
			for(int a = 0 ; a < UPA.get(0).size() ; a++){
				temparray.add(0);
			}
			
			//check if all users have that permission
			for(int j = firstPermissionIndex; j <= lastPermissionIndex ; j++){
				temp1 = temp1 * UPA.get(i).get(j);
			}
			
			//if all users have that permission in current perm list -> set all corresponding bits in temp1 to 1
			if(temp1 == 1){
				for(int j = firstPermissionIndex; j <= lastPermissionIndex ; j++){
					temparray.set(j, 1);
				}
			}
			
			//copy temp1 to tile
			currentConsideredTile.set(i, new ArrayList<Integer>(temparray));
		}
	}
	
	public static int getCurrentConsideredTileUncoveredArea(int firstPermissionIndex, int lastPermissionIndex){
		int areaOfCurrentTile = 0;
		for(int i = 0 ; i < UPA.size(); i++){
			for(int j = firstPermissionIndex; j <= lastPermissionIndex ; j++){
				if(currentConsideredTile.get(i).get(j) == 1){
					if(upaTilingMatrix.get(i).get(j) == 0){
						areaOfCurrentTile++;
					}
				}
			}
		}
		
		return areaOfCurrentTile;
		
	}
	
	public static void generateConditionalUPA(int currentPermissionIndex){
		for(int i = 0 ; i < UPA.size() ; i++){
			for(int j = (currentPermissionIndex + 1) ; j < UPA.get(0).size() ; j++){
				
				if(conditionalUPA.get(i).get(currentPermissionIndex) ==1){
					if(conditionalUPA.get(i).get(j) == 1){
						nextConditionalUPA.get(i).set(j, 1);
					}
				}
				else{
					nextConditionalUPA.get(i).set(j, 0);
				}
			}
		}
	}
	
	public static void generateConditionalUPAFromUPA(int currentPermissionIndex){
		for(int i = 0 ; i < UPA.size() ; i++){
			for(int j = (currentPermissionIndex + 1) ; j < UPA.get(0).size() ; j++){
				
				if((UPA.get(i).get(currentPermissionIndex) * UPA.get(i).get(j)) == 1){
					conditionalUPA.get(i).set(j, 1);
				}
				else{
					conditionalUPA.get(i).set(j, 0);
				}
			}
		}
	}
	
	public static boolean checkNextConditionalUPANull(){
		for(int i = 0 ; i < nextConditionalUPA.size() ; i ++){
			for(int j = 0 ; j < nextConditionalUPA.get(0).size() ; j++){
				if(nextConditionalUPA.get(i).get(j) == 1){
					return false;
				}
			}
		}
		return true;
	}
	
	public static boolean checkConditionalUPANull(){
		for(int i = 0 ; i < conditionalUPA.size() ; i ++){
			for(int j = 0 ; j < conditionalUPA.get(0).size() ; j++){
				if(conditionalUPA.get(i).get(j) == 1){
					return false;
				}
			}
		}
		return true;
	}
	
	public static void LUTM(){
		
		//init largest tile array LT & largest area known
		initLT();
		int areaLT = 0;
		countLUTM++;
		
		//locate the first uncovered permission to begin LUTM algorithm
		locateFirstUncoveredPermission();
		
		//initialize currentTile to all 0s
		initCurrentConsideredTile();
				
		for(int indexOfCurrentPermission = indexOfFirstPermissionLocated ; indexOfCurrentPermission < UPA.get(0).size(); indexOfCurrentPermission++){
			
			//get the current tile by passing permissions considered as arguments
			getCurrentConsideredTile(indexOfFirstPermissionLocated, indexOfCurrentPermission);
			//after this function , the "currentConsideredTile" 2d Arraylist will have the current tile in matrix form
			
			//get area of current tile 
			int uncoveredAreaTile = getCurrentConsideredTileUncoveredArea(indexOfFirstPermissionLocated, indexOfCurrentPermission);
			
			//if uncovered area of current tile is greater than current known area of largest know tile, update LT and AreaLT
			if(uncoveredAreaTile > areaLT){
				for(int i = 0 ; i < currentConsideredTile.size(); i++){
					LT.set(i, new ArrayList<Integer>(currentConsideredTile.get(i)));
				}
				areaLT = uncoveredAreaTile;
			}
			//output LT and areaLT
			
			if(indexOfCurrentPermission == indexOfFirstPermissionLocated){
				
				//create the conditional database for recursion and clear it
				for(int i = 0 ; i < UPA.size() ; i++){
					ArrayList<Integer> temp = new ArrayList<Integer>();
					for(int j = 0 ; j < UPA.get(0).size(); j++){
							temp.add(j, 0);
					}
					conditionalUPA.set(i, new ArrayList<Integer>(temp));
				}
				
				//create conditional database for first time in this for loop
				generateConditionalUPAFromUPA(indexOfCurrentPermission);
				//output conditionalUPA
				for(int i = 0 ; i < conditionalUPA.size() ; i++){
				}
			}
			else{
				
				//create the conditional database for recursion and clear it
				for(int i = 0 ; i < UPA.size() ; i++){
					ArrayList<Integer> temp = new ArrayList<Integer>();
					for(int j = 0 ; j < UPA.get(0).size(); j++){
							temp.add(j, 0);
					}
					nextConditionalUPA.set(i, new ArrayList<Integer>(temp));
				}
				
				//create conditional database based on past conditional database
				generateConditionalUPA(indexOfCurrentPermission);
				//output nextConditionalUPA
				for(int i = 0 ; i < nextConditionalUPA.size() ; i++){
				}
			}
			
			if(indexOfCurrentPermission == indexOfFirstPermissionLocated){
				if(checkConditionalUPANull() == true){
					break;
				}
			}else{
				if(checkNextConditionalUPANull() == true){
					break;
				}
			}
		}
	}
}
