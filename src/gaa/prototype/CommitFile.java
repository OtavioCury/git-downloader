package gaa.prototype;

import gaa.model.AbstractEntity;
import gaa.model.Status;

import java.sql.Timestamp;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

public class CommitFile extends AbstractEntity implements Comparable<CommitFile> {

	private String shaKey;
	
	private String commitSha;
	private String newFileSha;
	private String oldFileSha;
	private String oldFileName;
	private String newFileName;
	private Status status;
	private String login;
	private String name;
	private String email;
	private int additions;
	private int deletions;
	private int commitId;
	private String message;
	private Date date;
	String projectName;

	public CommitFile() {
		// TODO Auto-generated constructor stub
	}

	public CommitFile(Timestamp date, String oldFileName, String newFileName, Status status, String login,
			int additions, int deletions, String commitSha, int commitId,
			String message) {
		super();
		this.date = date;
		this.oldFileName = oldFileName;
		this.newFileName = newFileName;
		this.status = status;
		this.login = login;
		this.additions = additions;
		this.deletions = deletions;
		this.commitSha = commitSha;
		this.commitId = commitId;
		this.message = message;
	}


	public CommitFile(Timestamp date, String oldFileName, String newFileName, Status status, String login,
			String name, String email, int additions, int deletions,
			String commitSha, String oldFileSha, String newFileSHA, int commitId, String message) {
		super();
		this.oldFileName = oldFileName;
		this.newFileName = newFileName;
		this.status = status;
		this.login = login;
		this.name = name;
		this.email = email;
		this.additions = additions;
		this.deletions = deletions;
		this.commitSha = commitSha;
		this.oldFileSha = oldFileSha;
		this.newFileSha = newFileSHA;
//		setShaKey(commitSha+oldFileSha+newFileSHA);
		this.commitId = commitId;
		this.message = message;
		this.date = date;
	}

//	public Long getId() {
//		id = null;
//		return id;
//	}
//
//	public void setId(final Long id) {
//		this.id = null;
//	}

	@Override
	public String toString() {
		return commitSha + ", " + date + ", " + newFileName + ", " + status + ", " + login + ", " + name + ", " + email; //+ ", " + additions  + ", " + deletions + ", " + sha + ", " + commitId + ", " + message; 
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

	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}


	public String getEmail() {
		return email;
	}


	public void setEmail(String email) {
		this.email = email;
	}


	public int getAdditions() {
		return additions;
	}

	public void setAdditions(int additions) {
		this.additions = additions;
	}

	public int getDeletions() {
		return deletions;
	}

	public void setDeletions(int deletions) {
		this.deletions = deletions;
	}

	public String getCommitSha() {
		return commitSha;
	}

	public void setCommitSha(String sha) {
		this.commitSha = sha;
	}

	public int getCommitId() {
		return commitId;
	}

	public void setCommitId(int commitId) {
		this.commitId = commitId;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
	@Override
	public int compareTo(CommitFile o) {
//		if (this.status == Status.ADDED)
//			if (o.status == Status.ADDED)
//				return 0;
//			else 
//				return -1;
		return this.date.compareTo(o.date);
	}
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}

	public String getProjectName() {
		return projectName;
	}

	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}

	public String getOldFileName() {
		return oldFileName;
	}

	public void setOldFileName(String oldFileName) {
		this.oldFileName = oldFileName;
	}

	public String getNewFileSha() {
		return newFileSha;
	}

	public void setNewFileSha(String fileSha) {
		this.newFileSha = fileSha;
	}

	public String getOldFileSha() {
		return oldFileSha;
	}

	public void setOldFileSha(String oldFileSha) {
		this.oldFileSha = oldFileSha;
	}

	public String getShaKey() {
		return shaKey;
	}

	public void setShaKey(String shaKey) {
		this.shaKey = shaKey;
	}



}
