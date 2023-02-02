package gaa.extractor;

import gaa.dao.NewFileInfoDAO;
import gaa.dao.ProjectInfoDAO;
import gaa.model.NewFileInfo;
import gaa.model.ProjectInfo;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class FileInfoExtractor {
	private String path;
	
	public FileInfoExtractor(String path) {
		this.path = path;
	}
	
	public static void main(String[] args) throws IOException {
//		FileInfoExtractor gitLogerExtractor = new FileInfoExtractor("C:/Users/Guilherme/Dropbox/docs/doutorado UFMG/pesquisas/github/dataset/_filesinfo/");
		FileInfoExtractor gitLogerExtractor = new FileInfoExtractor("/Users/Guilherme/Dropbox/docs/doutorado UFMG/pesquisas/github/dataset/_filesinfo/");
		System.out.println("BEGIN at "+ new Date() + "\n\n");
		gitLogerExtractor.simpleExtract();
		System.out.println("\n\nEND at "+ new Date());
	}
	
	public void simpleExtract() throws IOException{
		ProjectInfoDAO piDAO = new ProjectInfoDAO();
		NewFileInfoDAO fiDAO = new NewFileInfoDAO();
		List<ProjectInfo> projects =  piDAO.findAll(ProjectInfo.class);
		for (ProjectInfo projectInfo : projects) {
			if (!projectInfo.isFiltered()) {
				try {
					List<NewFileInfo> files = new ArrayList<NewFileInfo>();
					System.out.format(
							"%s (%s): Extracting file information...",
							projectInfo.getFullName(), new Date());
					String fileName = projectInfo.getFullName().replace('/', '-')
							+ ".txt";
					BufferedReader br = new BufferedReader(new FileReader(path
							+ fileName));
					String sCurrentLine;
					while ((sCurrentLine = br.readLine()) != null) {
						files.add(new NewFileInfo(projectInfo.getFullName(), sCurrentLine));
					}
					fiDAO.persistAll(files);
					System.out.println("Files added = " + files.size());
					br.close();
				} catch (Exception e) {
					System.err.println("Error in project "+projectInfo.getFullName());
					System.err.println(e.toString());
				}
			}
			
		}
		
	}
	
	
}
