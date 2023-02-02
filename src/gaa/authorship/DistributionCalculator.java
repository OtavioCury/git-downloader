package gaa.authorship;

import gaa.authorship.dao.DeveloperAuthorshipInfoDAO;
import gaa.authorship.dao.RepositoryDAO;
import gaa.authorship.model.DeveloperAuthorshipInfo;
import gaa.authorship.model.Repository;
import gaa.authorship.model.RepositoryStatus;
import gaa.prototype.UserInfoData;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.Map.Entry;

import javax.swing.JFrame;
import javax.swing.JScrollPane;

import br.ufmg.aserg.topicviewer.control.distribution.DistributionMapCalculator;
import br.ufmg.aserg.topicviewer.gui.distribution.DistributionMap;
import br.ufmg.aserg.topicviewer.gui.distribution.DistributionMapPanel;
import br.ufmg.aserg.topicviewer.util.UnsufficientNumberOfColorsException;

public class DistributionCalculator {
	
//	static String filesPath ="/Users/Guilherme/Dropbox/docs/doutorado UFMG/pesquisas/github/dataset/devauthorshipfiles/";
	static String filesPath ="C:/Users/Guilherme/Dropbox/docs/doutorado UFMG/pesquisas/github/dataset/devauthorshipfiles/";
	
