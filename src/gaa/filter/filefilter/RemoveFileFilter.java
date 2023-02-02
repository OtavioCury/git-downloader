package gaa.filter.filefilter;

import gaa.model.FileInfo;
import gaa.model.ProjectInfo;

import java.util.Date;
import java.util.List;

public class RemoveFileFilter extends FileFilter{
	
	
	private String pattern;
	public RemoveFileFilter() {
		super("#Remove");
		// TODO Auto-generated constructor stub
	}

	
	public int filterAndPersistAllLanguages(String pattern) {
		String localFilterStamp = filterStamp+"(all): "+pattern+"#";
		
		String whereClauses = " fi.path LIKE \'" + pattern + "\'";
		
		int nRows = fiDAO.filterAndUpdateFilesInfo(whereClauses, localFilterStamp);
		if (nRows>0)
			System.out.println(new Date() + " - " + pattern +" - Updated "+ nRows);
		else
			System.err.println(new Date() + " - " + pattern +" - Updated "+ nRows);
		return nRows;
	}
	
	@Override
	public int filterAndPersistByLanguage(String language, String pattern) {
		String localFilterStamp = filterStamp+"("+language+"): "+pattern+"#";
		
		String whereClauses = "";
		if (language != null && !language.isEmpty()&&!language.equalsIgnoreCase("all")){
			whereClauses += " pi.language = \'" + language + "\'";
			whereClauses += " AND fi.path LIKE \'" + pattern + "\'";
		}
		else
			whereClauses += " fi.path LIKE \'" + pattern + "\'";
		
		int nRows = fiDAO.filterAndUpdateFilesInfoByLanguage(whereClauses, localFilterStamp);
		if (nRows>0)
			System.out.println(new Date() + " - " + pattern +" - Updated "+ nRows);
		else
			System.err.println(new Date() + " - " + pattern +" - Updated "+ nRows);
		return nRows;
	}
	
	public int filterAndPersistByProject(String projectName, String pattern) {
		String localFilterStamp = filterStamp+"("+projectName+"): "+pattern+"#";
		
		String whereClauses = " fi.repositoryname = \'" + projectName + "\'";
		whereClauses += " AND fi.path LIKE \'" + pattern + "\'";
		
		int nRows = fiDAO.filterAndUpdateFilesInfo(whereClauses, localFilterStamp);
		if (nRows>0)
			System.out.println(new Date() + " - " + pattern +" - Updated "+ nRows);
		else
			System.err.println(new Date() + " - " + pattern +" - Updated "+ nRows);
		return nRows;
	}
	
	public int removeEspecifcfilterAndPersistByProject(String projectName, String pattern) {
		String localFilterStamp = filterStamp+"("+projectName+"): "+pattern+"#";
		
		String whereClauses = " fi.repositoryname = \'" + projectName + "\'";
		whereClauses += " AND fi.path LIKE \'" + pattern + "\'";
		
		int nRows = fiDAO.removeFilterAndUpdateFilesInfo(whereClauses, localFilterStamp);
		if (nRows>0)
			System.out.println(new Date() + " - " + pattern +" - Updated "+ nRows);
		else
			System.err.println(new Date() + " - " + pattern +" - Updated "+ nRows);
		return nRows;
	}
	
		
	public String getPattern() {
		return pattern;
	}
	public void setPattern(String pattern) {
		this.pattern = pattern;
	}


}
