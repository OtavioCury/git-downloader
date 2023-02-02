package gaa.dao;

import java.time.Period;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityTransaction;
import javax.persistence.Query;

import gaa.filter.filefilter.FileType;
import gaa.model.FileInfo;
import gaa.model.ProjectInfo;

public class FileInfoDAO extends GenericDAO<FileInfo>{

	@Override
	public FileInfo find(Object o) {
		return this.em.find(FileInfo.class, o);
	}	

	@Override
	public boolean exist(FileInfo entity) {
		return this.find(entity.getId())!=null;
	}

//	public void update(FileInfo o){
//		FileInfo persistedProject = this.em.find(FileInfo.class, o.getId());
//		if (persistedProject != null){
//			persistedProject.setFiltered(o.getFiltered());
//			persistedProject.setFilterInfo(o.getFilterInfo());
//			persistedProject.setMode(o.getMode());
//			persistedProject.setPath(o.getPath());
//			persistedProject.setSha(o.getSha());
//			persistedProject.setSize(o.getSize());
//			persistedProject.setType(o.getType());
//			persistedProject.setLanguage(o.getLanguage());
//			super.merge(persistedProject);
//		}
//	}
	public List<String> getPathsOfNotFilteredProjectFiles(String projectName){
		String custom = "";
		     
		String hql = "SELECT fi.path FROM projectinfo_fileinfo pi_fi "
				+ "JOIN projectinfo pi ON pi_fi.projectinfo_fullname = pi.fullname "
				+ "JOIN fileinfo fi on pi_fi.files_id = fi.id "
				+ "WHERE pi.fullname = \'"+projectName+"\' AND fi.filtered = 'FALSE' "
				+ ";";
				
		Query q = em.createNativeQuery(hql);
		return q.getResultList();
			
	}
	
	public int filterAndUpdateFilesInfo(String whereClauses, String filterStamp){
		String custom = "";
		     
		String hql = "UPDATE  fileinfo AS fi "
				+ "SET filtered = \'TRUE\', filterinfo = \'"+ filterStamp +"\'  "
				+ "FROM projectinfo_fileinfo AS pi_fi, projectinfo AS pi    "
				+ "WHERE pi.filtered = \'FALSE\' AND fi.filtered = \'FALSE\' AND pi_fi.projectinfo_fullname = pi.fullname AND pi_fi.files_id = fi.id "
				+ whereClauses
				+ ";";
				
				
		Query q = em.createNativeQuery(hql);
		int rows =0 ;
		EntityTransaction tx = this.em.getTransaction();
		try {
			tx.begin();
			rows = q.executeUpdate();
			tx.commit();
		} catch (RuntimeException e) {
			if(tx != null && tx.isActive()) 
				tx.rollback();
			throw e;
		} 
		finally{
			this.em.clear();
		}
		return rows;
			
	}
	
	public int removeFilterAndUpdateFilesInfo(String whereClauses, String filterStamp){
		String custom = "";
		     
		String hql = "UPDATE  fileinfo AS fi "
				+ "SET filtered = \'FALSE\', filterinfo = \'\'  "
				+ "FROM projectinfo_fileinfo AS pi_fi, projectinfo AS pi    "
				+ "WHERE pi.filtered = \'FALSE\' AND fi.filtered = \'TRUE\' AND pi_fi.projectinfo_fullname = pi.fullname AND pi_fi.files_id = fi.id "
				+ whereClauses
				+ ";";
				
				
		Query q = em.createNativeQuery(hql);
		int rows =0 ;
		EntityTransaction tx = this.em.getTransaction();
		try {
			tx.begin();
			rows = q.executeUpdate();
			tx.commit();
		} catch (RuntimeException e) {
			if(tx != null && tx.isActive()) 
				tx.rollback();
			throw e;
		} 
		finally{
			this.em.clear();
		}
		return rows;
			
	}
	
	public int classifierAndUpdateFilesInfo(String whereClauses, FileType fileType){
		String custom = "";
		     
		String hql = "UPDATE  fileinfo AS fi "
				+ "SET kind = \'"+ fileType +"\'  "
				+ "FROM projectinfo_fileinfo AS pi_fi, projectinfo AS pi    "
				+ "WHERE pi.filtered = \'FALSE\' AND pi_fi.projectinfo_fullname = pi.fullname AND pi_fi.files_id = fi.id "
				+ whereClauses
				+ ";";
				
				
		Query q = em.createNativeQuery(hql);
		int rows =0 ;
		EntityTransaction tx = this.em.getTransaction();
		try {
			tx.begin();
			rows = q.executeUpdate();
			tx.commit();
		} catch (RuntimeException e) {
			if(tx != null && tx.isActive()) 
				tx.rollback();
			throw e;
		} 
		finally{
			this.em.clear();
		}
		return rows;
			
	}
	
	public int cleanFilter(){
			     
		String hql = "UPDATE  fileinfo "
				+ "SET filtered = \'FALSE', filterinfo = \'\';";
				
				
		Query q = em.createNativeQuery(hql);
		int rows =0 ;
		EntityTransaction tx = this.em.getTransaction();
		try {
			tx.begin();
			rows = q.executeUpdate();
			tx.commit();
		} catch (RuntimeException e) {
			if(tx != null && tx.isActive()) 
				tx.rollback();
			throw e;
		} 
		finally{
			this.em.clear();
		}
		return rows;
			
	}

	public int updateLanguageFileInfo(String projectName, String language, List<String> paths) {
		FileType fileType = FileType.getType(language);
		List<Query> queries = new ArrayList<Query>();
		for (String path : paths) {			
			if (path.contains("\'"))
				path = path.replace("'", "''");
			String hql = "UPDATE  fileinfo AS fi "
					+ "SET kind = \'"
					+ fileType
					+ "\'  , language = \'"
					+ language
					+ "\', filtered = \'FALSE\' "
					+ ", filterinfo = \'\' "
					+ "FROM projectinfo_fileinfo AS pi_fi, projectinfo AS pi    "
					+ "WHERE pi.fullname = \'" + projectName + "\' AND pi_fi.projectinfo_fullname = pi.fullname AND pi_fi.files_id = fi.id and fi.path =  \'"
					+ path + "\' " + ";";
			queries.add(em.createNativeQuery(hql));
		}
		int rows =0 ;
		EntityTransaction tx = this.em.getTransaction();
		try {
			tx.begin();
			for (Query query : queries) {
				rows += query.executeUpdate();
			}			
			tx.commit();
		} catch (RuntimeException e) {
			if(tx != null && tx.isActive()) 
				tx.rollback();
			throw e;
		} 
		finally{
			this.em.clear();
		}
		return rows;
		
	}
	

}
