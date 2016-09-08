import java.util.*;
import model.*;
import java.io.FileInputStream;
import java.util.Properties;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.FileNotFoundException;

/*
	Miles Tuffs

	based on:
	http://www.csegeek.com/csegeek/view/tutorials/algorithms/dynamic_prog/dp_part7.php
	&
	https://rosettacode.org/wiki/Knapsack_problem/Unbounded#Java
	
	Run	with 4G heap: java -Xmx4g MonthlyInventoryController <filename>
*/

public class MonthlyInventoryController {
	// recursive
	public int [] max;
	
	public int [] best;
	
	//iterative
	
	public int [] items;
	
	//both
	
	public int n;
	
	public int [] current;
	
	public Customer [] customers;
	
	public int MAX_IMPRESSIONS = 0;
	
		
	public long maxRevenue = 0;
	
	public static void main(String[] args ) {
		long startTime = System.nanoTime();
		if(args.length == 1)
			new MonthlyInventoryController(args[0]);
		else
			System.out.println("Incorrect usage. \nFor use: java MonthlyInventoryController <file name>");
		
		long estimatedTime = System.nanoTime() - startTime;
		System.out.println("Time taken: " + (double)estimatedTime / 1000000000.0);
	}
	
	public MonthlyInventoryController(String input) {
		// read input file
		readInput(input);
		n = customers.length;
		// array initilisation

		/*
			To handle case 3 or large maximum impression months
			a secondry algorithm using recursion is in place so 
			that large array sizes, that extend the heap size
			are not needed
		
			check for large sizes
		*/
		if (MAX_IMPRESSIONS <= 200000000) {
			// quick iterative solve
			
			items = new int [MAX_IMPRESSIONS+1];
			current = new int [MAX_IMPRESSIONS+1];
			iterativeCalc();
			iterativePrintOutput();
		
		} else {
			// slower recursive solve / larger ammount handler
			max = new int [n];
			best = new int [n];
			current = new int [n];
		
			for (int i=0; i<n; i++) {
				// calculate theorhetical maximums
				max[i] = (int)(MAX_IMPRESSIONS / customers[i].getImpressions());
				best[i] = 0;
				current[i] = 0;
			}
			
			recurseCalc(0);
			recursePrintOutput();
		}
	}
	
	/*
		Recursively calculates the maximum revenue (larger values of MAX_IMPRESSIONS >= 200000000)
		ileviates large array sizes being created
	*/
	private void recurseCalc(int customerId) {
		for(int i = 0; i <= max[customerId];i++) {
			current[customerId] = i;
			if(customerId < n-1) {
				recurseCalc(customerId+1);
			} else {
				long currentRevenue = 0;
				long currentImpressions = 0;
				for(int j=0;j<n;j++) {
					currentRevenue += current[j] * customers[j].getRevenue();
					currentImpressions += current[j] * customers[j].getImpressions();
				}

				if(currentRevenue > maxRevenue && currentImpressions <= MAX_IMPRESSIONS) {
					maxRevenue = currentRevenue;
					System.arraycopy(current,0,best,0,n);
				}
			}
		}
	}
	
	/*
		Iterativively calculates the maximum revenue (small values of MAX_IMPRESSIONS < 200000000)
	*/
	private void iterativeCalc() {
		current[0] = 0;
		items[0] = -1;
		for(int i=1; i<=MAX_IMPRESSIONS; i++) {
			// record order
			items[i] = items[i-1];
			int best = current[i-1];
			for(int j=0; j<n;j++) {
				int weight = i-customers[j].getImpressions();
				if(weight >= 0 && (current[weight] + customers[j].getRevenue()) > best ) {
					best = current[weight] + customers[j].getRevenue();
					items[i] = j;
				}
				current[i] = best;
			}
		}
	}
	
	/*
		Outputs the results from iterative calculation to command line
	*/
	private void iterativePrintOutput() {
		int [] quantities = new int[n];
		int i = MAX_IMPRESSIONS;
		
		for(int j=0; j<n;j++) {
			quantities[j] = 0;
		}
		while (i>=0) {
			int itemNum = items[i];
			if(itemNum == -1)
				break;
			
			quantities[itemNum]++;
			i -= customers[items[i]].getImpressions();
		}
		int maxImpressions=0;
		maxRevenue =0;
		for(i=0;i<n;i++) {
			System.out.println(
				customers[i].getName() + ","
				+ quantities[i] + ","
				+ quantities[i] * customers[i].getImpressions() + ","
				+ quantities[i] * customers[i].getRevenue()
			);
			maxImpressions += quantities[i] * customers[i].getImpressions();
			maxRevenue += quantities[i] * customers[i].getRevenue();
		}
		System.out.println(maxImpressions + ", " + maxRevenue);

	}
	/*
		Outputs the results from recursive calculation to command line
	*/
	private void recursePrintOutput() {
		int maxImpressions = 0;
		for(int i=0; i<n;i++) {
			System.out.println(
				customers[i].getName() + ","
				+ best[i] + ","
				+ best[i] * customers[i].getImpressions() + ","
				+ best[i] * customers[i].getRevenue()
			);
			maxImpressions += best[i] * customers[i].getImpressions();
		}
		System.out.println(maxImpressions + ", " + maxRevenue);
	}
	/*
		Read input file from command line
	*/
	private void readInput(String input) {
		try {
			FileInputStream fstream = new FileInputStream(input);
			BufferedReader br = new BufferedReader(new InputStreamReader(fstream));
			
			String str = br.readLine();
			MAX_IMPRESSIONS = Integer.parseInt(str);
			
			List<Customer> list = new ArrayList<Customer>();
            while ((str = br.readLine()) != null) {
				String[] ar=str.split(",");
				Customer newCust = new Customer(ar[0],Integer.parseInt(ar[1]),Integer.parseInt(ar[2]));
				list.add(newCust);
            }
			customers = new Customer[list.size()];
			list.toArray(customers);
		} catch (IOException e) {
			e.printStackTrace();
			return ;
		}

	}
}
