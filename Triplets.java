import java.util.*;
import java.io.*;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.io.File;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;

@SuppressWarnings("unchecked")
class Triplets{
	
	static class Pair
	{
		char[] triplet;
		int freq;

		Pair(char[] triplet, int freq)
		{
			this.triplet = triplet;
			this.freq = freq;
		}
	}
	
	
	public static void main(String args[])
	{
		
		TripletsTopTen("C:/Log.txt");
	
	}
	
	
	public static void TripletsTopTen(String log)
	{
	    //Create a HashMap<key,value>, where key = user_ID and value = list of pages visited
		HashMap<Character,ArrayList<Character>> pagesVisitedByUser = new HashMap<Character, ArrayList<Character>>();
	    StorePagesVisitedByUser(log, pagesVisitedByUser);	
		
		//Create a list of all triplets
		ArrayList triplets = GetTriplets(pagesVisitedByUser); // { [1,3,2] , [3,2,4] , [2,4,5] , ... }

		//Work out the frequency of each triplet in triplets
		ArrayList tripletsFrequency = TripletsFrequency(triplets); // { ([1,3,2], 2) , ([3,2,4] , 3) , ([2,4,5] , 2) ,  ... }

		//Sort tripletsFrequency from highest to lowest frequency
		ArrayList<Pair> tripletsFrequencySorted = SortTripletsFrequency(tripletsFrequency); // { ([3,2,4], 3) , ([5,6,8] , 3) , ([1,3,2] , 2) , ... }

		//Print Top Ten
		print("TOP TEN: ");
		ShowTopTen(tripletsFrequencySorted);

		
	}
	
	
	
	private static void StorePagesVisitedByUser(String log, HashMap<Character, ArrayList<Character>> pagesVisitedByUser)
	{
		String line;
		try
		{
			//Pass the file path and file name to the StreamReader constructor
			
			InputStream stream = new ByteArrayInputStream(log.getBytes("UTF-8"));
			
			BufferedReader sr = new BufferedReader(new InputStreamReader(stream));
			
			//Read the first line of text
			line = sr.readLine();
			
			AddPageVisitedByUser(line, pagesVisitedByUser);

			//Continue to read until you reach end of file
			while (line != null)
			{
				//Read the next line
				line = sr.readLine();

				AddPageVisitedByUser(line, pagesVisitedByUser);
			}

			//close the file
			sr.close();
			new Scanner(System.in).nextLine();
		}

		catch (IOException e)
		{
			System.out.println("Exception: " + e.getMessage());
		}

		finally
		{
			System.out.println("Executing finally block.");
		}
	}
	
	
	 
	private static void AddPageVisitedByUser(String line, HashMap<Character, ArrayList<Character>> pagesVisitedByUser)
	{
		String[] fields = line.split("[;]", -1); //user_ID ; timestamp ; page_ID

		char[] userID = fields[0].toCharArray();
		char userIDchar = userID[0];
		char[] pageID = fields[2].toCharArray();
		char pageIDchar = pageID[0];

		//Add the page visited by the user in pagesVisitedByUser
		if (pagesVisitedByUser.containsKey(userIDchar))
		{
			ArrayList<Character> pagesVisited;
			pagesVisited = pagesVisitedByUser.get(userIDchar);
			pagesVisited.add(pageIDchar);
		}
		else
		{ //create <key,value> <userID, List<first page visited>> in pagesVisitedByUser
			ArrayList<Character> pagesVisited = new ArrayList<Character>();
			pagesVisited.add(pageIDchar);
			pagesVisitedByUser.put(userIDchar, pagesVisited);
		}
	}


	private static ArrayList GetTriplets(HashMap<Character, ArrayList<Character>> pagesVisitedByUser)
	{
		ArrayList triplets = new ArrayList();

		for (Map.Entry<Character, ArrayList<Character>> kvp : pagesVisitedByUser.entrySet())
		{
			if (kvp.getValue().size() >= 3) //the user has visited at least 3 pages sequentially
			{
				ArrayList<Character> pagesVisited = kvp.getValue();

				for (int i = 0; i < pagesVisited.size() - 2; i++)
				{
					char[] triplet = new char[3];
					triplet[0] = pagesVisited.get(i);
					triplet[1] = pagesVisited.get(i + 1);
					triplet[2] = pagesVisited.get(i + 2);
					triplets.add(triplet);
				}
			}
		}

		return triplets;
	}

	
	