	public static void main(String[] args) {
		RepositoryDAO repoDAO = new RepositoryDAO();
		DeveloperAuthorshipInfoDAO daiDAO =  new DeveloperAuthorshipInfoDAO();
		for (Repository repository : repoDAO.findAll()) {
			String repoName = repository.getFullName();
//			if (repoName.equalsIgnoreCase("sferik/twitter")){
			if (repository.getStatus() == RepositoryStatus.DOA_CALCULATED){
				repository.setStatus(RepositoryStatus.ANALYZING);
				repoDAO.update(repository);
				System.out.format("%s (%s): Extracting authorship distribution information...\n", repoName, new Date());
				HashSet<DeveloperAuthorshipInfo> developersAuthoship = distributionMap(repoDAO.getFilesMainAuthor(repoName), repoName, "Rank "	+ repoName, true);
				
				try {
					saveInfile(repoName, developersAuthoship);
					
					System.out.format("%s (%s): Persisting authorship distribution information...\n", repoName, new Date());				
					daiDAO.persistAll(developersAuthoship);
					
					repository.setStatus(RepositoryStatus.ANALYZED);
					repoDAO.update(repository);
					
				} catch (IOException e) {

					repository.setStatus(RepositoryStatus.ERROR);
					repoDAO.update(repository);
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
			
		}
	}
	
	private static void saveInfile(String repoName,
			Collection<DeveloperAuthorshipInfo> developersAuthoship) throws IOException {
		FileWriter fw;
		fw = new FileWriter(new File(filesPath + repoName.replace('/', '-') + ".dof"));
		String textFile = null;
		for (DeveloperAuthorshipInfo userInfo : developersAuthoship) {
			if (textFile == null)
				textFile = String.format("%s;%d;%d;%f;%f", userInfo.getUserName(), userInfo.getnFiles(), userInfo.getSpread(), userInfo.getSpreadNormalized(), userInfo.getFocus());
			else
				textFile += String.format("%s;%d;%d;%f;%f", userInfo.getUserName(), userInfo.getnFiles(), userInfo.getSpread(), userInfo.getSpreadNormalized(), userInfo.getFocus());
			textFile += System.lineSeparator(); //new line
		}
//		fw.write(textFile.substring(0,textFile.lastIndexOf(System.lineSeparator())));
		fw.write(textFile);
		fw.close();
	}

	static Map<String, HashSet<DeveloperAuthorshipInfo>> usersInfo = new HashMap<String, HashSet<DeveloperAuthorshipInfo>>();
	static HashSet<DeveloperAuthorshipInfo> distributionMap(Map<String, Set<String>> maps, String projectName, String mapName, boolean showDM){
		HashSet<DeveloperAuthorshipInfo> developerAuthoshipSet;
		if (usersInfo.containsKey(projectName))
			developerAuthoshipSet = usersInfo.get(projectName);
		else{
			developerAuthoshipSet = new HashSet<DeveloperAuthorshipInfo>();
			usersInfo.put(projectName, developerAuthoshipSet);
		}
			
		
//		HashSet<UserInfoData> userInfo = new HashSet<UserInfoData>();
		DistributionMap dm = new DistributionMap(mapName);
		String semanticTopics[][] = new String[maps.size()][];
		int index = 0;
//		int stCount = 0;
		
		for (Entry<String, Set<String>> entry : maps.entrySet()) {
	
			//		for (Entry<String, Set<String>> entry : getOrderedMap(maps).entrySet()) {
			String sTopics[] = new String[entry.getValue().size()];
			int i =0;
			for (String name : entry.getValue()) {
//				name = name.replace("/", ".");
				String className = name;
//				className = className.replace("/", ".");
				sTopics[i++] = className;
				dm.put(getPackageName(name).replace("/", "."), className, index, 0.0);
			}
			developerAuthoshipSet.add(new DeveloperAuthorshipInfo(entry.getKey(), index, entry.getValue(), projectName));
			semanticTopics[index++] = sTopics;

		}
		
//		Queue<Entry<String, Set<String>>> queuedMap = getOrderedMap(maps);
//		while (!queuedMap.isEmpty()){
//			Entry<String, Set<String>> entry = queuedMap.poll();
//			//		for (Entry<String, Set<String>> entry : getOrderedMap(maps).entrySet()) {
//			String sTopics[] = new String[entry.getValue().size()];
//			int i =0;
//			for (String name : entry.getValue()) {
////				name = name.replace("/", ".");
//				String className = getClassName(name);
//				className = className.replace("/", ".");
//				sTopics[i++] = className;
//				dm.put(getPackageName(name).replace("/", "."), className, index, 0.0);
//			}
//			index++;
//			developerAuthoshipSet.add(new DeveloperAuthorshipInfo(entry.getKey(), stCount, entry.getValue(), projectName));
//			semanticTopics[stCount++] = sTopics;
//
//		}
			dm = DistributionMapCalculator.addSemanticClustersMetrics(dm, maps.size());
			calcDMValues(dm, developerAuthoshipSet);
			if (showDM) {
				try {
					DistributionMapPanel dmPanel = new DistributionMapPanel(dm,
							semanticTopics);
					JFrame frame = new JFrame("DistributionMap - " + mapName);
					JScrollPane scrollPane = new JScrollPane(dmPanel);
					frame.setContentPane(scrollPane);
					frame.setVisible(true);

				} catch (UnsufficientNumberOfColorsException e1) {
					e1.printStackTrace();
				}
			}
		return developerAuthoshipSet;
	}
	
	private static Queue<Entry<String, Set<String>>> getOrderedMap(
			Map<String, Set<String>> maps) {
		Map<String, Set<String>> clonedMap =  new HashMap<String, Set<String>>();
		Queue<Entry<String, Set<String>>> newMap =  new LinkedList<Entry<String, Set<String>>>();
		for (Entry<String, Set<String>> entry : maps.entrySet()) {
			clonedMap.put(entry.getKey(), entry.getValue());

		}

		while (!clonedMap.isEmpty()) {
			int bigSize = 0;
			Entry<String, Set<String>> bigEntry =  null;
			for (Entry<String, Set<String>> entry : clonedMap.entrySet()) {
				if (entry.getValue().size() > bigSize) {
					bigSize = entry.getValue().size();
					bigEntry = entry;
				}

			}
			if (bigEntry!=null){
				newMap.add(bigEntry);
				clonedMap.remove(bigEntry.getKey());
			}

		}
		return newMap;
	}
	static String getClassName(String id) {
		// TODO Auto-generated method stub
		if (id.contains("/"))
			return id.substring(id.lastIndexOf('/')+1);
		else
			return id;
	}
	static String getPackageName(String id) {
		if (id.contains("/"))
			return id.substring(0, id.lastIndexOf('/'));
		else
			return "";
	}
	private static void calcDMValues(DistributionMap dm, HashSet<DeveloperAuthorshipInfo> projectUsersInfo) {
		int numPackages = dm.getPackages().size();
//		System.out.println("Total packages number: "+ numPackages);
		for (DeveloperAuthorshipInfo userInfo : projectUsersInfo) {
			userInfo.setFocus(dm.getFocus(userInfo.getIndex()));
			userInfo.setSpread(dm.getSpread(userInfo.getIndex()).intValue());
			userInfo.setNumFiles(userInfo.getnFiles());
			double d = ((double)userInfo.getSpread())/numPackages;
			userInfo.setSpreadNormalized(d);
//			System.out.format("%S - files:%d, spread: %d , focus: %f, INDEX: %d\n", userInfo.userName, userInfo.getnFiles(), userInfo.getSpread(), userInfo.getFocus(), userInfo.getIndex());
//			System.out.format("%s;%d;%d;%f;%f\n", userInfo.getUserName(), userInfo.getnFiles(), userInfo.getSpread(), userInfo.getSpreadNormalized(), userInfo.getFocus());
//			Total packages number: 8
//			kyle@dontkry.com;1;1;0,125000;0,142857
//			cowboy@rj3.net;22;8;1,000000;0,961039
		}
		
	}
}
