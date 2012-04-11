package net.rageland.ragemod.text;

import java.util.HashMap;

public class WordGen {
	protected HashMap<Integer,String> Mid;
	protected HashMap<Integer,String> End;
	protected HashMap<Integer,String> Beg;
	
	public WordGen() {
		 Mid = new HashMap<Integer,String>();
		 End = new HashMap<Integer,String>();
		 Beg = new HashMap<Integer,String>();
	}
	
	public String gen(int length){
		String temp="";
		String word="";
		String end="";
		int count;
		while (temp == ""|| temp == null){
			temp = this.Beg.get((int)Math.floor((Math.random() * 50)));
		}
		word= word+temp;
		count= word.length();
		temp = "";
		while (temp == ""|| temp == null){
			temp = this.End.get((int)Math.floor((Math.random() * 50)));
		}
		end= temp;
		count=count+end.length();
		temp="";
		while (count < length){
			while (temp == ""|| temp == null){
				temp = this.Mid.get((int)Math.floor((Math.random() * 50)));
			}
			if (temp != null){
			if (temp.length()+count == length){
				word= word+temp+end;
				return word;
			}
			if (temp.length()+count < length){
				word= word+temp;
				count= count + temp.length();
				temp="";
			}
			if (temp.length()+count > length){
				temp="";
			}	
			}
		}
		return word+end;
	}
}
