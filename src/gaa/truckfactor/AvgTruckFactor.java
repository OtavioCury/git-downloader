package gaa.truckfactor;

import gaa.authorship.dao.FileDAO;
import gaa.authorship.dao.RepositoryDAO;
import gaa.authorship.model.File;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AvgTruckFactor extends TruckFactor{
	
	public Map<Integer, Float> getTruckFactor(String repName) {
		Map<Integer, Double> truckDist =  new HashMap<Integer, Double>();
		
		List<File> files =  repDAO.getFiles(repName);
		int numDevelopers = repDAO.getNumDevelopers();
		return null;
	}
	
	
}
