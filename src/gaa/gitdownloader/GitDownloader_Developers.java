package gaa.gitdownloader;

import gaa.dao.GitHubDeveloperDAO;
import gaa.dao.ProjectDevelopersDAO;
import gaa.dao.ProjectInfoDAO;
import gaa.model.GitHubDeveloper;
import gaa.model.ProjectDevelopers;
import gaa.model.ProjectInfo;
import gaa.model.ProjectStatus;
import gaa.util.github.MyGitHubClient;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.eclipse.egit.github.core.Commit;
import org.eclipse.egit.github.core.CommitUser;
import org.eclipse.egit.github.core.Repository;
import org.eclipse.egit.github.core.RepositoryCommit;
import org.eclipse.egit.github.core.User;
import org.eclipse.egit.github.core.client.GitHubClient;
import org.eclipse.egit.github.core.service.CommitService;
import org.eclipse.egit.github.core.service.RepositoryService;

import com.google.common.io.LineReader;

public class GitDownloader_Developers {

	public static void main(String[] args) throws IOException {
		String tokensPath = "tokens.info";
//		String[] tokens = new String[] {"233d0f25b3fa8f0cca3a0e9e63e2487c47d19030", "1dd2ec420df935e917faa9c66538dd9afe824744", "e39dedaf4d899d6c145972ae7d71d22aa0bb9928"};
		
		
		
		if (args.length>0)
			tokensPath = args[0];
		String[] tokens = getTokens(tokensPath);
		
		
		GitHubClient client = new MyGitHubClient(new HashSet<String>(Arrays.asList(tokens)));
		RepositoryService service = new RepositoryService(client);
		
		GitHubDeveloperDAO gitHubDevelopersDao = new GitHubDeveloperDAO();
		List<GitHubDeveloper> devs = new ArrayList<GitHubDeveloper>();
		
		devs = gitHubDevelopersDao.findAll(null);
		if (devs!=null && devs.size()>0)
			GitHubDeveloper.initiateGitHubDeveloper(devs);
		
		
		ProjectInfoDAO projectDAO = new ProjectInfoDAO();
		List<ProjectInfo> projects = projectDAO.findAll(null);
		ProjectDevelopersDAO projectDevsDAO = new ProjectDevelopersDAO(); 
		
		String currentProject = "";
		try {
			for (ProjectInfo project : projects){
				if (project.getStatus() == ProjectStatus.GETINFO) {
					currentProject = project.getFullName();
					ProjectDevelopers projectDevs = new ProjectDevelopers(currentProject);
					System.out.println(currentProject + " - Analyzing ...");
					Repository repo = service.getRepository(project.getOwner(),
							project.getName());
					project.setStatus(ProjectStatus.ANALYZING);
					projectDAO.update(project);
					CommitService commitService = new CommitService(client);
					List<RepositoryCommit> commits = commitService.getCommits(repo);
					int nCommits1=0;
					int nCommits2=0;
					try {
						for (RepositoryCommit repoCommit : commits) {
							Commit commit = repoCommit.getCommit();

							if (commit != null) {
								User author = repoCommit.getAuthor();
								User committer = repoCommit.getCommitter();
								if (author!= null||committer!=null)
									nCommits1++;
								if (author!= null)
									nCommits2++;
								addGitHubDeveloper(
										author,
										commit.getAuthor(), projectDevs);
								addGitHubDeveloper(
										committer,
										commit.getCommitter(), projectDevs);

							}

						}
						System.out.println("**"+currentProject+";"+commits.size()+";"+nCommits1+";"+nCommits2+";"+projectDevs.getGitHubDevs().size()+";"+projectDevs.getNotGitHubDevs().size());
						projectDevs.setnCommits(commits.size());
						projectDevs.setnCommitsGitHubAuthorOrCommitter(nCommits1);
						projectDevs.setnCommitsGitHubAuthor(nCommits2);
						
						System.out.println("Persisting GitHub developers...");
						gitHubDevelopersDao.persistOrUpdateAll();
						project.setStatus(ProjectStatus.RECALC);
						projectDAO.update(project);
						
						projectDevsDAO.persist(projectDevs);
						
							
					} catch (Exception e) {
						project.setStatus(ProjectStatus.ERROR);
						project.setErrorMsg("Error in GitHub developers collector:  "+e.getMessage());
						projectDAO.update(project);
					
						System.err.println(e.getMessage());
					}
				}

			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private static String[] getTokens(String tokensPath) throws IOException {
		List<String> tokens = new ArrayList<String>() ;
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(tokensPath), "UTF8"));
			LineReader lineReader = new LineReader(br);
			String sCurrentLine;
			while ((sCurrentLine = lineReader.readLine()) != null) {
				tokens.add(sCurrentLine);
			}
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return (String[])tokens.toArray(new String[0]);
	}

	public static GitHubDeveloper addGitHubDeveloper(User user,
			CommitUser commitUser, ProjectDevelopers projectDevs) {
		String userString = GitHubDeveloper.createUserString(commitUser.getName(), commitUser.getEmail());
		Map<Integer, GitHubDeveloper> gitHubDevs = GitHubDeveloper.getGitHubDevs();
		if(user!=null&&user.getLogin()!=null){
			if (!gitHubDevs.containsKey(user.getId()))
				gitHubDevs.put(user.getId(), new GitHubDeveloper(user));
			GitHubDeveloper gitHubDev = gitHubDevs.get(user.getId());
			if (!gitHubDev.getPairsNameEmail().contains(userString)){
				gitHubDev.getPairsNameEmail().add(GitHubDeveloper.createUserString(commitUser.getName(), commitUser.getEmail()));
				gitHubDev.setUpdated(true);
			}
			projectDevs.addGitHubDevs(user.getLogin());
			return gitHubDev;
		}
		else{
			projectDevs.addNotGitHubDevs(userString);
		}
		return null;
	}
}
