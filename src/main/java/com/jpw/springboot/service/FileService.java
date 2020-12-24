package com.jpw.springboot.service;

import java.util.List;

import com.jpw.springboot.model.Post;
import com.mongodb.client.gridfs.model.GridFSFile;
import com.mongodb.gridfs.GridFSDBFile;

public interface FileService {
	public GridFSFile downloadFile(String id);
}