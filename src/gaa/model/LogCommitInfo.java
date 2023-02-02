package gaa.model;

import gaa.authorship.model.Developer;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.eclipse.persistence.annotations.Index;

@Entity
public class LogCommitInfo extends AbstractEntity{



	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	protected Long id;
	
	@Index(name="COMMITREPOSITORYNAMEINDEX")
	private String repositoryName;
	private String sha;
	
	private String authorName;
	private String authorEmail;
	@Temporal(TemporalType.TIMESTAMP)
	private Date authorDate;
	
	private String commiterName;
	private String commiterMail;
	@Temporal(TemporalType.TIMESTAMP)
	private Date commiterDate;
	
	private String userName;
	

	@Lob
	private String message;
	

	@OneToMany(cascade = { CascadeType.ALL })
	private List<LogCommitFileInfo> logCommitFiles;

	public LogCommitInfo() {
		// TODO Auto-generated constructor stub
	}
	
	

	public LogCommitInfo(String repositoryName, String sha, String authorName,
			String authorEmail, Date authorDate, String commiterName,
			String commiterEmail, Date commiterDate, String message) {
		super();
		this.repositoryName = repositoryName;
		this.sha = sha;
		this.authorName = authorName;
		this.authorEmail = authorEmail;
		this.authorDate = authorDate;
		this.commiterName = commiterName;
		this.commiterMail = commiterEmail;
		this.commiterDate = commiterDate;
		this.message = message;
		this.userName = createUserName(authorName, authorEmail, commiterName, commiterEmail);
	}
	
	
	public void addCommitFile(LogCommitFileInfo commitFile) {
		if (logCommitFiles == null)
			logCommitFiles = new ArrayList<LogCommitFileInfo>();
		logCommitFiles.add(commitFile);
		
	}

	public static String createUserName(String authorName, String authorEmail, String commiterName, String commiterEmail){
		String userName;
		if (!authorEmail.isEmpty())
			userName = authorEmail;	
		else if (!authorName.isEmpty())
			userName = authorName + "[NoAuthorEmail]";
		else if (!commiterEmail.isEmpty())
			userName = commiterEmail + "[NoAuthor]";
		else if (!commiterName.isEmpty())
			userName = commiterName + "[NoAuthor-NoEmail]";
		else 
			userName = "[NoAuthor-NoCommiter]";	
		
		return userName;
	}
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public String getAuthorName() {
		return authorName;
	}




	public void setAuthorName(String authorName) {
		this.authorName = authorName;
	}




	public String getAuthorEmail() {
		return authorEmail;
	}




	public void setAuthorEmail(String authorEmail) {
		this.authorEmail = authorEmail;
	}




	public Date getAuthorDate() {
		return authorDate;
	}




	public void setAuthorDate(Date authorDate) {
		this.authorDate = authorDate;
	}




	public String getCommiterName() {
		return commiterName;
	}




	public void setCommiterName(String commiterName) {
		this.commiterName = commiterName;
	}




	public String getCommiterEmail() {
		return commiterMail;
	}




	public void setCommiterEmail(String commiterMail) {
		this.commiterMail = commiterMail;
	}




	public Date getCommiterDate() {
		return commiterDate;
	}




	public void setCommiterDate(Date commiterDate) {
		this.commiterDate = commiterDate;
	}




	public String getMessage() {
		return message;
	}




	public void setMessage(String message) {
		this.message = message;
	}




	public void setId(Long id) {
		this.id = id;
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




	public List<LogCommitFileInfo> getLogCommitFiles() {
		return logCommitFiles;
	}




	public void setLogCommitFiles(List<LogCommitFileInfo> logCommitFiles) {
		this.logCommitFiles = logCommitFiles;
	}



	public String getUserName() {
		return userName;
	}



	public void setUserName(String userName) {
		this.userName = userName;
	}

	


}
