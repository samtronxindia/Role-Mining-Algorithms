package DeltaRMP;

import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;

public class InitAndDataCollection {
	
	protected static ArrayList<ArrayList<Integer>> static_UPA = new ArrayList<ArrayList<Integer>>();
	protected static ArrayList<ArrayList<Integer>> static_CR = new ArrayList<ArrayList<Integer>>();
	protected static ArrayList<ArrayList<Integer>> static_sortedCR = new ArrayList<ArrayList<Integer>>();
	protected static ArrayList<Integer> static_areaOfSortedRole = new ArrayList<Integer>();
	protected static ArrayList<Integer> static_areaOfRole = new ArrayList<Integer>();
	protected static ArrayList<String> static_perms1 = new ArrayList<String>();
	protected static ArrayList<String> static_apps1 = new ArrayList<String>();
	protected static ArrayList<Integer> static_roleSetCardinality = new ArrayList<Integer>();
	protected static int delta = 0;
	protected  ArrayList<Integer> added = new ArrayList<Integer>();
	protected  ArrayList<ArrayList<Integer>> finalCR = new ArrayList<ArrayList<Integer>>();
	protected  ArrayList<ArrayList<Integer>> tileForRole = new ArrayList<ArrayList<Integer>>();
	protected  ArrayList<ArrayList<Integer>> upaTilingMatrix = new ArrayList<ArrayList<Integer>>();
	protected  ArrayList<Integer> uncoveredAreaOfSortedRole = new ArrayList<Integer>();
	protected ArrayList<Integer> addedPerm = new ArrayList<Integer>();
	
	protected double underPrivilegeTotal = 0.0;
	protected double overPrivilegeTotal = 0.0;
	
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
				static_UPA.add(col);
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
				static_CR.add(col);
			}//outer while
			input.close();
		}//end try 
		catch(Exception e){
			e.printStackTrace();
		}//end catch
	}//end getCR
	
	public static void displayIr() {
		try{
			Scanner permReader = new Scanner(new File("C:\\Users\\Samir\\Desktop\\workspace\\DeltaRMP\\src\\permissionsNew.csv")/*,"UTF-16le"*/).useDelimiter(",");			
			Scanner AppReader = new Scanner(new File("C:\\Users\\Samir\\Desktop\\workspace\\DeltaRMP\\src\\Applications")/*,"UTF-16le"*/);			

			while(permReader.hasNext())
			{
			    ArrayList temp1 = new ArrayList();
				static_perms1.add(permReader.next());
			}//end while
			while(AppReader.hasNext())
			{
			    ArrayList temp2 = new ArrayList();
				static_apps1.add(AppReader.next());
			}//end while
		}//end try
		catch(Exception e){
			e.printStackTrace();
		}
	}//end disaplyIr()

}
