package gaa.authorship;

import gaa.authorship.dao.AuthorshipInfoDAO;
import gaa.authorship.dao.RepositoryDAO;
import gaa.authorship.model.AuthorshipInfo;
import gaa.authorship.model.File;
import gaa.authorship.model.Repository;
import gaa.authorship.model.RepositoryStatus;
import gaa.dao.LogCommitFileDAO;
import gaa.dao.ProjectInfoDAO;
import gaa.model.ProjectInfo;
import gaa.model.Status;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class DOACalculator {
	public static void main(String[] args) {
		DOARecalc();
//		ProjectInfoDAO piDAO = new ProjectInfoDAO();
//		List<Repository> repositories = getRepositories(piDAO.findNotFiltered());
	}
	
	public static void DOARecalc() {
		AuthorshipInfoDAO aiDAO = new AuthorshipInfoDAO();
		RepositoryDAO repDAO = new RepositoryDAO();
		for (Repository rep : repDAO.findAll()) {
			if (rep.getStatus() == RepositoryStatus.RECALC) {
				System.out.format("%s (%s): Recalculating DOA...\n",
						rep.getFullName(), new Date());
				for (File file : rep.getFiles()) {
					file.setBestAuthorshipInfo(null);
					file.setBestAuthorshipInfoMult(null);
					file.setBestAuthorshipAddDeliveries(null);
					for (AuthorshipInfo authorshipInfo : file.getAuthorshipInfos()) {
						authorshipInfo.updateDOA();
						
					}
				}
				System.out.format("%s (%s): Updating DOA...\n",
						rep.getFullName(), new Date());
				rep.setStatus(RepositoryStatus.DOA_CALCULATED); 
				repDAO.update(rep);
			}
			else{
				System.out.format("%s (%s)\n",
						rep.getFullName(), rep.getStatus());
			}
		}
		
		
	}

	private static List<Repository> getRepositories(List<ProjectInfo> projects) {
		List<Repository> repositories = new ArrayList<>();
		RepositoryDAO reDAO = new RepositoryDAO();
		Set<String> repositoriesPersisted = new HashSet<String>(reDAO.getAllRepositoryNames());
		for (ProjectInfo projectInfo : projects) {
//			if (!repositoriesPersisted.contains(projectInfo.getFullName())&& projectInfo.getFullName().equalsIgnoreCase("django/django")){
			if (!repositoriesPersisted.contains(projectInfo.getFullName())){
//			if (projectInfo.getFullName().equalsIgnoreCase("Homebrew/homebrew")){
			System.out.format("%s (%s): Extracting authorship information...\n",
					projectInfo.getFullName(), new Date());
			Repository repo = new Repository(projectInfo.getFullName());
			repo.setFiles(getFiles(repo));
			System.out.format("%s (%s): Persisting authorship information...\n",
					projectInfo.getFullName(), new Date());
			
			try{
				repo.setStatus(RepositoryStatus.DOA_CALCULATED);
				reDAO.persist(repo);
			}
			catch(Exception e){
				System.err.println("Erro ao persistir projeto " + repo.getFullName() + "\n"+e.toString());
			} 
			finally{
				reDAO.clear();
			}
//			printRepository(repo);
			repositoriesPersisted.add(repo.getFullName());
			}
			else
				System.out.println(projectInfo.getFullName() + " already analysed!");
		}
		
		
		
		return repositories;
	}

	

	private static List<File> getFiles(Repository repository) {
		LogCommitFileDAO lcfDAO = new LogCommitFileDAO();
		List<File> files = new ArrayList<>();
		List<Object[]> allFilesObjectInfo = lcfDAO.getLogCommitFileInfoForAllFiles(repository.getFullName());
		System.out.format("%s (%s): All %d filesInfo loaded ...\n",
				repository.getFullName(), new Date(), allFilesObjectInfo.size());
		Map<String,List<Object[]>> mapFiles = getMapFiles(allFilesObjectInfo);
		for (Entry<String, List<Object[]>> entry : mapFiles.entrySet()) {
			File file = new File(entry.getKey());
			setFileHistory(file, repository, entry.getValue());
			files.add(file);
		}
		return files;
	}

	private static Map<String, List<Object[]>> getMapFiles(
			List<Object[]> allFilesObjectInfo) {
		Map<String, List<Object[]>> map = new HashMap<String, List<Object[]>>();
		for (Object[] objects : allFilesObjectInfo) {
			//fi.path, ci.name, ci.email, lcfi.oldfilename, lcfi.newfilename, lcfi.status, lcfi.id, userName
			String fileName = objects[0].toString();
			if (!map.containsKey(fileName))
					map.put(fileName, new ArrayList<Object[]>());
			Object[] auxObjects = {objects[1], objects[2], objects[3], objects[4], objects[5], objects[6], objects[7]};
			map.get(fileName).add(auxObjects);
			
		}
		return map;
	}

	private static void setFileHistory(File file, Repository repository, List<Object[]> values) {
		List<Object[]> logFilesObjectInfo = getExpendedLogFiles(values, repository, new LogCommitFileDAO(), file);
		String firstAuthor = null;
		for (Object[] objects : logFilesObjectInfo) {
			//ci.name, ci.email, lcfi.oldfilename, lcfi.newfilename, lcfi.status, lcfi.id, username
			AuthorshipInfo authorshipInfo = repository.getAuthorshipInfo((String)objects[0], (String)objects[1], (String)objects[6],  file);
			Status status = Status.getStatus((String)objects[4]);
			
			if (status == Status.ADDED){
				if (firstAuthor == null){ //FIRST ADD
					firstAuthor = authorshipInfo.getDeveloper().getUserName();
					authorshipInfo.setAsFirstAuthor();
				}
				else if (!authorshipInfo.isFirstAuthor()){ //New ADD made by a different developer of the first add
					System.err.format("New add;%s;%s;%s;%s\n", repository.getFullName(), file.getPath(), firstAuthor, authorshipInfo.getDeveloper().getUserName());
					authorshipInfo.setAsSecondaryAuthor();
					authorshipInfo.addNewAddDelivery();
				}
				else{ //Treat as delivery if the extra add was made by the first author 
					authorshipInfo.addNewAddDelivery();
				}
					
				
			}
			else if (status == Status.MODIFIED){
				authorshipInfo.addNewDelivery();
				//file.addNewChange();					
			}
			else if (status == Status.RENAMED_TREATED){
				// Considering a rename as a new delivery
				authorshipInfo.addNewDelivery();
				//file.addNewChange();		
			}
			else System.err.println("Invalid Status: "+ status);
		}
		
		double bestDoaValue = 0;
		for (AuthorshipInfo authorshipInfo : file.getAuthorshipInfos()) {
			double authorshipDoa = authorshipInfo.getDOA();
			if (authorshipDoa > bestDoaValue){
				bestDoaValue = authorshipDoa;
				file.setBestAuthorshipInfo(authorshipInfo);
			}	
		}
		double bestDoaValueMult = 0;
		for (AuthorshipInfo authorshipInfo : file.getAuthorshipInfos()) {
			double authorshipDoaMult = authorshipInfo.getDoaMultAuthor();
			if (authorshipDoaMult > bestDoaValueMult){
				bestDoaValueMult = authorshipDoaMult;
				file.setBestAuthorshipInfoMult(authorshipInfo);
			}	
		}
		double bestDoaValueAddDeliveries = 0;
		for (AuthorshipInfo authorshipInfo : file.getAuthorshipInfos()) {
			double authorshipDoaAddDeliveries = authorshipInfo.getDoaAddDeliveries();
			if (authorshipDoaAddDeliveries > bestDoaValueAddDeliveries){
				bestDoaValueAddDeliveries = authorshipDoaAddDeliveries;
				file.setBestAuthorshipAddDeliveries(authorshipInfo);
			}	
		}
		
		
	}

//	private static void setFileHistory(File file, Repository repository) {
//		LogCommitFileDAO lcfDAO = new LogCommitFileDAO();
//		List<Object[]> logFilesObjectInfo = getExpendedLogFiles(lcfDAO.getLogCommitFileInfoOrderByDate(repository.getFullName(), file.getPath()),repository, lcfDAO, file);
//		String firstAuthor = null;
//		for (Object[] objects : logFilesObjectInfo) {
//			//ci.name, ci.email, lcfi.oldfilename, lcfi.newfilename, lcfi.status, lcfi.id
//			AuthorshipInfo authorshipInfo = repository.getAuthorshipInfo((String)objects[0], (String)objects[1], file);
//			Status status = Status.getStatus((String)objects[4]);
//			
//			if (status == Status.ADDED){
//				if (firstAuthor == null){
//					firstAuthor = authorshipInfo.getDeveloper().getUserName();
//				}
//				else
//					System.err.format("New add - %s - author: %s - newauthor: %s\n", file.getPath(), firstAuthor, authorshipInfo.getDeveloper().getUserName());
//				authorshipInfo.setAsFirstAuthor();
//				
//			}
//			else if (status == Status.MODIFIED){
//				authorshipInfo.addNewDelivery();
//				file.addNewChange();					
//			}
//			else if (status == Status.RENAMED_TREATED){
//				// Considering a rename as a new delivery
//				authorshipInfo.addNewDelivery();
//				file.addNewChange();				
//				
////				File oldFile = new File((String)objects[2]);
////				setFileHistory(oldFile, repository);
////				renamesBuffer.add(oldFile);
//			}
//			else System.err.println("Invalid Status: "+ status);
//		}
//		
//		double bestDoaValue = 0;
//		for (AuthorshipInfo authorshipInfo : file.getAuthorshipInfos()) {
//			double auhtorshipDoa = authorshipInfo.getDOA();
//			if (auhtorshipDoa > bestDoaValue){
//				bestDoaValue = auhtorshipDoa;
//				file.setBestAuthorshipInfo(authorshipInfo);
//			}	
//		}
//		
//		
//	}

	private static List<Object[]> getExpendedLogFiles(
			List<Object[]> logCommitFiles, Repository repository, LogCommitFileDAO lcfDAO, File file) {
		
 		Map<String, Object[]> map =  new HashMap<String, Object[]>();

		for (Object[] objects : logCommitFiles)
			map.put(objects[5].toString(), objects);
 		
 		while (hasRenamed(map.values())) {
 			List<Object[]> tempObjects = new ArrayList<Object[]>(map.values());
			for (Object[] objects : tempObjects) {
				
				Status status = Status.getStatus((String) objects[4]);
				if (status == Status.RENAMED) {
					List<Object[]> newList = lcfDAO.getLogCommitFileInfoOrderByDate(
							repository.getFullName(), (String) objects[2]);
					for (Object[] newObjects : newList) {
						if (!map.containsKey(newObjects[5].toString()))
							map.put(newObjects[5].toString(), newObjects);
					}
					objects[4] = Status.RENAMED_TREATED.toString();
				}
			}
		}
		return new ArrayList<Object[]>(map.values());
	}

	private static boolean hasRenamed(Collection<Object[]> values) {
		for (Object[] objects : values) {
			Status status = Status.getStatus((String) objects[4]);
			if (status== Status.RENAMED)
				return true;
		}
		return false;
	}
	
	private static void printRepository(Repository repo) {
		System.out.println("Repository: "+repo.getFullName());
		for (File file : repo.getFiles()) {
			printFile(file);
		}
		
	}

	private static void printFile(File file) {
		System.out.println("--File: "+file.getPath());
		Collections.sort(file.getAuthorshipInfos());
		Collections.reverse(file.getAuthorshipInfos());
		for (AuthorshipInfo authorshioinfo : file.getAuthorshipInfos()) {
			printAuthorshipInfo(authorshioinfo);
		}
		
	}

	private static void printAuthorshipInfo(AuthorshipInfo authorshioinfo) {
		System.out.format("---- %s: %b - %d - %d - (%f)\n", 
				authorshioinfo.getDeveloper().getUserName(),
				authorshioinfo.isFirstAuthor(), 
				authorshioinfo.getnDeliveries(),
				authorshioinfo.getnAcceptances(),
				authorshioinfo.getDOA());
		
	}
	
	
}
