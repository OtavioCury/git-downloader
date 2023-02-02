package gaa.gitdownloader;

import gaa.model.FileInfo;

import java.util.List;

class FileInfoAux{
	List<FileInfo> files;
	int numFiles;
	public FileInfoAux(List<FileInfo> files, int numFiles) {
		super();
		this.files = files;
		this.numFiles = numFiles;
	}
	
}