	private static ArrayList TripletsFrequency(ArrayList triplets)
	{
		ArrayList tripletsFrequency = new ArrayList();
		HashSet<char[]> queriedTriplets = new HashSet<char[]>();

		for (int i = 0; i < triplets.size() - 1; i++)
		{
			if (IsAnElementOf(queriedTriplets, triplets.get(i) instanceof char[] ? (char[])triplets.get(i) : null))
			{
				continue;
			} //if already queried for frequency, ignore it
			queriedTriplets.add(triplets.get(i) instanceof char[] ? (char[])triplets.get(i) : null);

			int frequency = 1;
			
		    Pair tripletFrequency;
		
			char[] ithTriplet = triplets.get(i) instanceof char[] ? (char[])triplets.get(i) : null;

			for (int j = i + 1; j < triplets.size() ; j++)
			{
				char[] jthTriplet = triplets.get(j) instanceof char[] ? (char[])triplets.get(j) : null;
				if (AreEqual(ithTriplet,jthTriplet))
				{
					frequency++;
				}
			}

			tripletFrequency = new Pair(ithTriplet, frequency);
			
			tripletsFrequency.add(tripletFrequency);
		}

		//Add the last triplet if not already queried
		char[] lastTriplet = triplets.get(triplets.size() - 1) instanceof char[] ? (char[])triplets.get(triplets.size() - 1) : null;
		if (!IsAnElementOf(queriedTriplets, lastTriplet))
		{
			
			Pair lastTripletFrequency = new Pair(lastTriplet, 1);
			
			tripletsFrequency.add(lastTripletFrequency);
		}

		return tripletsFrequency;
	}


	private static boolean IsAnElementOf(HashSet<char[]> queriedTriplets, char[] triplet)
	{
		boolean contained = false;

		for (char[] elem : queriedTriplets)
		{
			if (AreEqual(elem, triplet))
			{
				contained = true;
				break;
			}
		}

		return contained;
	}

	
	private static boolean AreEqual(char[] ithTriplet, char[] jthTriplet)
	{
		//PRECONDITION: both input arrays have 3 elements each
		boolean areEqual = true;

		for (int k = 0; k < ithTriplet.length; k++)
		{
			if (ithTriplet[k] != jthTriplet[k])
			{
				areEqual = false;
				break;
			}
		}

			return areEqual;
	}


	private static List<Pair> SortTripletsFrequency(ArrayList tripletsFrequency)
	{
		List<Pair> tripletsFrequencyList = ToList(tripletsFrequency);
		tripletsFrequencyList.Sort((x, y) -> y.freq.CompareTo(x.freq));
		return tripletsFrequencyList;
	}
	

	private static List<Pair> ToList(ArrayList tripletsFrequency)
    {
		List<Pair> tripletsFrequencyList = new List<Pair>();
		
		for (Pair element : tripletsFrequency)
		{
			tripletsFrequencyList.Add(element);
		}

		return tripletsFrequencyList;
    }
	

	private static void ShowTopTen(ArrayList<Pair> tripletsFrequencySorted)
	{
		int i = 1;
		for (P triplet : tripletsFrequencySorted)
		{
			if (i > 10)
			{
				break;
			}
			if (i == 1)
			{
				print("1st place: ");
			}
			else if (i == 2)
			{
				print("2nd place: ");
			}
			else if (i == 3)
			{
				print("3rd place: ");
			}
			else
			{
				print(i + "th place: ");
			}

			print("Triplet of Page IDs visited: " + triplet.triplet[0] + " - " + triplet.triplet[1] + " - " + triplet.triplet[2] + ". Number of occurrences of this triplet: " + triplet.freq);
			i++;
		}
	}

}













