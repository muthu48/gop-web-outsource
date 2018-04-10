package com.jpw.springboot.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jpw.springboot.model.Post;
import com.jpw.springboot.repositories.PostRepository;

@Service("PostService")
@Transactional
public class PostServiceImpl implements PostService {

	@Autowired
	private PostRepository postRepository;

	public Post findById(String id) {
		return postRepository.findOne(id);
	}

	public Post findByName(String name) {
		return postRepository.findByUserId(name);
	}
	
	public Post createPost(Post post) {
		return postRepository.insert(post);
	}
	
/*	public void savePost(Post post) {
		postRepository.save(post);
	}
*/
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

	public boolean isPostExist(Post post) {
		return findByName(post.getUserId()) != null;
	}

}
