package gaa.gitdownloader;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonValue;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.lib.RepositoryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jcabi.github.Github;
import com.jcabi.http.Request;
import com.jcabi.http.response.JsonResponse;

import gaa.model.FileInfo;
import gaa.model.LanguageInfo;
import gaa.model.ProjectInfo;


public class GitServiceImpl implements GitService {
	Github github;
	Logger logger = LoggerFactory.getLogger(GitServiceImpl.class);
	
	public GitServiceImpl(Github github) {
		this.github = github;
	}
	public GitServiceImpl() {
	}
	
	public Repository cloneIfNotExists(ProjectInfo projectInfo) throws Exception {
		String projectPath = DownloaderUtil.PATH+projectInfo.getFullName();
		String cloneUrl = projectInfo.getCloneUrl();
		String branch = projectInfo.getDefault_branch();
		
		File folder = new File(projectPath);
		Git git;
		if (folder.exists()) {
			logger.info("Project {} already cloned", cloneUrl);

			RepositoryBuilder builder = new RepositoryBuilder();
			Repository repository = builder
					.setGitDir(new File(folder, ".git"))
					.readEnvironment()
					.findGitDir()
					.build();
			git = new Git(repository);

			git.checkout()
			.setStartPoint(Constants.HEAD)
			.setName(branch)
			.call();
			projectInfo.setUpdated(false);
		} else {
			logger.info("Cloning {} ...", cloneUrl);
			git = Git.cloneRepository()
					.setDirectory(folder)
					.setURI(cloneUrl)
					.setCloneAllBranches(true)
					.call();
			logger.info("Done cloning {}", cloneUrl);
			projectInfo.setUpdated(true);
		}
		return git.getRepository();
	}
	
	public Repository getClonedRepository(String projectPath, String branch) throws Exception {
		File folder = new File(projectPath);
		Git git;
		if (folder.exists()) {
			RepositoryBuilder builder = new RepositoryBuilder();
			Repository repository = builder
					.setGitDir(new File(folder, ".git"))
					.readEnvironment()
					.findGitDir()
					.build();
			git = new Git(repository);

//			git.checkout()
//			.setStartPoint(Constants.HEAD)
//			.setName(branch)
//			.call();
		} else {
			System.err.println("Repositorio nao clonado: "  + branch );
			return null;
		}
		return git.getRepository();
	}

	public FileInfoAux getRepositoriesFiles(ProjectInfo project) throws IOException {
			Request request;
			
				request = github.entry()
						.uri().path("/repos/"+ project.getFullName()+"/git/trees/"+project.getDefault_branch())
						.queryParam("recursive", "1")
						.back()
						.method(Request.GET);
	//			JsonArray items = (JsonArray) request.fetch().as(JsonResponse.class).json().readObject();
				JsonArray items = request.fetch().as(JsonResponse.class).json().readObject().getJsonArray("tree");
				List<FileInfo> files = new ArrayList<FileInfo>();
				int countFiles = 0;
				int countDirectories = 0;
				int countAll = 0;
				if (items == null)
					System.err.println("\n\n" +project + "\n\n" );
				else{
					for (JsonValue item : items) {
						JsonObject repoData = (JsonObject) item;
						FileInfo file = new FileInfo(project);
						file.setPath(repoData.getString("path"));
						file.setMode(repoData.getString("mode"));
						file.setType(repoData.getString("type"));
						file.setSha(repoData.getString("sha"));
						if(!repoData.containsKey("size"))
							file.setSize(0);
						else
							file.setSize(repoData.getInt("size"));
						files.add(file);
						if (file.getType().equalsIgnoreCase("blob"))
							countFiles++;
						else if (file.getType().equalsIgnoreCase("tree"))
							countDirectories++;
						countAll++;
					}
//					System.out.format("%s - Files=%d, Directories=%d, All=%d\n",project.getFullName(), countFiles, countDirectories, countAll);
				}
				FileInfoAux fileInfo = new FileInfoAux(files, countFiles);
				return fileInfo;
	
		}

	public LanguageInfo getMainLanguage(List<LanguageInfo> languages) {
		LanguageInfo mainLanguage = null;
		long maxValue=0;
		for (LanguageInfo languageInfo : languages) {
			if (languageInfo.getSize()>maxValue){
				maxValue = languageInfo.getSize();
				mainLanguage = languageInfo;
			}
		}
		return mainLanguage;
	}

	public List<LanguageInfo> getRepositoriesLanguages(ProjectInfo project) throws IOException {
			Request request;
			
				request = github.entry()
						.uri().path("/repos/"+ project.getFullName()+"/languages")
						.back()
						.method(Request.GET);
	//			JsonArray items = (JsonArray) request.fetch().as(JsonResponse.class).json().readObject();
				JsonObject jsonObject = request.fetch().as(JsonResponse.class).json().readObject();
				List<LanguageInfo> languages = new ArrayList<LanguageInfo>();
				for (Entry<String, JsonValue> entry : jsonObject.entrySet()) {
					if (entry.getKey().equalsIgnoreCase("message")||entry.getKey().equalsIgnoreCase("documentation_url"))
						return new ArrayList<LanguageInfo>();
					languages.add(new LanguageInfo(entry.getKey(), Long.parseLong(entry.getValue().toString())));
				}
			
				return languages;
	
		}

	public List<ProjectInfo> searchRepositories(int numRepository, String query) throws IOException {
		Request request = github.entry()
				.uri().path("/search/repositories")
				//				.queryParam("q", "language:Java created:<=2014-06-01")
				.queryParam("q", query )
								.queryParam("sort", "stars")
								.queryParam("order", "desc")
								.queryParam("per_page", numRepository>100?"100":String.valueOf(numRepository))
				.back()
				.method(Request.GET);
	
		int page=1;
		List<ProjectInfo> projectsInfo = new ArrayList<ProjectInfo>();
		GitProjectFinder projectFinder = new GitProjectFinder();
		while (projectsInfo.size()<numRepository){
			request = github.entry()
					.uri().path("/search/repositories")
					//				.queryParam("q", "language:Java created:<=2014-06-01")
					.queryParam("q", query )
									.queryParam("sort", "stars")
									.queryParam("order", "desc")
									.queryParam("per_page", numRepository>100?"100":String.valueOf(numRepository))
									.queryParam("page", String.valueOf(page++))
					.back()
					.method(Request.GET);
			projectsInfo.addAll(projectFinder.findRepos(request, query));
		}
		return projectsInfo;
	}

}
