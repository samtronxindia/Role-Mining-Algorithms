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
	//protected static ArrayList<Double> static_privilege = new ArrayList<Double>();
	//protected static ArrayList<Integer> static_permissionCoverage = new ArrayList<Integer>();
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
			//System.out.println(input + "\n");
			int count = 0;
			//System.out.println(input.hasNextLine());
			//System.out.println("File loaded!");
			if(input.hasNextLine()){
				input.nextLine();
				}//end if
			while(input.hasNextLine())
			{
				count = count + 1;
				//System.out.println("Inside outer while: " + count);
			    Scanner colReader = new Scanner(input.nextLine()).useDelimiter(",");
			    ArrayList col = new ArrayList();
			    while(colReader.hasNextInt())
				{
			        col.add(colReader.nextInt());
											//System.out.println(col + " ");
			    }//inner while
			    colReader.close();
							//System.out.println(col.indexOf(col) + ":" + col + "\n");
				static_UPA.add(col);
			}//outer while
			//System.out.println("\n" + uP + "\n");
			//System.out.println(uP.get(0));
			input.close();
		}//end try 
		catch(Exception e){
			e.printStackTrace();
		}//end catch
	}//end getUPA
	
	public static void getCR(String fileNameCR) {
		try{
			Scanner input = new Scanner(new File(fileNameCR)/*,"UTF-16le"*/);
			//System.out.println(input + "\n");
			int count = 0;
			//System.out.println(input.hasNextLine());
			//System.out.println("File loaded!");
			if(input.hasNextLine()){
				input.nextLine();
				}//end if
			while(input.hasNextLine())
			{
				count = count + 1;
				//System.out.println("Inside outer while: " + count);
			    Scanner colReader = new Scanner(input.nextLine()).useDelimiter(",");
			    ArrayList col = new ArrayList();
			    while(colReader.hasNextInt())
				{
			        col.add(colReader.nextInt());
											//System.out.println(col + " ");
			    }//inner while
			    colReader.close();
				//System.out.println(col.indexOf(col) + ":" + col + "\n");
				static_CR.add(col);
			}//outer while
			//System.out.println("\n" + uP + "\n");
			//System.out.println(uP.get(0));
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
					//System.out.println(col + " ");
			}//end while
			//System.out.println("Total number of permissions: " + static_perms1.size() + "\n");
			//System.out.println("Permissions: " + static_perms1 + "\n");
			while(AppReader.hasNext())
			{
			    ArrayList temp2 = new ArrayList();
				static_apps1.add(AppReader.next());
					//System.out.println(col + " ");
			}//end while
		}//end try
		catch(Exception e){
			e.printStackTrace();
		}
	}//end disaplyIr()

}
