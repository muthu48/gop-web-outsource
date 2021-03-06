package com.jpw.springboot.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsOperations;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jpw.springboot.model.Post;
import com.jpw.springboot.repositories.PostRepository;
import com.mongodb.client.gridfs.model.GridFSFile;
import com.mongodb.gridfs.GridFSDBFile;

@Service("FileService")
@Transactional
public class FileServiceImpl implements FileService {
	@Autowired
	GridFsOperations gridOperations;
	@Autowired
	GridFsTemplate gridFsTemplate;
	public GridFSFile downloadFile(String id) {
		//GridFSDBFile file = gridOperations.findOne(new Query(Criteria.where("_id").is(id)));
		GridFSFile file = gridFsTemplate.findOne(new Query(Criteria.where("_id").is(id)));
	    return file;
	}

}
