package gaa.truckfactor;

import gaa.authorship.dao.FileDAO;
import gaa.authorship.dao.RepositoryDAO;
import gaa.authorship.model.File;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class TruckFactor {
	RepositoryDAO repDAO;
	
	public TruckFactor() {
		repDAO = new RepositoryDAO();
	}
	
	public abstract Map<Integer, Float> getTruckFactor(String repName);
	
	
}
