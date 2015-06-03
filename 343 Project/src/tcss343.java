/**
 * TCSS 343A HW5
 * @author Wing-Sea Poon, Jude Guo, Nikhila Potharaj, Taylor Gorman
 */

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.Stack;

public class tcss343 {
	public static final String OUTPUT_FILE1 = "100x100.txt";
	public static final String OUTPUT_FILE2 = "200x200.txt";
	public static final String OUTPUT_FILE3 = "400x400.txt";
	public static final String OUTPUT_FILE4 = "600x600.txt";
	public static final String OUTPUT_FILE5 = "800x800.txt";
	
	public static final int INFINITY = Integer.MAX_VALUE;
	public static final int MAX_COST = 9;
	
	public static List<ArrayList<Integer>> bruteForcePath;
	public static int bruteForceMinCost = INFINITY;
	
	public static void main(String[] args) throws FileNotFoundException {
		String fileName = args[0];
		//String fileName = "input.txt";

		//generateAllCostTables();
		
		// Get the cost table from the input file
		Scanner input = new Scanner(new File(fileName));
		List<List<Integer>> R = getR(input);
		input.close();
		
		runBruteForce(R);
		runDivideAndConquer(R);
		runDynamic(R);
	}
	
	
	
	
	/*==========================*
	 * Cost Table Gen & Loading *
	 *==========================*/
	
	/**
	 * Get R[][] by parsing the input file.
	 * @param input A Scanner of the input file.
	 * @return R[][] The cost matrix
	 * @author Wing-Sea Poon
	 */
	public static List<List<Integer>> getR(Scanner input) {
		List<List<Integer>> R = new ArrayList<List<Integer>>();
		
		int i = 0;
		while(input.hasNextLine()) {
			R.add(new ArrayList<Integer>());
			String line = input.nextLine();
			Scanner lineScanner = new Scanner(line);
			
			while(lineScanner.hasNext()) {
				String token = lineScanner.next();
				if(token.equalsIgnoreCase("NA")) {
					R.get(i).add(INFINITY);
				}
				else {
					R.get(i).add(Integer.parseInt(token));
				}
			}
			
			i++;
			lineScanner.close();
		}
		
		return R;
	}
	

	/**
	 * Creates an n x n matrix of random costs between 1 inclusive and
	 * MAX_COST inclusive.
	 * @param n The dimension of the square matrix.
	 * @param fileName The name of the File to write to
	 * @throws FileNotFoundException
	 * @author Wing-Sea Poon
	 */
	public static void generateCostTable(int n, String fileName) 
	throws FileNotFoundException {
		PrintStream output = new PrintStream(new File(fileName));
		
		for(int i = 0; i < n; i++) {
			for(int j = 0; j < n; j++) {
				if(i > j) {
					output.print("NA\t");
				}
				else if(i == j) {
					output.print(0 + "\t");
				}
				else {
					// randCost between 1 inclusive and MAX_COST inclusive
					int randCost = (new Random().nextInt(MAX_COST)) + 1; 
					output.print(randCost + "\t");
				}
			}
			output.println();
		}
		
		output.close();
	}
	
	
	
