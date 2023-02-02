package gaa.extractor;

import gaa.dao.NewFileInfoDAO;
import gaa.dao.ProjectInfoDAO;
import gaa.model.ProjectInfo;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class FileLanguageExtractor {
	private String path;
	
	public FileLanguageExtractor(String path) {
		this.path = path;
	}
	
	public static void main(String[] args) throws IOException {
		String path = "C:/Users/Guilherme/Dropbox/docs/doutorado UFMG/pesquisas/github/dataset/_linguistfiles/";
//		String path = "/Users/Guilherme/Dropbox/docs/doutorado UFMG/pesquisas/github/dataset/_linguistfiles/";
		if (args.length>0)
			path = args[0]; 
		FileLanguageExtractor gitLogerExtractor = new FileLanguageExtractor(path);
			
		System.out.println("BEGIN at "+ new Date() + "\n\n");
		gitLogerExtractor.simpleExtract();
		System.out.println("\n\nEND at "+ new Date());
	}
	
	public void simpleExtract() throws IOException{
		ProjectInfoDAO piDAO = new ProjectInfoDAO();
		NewFileInfoDAO fiDAO = new NewFileInfoDAO();
		List<ProjectInfo> projects =  piDAO.findAll(ProjectInfo.class);
		fiDAO.setAllAsNotLinguist();
		for (ProjectInfo projectInfo : projects) {
			if (!projectInfo.isFiltered()) {
				try {
					Map<String, List<String>> languageMap = new HashMap<String, List<String>>();
					System.out.format(
							"%s (%s): Extracting file language information...",
							projectInfo.getFullName(), new Date());
					String fileName = projectInfo.getFullName().replace('/', '-')
							+ ".txt";
					BufferedReader br = new BufferedReader(new FileReader(path
							+ fileName));
					String sCurrentLine;
					String[] values;
					while ((sCurrentLine = br.readLine()) != null) {
						values = sCurrentLine.split(";");
						String language = values[0];
						if (language.contains("\'"))
							language = language.replace("'", "''");
						String path = values[1];
						List<String> paths;
						if (languageMap.containsKey(language))
							paths = languageMap.get(language);
						else {
							paths = new ArrayList<String>();
							languageMap.put(language, paths);
						}
						paths.add(path);
					}
					int count = 0;
					for (Entry<String, List<String>> entry : languageMap.entrySet()) {
						count += fiDAO.updateLanguageFileInfo(
								projectInfo.getFullName(), entry.getKey(),
								entry.getValue());
					}
					System.out.println("Arquivos selecionados = " + count);
					br.close();
				} catch (Exception e) {
					System.err.println("Erro no projeto "+projectInfo.getFullName());
					System.err.println(e.getMessage());
				}
			}
			
		}
		
	}
	
	
}
