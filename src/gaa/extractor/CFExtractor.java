package gaa.extractor;

import java.util.Date;
import java.util.List;

import gaa.dao.ProjectInfoDAO;
import gaa.gitdownloader.DownloaderUtil;
import gaa.model.ProjectInfo;
import gaa.model.ProjectStatus;

public class CFExtractor {
	public static void commitsExtractor() {
		String logFilesPath = null;
		//		if (args.length>0)
		//			DownloaderUtil.PATH = args[0];
		//		if (args.length>1)
		logFilesPath = "/home/lost/arquivosLogsCommits/";
		List<ProjectInfo> projectsInfo =  DownloaderUtil.getProjects();
		ProjectInfoDAO projectDAO = new ProjectInfoDAO();
		for (ProjectInfo projectInfo : projectsInfo) {
			//			if (especificProject !=null && projectInfo.getFullName().equalsIgnoreCase(especificProject)) {
			if (projectInfo.getStatus() == ProjectStatus.DOWNLOADED) {				
				try {
					System.out.println(new Date());
					projectInfo.setStatus(ProjectStatus.ANALYZING);
					projectDAO.update(projectInfo);
					System.out.println(projectInfo + ": Persisting CommitFiles...");
					DownloaderUtil.getAndPersistCommitsUsingLogFiles(logFilesPath, projectInfo);
					System.out.println(projectInfo + ": CommitFiles were persisted");
					projectInfo.setStatus(ProjectStatus.ANALYZED);
					projectDAO.update(projectInfo);
					System.out.println(new Date());
				} catch (Exception e) {
					projectInfo.setErrorMsg("CFExtractor error: "+e.toString());
					projectInfo.setStatus(ProjectStatus.ERROR);
					projectDAO.update(projectInfo);
				}
			}
		}


		//		CommitFileDAO cfDAO = new CommitFileDAO();
		//		for (ProjectInfo projectInfo : projectsInfo) {
		//			for (CommitFile cFile : map.get(projectInfo.getName())) {
		//				cFile.setProjectName(projectInfo.getName());
		//				cfDAO.merge(cFile);				
		//			}
		//		}
	}
	//	static void printCommit(Map<String, List<CommitFile>> map ){
	//		for (Entry<String, List<CommitFile>> entry : map.entrySet()) {
	//			for (CommitFile cfiles : entry.getValue()) {
	//				System.out.println(cfiles);
	//			}
	//		}
	//	}
}
