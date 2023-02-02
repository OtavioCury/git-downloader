package gaa.filter.filefilter;

import gaa.dao.NewFileInfoDAO;
import gaa.model.FileInfo;

import java.util.List;

public abstract class FileFilter {
	List<FileInfo> files;
	String filterStamp;
	NewFileInfoDAO fiDAO;
	public FileFilter(String filterStamp) {
		this.filterStamp = filterStamp;
		fiDAO =  new NewFileInfoDAO();
	}
	
	public abstract int filterAndPersistByLanguage(String language, String pattern);
	
	public void clean(){
		fiDAO.cleanFilter();
	}
	
	
}
