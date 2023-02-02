package gaa.gitdownloader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonValue;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jcabi.http.Request;
import com.jcabi.http.response.JsonResponse;

import gaa.model.ProjectInfo;

public class GitProjectFinder {

	Logger logger = LoggerFactory.getLogger(GitProjectFinder.class);
	//	public static void main(String[] args) throws IOException {
	//		new GitProjectFinder().findRepos();
	//	}
	public GitProjectFinder() {
		BasicConfigurator.configure();
		LogManager.getRootLogger().setLevel(Level.OFF);
	}

	public List<ProjectInfo> findRepos(Request request, String query) throws IOException {


		JsonArray items = request.fetch().as(JsonResponse.class).json().readObject().getJsonArray("items");

		List<ProjectInfo> projects = new ArrayList<ProjectInfo>();
		for (JsonValue item : items) {
			JsonObject repoData = (JsonObject) item;
			ProjectInfo p = new ProjectInfo();
			if (!repoData.isNull("full_name")){
				p.setFullName(repoData.getString("full_name"));
				p.setName(repoData.getString("name"));
				p.setOwner(p.getFullName().split("/")[0]);
			}
			p.setSize(repoData.getInt("size"));
			p.setFork(repoData.getBoolean("fork"));
			p.setStargazers_count(repoData.getInt("stargazers_count"));
			p.setWatchers_count(repoData.getInt("watchers_count"));
			p.setForks_count(repoData.getInt("forks_count"));
			if (!repoData.isNull("default_branch"))
				p.setDefault_branch(repoData.getString("default_branch"));
			p.setOpen_issues(repoData.getInt("open_issues"));
			if (!repoData.isNull("created_at"))
				p.setCreated_at(StringToDate.parseDatePatterns(repoData.getString("created_at")));
			if (!repoData.isNull("updated_at"))
				p.setUpdated_at(StringToDate.parseDatePatterns(repoData.getString("updated_at")));
			if (!repoData.isNull("pushed_at"))
				p.setPushed_at(StringToDate.parseDatePatterns(repoData.getString("pushed_at")));
			if (!repoData.isNull("language"))
				p.setLanguage(repoData.getString("language"));
			p.setCloneUrl(repoData.getString("clone_url"));
			if (!repoData.isNull("homepage"))
				p.setHomepage(repoData.getString("homepage"));
			p.setQuery(query);
			if (!repoData.isNull("description")) {
				p.setDescription(repoData.getString("description"));
			}
			projects.add(p);
			//			logger.info("Project {} {}", repoData.getString("clone_url"), repoData.getString("default_branch"));
		}
		return projects;
	}	
}
