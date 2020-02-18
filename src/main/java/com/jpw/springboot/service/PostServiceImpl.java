package com.jpw.springboot.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jpw.springboot.model.Connection;
import com.jpw.springboot.model.Post;
import com.jpw.springboot.repositories.PostRepository;

@Service("PostService")
@Transactional
public class PostServiceImpl implements PostService {

	@Autowired
	private PostRepository postRepository;
	
	@Autowired
	private SocialService socialService;

	public Post findById(String id) {
		return postRepository.findOne(id);
	}

/*	public Post findByName(String name) {
		return postRepository.findByUserId(name);
	}
*/	
	public Post createPost(Post post) {
		return postRepository.insert(post);
	}
	
	public Post updatePost(Post post) {
		return postRepository.save(post);
	}

	public void deletePostById(String id) {
		postRepository.delete(id);
	}

	public void deleteAllPosts() {
		postRepository.deleteAll();
	}

	public List<Post> findAllPosts() {
		return postRepository.findAll(new Sort(Sort.Direction.DESC, "_id"));
	}
	
	public List<Post> findAllPosts(String entityId) {		
		//select targetEntityid from connection where sourceEntityid = entity_id and status = accepted
		List<String> connectionsIdList = socialService.getConnectionsEntityId(entityId, "FOLLOWING");
		
		//TODO		
		//entityId can also be added to getConnectionsId
		Sort sort = new Sort(Sort.Direction.DESC, "createdDate");
		return postRepository.findByEntityIdIn(connectionsIdList, sort); 
	}
	
/*	public boolean isPostExist(Post post) {
		return findByName(post.getUserId()) != null;
	}*/

}
