package gaa.extractor;

import gaa.dao.LogCommitDAO;
import gaa.dao.ProjectInfoDAO;
import gaa.model.LogCommitFileInfo;
import gaa.model.LogCommitInfo;
import gaa.model.ProjectInfo;
import gaa.util.CRLFLineReader;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class GitLoggerExtractor {
	private String pathCommits;
	private String pathCommitFiles;
	
	
	
	
	public GitLoggerExtractor(String pathCommits, String pathCommitFiles) {
		super();
		this.pathCommits = pathCommits;
		this.pathCommitFiles = pathCommitFiles;
	}


	static String defaultPath = "C:/Users/Guilherme/Dropbox/docs/doutorado UFMG/pesquisas/github/dataset/";
//	static String defaultPath = "/Users/Guilherme/Dropbox/docs/doutorado UFMG/pesquisas/github/dataset/";
	public static void main(String[] args) throws IOException {
		if (args.length>0)
			defaultPath = args[0];
		GitLoggerExtractor gitLoggerExtractor = new GitLoggerExtractor(defaultPath+"_commitinfo/",defaultPath+"_commitfileinfo/");
		System.out.println("BEGIN at "+ new Date() + "\n\n");
		gitLoggerExtractor.simpleExtract();
		System.out.println("\n\nEND at "+ new Date());
	}
	

	int MAXBUFFER = 100000;
	public void simpleExtract() throws IOException{
		ProjectInfoDAO piDAO = new ProjectInfoDAO();
		LogCommitDAO lcDAO = new LogCommitDAO();
		List<ProjectInfo> projects =  piDAO.findAll(ProjectInfo.class);
		int countcfs = 0;
		Set<String> repositoriesPersisted = new HashSet<String>(lcDAO.getProjectsName());
		for (ProjectInfo projectInfo : projects) {
			//			if (projectInfo.getFullName().equalsIgnoreCase("torvalds/linux")){
			try{
				if (!repositoriesPersisted.contains(projectInfo.getFullName())){
//				if (!repositoriesPersisted.contains(projectInfo.getFullName())&&!projectInfo.getFullName().equalsIgnoreCase(""
//						+ "thoughtbot/paperclip")&&!projectInfo.getFullName().equalsIgnoreCase(""
//								+ "yiisoft/yii2")){
					Map<String, LogCommitInfo> mapCommits = new HashMap<String, LogCommitInfo>();
					System.out.println(projectInfo.getFullName()
							+ ": Extracting logCommits...  "+pathCommits);
					String fileName = projectInfo.getFullName().replace('/', '-')
							+ ".txt";
					BufferedReader br = new BufferedReader(new FileReader(
							pathCommits + fileName));
					CRLFLineReader lineReader = new CRLFLineReader(br);
					String sCurrentLine;
					String[] values;
					while ((sCurrentLine = lineReader.readLine()) != null) {
						values = sCurrentLine.split("-;-");
						if (values.length<7)
							System.err.println("Erro na linha " + countcfs);
						Date authorDate = !values[3].isEmpty() ? new Timestamp(Long.parseLong(values[3]) * 1000L) : null;
						Date commiterDate = !values[6].isEmpty() ? new Timestamp(Long.parseLong(values[6]) * 1000L) : null;
						String msg = (values.length == 8) ? values[7] : "";

						mapCommits.put(values[0],
								new LogCommitInfo(projectInfo.getFullName(),
										values[0], values[1], values[2],
										authorDate, values[4], values[5],
										commiterDate, msg));
						countcfs++;
					}
					//			if (countcfs >= MAXBUFFER){
					//				System.out.println("Persistindo CommitFilesLog = "+countcfs);
					//				countcfs = 0;
					//				lcDAO.persistAll(logCommits);					
					//				logCommits = new ArrayList<LogCommitFileInfo>();
					//			}
					insertFiles(projectInfo.getFullName(), mapCommits);
					br.close();
					lcDAO.persistAll(mapCommits.values());
				}
				else{
					System.out.println(projectInfo.getFullName() + " already analysed!");
				}
			}
			catch(Exception e ){
				System.err.format("Error in file %s, line %d\n%s", projectInfo.getFullName(), countcfs, e.getMessage() );
			}
				
			
		}
//		if (logCommits.size()>0)
//			lcDAO.persistAll(logCommits);	
		
		
	}
	
	
	static public Map<String, List<LogCommitFileInfo>> extractProject(String localPath, String projectName) throws IOException{
		Map<String, List<LogCommitFileInfo>> map = new HashMap<String, List<LogCommitFileInfo>>();
		List<LogCommitFileInfo> logCommitFiles;
		int countcfs = 0;
		System.out.println(projectName+": Extracting logCommitFiles...");
		String fileName = projectName.replace('/', '-')+".log";
		BufferedReader br = new BufferedReader(new FileReader(localPath+fileName));
		String sCurrentLine;
		String[] values;

		while ((sCurrentLine = br.readLine()) != null) {
			values = sCurrentLine.split(";");
			String sha = values[0];
			if (map.containsKey(sha))
				logCommitFiles = map.get(sha);
			else{
				logCommitFiles = new ArrayList<LogCommitFileInfo>();
				map.put(sha, logCommitFiles);
			}
			logCommitFiles.add(new LogCommitFileInfo(values[1], values[2], values[3]));
		}
		br.close();
		return map;
	}
	
	private void insertFiles(String projectName, Map<String, LogCommitInfo> mapCommit) throws IOException{
		System.out.println(projectName+": Extracting logCommitFiles...");
		String fileName = projectName.replace('/', '-')+".txt";
		BufferedReader br = new BufferedReader(new FileReader(pathCommitFiles+fileName));
		String sCurrentLine;
		String[] values;

		while ((sCurrentLine = br.readLine()) != null) {
			values = sCurrentLine.split(";");
			String sha = values[0];
			mapCommit.get(sha).addCommitFile(new LogCommitFileInfo(values[1], values[2], values[3]));
		}
		br.close();
	}

	public String getPathCommits() {
		return pathCommits;
	}

	public void setPathCommits(String pathCommits) {
		this.pathCommits = pathCommits;
	}

	public String getPathCommitFiles() {
		return pathCommitFiles;
	}

	public void setPathCommitFiles(String pathCommitFiles) {
		this.pathCommitFiles = pathCommitFiles;
	}
}
