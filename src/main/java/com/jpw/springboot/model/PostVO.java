package com.jpw.springboot.model;

import org.springframework.web.multipart.MultipartFile;

public class PostVO {
	private MultipartFile file;
	private Post post;
	
	public MultipartFile getFile() {
		return file;
	}
	public void setFile(MultipartFile file) {
		this.file = file;
	}
	public Post getPost() {
		return post;
	}
	public void setPost(Post post) {
		this.post = post;
	}
}