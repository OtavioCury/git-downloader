package gaa.filter.projectfilter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.math3.stat.StatUtils;

import gaa.dao.LogCommitFileDAO;
import gaa.model.ProjectInfo;

public class MigrationProjectFilterUsingIQR extends ProjectFilter{
	HashMap<ProjectInfo, List<Long>> mapProjetos = new HashMap<ProjectInfo, List<Long>>();

	public MigrationProjectFilterUsingIQR(List<ProjectInfo> projects) {
		super(projects, "*MIGRATION-IQR*");
	}

	public void preencheMap() {
		LogCommitFileDAO logCommitFileDAO = new LogCommitFileDAO();
		for (int i = 0; i < projects.size(); i++) {
			if (projects.get(i).isFiltered() == false) {
				mapProjetos.put(projects.get(i), 
						logCommitFileDAO.getAddsCommitFileOrderByNumberOfCFsbyType(projects.get(i).getFullName()));
			}
		}
	}

	@Override
	public List<ProjectInfo> filter() {
		preencheMap();
		List<ProjectInfo> eliminados = new ArrayList<ProjectInfo>(), restantes = new ArrayList<ProjectInfo>();
		Iterator<Entry<ProjectInfo, List<Long>>> it = mapProjetos.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<ProjectInfo, List<Long>> pair = (Map.Entry<ProjectInfo, List<Long>>)it.next();
			ProjectInfo projeto = (ProjectInfo) pair.getKey();
			List<Long> numeroCommits = pair.getValue();
			double[] vetor = new double[numeroCommits.size()];
			int soma = 0;
			for (int i = 0; i < numeroCommits.size(); i++) {
				vetor[i] = numeroCommits.get(i);
				soma = soma + numeroCommits.get(i).intValue();
			}
			double primeiro = StatUtils.percentile(vetor, 25)
					,terceiro = StatUtils.percentile(vetor, 75);
			double amplitude = terceiro - primeiro;
			double limite = terceiro+(amplitude*3);
			int somaOutlier = 0;
			for (int j = 0; j < numeroCommits.size(); j++) {
				if(numeroCommits.get(j).intValue() > limite) {
					somaOutlier = somaOutlier + numeroCommits.get(j).intValue();
				}
			}
			if (somaOutlier > soma*0.5) {
				for (int i = 0; i < projects.size(); i++) {
					if (projects.get(i).getFullName().equals(projeto.getFullName())) {
						projects.get(i).setFiltered(true);
						projects.get(i).setFilterinfo(filterStamp);
						eliminados.add(projeto);
					}
				}
			}else {
				restantes.add(projeto);
			}
		}
		return restantes;
	}

}