	/*=============*
	 * Brute Force *
	 *=============*/
	
	
	/*
	 * bruteForceMin() updates Class Variables for the minimum cost and its associated path.
	 * The reason for this design choice was because the amount of parameters to be passed to bruteForceStep()
	 * was already at the high number of 5. I felt that increasing this to 7 would just decrease readability
	 * without actually increasing efficiency.
	 */
	
	
	/*
	 * While bruteForceMin() takes a recursive approach, it is still an example of a Brute Force algorithm.
	 * It sequentially checks every single possibility and makes zero comparisons between those possibilities.
	 * Instead, the possibility is kept isolated and only compared to the current solution.
	 */
	
	
	/**
	 * For a given 2D-array of canoe rental prices for various trading posts, set bruteForceMinValue
	 * to the minimal possible cost of going downstream, and bruteForcePath to the corresponding path of rentals.<p>
	 * 
	 * In particular, do so using a Brute Force approach, where all possibilities are tested
	 * before the minimum value is found.
	 * @author Jude Guo and Taylor Gorman
	 */
	public static void bruteForceMin(List<List<Integer>> costArray) {
		int numTradingPosts = costArray.size();
		int startingPoint = 0;
		
		//For every trading post, rent a canoe from the starting point and travel to it.
		//Then, recursively check all remaining paths that can be derived following that first rental.
		for(int firstStop = 1; firstStop < numTradingPosts; firstStop++) {
			bruteForceStep(startingPoint, firstStop, 0, new ArrayList<ArrayList<Integer>>(), costArray);
		}		
		return;
	}
	
	
	/*
	 * Update the cost and path, and then recursively check all remaining paths that can be derived
	 * from the current path. Once we reach the destination, check if the global min is beaten, and if so, update it.
	 */
	private static void bruteForceStep(int thePrevStop, int theCurrentStop, int theCurrentCost,
			ArrayList<ArrayList<Integer>> theCurrentPath, List<List<Integer>> theCostArray) {
		
		ArrayList<ArrayList<Integer>> pathInstance = new ArrayList<ArrayList<Integer>>();
		pathInstance.addAll(theCurrentPath);
		
		//Update our cost based on the rental fee to get from the previous stop to the current one.
		theCurrentCost += theCostArray.get(thePrevStop).get(theCurrentStop);
		
		//Update our rental path with the location that we have stopped at.
		ArrayList<Integer> currentStopLocation = new ArrayList<Integer>();
		currentStopLocation.add(thePrevStop);
		currentStopLocation.add(theCurrentStop);		
		pathInstance.add(currentStopLocation);
		
		if(theCurrentStop == theCostArray.size() - 1) {
			//We have reached our destination.
			
			if(theCurrentCost < bruteForceMinCost) {
				//Check to see if the current rental cost is lower than the global minimum.
				//If so, update the global minimum, along with the attached path.
				
				bruteForceMinCost = theCurrentCost;
				bruteForcePath = pathInstance;
			}
			

		} else {
			//We have not reached our destination.
			//Recursively check all possible paths that derive from the current Path.
			for(int nextStop = theCurrentStop + 1; nextStop < theCostArray.size(); nextStop++) {
				bruteForceStep(theCurrentStop, nextStop, theCurrentCost, pathInstance, theCostArray);
			}
		}	
		return;
	}
	
	
	
	
	
	/*====================*
	 * Divide and Conquer *
	 *====================*/
	
	/**
	 * @author Nikhila Potharaj
	 */
	public static int divideRecursion(List<List<Integer>> R) {
		List<Integer> seq = new ArrayList<Integer>();
		seq.add(0);
		seq.add(1);
		if(R.size() != 0) {
			return divideRecursion(R, R.size(), seq);
		} else {
			return -1;
		}
	}
	
	/**
	 * @author Nikhila Potharaj
	 */
	private static int divideRecursion(List<List<Integer>> R, 
			int n, List<Integer> seq) {
		if(n == 1) {
			return 0;
		} else if(n == 2) {
			return R.get(0).get(1);
		} else {
			int min = Integer.MAX_VALUE;
			int selected = Integer.MAX_VALUE;
			for(int i = 0; i < n - 1; i++) {
				int prev = R.get(0).get(i) + R.get(i).get(n - 1);
				min = Math.min(prev, min);
				if(prev == min) {
					selected = i;				
				}
			}
			int prev = divideRecursion(R, n - 1, seq);
			int sum = prev + R.get(n - 2).get(n - 1);
			if(prev == sum) {
				seq.add(n - 2);
			} else {
				seq.add(selected);
			}
			divideAndConquerRecover(seq, n - 2);
			return Math.min(min, sum);
		}
	}
	
	/**
	 * @author Nikhila Potharaj
	 */
	public static void divideAndConquerRecover(List<Integer> sequence, int n) {
		if(n == 2) {
			System.out.println(dynamicProgrammingRecover(sequence));
		}
	}
	
	
	
	
	
