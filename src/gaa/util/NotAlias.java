package gaa.util;

import gaa.model.LogCommitInfo;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class NotAlias {
	public String repository;
	public String dev1;
	public String dev2;
	
	public NotAlias(String repository, String dev1, String dev2) {
		super();
		this.repository = repository;
		this.dev1 = dev1;
		this.dev2 = dev2;
	}
	
	public static boolean isAlias(String repository, String dev1, String dev2){
		if (notAliases == null)
			try {
				notAliases = readFile("notalias.txt");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		NotAlias newNot = new NotAlias(repository, dev1, dev2);
		for (int i = 0; i < notAliases.length; i++) {
			if (notAliases[i].equals(newNot))
				return false;
		}
		return true;
	}
	@Override
	public boolean equals(Object obj) {
		NotAlias other = (NotAlias)obj;
		if(this.repository.equals(other.repository) && this.dev1.equals(other.dev1) && this.dev2.equals(other.dev2))
			return true;
		return false;
	}
	
	private static NotAlias[] readFile(String fileName) throws IOException{
		List<NotAlias> notAliases =  new ArrayList<NotAlias>();
		BufferedReader br = new BufferedReader(new FileReader(fileName));
		CRLFLineReader lineReader = new CRLFLineReader(br);
		String sCurrentLine;
		String[] values;
		int countcfs = 0;
		while ((sCurrentLine = lineReader.readLine()) != null) {
			values = sCurrentLine.split(";");
			if (values.length<3)
				System.err.println("Erro na linha " + countcfs);
			String rep = values[0];
			String dev1 = values[1];
			String dev2 = values[2];
			notAliases.add(new NotAlias(rep, dev1, dev2));
			countcfs++;
		}
		return notAliases.toArray(new NotAlias[0]);
	}
	
	private static NotAlias[] notAliases = null;
//		{new NotAlias("rails/rails","Nick", "rick"), new NotAlias("rails/rails","Nick", "Nico"),
//		new NotAlias("ruby/ruby","kou", "knu"), new NotAlias("ruby/ruby","kou", "ko1"),new NotAlias("ruby/ruby","nahi", "nari"),
//		new NotAlias("ruby/ruby","eban", "evan")};  
}