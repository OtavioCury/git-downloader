package gaa.gitdownloader;

import gaa.model.ProjectInfo;

import org.eclipse.jgit.lib.Repository;

public interface GitService {

	Repository cloneIfNotExists(ProjectInfo projectInfo) throws Exception;

}