	/*=====================*
	 * Dynamic Programming *
	 *=====================*/
	
	
	/**
	 * @author Wing-Sea Poon
	 */
	public static int dynamicProgrammingMin(List<List<Integer>> R) {
		if(R.isEmpty()) {
			return 0;
		}
		
		// declare
		List<Integer> result = new ArrayList<Integer>();
		List<Integer> sequence = new ArrayList<Integer>();
		int matrixSize = R.size() - 1;		
		
		// initialize (BC)
		if(matrixSize >= 0) {
			result.add(0);
			sequence.add(0);
		}
		if(matrixSize >= 1) {
			result.add(R.get(0).get(1));
			sequence.add(1);
		}
		
		// iterate (RC)
		for(int row = 2; row <= matrixSize; row++) {
			int curr = R.get(row - 1).get(row);
			int prevSolutionPlusCurr = result.get(row - 1) + curr;
			
			int lastColMin = INFINITY;
			int selected = INFINITY;
			for(int k = 0; k < row - 1; k++) {
				int thisColValue = result.get(k) + R.get(k).get(row);
				lastColMin = Math.min(thisColValue, lastColMin);
				
				if(lastColMin == thisColValue) {
					selected = k;
				}
			}
			
			int min = Math.min(prevSolutionPlusCurr, lastColMin);
			result.add(min);
			
			if(result.get(row) == prevSolutionPlusCurr) {
				sequence.add(row - 1);
			}
			else {
				sequence.add(selected);
			}
		}
		
		System.out.println(dynamicProgrammingRecover(sequence));
		return result.get(matrixSize);
	}
	
	/**
	 * Returns the 0-based sequence of posts to select.
	 * @author Wing-Sea Poon
	 */
	public static List<Integer> dynamicProgrammingRecover(List<Integer> sequence) {
		List<Integer> result = new ArrayList<Integer>();
		Stack<Integer> backwardsOrder = new Stack<Integer>();
		int lastElem = sequence.size() - 1;
		backwardsOrder.push(lastElem);
		
		while(lastElem > 0) {
			lastElem = sequence.get(lastElem);
			backwardsOrder.push(lastElem);
		}
		
		while(!backwardsOrder.isEmpty()) {
			result.add(backwardsOrder.pop());
		}
		
		return result;
	}
	
	
	
	
	
	/*================*
	 * Helper Methods *
	 *================*/	
	
	private static void generateAllCostTables() throws FileNotFoundException {
		generateCostTable(100, OUTPUT_FILE1);
		generateCostTable(200, OUTPUT_FILE2);
		generateCostTable(400, OUTPUT_FILE3);
		generateCostTable(600, OUTPUT_FILE4);
		generateCostTable(800, OUTPUT_FILE5);
	}
	
	
	private static void runBruteForce(List<List<Integer>> theCostTable) {
		System.out.println("-----------------\nBrute Force\n");
		
		long startTime = System.currentTimeMillis();
		
		//Run the algorithm
		bruteForcePath = new ArrayList<ArrayList<Integer>>();
		bruteForceMin(theCostTable);
		
		long endTime = System.currentTimeMillis();
		
		//Display the results.
		System.out.print("Path:\n[0");
		for(ArrayList<Integer> coordinate : bruteForcePath) {
			System.out.print(", " + coordinate.get(1));
		}
		System.out.println("]");
		
		System.out.println("\nMinimum Cost: " + bruteForceMinCost);
		System.out.println("Running Time: " + (endTime - startTime));
	}
	
	
	private static void runDivideAndConquer(List<List<Integer>> theCostTable) {
		System.out.println("-----------------\nDivide & Conquer\n");
		
		long startTime = System.currentTimeMillis();
		
		//Run the algorithm and display the results.
		System.out.println("Path:");
		int divideMin = divideRecursion(theCostTable);
		
		long endTime = System.currentTimeMillis();
		
		System.out.println("\nMinimum Cost: " + divideMin);
		System.out.println("Running Time: " + (endTime - startTime));
	}
	
	private static void runDynamic(List<List<Integer>> theCostTable) {
		System.out.println("-----------------\nDynamic Programming\n");
		
		long startTime = System.currentTimeMillis();
		
		//Run the algorithm and display the results.
		System.out.println("Path:");
		int dynamicMin = dynamicProgrammingMin(theCostTable);
		
		long endTime = System.currentTimeMillis();
		
		System.out.println("\nMinimum Cost: " + dynamicMin);
		System.out.println("Running Time: " + (endTime - startTime));
	}
	
	
}
