package gaa.gitdownloader;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;

import com.jcabi.github.Github;
import com.jcabi.github.RtGithub;

import gaa.dao.ProjectInfoDAO;
import gaa.model.FileInfo;
import gaa.model.LanguageInfo;
import gaa.model.ProjectInfo;
import gaa.model.ProjectStatus;

public class Downloader {

	public static void main(String[] args) {
		Downloader downloader = new Downloader();
		String caminho = "/media/lost/e04b3034-2506-41c9-a1d4-e3d38fe04256/charles/projetos/";
		int numRepository = 600;
		try {
			System.out.println("=========== DOWNLOAD PROJETOS JAVA ==================");
			downloader.downloader("1", caminho, "language:java stars:>500", numRepository);
			System.out.println("=========== DOWNLOAD PROJETOS JAVASCRIPT ==================");
			downloader.downloader("1", caminho, "language:javascript stars:>500", numRepository);
			System.out.println("=========== DOWNLOAD PROJETOS PHP ==================");
			downloader.downloader("1", caminho, "language:php stars:>500", numRepository);
			//			System.out.println("=========== DOWNLOAD PROJETOS C++ ==================");
			//			downloader.downloader("1", "/home/lost/projetos/", "language:c++ stars:>1000", 1000);
			System.out.println("=========== DOWNLOAD PROJETOS RUBY ==================");
			downloader.downloader("1", caminho, "language:ruby stars:>500", numRepository);
			System.out.println("=========== DOWNLOAD PROJETOS PYTHON ==================");
			downloader.downloader("1", caminho, "language:python stars:>500", numRepository);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


	public void downloader(String op, String caminho, String query, int numRepository) throws IOException {
		DownloaderUtil.PATH = caminho;
		Github github = new RtGithub("asergprogram", "aserg.ufmg2009");
		List<ProjectInfo> projectsInfo = null;
		//	ProjectInfoDAO projectDAO = new ProjectInfoDAO();
		GitServiceImpl gitService = new GitServiceImpl(github);
		//	if (op.equals("0")||op.equals("1")){
		projectsInfo = gitService.searchRepositories(numRepository, query);
		//	DownloaderUtil.persistProjects(projectsInfo);
		UpdateRepositoriesInfoThread repo = new UpdateRepositoriesInfoThread(github, projectsInfo);
		repo.run();
		//		}
		//		else 
		//			projectsInfo = projectDAO.findAll(null); 


		for (ProjectInfo projectInfo : projectsInfo) {

			//			if (op.equals("3")){
			//				addProjectLanguages(gitService, projectInfo);
			//				projectDAO.update(projectInfo);
			//			}
			//			else if(op.equals("4")){
			//				addProjectFilesInfo(gitService, projectInfo);
			//				projectDAO.update(projectInfo);
			//			}
			//			else{
			if (projectInfo.getStatus() == ProjectStatus.NULL) {
				try {

					System.out.println("Clonando " + projectInfo.getFullName());
					Repository repository = gitService.cloneIfNotExists(projectInfo);
					//					//					System.out.println("Clonou");
					//					if (!op.equals("0") && projectInfo.hasUpdated()) {
					//						Iterable<RevCommit> logs = new Git(repository).log()
					//								.call();
					//						int count = 0;
					//						Date lastCommitDate = null;
					//						for (RevCommit rev : logs) {
					//							System.out.println("Commit: " + rev /* + ", name: " + rev.getName() + ", id: " + rev.getId().getName() */);
					//							count++;
					//							if (lastCommitDate == null
					//									|| lastCommitDate.compareTo(rev.getCommitterIdent().getWhen()) < 0)
					//								lastCommitDate = rev.getCommitterIdent()
					//								.getWhen();
					//
					//						}
					//						//						System.out.println("Had " + count
					//						//								+ " commits overall in repository "
					//						//								+ lastCommitDate);
					//						projectInfo.setCommits_count(count);
					//						projectInfo.setLastCommit(lastCommitDate);
					//					}
					//					projectInfo.setStatus(ProjectStatus.DOWNLOADED);
					//					projectDAO.update(projectInfo);
				} catch (Exception e) {
					e.printStackTrace();
					//					projectInfo.setErrorMsg("GitDownloader error: "
					//							+ e.toString());
					//					projectInfo.setStatus(ProjectStatus.ERROR);
					//					projectDAO.update(projectInfo);
				}
			}
		}
		//}
	}

	private static void addProjectFilesInfo(GitServiceImpl gitService, ProjectInfo projectInfo)
			throws IOException {
		FileInfoAux fileAux =  gitService.getRepositoriesFiles(projectInfo);
		List<FileInfo> files = fileAux.files;
		projectInfo.setFiles(files);
		projectInfo.setNumFiles(fileAux.numFiles);
	}


	private static void addProjectLanguages(GitServiceImpl gitService, ProjectInfo projectInfo)
			throws IOException {
		List<LanguageInfo> languages = gitService.getRepositoriesLanguages(projectInfo);
		projectInfo.setLanguages(languages);
		LanguageInfo mainLanguage = gitService.getMainLanguage(languages);
		projectInfo.setMainLanguage(mainLanguage!=null?mainLanguage.getLanguage():"");
	}
}
