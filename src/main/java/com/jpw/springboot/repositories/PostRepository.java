package com.jpw.springboot.repositories;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.jpw.springboot.model.Post;

@Repository
public interface PostRepository extends MongoRepository<Post, String> {

	List<Post> findByEntityId(String entityId, Sort sort);
	List<Post> findByEntityIdIn(List<String> connectionsIdList, Sort sort);
}
