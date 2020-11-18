package BasicRMP;

import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;

public class DataAquisition{
	
	protected static ArrayList<ArrayList<Integer>> UPA = new ArrayList<ArrayList<Integer>>();
	protected static ArrayList<String> perms1 = new ArrayList<String>();
	protected static ArrayList<String> apps1 = new ArrayList<String>();
	 
	protected static ArrayList<ArrayList<Integer>> upaTilingMatrix = new ArrayList<ArrayList<Integer>>();
	protected static ArrayList<ArrayList<Integer>> conditionalUPA = new ArrayList<ArrayList<Integer>>();
	protected static ArrayList<ArrayList<Integer>> nextConditionalUPA = new ArrayList<ArrayList<Integer>>();
	protected static ArrayList<ArrayList<Integer>> TilingPermissions = new ArrayList<ArrayList<Integer>>();
	protected static ArrayList<ArrayList<Integer>> TilingUsers = new ArrayList<ArrayList<Integer>>();	
	
	protected static int countLUTM = 0;
	
	//P is the covered permissions arraylist, 1 in a position of P means that permission was never assigned to any app
	//OR is fully covered by the current Tiling
	protected static ArrayList<Integer> P = new ArrayList<Integer>();
	
	//currently considered perms for forming a tile
	protected static ArrayList<Integer> currentPermsForTile = new ArrayList<Integer>();
	
	//store current largest uncovered tile
	protected static ArrayList<ArrayList<Integer>> LT = new ArrayList<ArrayList<Integer>>();
	
	protected static ArrayList<ArrayList<Integer>> currentConsideredTile = new ArrayList<ArrayList<Integer>>();

	protected static int areaLT = 0;
	
	protected static ArrayList<ArrayList<Integer>> reconstructedUPA = new ArrayList<ArrayList<Integer>>();
	protected static ArrayList<Integer> numOfPermsPerAppUPA = new ArrayList<Integer>();
	protected static ArrayList<Integer> numOfPermsPerAppReconstructedUPA = new ArrayList<Integer>();
	protected static ArrayList<Integer> diffOfPermsPerAppUPAAndReconsctructedUPA = new ArrayList<Integer>();

	public static void fm() {
		try{
			Scanner input = new Scanner(new File("C:\\Users\\Samir\\Desktop\\workspace\\BasicRMP\\src\\combineNew.csv")/*,"UTF-16le"*/);
			int count = 0;
			if(input.hasNextLine()){
				input.nextLine();
				}//end if
			while(input.hasNextLine())
			{
				count = count + 1;
			    Scanner colReader = new Scanner(input.nextLine()).useDelimiter(",");
			    ArrayList col = new ArrayList();
			    while(colReader.hasNextInt())
				{
			        col.add(colReader.nextInt());
			    }//inner while
			    colReader.close();
				UPA.add(col);
			}//outer while
			input.close();
		}//end try 
		catch(Exception e){
			e.printStackTrace();
		}//end catch
	}//end fm
	
	public static void displayIr() {
		try{
			Scanner permReader = new Scanner(new File("C:\\Users\\Samir\\Desktop\\workspace\\BasicRMP\\src\\permissionsNew.csv")/*,"UTF-16le"*/).useDelimiter(",");			
			Scanner AppReader = new Scanner(new File("C:\\Users\\Samir\\Desktop\\workspace\\BasicRMP\\src\\Applications")/*,"UTF-16le"*/);			

			while(permReader.hasNext())
			{
			    ArrayList temp1 = new ArrayList();
				perms1.add(permReader.next());
			}//end while
			while(AppReader.hasNext())
			{
			    ArrayList temp2 = new ArrayList();
				apps1.add(AppReader.next());
			}//end while
		}//end try
		catch(Exception e){
			e.printStackTrace();
		}
	}//end disaplyIr()

	public DataAquisition() {
		super();
	}

}
