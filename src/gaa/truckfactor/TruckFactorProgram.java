package gaa.truckfactor;

import gaa.authorship.dao.FileDAO;
import gaa.authorship.dao.RepositoryDAO;
import gaa.authorship.model.File;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TruckFactorProgram {
	public static void main(String[] args) {
		TruckFactor truckFactor = new GreedyTruckFactor();
		for (String repName : new RepositoryDAO().getAllRepositoryNames()) {
			truckFactor.getTruckFactor(repName);			
		}
//		truckFactor.getTruckFactor("codemirror/CodeMirror");	
	}

	
	
}
