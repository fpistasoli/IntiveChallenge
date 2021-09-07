using System.Collections;
using System.Collections.Generic;
using System.IO;
using System;
using UnityEngine;
using UnityEngine.UI;


public class Triplets : MonoBehaviour
{
	 
	 
	void Awake(){

		TripletsTopTen(@"C:\Log.txt");
       
	}


    public void TripletsTopTen(string log)
	{
        //Create a dictionary<key,value>, where key = user_ID and value = list of pages visited
		Dictionary<char,List<char>> pagesVisitedByUser = new Dictionary<char, List<char>>();
        StorePagesVisitedByUser(log, pagesVisitedByUser);

        //Create a list of all triplets
		ArrayList triplets = GetTriplets(pagesVisitedByUser); // { [1,3,2] , [3,2,4] , [2,4,5] , ... }

		//Work out the frequency of each triplet in triplets
		ArrayList tripletsFrequency = TripletsFrequency(triplets); // { ([1,3,2], 2) , ([3,2,4] , 3) , ([2,4,5] , 2) ,  ... }

        //Sort tripletsFrequency from highest to lowest frequency
        List<(char[], int)> tripletsFrequencySorted = SortTripletsFrequency(tripletsFrequency); // { ([3,2,4], 3) , ([5,6,8] , 3) , ([1,3,2] , 2) , ... }
		
		//Print Top Ten
		print("TOP TEN: ");
		ShowTopTen(tripletsFrequencySorted);
	}
	
	
	private void StorePagesVisitedByUser(string log, Dictionary<char, List<char>> pagesVisitedByUser)
    {
		string line;
        try
        {
			//Pass the file path and file name to the StreamReader constructor
            StreamReader sr = new StreamReader(log);
            //Read the first line of text
            line = sr.ReadLine();

			AddPageVisitedByUser(line, pagesVisitedByUser);
            
			//Continue to read until you reach end of file
            while (line != null)
            {
                //Read the next line
                line = sr.ReadLine();

                AddPageVisitedByUser(line, pagesVisitedByUser);
            }
			
			//close the file
            sr.Close();
            Console.ReadLine();
        }
		
		catch (Exception e)
        {
            Console.WriteLine("Exception: " + e.Message);
        }
        
		finally
        {
            Console.WriteLine("Executing finally block.");
        }
    }

	
	private void AddPageVisitedByUser(string line, Dictionary<char, List<char>> pagesVisitedByUser)
    {
        String[] fields = line.Split(';'); //user_ID ; timestamp ; page_ID

        char[] userID = fields[0].ToCharArray();
        char userIDchar = userID[0];
        char[] pageID = fields[2].ToCharArray();
        char pageIDchar = pageID[0];

        //Add the page visited by the user in pagesVisitedByUser
        if (pagesVisitedByUser.ContainsKey(userIDchar))
        {
            List<char> pagesVisited;
            pagesVisitedByUser.TryGetValue(userIDchar, out pagesVisited);
            pagesVisited.Add(pageIDchar);
        }
        else
        { //create <key,value> <userID, List<first page visited>> in pagesVisitedByUser
            List<char> pagesVisited = new List<char>();
            pagesVisited.Add(pageIDchar);
            pagesVisitedByUser.Add(userIDchar, pagesVisited);
        }
    }
	
	
	private ArrayList GetTriplets(Dictionary<char, List<char>> pagesVisitedByUser)
    {
        ArrayList triplets = new ArrayList();
		
		foreach (KeyValuePair<char, List<char>> kvp in pagesVisitedByUser)
        {
			if (kvp.Value.Count >= 3) //the user has visited at least 3 pages sequentially
			{
				List<char> pagesVisited = kvp.Value;

				for(int i=0; i< pagesVisited.Count - 2; i++)
				{
                    char[] triplet = new char[3];
					triplet[0] = pagesVisited[i];
					triplet[1] = pagesVisited[i+1];
                    triplet[2] = pagesVisited[i+2];
					triplets.Add(triplet);
                }
			}
		}

		return triplets;
    }
	
	
	private ArrayList TripletsFrequency(ArrayList triplets)
	{
		ArrayList tripletsFrequency  = new ArrayList();
		HashSet<char[]> queriedTriplets = new HashSet<char[]>();
		
		for(int i = 0; i < triplets.Count - 1; i++)
		{
			if (IsAnElementOf(queriedTriplets, triplets[i] as char[])) {continue;} //if already queried for frequency, ignore it
			queriedTriplets.Add(triplets[i] as char[]); 
			
			int frequency = 1;
			(char[], int) tripletFrequency;
			char[] ithTriplet = triplets[i] as char[];

			for(int j = i + 1; j < triplets.Count ; j++)
			{
				char[] jthTriplet = triplets[j] as char[];
				if(AreEqual(ithTriplet,jthTriplet))
				{
					frequency++;
				}
			}

			tripletFrequency = (ithTriplet, frequency);
			tripletsFrequency.Add(tripletFrequency);
		}

        //Add the last triplet if not already queried
        char[] lastTriplet = triplets[triplets.Count - 1] as char[];
		if(!IsAnElementOf(queriedTriplets, lastTriplet))
		{
			(char[], int) lastTripletFrequency = (lastTriplet, 1);
			tripletsFrequency.Add(lastTripletFrequency);
		}
		
		return tripletsFrequency;
	}
	
	
	private bool IsAnElementOf(HashSet<char[]> queriedTriplets, char[] triplet)
    {
		bool contained = false;
		
		foreach (char[] elem in queriedTriplets)
		{
			if(AreEqual(elem, triplet))
			{   
				contained = true;
				break;
			}
		}
		
		return contained;
    }
	
	
	private bool AreEqual(char[] ithTriplet, char[] jthTriplet)
    {
		//PRECONDITION: both input arrays have 3 elements each
		bool areEqual = true;
		
		for (int k = 0; k < ithTriplet.Length; k++)
		{
			if (ithTriplet[k] != jthTriplet[k])
			{
				areEqual = false;
				break;
			}
		}

			return areEqual;
	}
	
	
	private List<(char[], int)> SortTripletsFrequency(ArrayList tripletsFrequency)
	{
		List<(char[], int)> tripletsFrequencyList = ToList(tripletsFrequency);
		tripletsFrequencyList.Sort((x, y) => y.Item2.CompareTo(x.Item2));
		return tripletsFrequencyList;
	}
	
	       
	private List<(char[], int)> ToList(ArrayList tripletsFrequency)
    {
		List<(char[], int)> tripletsFrequencyList = new List<(char[], int)>();
		
		foreach ((char[], int) element in tripletsFrequency)
		{
			tripletsFrequencyList.Add(element);
		}

		return tripletsFrequencyList;
    }
	

	private void ShowTopTen(List<(char[], int)> tripletsFrequencySorted)
	{
		int i=1;
		foreach((char[], int) triplet in tripletsFrequencySorted)
		{
			if(i>10) {break;}
			if(i==1){
				print("1st place: ");
			} else if (i == 2){
                print("2nd place: ");
            } else if (i == 3) {
                print("3rd place: ");
            } else {
				print(i + "th place: ");
			}
				
			print("Triplet of Page IDs visited: " + triplet.Item1[0] + " - " + triplet.Item1[1] + " - " + 
			triplet.Item1[2] + ". Number of occurrences of this triplet: " + triplet.Item2);
			i++;
		}
	}
	

}
