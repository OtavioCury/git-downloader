package gaa.filter.filefilter;

import gaa.dao.NewFileInfoDAO;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class FileClassifier {
	public static void main(String[] args) throws IOException {
		NewFileInfoDAO fiDAO = new NewFileInfoDAO();
		FileType fileTypes[] = FileType.values();
		for (int i = 0; i < fileTypes.length; i++) {
			FileType fileType = fileTypes[i];
			if (fileType != FileType.NOTIDENTIFIED) {
				String whereClauses = " (";
				List<String> patterns = FileType.getPatterns(fileType);
				if (patterns!=null && patterns.size()>0) {
					Iterator<String> iterator = patterns.iterator();
					while (iterator.hasNext()) {
						String pattern = iterator.next();
						if (fileType == FileType.DOCUMENTATION || fileType == FileType.EXAMPLES || fileType == FileType.LIBRARY) 
							whereClauses += " LOWER (fi.path) LIKE \'" + pattern + "\' ";
						else
							whereClauses += " fi.path LIKE \'" + pattern + "\' ";
						if (iterator.hasNext())
							whereClauses += " OR ";
					}
					whereClauses += " ) ";
					System.out.println("Finding " + fileType + " files ...");
					int numUpdates = fiDAO.classifierAndUpdateFilesInfo(whereClauses, fileType);
					if (numUpdates >0)
						System.out.format("OK. Updated %d files using %s\n", numUpdates, patterns);
					else
						System.err.format("Error, no Update for %s\n", patterns);
				}
			}
		}
		
		extractClassifierPatterns(fiDAO, "patterns.txt");
//		
//		for (int i = 0; i < fileTypes.length; i++) {
//			FileType fileType = fileTypes[i];
//			if (fileType != FileType.NOTIDENTIFIED) {
//				String whereClauses = "AND (";
//				List<String> patterns = FileType.getPosixPatterns(fileType);
//				if (patterns.size()>0) {
//					Iterator<String> iterator = patterns.iterator();
//					while (iterator.hasNext()) {
//						String pattern = iterator.next();
//						whereClauses += " fi.path ~ \'" + pattern + "\' ";
//						if (iterator.hasNext())
//							whereClauses += " OR ";
//					}
//					whereClauses += " ) ";
//					System.out.println("Finding " + fileType + " files ...");
//					int numUpdates = fiDAO.classifierAndUpdateFilesInfo(whereClauses, fileType);
//					if (numUpdates >0)
//						System.out.format("OK. Updated %d files using %s\n", numUpdates, patterns);
//					else
//						System.err.format("Error, no Update for %s\n", patterns);
//				}
//			}
//		}
		
		System.out.println(new Date());
	}
	
	static public void extractClassifierPatterns(NewFileInfoDAO fiDAO, String localPath) throws IOException{
		Map<String, List<String>> mapLike = new HashMap<String, List<String>>();
		Map<String, List<String>> mapPosix = new HashMap<String, List<String>>();
		int countcfs = 0;
		BufferedReader br = new BufferedReader(new FileReader(localPath));
		String sCurrentLine;
		String[] values;

		while ((sCurrentLine = br.readLine()) != null) {
			if (sCurrentLine.isEmpty() || sCurrentLine.charAt(0) == '#')
				continue;
			values = sCurrentLine.split(" ");
			if (values[0].equalsIgnoreCase("LIKE"))
				mapInsert(mapLike, values[1], values[2]);
			else if (values[0].equalsIgnoreCase("POSIX"))
				mapInsert(mapPosix, values[1], values[2]);
			else 
				System.err.println("Arquivo com formatação errada");
		}
		for (Entry<String, List<String>> entry : mapLike.entrySet()) {
			for (String pattern : entry.getValue()) {
				String whereClauses = "AND (fi.path LIKE \'" + pattern + "\') ";
				int numUpdates = fiDAO.classifierAndUpdateFilesInfo(whereClauses, FileType.valueOf(entry.getKey()));
				if (numUpdates >0)
					System.out.format("OK. Updated %d files using %s\n", numUpdates, pattern);
				else
					System.err.format("Error, no Update for %s\n", pattern);
			}
		}
		for (Entry<String, List<String>> entry : mapPosix.entrySet()) {
			for (String pattern : entry.getValue()) {
				String whereClauses = "AND (fi.path ~ \'" + pattern + "\') ";
				int numUpdates = fiDAO.classifierAndUpdateFilesInfo(whereClauses, FileType.valueOf(entry.getKey()));
				if (numUpdates >0)
					System.out.format("OK. Updated %d files using %s\n", numUpdates, pattern);
				else
					System.err.format("Error, no Update for %s\n", pattern);
			}
		}
	}

	private static void mapInsert(Map<String, List<String>> map,
			String kind, String pattern) {
		if (!map.containsKey(kind))
			map.put(kind, new ArrayList<String>());
		map.get(kind).add(pattern);
	}
}
