package com.githubusers.controller;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.githubusers.model.User;

@RestController
public class UserController {
	private final Logger logger = LoggerFactory.getLogger(UserController.class);
	@Autowired
	RestTemplate restTemplate;
	@Bean
	RestTemplate restTemplate() {
		return new RestTemplate();
	}
	
	@GetMapping("/followers-3-levels/{username}")
	public Set<User> getFollowers3Levels(@PathVariable String username){
		logger.warn("user followers requested");
		Set<User> users = new HashSet<>();
		getFollowersRecursive(users, username, 0);
		return users;
	}
	public Set<User> getFollowersRecursive(Set<User> users, String username, int level) {
		if(level>=3 || users.size()>5) {
			return users;
		}
		Set<User> followers = getFollowers(username);
		if(followers==null||followers.size()==0) {
			return users;
		}
		for(User follower:followers) {
			if(users.size()<5) {
				users.add(follower);
			}else {
				break;
			}
		}
		for(User follower:followers) {
			getFollowersRecursive(users, follower.getLogin(), level+1);
		}
		return users;
	}
	public Set<User> getFollowers(String username){
		HttpHeaders headers = new HttpHeaders();
	    headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
	    StringBuilder sb = new StringBuilder();
	    sb.append("https://api.github.com/users/")
	    	.append(username)
	    	.append("/followers");
		User[] response = restTemplate.getForObject(
				  sb.toString(),
				  User[].class);
		Set<User> users = Set.of(response);
		logger.warn("user followers retrieved");
		return users;
	}
}
