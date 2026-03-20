package com.example.revconnect.serviceimpl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.revconnect.dto.PostResponse;
import com.example.revconnect.entity.Post;
import com.example.revconnect.entity.SavedPost;
import com.example.revconnect.entity.User;
import com.example.revconnect.exception.BadRequestException;
import com.example.revconnect.exception.ResourceNotFoundException;
import com.example.revconnect.repository.PostRepository;
import com.example.revconnect.repository.SavedPostRepository;
import com.example.revconnect.repository.UserRepository;
import com.example.revconnect.service.SavedPostService;

@Service
public class SavedPostServiceImpl implements SavedPostService{
	
	@Autowired
    private SavedPostRepository savedPostRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PostRepository postRepository;

    // Save Post
    public String savePost(Long userId, Long postId) {

        if (savedPostRepository
                .findByUserUserIdAndPostPostId(userId, postId)
                .isPresent()) {

            throw new BadRequestException("Post already saved");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found"));

        SavedPost savedPost = new SavedPost();
        savedPost.setUser(user);
        savedPost.setPost(post);

        savedPostRepository.save(savedPost);

        return "Post saved successfully";
    }

    // Unsave Post
    public String unsavePost(Long userId, Long postId) {

        SavedPost savedPost = savedPostRepository
                .findByUserUserIdAndPostPostId(userId, postId)
                .orElseThrow(() -> new ResourceNotFoundException("Saved post not found"));

        savedPostRepository.delete(savedPost);

        return "Post removed from saved list";
    }

    // Get Saved Posts
    public List<PostResponse> getSavedPosts(Long userId) {

        return savedPostRepository.findByUserUserId(userId)
                .stream()
                .map(saved -> new PostResponse(saved.getPost()))
                .toList();
    }
}
