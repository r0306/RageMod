package net.rageland.ragemod.language;

import java.util.AbstractCollection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.ChatColor;

public class Language 
{
	private String name;
	private ArrayList<ArrayList<String>> dictionary;	// First index is word size
	private Pattern puncPattern;
	
	public Language()
	{
		// Temp - set up sample dictionary
		name = "Test Language";
		puncPattern = Pattern.compile("([^\\.\\,\\!\\?\\;\\:]*)([\\.\\,\\!\\?\\;\\:]*)$");
		
		dictionary = new ArrayList<ArrayList<String>>();
		
		// 1 letter words
		dictionary.add(new ArrayList<String>());
		dictionary.get(0).add("a");
		dictionary.get(0).add("å");
		dictionary.get(0).add("o");
		dictionary.get(0).add("u");
		dictionary.get(0).add("y");
		
		// 2 letter words
		dictionary.add(new ArrayList<String>());
		dictionary.get(1).add("sa");
		dictionary.get(1).add("ze");
		dictionary.get(1).add("ea");
		dictionary.get(1).add("lo");
		dictionary.get(1).add("es");
		dictionary.get(1).add("ål");
		dictionary.get(1).add("su");
		dictionary.get(1).add("iå");
		dictionary.get(1).add("la");
		dictionary.get(1).add("lu");
		dictionary.get(1).add("se");
		dictionary.get(1).add("så");
		dictionary.get(1).add("lo");
		dictionary.get(1).add("za");
		dictionary.get(1).add("ze");
		dictionary.get(1).add("zo");
		dictionary.get(1).add("zu");
		dictionary.get(1).add("ix");
		dictionary.get(1).add("åx");
		dictionary.get(1).add("åz");
		
		// 3 letter words
		dictionary.add(new ArrayList<String>());
		dictionary.get(2).add("zal");
		dictionary.get(2).add("oss");
		dictionary.get(2).add("l'n");
		dictionary.get(2).add("ois");
		dictionary.get(2).add("xui");
		dictionary.get(2).add("ess");
		dictionary.get(2).add("oln");
		dictionary.get(2).add("l'a");
		dictionary.get(2).add("ool");
		dictionary.get(2).add("sar");
		dictionary.get(2).add("sao");
		dictionary.get(2).add("uss");
		dictionary.get(2).add("ssa");
		dictionary.get(2).add("sse");
		dictionary.get(2).add("szo");
		dictionary.get(2).add("sze");
		dictionary.get(2).add("l'e");
		dictionary.get(2).add("l'o");
		dictionary.get(2).add("sur");
		dictionary.get(2).add("ssu");
		
		// 4 letter words
		dictionary.add(new ArrayList<String>());
		dictionary.get(3).add("woln");
		dictionary.get(3).add("liss");
		dictionary.get(3).add("sall");
		dictionary.get(3).add("roi'");
		dictionary.get(3).add("sirn");
		dictionary.get(3).add("låza");
		dictionary.get(3).add("xorl");
		dictionary.get(3).add("aron");
		dictionary.get(3).add("ssor");
		dictionary.get(3).add("rusz");
		dictionary.get(3).add("luor");
		dictionary.get(3).add("asal");
		dictionary.get(3).add("oass");
		dictionary.get(3).add("zull");
		dictionary.get(3).add("rolu");
		dictionary.get(3).add("isså");
		
		// 5 letter words
		dictionary.add(new ArrayList<String>());
		dictionary.get(4).add("selen");
		dictionary.get(4).add("so'ss");
		dictionary.get(4).add("relii");
		dictionary.get(4).add("essaa");
		dictionary.get(4).add("lonix");
		dictionary.get(4).add("r'lån");
		dictionary.get(4).add("leess");
		dictionary.get(4).add("ossil");
		dictionary.get(4).add("rolaa");
		dictionary.get(4).add("senni");
		dictionary.get(4).add("innas");
		dictionary.get(4).add("lo'as");
		dictionary.get(4).add("zunnu");
		dictionary.get(4).add("leess");
		dictionary.get(4).add("saloo");
		dictionary.get(4).add("luoso");
		
		// 6 letter words
		dictionary.add(new ArrayList<String>());
		dictionary.get(5).add("xåluss");
		dictionary.get(5).add("soll'a");
		dictionary.get(5).add("oossan");
		dictionary.get(5).add("zur'la");
		dictionary.get(5).add("lassas");
		dictionary.get(5).add("rolnaa");
		dictionary.get(5).add("sårlen");
		dictionary.get(5).add("essiss");
		dictionary.get(5).add("roolas");
		dictionary.get(5).add("sennir");
		dictionary.get(5).add("seunal");
		dictionary.get(5).add("ussarr");
		
		// 7 letter words
		dictionary.add(new ArrayList<String>());
		dictionary.get(6).add("zal'ruo");
		dictionary.get(6).add("ullessa");
		dictionary.get(6).add("roolees");
		dictionary.get(6).add("xousseu");
		dictionary.get(6).add("olåssei");
		dictionary.get(6).add("ussisse");
		dictionary.get(6).add("iss'ero");
		dictionary.get(6).add("lels'så");
		dictionary.get(6).add("lo'losz");
		dictionary.get(6).add("zularrx");
		dictionary.get(6).add("azussos");
		dictionary.get(6).add("ossissu");
		
		// 8 letter words
		dictionary.add(new ArrayList<String>());
		dictionary.get(7).add("l'ssensx");
		dictionary.get(7).add("årossena");
		dictionary.get(7).add("xa'nzole");
		dictionary.get(7).add("sslinåln");
		dictionary.get(7).add("ralansee");
		dictionary.get(7).add("luossuss");
		dictionary.get(7).add("ssarr'le");
		dictionary.get(7).add("ossleran");
		
		// 9 letter words
		dictionary.add(new ArrayList<String>());
		dictionary.get(8).add("låzass'ro");
		dictionary.get(8).add("ossironsx");
		dictionary.get(8).add("le'eronis");
		dictionary.get(8).add("oosroliss");
		dictionary.get(8).add("enlarress");
		dictionary.get(8).add("sil'zeren");
		dictionary.get(8).add("orrilissu");
		dictionary.get(8).add("rissessle");
		
		// 10 letter words
		dictionary.add(new ArrayList<String>());
		dictionary.get(9).add("esszan'roå");
		dictionary.get(9).add("ullzuralan");
		dictionary.get(9).add("sollessiss");
		dictionary.get(9).add("ruo'zussox");
		
		// 11 letter words
		dictionary.add(new ArrayList<String>());
		dictionary.get(10).add("siolle'ånao");
		dictionary.get(10).add("roossenellå");
		dictionary.get(10).add("lelsa'ossil");
		dictionary.get(10).add("rolanssixia");
		
		// 12 letter words
		dictionary.add(new ArrayList<String>());
		dictionary.get(11).add("siolleo'ruos");
		dictionary.get(11).add("essentrolaax");
		dictionary.get(11).add("szorrållinez");
		dictionary.get(11).add("oln'ållessee");
		
	}
	
