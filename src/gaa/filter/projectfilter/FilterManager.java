package gaa.filter.projectfilter;

import gaa.dao.ProjectInfoDAO;
import gaa.model.ProjectInfo;

import java.util.ArrayList;
import java.util.List;

public class FilterManager {
	List<ProjectInfo> projects;
	List<ProjectFilter> filters;
	ProjectFilter mainFilter;

	public static void main(String[] args) throws Exception {
		FilterManager filterManager =  new FilterManager(new ProjectInfoDAO().findAll(null));
		FilterThreshold th = new FilterThreshold();
		//		filterManager.addFilter(new NumberOfProjectFilter(filterManager.getProjects(), 500));
		//		filterManager.addFilter(new TeamProjectFilter(filterManager.getProjects(), 32));
		//		filterManager.addFilter(new HistoryProjectFilter(filterManager.getProjects(), 334));
		//		filterManager.addFilter(new SizeProjectFilter(filterManager.getProjects(), 45));
		//		filterManager.addFilter(new MigrationProjectFilter(filterManager.getProjects(), 2, 0.5f, 20));

		filterManager.addFilter(new TeamProjectFilter(filterManager.getProjectsByLanguage("c/c++"), th.authors("C++")));
		filterManager.addFilter(new HistoryProjectFilter(filterManager.getProjectsByLanguage("c/c++"), th.history("C++")));
		filterManager.addFilter(new SizeProjectFilter(filterManager.getProjectsByLanguage("c/c++"), th.size("C++")));

		filterManager.addFilter(new TeamProjectFilter(filterManager.getProjectsByLanguage("java"), th.authors("Java")));
		filterManager.addFilter(new HistoryProjectFilter(filterManager.getProjectsByLanguage("java"), th.history("Java")));
		filterManager.addFilter(new SizeProjectFilter(filterManager.getProjectsByLanguage("java"), th.size("Java")));

		filterManager.addFilter(new TeamProjectFilter(filterManager.getProjectsByLanguage("javascript"), th.authors("JavaScript")));
		filterManager.addFilter(new HistoryProjectFilter(filterManager.getProjectsByLanguage("javascript"), th.history("JavaScript")));
		filterManager.addFilter(new SizeProjectFilter(filterManager.getProjectsByLanguage("javascript"), th.size("JavaScript")));

		filterManager.addFilter(new TeamProjectFilter(filterManager.getProjectsByLanguage("php"), th.authors("PHP")));
		filterManager.addFilter(new HistoryProjectFilter(filterManager.getProjectsByLanguage("php"), th.history("PHP")));
		filterManager.addFilter(new SizeProjectFilter(filterManager.getProjectsByLanguage("php"), th.size("PHP")));

		filterManager.addFilter(new TeamProjectFilter(filterManager.getProjectsByLanguage("python"), th.authors("Python")));
		filterManager.addFilter(new HistoryProjectFilter(filterManager.getProjectsByLanguage("python"), th.history("Python")));
		filterManager.addFilter(new SizeProjectFilter(filterManager.getProjectsByLanguage("python"), th.size("Python")));

		filterManager.addFilter(new TeamProjectFilter(filterManager.getProjectsByLanguage("ruby"), th.authors("Ruby")));
		filterManager.addFilter(new HistoryProjectFilter(filterManager.getProjectsByLanguage("ruby"), th.history("Ruby")));
		filterManager.addFilter(new SizeProjectFilter(filterManager.getProjectsByLanguage("ruby"), th.size("Ruby")));

		//		filterManager.setMainFilter(new NewMigrationProjectFilter(filterManager.getProjects(), 1, 0.5f, 20));
		filterManager.setMainFilter(new MigrationProjectFilterUsingIQR(filterManager.getProjects()));
		filterManager.addFilter(filterManager.getMainFilter());

		filterManager.cleanAndFilter();
		filterManager.persistFiltredProjects();
	}

	public FilterManager(List<ProjectInfo> projects) {
		this.projects = projects;
		this.filters = new ArrayList<ProjectFilter>(); 
	}

	public void addFilter(ProjectFilter filter){
		this.filters.add(filter);
	}

	public List<ProjectInfo> cleanAndFilter(){
		List<ProjectInfo> filteredList = new ArrayList<>();
		for (ProjectInfo projectInfo : projects) {
			projectInfo.setFiltered(false);
			projectInfo.setFilterinfo("");
		}		
		//		for (ProjectFilter projectFilter : filters) {
		//			projectFilter.clean();
		//		}
		for (int i = 0; i < filters.size(); i++) {
			filteredList.addAll(filters.get(i).filter());
		}
		return filteredList;
	}

	public void persistFiltredProjects(){
		this.mainFilter.persistFilterInformations();
	}

	public List<ProjectInfo> getProjects() {
		return projects;
	}

	private List<ProjectInfo> getProjectsByLanguage(String language) {
		List<ProjectInfo> newProjects = new ArrayList<>();
		for (ProjectInfo projectInfo : projects) {
			if (language.equalsIgnoreCase("c/c++")){
				if (projectInfo.getLanguage().equalsIgnoreCase("c")||projectInfo.getLanguage().equalsIgnoreCase("c++"))
					newProjects.add(projectInfo);
			}
			else if (projectInfo.getLanguage().equalsIgnoreCase(language))
				newProjects.add(projectInfo);
		}
		return newProjects;
	}

	public void setProjects(List<ProjectInfo> projects) {
		this.projects = projects;
	}

	public List<ProjectFilter> getFilters() {
		return filters;
	}

	public void setFilters(List<ProjectFilter> filters) {
		this.filters = filters;
	}

	public ProjectFilter getMainFilter() {
		return mainFilter;
	}

	public void setMainFilter(ProjectFilter mainFilter) {
		this.mainFilter = mainFilter;
	}

}
