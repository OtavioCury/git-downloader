package gaa.filter.projectfilter;

import java.util.ArrayList;
import java.util.List;

import gaa.model.ProjectInfo;

public class HistoryProjectFilter extends ProjectFilter {
	
	private float historyThreshold;
	
	
	
	public HistoryProjectFilter(List<ProjectInfo> projects, float historyThreshold) {
		super(projects, "*HISTORY*");
		this.historyThreshold = historyThreshold;
	}

	@Override
	public List<ProjectInfo> filter() {
		List<ProjectInfo> newList = new ArrayList<ProjectInfo>();
		for (ProjectInfo projectInfo : projects) {
			if (!projectInfo.isFiltered()) {
				if (projectInfo.getCommits_count() < historyThreshold){
					projectInfo.setFiltered(true);
					String filterInfo = projectInfo.getFilterinfo();
					projectInfo.setFilterinfo(filterInfo==null || filterInfo.isEmpty()?filterStamp:filterInfo+filterStamp);
				}	
				else
					newList.add(projectInfo);
			}
		}
		return newList;
	}
	
	

	public float getTeamThreshold() {
		return historyThreshold;
	}

	public void setTeamThreshold(float teamThreshold) {
		this.historyThreshold = teamThreshold;
	}


}
