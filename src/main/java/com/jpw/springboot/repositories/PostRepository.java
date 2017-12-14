package com.jpw.springboot.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.jpw.springboot.model.Post;

@Repository
public interface PostRepository extends MongoRepository<Post, String> {

	Post findByUserId(String userId);

}