	// Returns a list of partially and completely translated version of the source string
	public ArrayList<String> translate(String source)
	{
		ArrayList<String> result = new ArrayList<String>();
		String[] split = source.split(" ");
		int total = split.length;
		int index, wordIndex;
		Random random = new Random();
		
		// Create an array of numbers that represents the words not translated yet
		ArrayList<Integer> toTranslate = new ArrayList<Integer>();
		for( int i = 0; i < total; i++)
			toTranslate.add(i);
		
		// Go through 4 passes to get 25%, 50%, 75%, and 100% translation
		for( int i = 1; i <= 4; i++ )
		{
			while( toTranslate.size() > (total * (1 - ((double)i / 4))) )
			{
				index = random.nextInt(toTranslate.size());
				wordIndex = toTranslate.remove(index);
				split[wordIndex] = translateWord(split[wordIndex], random);
			}
			result.add(join(split, " "));
		}
		
		return result;
		
	}
	
	private String translateWord(String word, Random random) 
	{
		Matcher matcher = puncPattern.matcher(word);

	    if( matcher.find() )
	    {
	    	word = matcher.group(1);	// Separate the word without the punctuation
	    }
	    
		// Find the length of the word at the specified index
		int wordLength = word.length();
		
		
		if( wordLength > 0 )
		{
			// Cut all word lengths down to 12
			if( wordLength > 12 )
				wordLength = 12;
			
			// Pull a random word of that length from the dictionary
			String newWord = dictionary.get(wordLength - 1).get(random.nextInt(dictionary.get(wordLength - 1).size()));
			
			// Test for capitalization
			if( word.substring(0, 1).equals(word.substring(0, 1).toUpperCase()) )
				newWord = newWord.substring(0, 1).toUpperCase() + newWord.substring(1);
			return newWord + matcher.group(2);		// Add the punctuation back	
		}
		else
			return "";
	}

	public static String join(String[] array, String delimiter) 
	{
		List<String> s = Arrays.asList(array);

	    if (s == null || s.isEmpty()) return "";
	    Iterator<String> iter = s.iterator();
	    StringBuilder builder = new StringBuilder(iter.next());
	    while( iter.hasNext() )
	    {
	        builder.append(delimiter).append(iter.next());
	    }
	    return builder.toString();
	}

	
	
	
	
	
	
	
	
	
}
