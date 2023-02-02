package gaa.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Transient;

import org.eclipse.persistence.annotations.Index;

@Entity
public class LogCommitFileInfo extends AbstractEntity{

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	protected Long id;
	
	@Transient
	private String repositoryName;
	@Transient
	private String sha;
	
	@Index(name="OLDFILENAMEINDEX")
	@Column(length = 1000)
	private String oldFileName;
	@Index(name="NEWFILENAMEINDEX")
	@Column(length = 1000)
	private String newFileName;
	@Enumerated(EnumType.STRING)
	private Status status;

	public LogCommitFileInfo() {
		// TODO Auto-generated constructor stub
	}


	public LogCommitFileInfo(String repositoryName, String sha, String status, String oldFileName, String newFileName) {
		super();
		this.repositoryName = repositoryName;
		this.sha = sha;
		this.oldFileName = oldFileName;
		this.newFileName = newFileName;
		this.status = Status.getStatus(status);
	}
	
	public LogCommitFileInfo(String status, String oldFileName, String newFileName) {
		super();
		this.oldFileName = oldFileName;
		this.newFileName = newFileName;
		this.status = Status.getStatus(status);
	}
	
	@Override
	public String toString() {
		return newFileName + ", " + status; //+ ", " + additions  + ", " + deletions + ", " + " + commitId + ", " + message; 
	}
	public String getNewFileName() {
		return newFileName;
	}

	public void setNewFileName(String fileName) {
		this.newFileName = fileName;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

//	@Override
//	public int compareTo(CommitFileInfo o) {
////		if (this.status == Status.ADDED)
////			if (o.status == Status.ADDED)
////				return 0;
////			else 
////				return -1;
//		return this.date.compareTo(o.date);
//	}
	

	public String getOldFileName() {
		return oldFileName;
	}

	public void setOldFileName(String oldFileName) {
		this.oldFileName = oldFileName;
	}


	public Long getId() {
		return id;
	}


	public String getRepositoryName() {
		return repositoryName;
	}


	public void setRepositoryName(String repositoryName) {
		this.repositoryName = repositoryName;
	}


	public String getSha() {
		return sha;
	}


	public void setSha(String sha) {
		this.sha = sha;
	}

	


}
