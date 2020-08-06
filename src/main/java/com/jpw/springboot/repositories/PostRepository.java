package com.jpw.springboot.repositories;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import com.jpw.springboot.model.Post;

@Repository
public interface PostRepository extends MongoRepository<Post, String> {

	List<Post> findByEntityId(String entityId, Sort sort);
	List<Post> findByEntityId(String entityId, Pageable pageable);
	List<Post> findByEntityIdIn(List<String> connectionsIdList, Sort sort);

	//@Query("{'parentPostId' : null}")
    //@Query("{'parentPostId' : {$ne : null, $ne : ''}}")
    //@Query("{'entityId':{$in:?0}}, '$or':[ {'parentPostId':null}, {'parentPostId':''} ]")
	@Query("{'entityId':{$in:?0}, 'parentPostId':null}")
	List<Post> findByEntityIdIn(List<String> connectionsIdList, Pageable pageable);
    
    @Query("{'parentPostId':?0}")
	List<Post> findByParentPostId(String parentPostId);
    
    @Query("{'parentPostId':?0, 'commentlevel':?1}")
	List<Post> findByParentPostId(String parentPostId, int commentLevel, Pageable pageable);
    
    @Query("{'parentPostId':?0}")
	List<Post> findByParentPostId(String parentPostId, Pageable pageable);
}
