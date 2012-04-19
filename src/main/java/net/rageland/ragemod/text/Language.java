package net.rageland.ragemod.text;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.rageland.ragemod.text.languages.WordGenCreepTongue;

@SuppressWarnings("unused")
public class Language 
{
	private String name;
	private HashMap<String,String> bdEnLa = new HashMap<String,String>();
	private HashMap<String,String> bdLaEn = new HashMap<String,String>();
	private HashMap<String,String> tdEnLa = new HashMap<String,String>();
	private HashMap<String,String> tdLaEn = new HashMap<String,String>();
	private WordGen Gen;
	
	public Language(String name,WordGen gen)
	{
		// Temp - set up sample dictionary
		this.name = name;
		this.Gen=gen;
		if (this.name=="CreepTongue"){
			this.Gen= new WordGenCreepTongue();
		}
		
		//load Data
	}
	
	public String getName(){
		return this.name;
	}
	
	// Add a new word to the dictionary
	public void addWord(String word)
	{
		String temp= this.Gen.gen(word.length()+1);
		this.tdEnLa.put(word,temp );
		this.tdLaEn.put(temp,word );
	}
	
	public String createWord(String word)
	{
		String temp= this.Gen.gen(word.length()+1);
		this.tdEnLa.put(word,temp );
		this.tdLaEn.put(temp,word );
		return temp;
	}
	
	public void addWord(String word, String meaning)
	{
		this.bdEnLa.put(word,meaning );
		this.bdLaEn.put(meaning,word );	
	}
	
	// Returns a list of partially and completely translated version of the source string
	public String translateLaEn(String source, int Skill)
	{
		if (Skill == 0){
			return source; // dont wanna do what's not needed
		}
		String result="";
		String[] split = source.split(" ");
		int total = split.length;
		for (int i=0;i<total;i++){
			result= result+" "+this.translateWordLaEn(split[i], Skill);
		}
		return result;	
	}
	
	private String translateWordLaEn(String word, int Skill){
		String result = word;
		if (((int)Math.random()*100) <= Skill){
			if (this.bdLaEn.containsKey(word)){
				result= this.bdLaEn.get(word);
			}else{
				if (this.tdLaEn.containsKey(word)){
					result= this.tdLaEn.get(word);
				}
			}
		}
		return result;
	}
	
	public String translateEnLa(String source){
		String result = "";
		String[] split = source.split(" ");
		int total = split.length;
		for (int i=0;i<total;i++){
			if (this.bdEnLa.containsKey(split[i])){
				result= result+" "+this.bdEnLa.get(split[i]);
			}else{
				if (this.tdEnLa.containsKey(split[i])){
					result= result+" "+this.tdEnLa.get(split[i]);
				}else{
					result= result+" "+this.createWord(split[i]);
				}
			}
		}
		return result;
	}	
	
	
}
