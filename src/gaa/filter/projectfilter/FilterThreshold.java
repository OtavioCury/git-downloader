package gaa.filter.projectfilter;

import java.util.List;

import org.apache.commons.math3.stat.StatUtils;

import gaa.dao.ProjectInfoDAO;
import gaa.model.ProjectInfo;

public class FilterThreshold {

	public static void main(String[] args) {
		FilterThreshold filterThreshold = new FilterThreshold();
		System.out.println("===C++===");
		System.out.println("Autores: "+filterThreshold.authors("C++"));
		System.out.println("Commits: "+filterThreshold.history("C++"));
		System.out.println("Arquivos: "+filterThreshold.size("C++"));
		System.out.println("===Java===");
		System.out.println("Autores: "+filterThreshold.authors("Java"));
		System.out.println("Commits: "+filterThreshold.history("Java"));
		System.out.println("Arquivos: "+filterThreshold.size("Java"));
		System.out.println("===JavaScript===");
		System.out.println("Autores: "+filterThreshold.authors("JavaScript"));
		System.out.println("Commits: "+filterThreshold.history("JavaScript"));
		System.out.println("Arquivos: "+filterThreshold.size("JavaScript"));
		System.out.println("===PHP===");
		System.out.println("Autores: "+filterThreshold.authors("PHP"));
		System.out.println("Commits: "+filterThreshold.history("PHP"));
		System.out.println("Arquivos: "+filterThreshold.size("PHP"));
		System.out.println("===Python===");
		System.out.println("Autores: "+filterThreshold.authors("Python"));
		System.out.println("Commits: "+filterThreshold.history("Python"));
		System.out.println("Arquivos: "+filterThreshold.size("Python"));
		System.out.println("===Ruby===");
		System.out.println("Autores: "+filterThreshold.authors("Ruby"));
		System.out.println("Commits: "+filterThreshold.history("Ruby"));
		System.out.println("Arquivos: "+filterThreshold.size("Ruby"));
	}

	public int history(String linguagem) {
		ProjectInfoDAO dao = new ProjectInfoDAO();
		List<ProjectInfo> projetos = dao.findByLanguage(linguagem);
		double[] vetor = new double[projetos.size()];
		for (int i = 0; i < projetos.size(); i++) {
			vetor[i] = projetos.get(i).getCommits_count();
		}
		double primeiro = StatUtils.percentile(vetor, 25);
		return (int) primeiro;
	}
	
	public int authors(String linguagem) {
		ProjectInfoDAO dao = new ProjectInfoDAO();
		List<ProjectInfo> projetos = dao.findByLanguage(linguagem);
		double[] vetor = new double[projetos.size()];
		for (int i = 0; i < projetos.size(); i++) {
			vetor[i] = projetos.get(i).getNumAuthors();
		}
		double primeiro = StatUtils.percentile(vetor, 25);
		return (int) primeiro;
	}
	
	public int size(String linguagem) {
		ProjectInfoDAO dao = new ProjectInfoDAO();
		List<ProjectInfo> projetos = dao.findByLanguage(linguagem);
		double[] vetor = new double[projetos.size()];
		for (int i = 0; i < projetos.size(); i++) {
			vetor[i] = projetos.get(i).getNumFiles();
		}
		double primeiro = StatUtils.percentile(vetor, 25);
		return (int) primeiro;
	}

}
