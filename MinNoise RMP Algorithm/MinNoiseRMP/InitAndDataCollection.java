package MinNoiseRMP;

import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;

public class InitAndDataCollection {
	protected static ArrayList<ArrayList<Integer>> UPA = new ArrayList<ArrayList<Integer>>();
	protected static ArrayList<ArrayList<Integer>> CR = new ArrayList<ArrayList<Integer>>();
	protected static ArrayList<Integer> areaOfRole = new ArrayList<Integer>();
	protected static ArrayList<ArrayList<Integer>> sortedCR = new ArrayList<ArrayList<Integer>>();
	protected static ArrayList<Integer> areaOfSortedRole = new ArrayList<Integer>();
	protected static  ArrayList<Integer> added = new ArrayList<Integer>();
	protected static  ArrayList<ArrayList<Integer>> tileForRole = new ArrayList<ArrayList<Integer>>();
	protected static  ArrayList<ArrayList<Integer>> upaTilingMatrix = new ArrayList<ArrayList<Integer>>();
	protected static  ArrayList<ArrayList<Integer>> finalCR = new ArrayList<ArrayList<Integer>>();
	protected static  ArrayList<Integer> uncoveredAreaOfSortedRole = new ArrayList<Integer>();
	protected static ArrayList<String> perms1 = new ArrayList<String>();
	protected static ArrayList<String> apps1 = new ArrayList<String>();
	protected static double underPrivilegeTotal = 0.0;
	protected static double overPrivilegeTotal = 0.0;
	protected static int delta = 0;
	protected static ArrayList<Integer> roleSetCardinality = new ArrayList<Integer>();
	
	//PA
	protected static ArrayList<ArrayList<Integer>> TilingPermissions = new ArrayList<ArrayList<Integer>>();
	
	//UA
	protected static ArrayList<ArrayList<Integer>> TilingUsers = new ArrayList<ArrayList<Integer>>();	
	protected static int tileNum = 0;
	public static void getUPA(String fileName) {
		try{
			Scanner input = new Scanner(new File(fileName)/*,"UTF-16le"*/);
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
	}//end getUPA
	
	public static void getCR(String fileNameCR) {
		try{
			Scanner input = new Scanner(new File(fileNameCR)/*,"UTF-16le"*/);
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
				CR.add(col);
			}//outer while
			input.close();
		}//end try 
		catch(Exception e){
			e.printStackTrace();
		}//end catch
	}//end getCR
	
	public static void displayIr() {
		try{
			Scanner permReader = new Scanner(new File("C:\\Users\\Samir\\Desktop\\workspace\\fastMinerTwo\\src\\permissionsNew.csv")/*,"UTF-16le"*/).useDelimiter(",");			
			Scanner AppReader = new Scanner(new File("C:\\Users\\Samir\\Desktop\\workspace\\fastMinerTwo\\src\\Applications")/*,"UTF-16le"*/);			

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

}
