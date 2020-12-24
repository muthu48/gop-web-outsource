package com.jpw.springboot.service;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jpw.springboot.controller.PostManagementController;
import com.jpw.springboot.model.Post;
import com.jpw.springboot.repositories.PostRepository;
import com.jpw.springboot.util.SystemConstants;

@Service("PostService")
@Transactional
public class PostServiceImpl implements PostService {
	public static final Logger logger = LoggerFactory.getLogger(PostServiceImpl.class);

	@Autowired
	private PostRepository postRepository;
	
	@Autowired
	private SocialService socialService;

	public Post findById(String id) throws Exception{
		Optional<Post> oPost = postRepository.findById(id);
		Post post = null;
		if(oPost.isPresent()){
			post = (Post)oPost.get();
		}else {
			logger.info("Post not found postId " + id);

			throw new Exception("Post not found postId " + id);
		}
		
		return post;
	}

/*	public Post findByName(String name) {
		return postRepository.findByUserId(name);
	}
*/	
	public Post createPost(Post post) throws Exception{
		return postRepository.save(post);
	}
	
	public Post updatePost(Post post) throws Exception{
		return postRepository.save(post);
	}

	public void deletePostById(String id) throws Exception{
		postRepository.deleteById(id);
	}

	public void deleteAllPosts() throws Exception{
		postRepository.deleteAll();
	}

	public List<Post> findAllPosts() throws Exception{
		//Sort sort = new Sort(Sort.Direction.DESC, "_id");
/*		Pageable pageRequest = new PageRequest(10, SystemConstants.POST_PAGINATION_SIZE, sort);
		
		Page<Post> postsPage = postRepository.findAll(pageRequest); 
		List<Post> posts = postsPage.getContent();
*/
		List<Post> posts = postRepository.findAll(Sort.by(Sort.Direction.DESC, "_id"));
		return posts;
	}
	
	public List<Post> findPosts(String entityId) throws Exception{		
		//Sort sort = new Sort(Sort.Direction.DESC, "lastModifiedDate");

		//select targetEntityid from connection where sourceEntityid = entity_id and status = accepted
		List<String> connectionsIdList = socialService.getConnectionsEntityId(entityId, SystemConstants.FOLLOWING_CONNECTION);
		
		//TODO		
		//entityId can also be added to getConnectionsId
		List<Post> posts = postRepository.findByEntityIdIn(connectionsIdList, Sort.by(Sort.Direction.DESC, "lastModifiedDate"));
		return posts; 
	}

	public List<Post> findPosts(String entityId, int pageNumber) throws Exception{		

		//select targetEntityid from connection where sourceEntityid = entity_id and status = accepted
		List<String> connectionsIdList = socialService.getConnectionsEntityId(entityId, SystemConstants.FOLLOWING_CONNECTION);

		//Sort sort = new Sort(Sort.Direction.DESC, "lastModifiedDate");
		Pageable pageRequest = PageRequest.of(pageNumber, SystemConstants.POST_PAGINATION_SIZE, Sort.by(Sort.Direction.DESC, "lastModifiedDate"));
		//TODO		
		//get any new post from connections
		//also get parent posts incase any of the comments got created, changed
		List<Post> posts = postRepository.findByEntityIdIn(connectionsIdList, pageRequest);
		
		//List<Post> posts  = postRepository.findByEntityIdIn(connectionsIdList, sort);
		return posts; 
	}
	
	public List<Post> findPosts(String parentpostid, int commentLevel, int pageNumber) throws Exception{		

		//Sort sort = new Sort(Sort.Direction.DESC, "lastModifiedDate");
		Pageable pageRequest = PageRequest.of(pageNumber, SystemConstants.POST_PAGINATION_SIZE, Sort.by(Sort.Direction.DESC, "lastModifiedDate"));

		List<Post> posts = postRepository.findByParentPostId(parentpostid, commentLevel, pageRequest);

		return posts; 
	}

	public List<Post> findComments(String parentPostId, int pageNumber) throws Exception{		

		//Sort sort = new Sort(Sort.Direction.DESC, "lastModifiedDate");
		Pageable pageRequest = PageRequest.of(pageNumber, SystemConstants.POST_PAGINATION_SIZE, Sort.by(Sort.Direction.DESC, "lastModifiedDate"));

		List<Post> posts = postRepository.findByParentPostId(parentPostId, pageRequest);

		return posts; 
	}

	public int findCommentsCount(String parentPostId) throws Exception{		
		int commentsCount = 0;
		List<Post> posts = postRepository.findByParentPostId(parentPostId);
		commentsCount = posts.size();
		
		return commentsCount; 
	}
	
	public List<Post> findMyPosts(String entityId, int pageNumber) throws Exception{		
		//Sort sort = new Sort(Sort.Direction.DESC, "lastModifiedDate");
		Pageable pageRequest = PageRequest.of(pageNumber, SystemConstants.POST_PAGINATION_SIZE, Sort.by(Sort.Direction.DESC, "lastModifiedDate"));
		/*Page<Post> postsPage = postRepository.findByEntityId(entityId, pageRequest);
		List<Post> posts = postsPage.getContent();*/
		List<Post> posts = postRepository.findByEntityId(entityId, pageRequest);
		
		return posts; 
	}	
	
	public List<Post> findMyPosts(String entityId) throws Exception{		
		//Sort sort = new Sort(Sort.Direction.DESC, "lastModifiedDate");
		return postRepository.findByEntityId(entityId, Sort.by(Sort.Direction.DESC, "lastModifiedDate")); 
	}	
	
	/*	public boolean isPostExist(Post post) {
		return findByName(post.getUserId()) != null;
	}*/

}
