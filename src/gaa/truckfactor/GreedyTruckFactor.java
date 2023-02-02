package gaa.truckfactor;

import gaa.authorship.model.File;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.google.common.collect.Sets;

public class GreedyTruckFactor extends TruckFactor {

	@Override
	public Map<Integer, Float> getTruckFactor(String repName) {
		Map<Integer, Float> trucKFactorMap = new HashMap<Integer, Float>();
		Set<Long> repFiles =  repDAO.getFilesSet(repName);
		Map<Long, Set<Long>> authorsMap = repDAO.getFilesAuthorMap(repName);
		int factor = 0;
		Set<Long> clonedRepFiles = new  HashSet<Long>(repFiles);
		while(authorsMap.size()>0){
			Float coverage = getCoverage(repFiles, authorsMap);
//			if (coverage<0.01)
//				break;
			trucKFactorMap.put(factor++, coverage);
			removeBig(clonedRepFiles, authorsMap);
		}
//		printAuthorsMap(authorsMap);
		printTruckMap(repName, trucKFactorMap);
		return trucKFactorMap;
	}

	private float getCoverage(Set<Long> repFiles, Map<Long, Set<Long>> authorsMap) {
//		Map<Long, Set<Long>> clonedAuthorsMap = new HashMap<Long, Set<Long>>(authorsMap);
//		Set<Long> clonedRepFiles = new  HashSet<Long>(repFiles);
		Set<Long> authorsSet = new HashSet<Long>();
		for (Entry<Long, Set<Long>> entry : authorsMap.entrySet()) {
			for (Long fileId : entry.getValue()) {
				authorsSet.add(fileId);
				if(authorsSet.size()==repFiles.size())
					return 1f;
			}
		}
		return (float)authorsSet.size()/repFiles.size();
	}

	private void removeBig(Set<Long> repFiles, Map<Long, Set<Long>> authorsMap) {
		int biggerNumber = 0;
		Long biggerDev = null;
		for (Entry<Long, Set<Long>> entry : authorsMap.entrySet()) {
			if (entry.getValue().size()>biggerNumber){
				biggerNumber = entry.getValue().size();
				biggerDev = entry.getKey();
			}
		}
		//System.out.format("removed: %s:%s - %d (%.2f)\n" , biggerDev, biggerDev, biggerNumber, ((float)biggerNumber)/repFiles.size() );
		authorsMap.remove(biggerDev);
		
	}
	
	private void removeBest(Set<Long> repFiles, Map<Long, Set<Long>> authorsMap) {
		int bestIntersection = 0;
		Long bestDev = null;
		for (Entry<Long, Set<Long>> entry : authorsMap.entrySet()) {
			int intersection = Sets.intersection(repFiles,entry.getValue()).size();
			if (intersection>bestIntersection){
				bestIntersection = intersection;
				bestDev = entry.getKey();
			}
		}
		authorsMap.remove(bestDev);
		
	}
	
	private void removeBest2(Set<Long> clonedRepFiles, Map<Long, Set<Long>> authorsMap) {
		int bestIntersection = 0;
		Long bestDev = null;
		for (Entry<Long, Set<Long>> entry : authorsMap.entrySet()) {
			int intersection = Sets.intersection(clonedRepFiles,entry.getValue()).size();
			if (intersection>bestIntersection){
				bestIntersection = intersection;
				bestDev = entry.getKey();
			}
		}
		clonedRepFiles.remove(authorsMap.get(bestDev));
		authorsMap.remove(bestDev);
		
	}
			

	private void printAuthorsMap(Map<Long, Set<Long>> authorsMap) {
		for (Entry<Long, Set<Long>> entry : authorsMap.entrySet()) {
			System.out.print(entry.getKey() + ": ");
			for (Long fileId : entry.getValue()) {
				System.out.print(fileId + " ");
			}
			System.out.println();
		}
		
	}
	
	private void printTruckMap(String repName, Map<Integer, Float> truckMap) {
		Date date = new Date();
		for (Entry<Integer, Float> entry : truckMap.entrySet()) {
			System.out.format("%s;%d;%.2f\n", repName, entry.getKey(), entry.getValue());
//			System.out.format("%s;%d;%.2f;%s\n", repName, entry.getKey(), entry.getValue(), date);
			
		}
		
	}

}
