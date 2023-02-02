package gaa.extractor;

import java.util.Date;
import java.util.List;

import gaa.dao.CommitInfoDAO;
import gaa.dao.ProjectInfoDAO;
import gaa.model.ProjectInfo;

public class AuthorExtractor {
	public static void authorsExtractor() {
		System.out.println(new Date());

		CommitInfoDAO ciDAO = new CommitInfoDAO();
		ProjectInfoDAO piDAO = new ProjectInfoDAO();
		List<ProjectInfo> projects = piDAO.findAll(null);
		for (ProjectInfo projectInfo : projects) {
			if (projectInfo.getNumAuthors()==0) {
				projectInfo.setNumAuthors(ciDAO.getNumberAuthors(projectInfo
						.getFullName()));
				System.out.println(projectInfo.getFullName() + " = "
						+ projectInfo.getNumAuthors());
				piDAO.update(projectInfo);
			}
		}

		System.out.println(new Date());
	}		
}
