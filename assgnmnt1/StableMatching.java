import java.util.*;
import java.io.*;


public class StableMatching
{
	private static Stack<Person> men;
	private static Person[] menList;
	private static Person[] women;

	public static void main(String args[])
	{
		if(args.length == 0)
		{
			System.out.println("Please give us a filename");
			System.exit(0);
		}

		men = new Stack<Person>();
		
		StringBuilder sb = new StringBuilder();
		File file = new File(args[0]);
		BufferedReader reader = null;
		boolean nameInput;
		int size = 0;
		
		try {
			reader = new BufferedReader(new FileReader(file));
			String text = null;
            //Get rid of comments and find size
			while((text = reader.readLine()) != null){
				if(text.startsWith("#")) continue;
				size = Integer.parseInt(text.substring(2));
				women = new Person[size*2];
				menList = new Person[size*2];
				break;
			}
            //Get names
			int personArrayNumber;
			for(int i = 1; i<=size*2; i++) {
				text = reader.readLine();
				if(i%2==0){
					Person woman = new Person(Integer.parseInt(text.split(" ")[0]), text.split(" ")[1]);
					women[woman.GetId()-1] = woman;
				}
				else{
					Person man = new Person(Integer.parseInt(text.split(" ")[0]),text.split(" ")[1]);
					menList[man.GetId()-1] = man;
				}
			}

			//reverse
			for(int i=menList.length; i>0; i--){
				if(menList[i-1] != null)
					men.push(menList[i-1]);
			}


            //Create table of preferences
            //tableOfPreferences = new int[size*2][size];
			reader.readLine();
			for(int i = 1; i<=size*2; i++){
				text = reader.readLine();
				Integer personIndex = Integer.parseInt(text.split(": ")[0]);
				String[] prefs = text.split(": ")[1].split(" ");
				Integer[] newPrefs = new Integer[prefs.length];

				for (int y = 0; y < prefs.length; y++ ) {
					newPrefs[y] = Integer.parseInt(prefs[y]);
				}

				if(women[personIndex-1] != null){
					Person tmp = women[personIndex-1];

					Integer[] reversePref = reverseArray(newPrefs, size*2);

					tmp.SetWomanPreferences(reversePref);				
				}
				else if(menList[personIndex-1] != null){
					Person tmp = menList[personIndex-1];
					tmp.SetPreferences(newPrefs);		
				}
			}

		} catch (IOException e) {
			e.printStackTrace();
		}

		DoMatching(men);

		for(int i = 0; i < menList.length; i++)
			if(menList[i] != null)
			{
				Person man = menList[i];
				System.out.println(man.GetName() + " -- "+man.GetMatch().GetName());
			}
		}

		public static Integer[] reverseArray(Integer[] persons, int size)
		{
			Integer[] returnList = new Integer[size];

			for(int i = 0; i < persons.length; i++)
			{
				Integer tmp = persons[i];
				returnList[tmp-1] = i;
			}

			return returnList;
		}

		public static void DoMatching(Stack<Person> men)
		{
			while(!men.empty())
			{
				DoMatchingOne(men.pop(), men);
			}
		}

		public static void DoMatchingOne(Person man, Stack<Person> men)
		{
			Integer[] preferences = man.GetPreferences();
			
			for (int i = man.GetStartingIndex(); i < preferences.length; i++) {
				Person preference = women[preferences[i] - 1];
				
				man.SetStartingInteger(i + 1);

				if(preference.IsMatched())
				{
					Person marriedTo = preference.GetMatch(); 
					boolean breakk = preference.CompareMen(marriedTo, man, men);	
					
					if(breakk)
						return;
				}
				else
				{
					man.SetMatched(preference);
					preference.SetMatched(man);

					return;
				}	
			}
		} 


	}

	class Person
	{
		private int id;
		private String name;
		private Person matchedTo;
		private Integer[] preferences;
		private Integer[] womanPreferences;
		private Integer startingInteger;

		public Person(int id, String name)
		{
			this.id=id;
			this.name = name;
			this.startingInteger = 0;
		}

		public Integer GetStartingIndex()
		{
			return startingInteger;
		}

		public void SetStartingInteger(Integer i)
		{
			this.startingInteger = i;
		}

		public Integer[] GetPreferences()
		{
			return preferences;
		}

		public void SetPreferences(Integer[] preferences)
		{
			this.preferences = preferences;
		}

		public Person GetMatch()
		{
			return matchedTo;
		}
		public boolean IsMatched()
		{
			if (matchedTo == null) {
				return false;
			}
			return true;
		}

		public void SetMatched(Person person)
		{
			this.matchedTo = person;
		}

		public boolean CompareMen(Person married, Person single, Stack<Person> men)
		{
			int marriedPref = womanPreferences[married.GetId() - 1];
			int singlePref = womanPreferences[single.GetId() - 1];
			if(marriedPref <= singlePref)
			{
				return false;
			}
			else
			{
				this.matchedTo = single;
				single.SetMatched(this);
				married.SetMatched(null);
				men.add(married);
			}

			return true;
		}

		public String GetName()
		{
			if(name == null)
				return "null";
			return name;
		}

		public int GetId()
		{
			return id;
		}

		public void SetWomanPreferences(Integer[] womanPreferences)
		{
			this.womanPreferences = womanPreferences;
		}
	}