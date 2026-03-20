package com.example.revconnect.serviceimpl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.example.revconnect.dto.PostAnalyticsResponse;
import com.example.revconnect.dto.PostResponse;
import com.example.revconnect.entity.AccountType;
import com.example.revconnect.entity.Follow;
import com.example.revconnect.entity.Like;
import com.example.revconnect.entity.Post;
import com.example.revconnect.entity.User;
import com.example.revconnect.exception.BadRequestException;
import com.example.revconnect.exception.ResourceNotFoundException;
import com.example.revconnect.repository.CommentRepository;
import com.example.revconnect.repository.FollowRepository;
import com.example.revconnect.repository.LikeRepository;
import com.example.revconnect.repository.PostRepository;
import com.example.revconnect.repository.ProductRepository;
import com.example.revconnect.repository.ShareRepository;
import com.example.revconnect.repository.UserRepository;
import com.example.revconnect.service.PostService;

@Service
public class PostServiceImpl implements PostService{
	
	@Autowired
    private PostRepository postRepository;
	
    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private FollowRepository followRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private LikeRepository likeRepository;



    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ShareRepository shareRepository;

    // =========================
    // Create Post
    // =========================
    public PostResponse createPost(Long userId, Post post) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        post.setUser(user);

        if (post.getScheduledAt() != null) {
            post.setCreatedAt(post.getScheduledAt());
        }

        boolean hasProductName = post.getProductName() != null && !post.getProductName().trim().isEmpty();
        boolean hasProductLink = post.getProductLink() != null && !post.getProductLink().trim().isEmpty();
        boolean hasCtaLabel    = post.getCtaLabel()    != null && !post.getCtaLabel().trim().isEmpty();
        boolean hasCtaLink     = post.getCtaLink()     != null && !post.getCtaLink().trim().isEmpty();

        // PERSONAL cannot use any promotional fields
        if (user.getAccountType() == AccountType.PERSONAL &&
                (hasProductName || hasProductLink || hasCtaLabel || hasCtaLink)) {
            throw new BadRequestException("Personal users cannot create promotional posts");
        }

        // CREATOR cannot use product fields (only CTA allowed)
        if (user.getAccountType() == AccountType.CREATOR &&
                (hasProductName || hasProductLink)) {
            throw new BadRequestException("Creators cannot tag products");
        }

        // clean up empty strings — save null instead of ""
        if (!hasProductName) post.setProductName(null);
        if (!hasProductLink) post.setProductLink(null);
        if (!hasCtaLabel)    post.setCtaLabel(null);
        if (!hasCtaLink)     post.setCtaLink(null);

        Post savedPost = postRepository.save(post);

        return new PostResponse(savedPost);
    }

    // =========================
    // Get All Posts
    // =========================
    public List<Post> getAllPosts() {
        return postRepository.findAll();
    }

    // =========================
    // Get Post By Id
    // =========================
    public Post getPostById(Long postId) {

        return postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found"));
    }

    // =========================
    // Get Posts By User
    // =========================
    public List<Post> getPostsByUser(Long viewerId, Long targetUserId) {

        User targetUser = userRepository.findById(targetUserId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (targetUser.getIsPrivate()) {

            boolean isFollower = followRepository
                    .existsByFollowerUserIdAndFollowingUserId(viewerId, targetUserId);

            if (!isFollower && !viewerId.equals(targetUserId)) {
                throw new BadRequestException("This account is private");
            }
        }

        return postRepository
                .findByUserUserIdOrderByCreatedAtDesc(targetUserId);
    }

    public List<Post> getPostsByUser(Long userId) {

        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User not found");
        }

        return postRepository.findByUserUserIdOrderByCreatedAtDesc(userId);
    }

    // =========================
    // Edit Post
    // =========================
    public String editPost(Long userId, Long postId, String newContent) {

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found"));

        if (!post.getUser().getUserId().equals(userId)) {
            throw new BadRequestException("You are not authorized to edit this post");
        }

        post.setContent(newContent);
        postRepository.save(post);

        return "Post updated successfully";
    }

    // =========================
    // Delete Post
    // =========================
    public String deletePost(Long userId, Long postId) {

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found"));

        if (!post.getUser().getUserId().equals(userId)) {
            throw new BadRequestException("You are not authorized to delete this post");
        }

        postRepository.delete(post);

        return "Post deleted successfully";
    }

    // =========================
    // Trending Posts
    // =========================
    public List<Post> getTrendingPosts() {
        return postRepository.findTrendingPosts();
    }

    // =========================
    // Unlike Post
    // =========================
    public void unlikePost(Long userId, Long postId) {

        Like like = likeRepository
                .findByUserUserIdAndPostPostId(userId, postId)
                .orElseThrow(() -> new ResourceNotFoundException("Like not found"));

        likeRepository.delete(like);
    }

    // =========================
    // Update Post
    // =========================
    public void updatePost(Long postId, Long userId, String content) {

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found"));

        if (!post.getUser().getUserId().equals(userId)) {
            throw new BadRequestException("You are not authorized to update this post");
        }

        post.setContent(content);
        postRepository.save(post);
    }

    // =========================
    // Search Posts
    // =========================
    public List<Post> searchPosts(String keyword) {
        return postRepository.findByContentContainingIgnoreCase(keyword);
    }

    // =========================
    // Share Post
    // =========================
    public void sharePost(Long postId, Long userId) {

        Post originalPost = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Original post not found"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Post sharedPost = new Post();

        sharedPost.setUser(user);
        sharedPost.setContent(originalPost.getContent());
        sharedPost.setHashtag(originalPost.getHashtag());
        sharedPost.setOriginalPost(originalPost);
        sharedPost.setCreatedAt(LocalDateTime.now());

        postRepository.save(sharedPost);
    }

    // =========================
    // FEED
    // =========================
    public Page<PostResponse> getFeed(Long userId, Pageable pageable) {

        List<Follow> followingList = followRepository.findByFollowerUserId(userId);

        List<Long> followingIds = followingList.stream()
                .map(f -> f.getFollowing().getUserId())
                .toList();

        List<Long> feedUserIds = new ArrayList<>(followingIds);
        feedUserIds.add(userId);

        Page<Post> posts =
                postRepository.findByUserUserIdInOrderByCreatedAtDesc(feedUserIds, pageable);

        List<PostResponse> filteredPosts = posts.getContent().stream()
                .filter(post ->
                        post.getScheduledAt() == null ||
                        !post.getScheduledAt().isAfter(LocalDateTime.now()))
                .map(PostResponse::new)
                .toList();

        return new PageImpl<>(filteredPosts, pageable, posts.getTotalElements());
    }
    // =========================
    // Trending Hashtags
    // =========================
    public List<Map<String, Object>> getTrendingHashtags() {

        List<Object[]> results = postRepository.findTrendingHashtags();

        List<Map<String, Object>> response = new ArrayList<>();

        for (Object[] row : results) {

            Map<String, Object> map = new HashMap<>();
            map.put("hashtag", row[0]);
            map.put("count", row[1]);

            response.add(map);
        }

        return response;
    }

    // =========================
    // Filtered Feed
    // =========================
    public List<PostResponse> getFilteredFeed(Long userId, String type) {

        List<Post> posts;

        if (type.equalsIgnoreCase("connections")) {
            posts = postRepository.getConnectionPosts(userId);
        } else if (type.equalsIgnoreCase("following")) {
            posts = postRepository.getFollowingPosts(userId);
        } else if (type.equalsIgnoreCase("creator")) {
            posts = postRepository.getCreatorPosts();
        } else {
            throw new BadRequestException("Invalid filter type");
        }

        return posts.stream()
                .map(PostResponse::new)
                .toList();
    }

    // =========================
    // Pin Post
    // =========================
    public String pinPost(Long userId, Long postId) {

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found"));

        if (!post.getUser().getUserId().equals(userId)) {
            throw new BadRequestException("You can only pin your own posts");
        }

        post.setPinned(true);
        postRepository.save(post);

        return "Post pinned successfully";
    }

    // =========================
    // Unpin Post
    // =========================
    public String unpinPost(Long userId, Long postId) {

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found"));

        post.setPinned(false);
        postRepository.save(post);

        return "Post unpinned successfully";
    }

    public Map<String, Object> getEngagementMetrics(Long postId) {

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found"));

        long likes = likeRepository.countByPostPostId(postId);
        long comments = commentRepository.countByPostPostId(postId);
        long shares = shareRepository.countByPostPostId(postId);

        long engagement = likes + comments + shares;

        Map<String, Object> result = new HashMap<>();

        result.put("postId", post.getPostId());
        result.put("totalLikes", likes);
        result.put("totalComments", comments);
        result.put("totalShares", shares);
        result.put("totalEngagement", engagement);

        return result;
    }
    public Post viewPost(Long postId) {

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found"));

        if (post.getViewCount() == null) {
            post.setViewCount(0L);
        }

        post.setViewCount(post.getViewCount() + 1);

        return postRepository.save(post);
    }
    public List<PostResponse> searchByHashtagResponse(String tag) {

        if (!tag.startsWith("#")) {
            tag = "#" + tag;
        }

        List<Post> posts = postRepository.findByHashtagIgnoreCase(tag);

        return posts.stream()
                .map(PostResponse::new)
                .toList();
    }
    public Page<PostResponse> getPosts(Pageable pageable) {

        Page<Post> posts = postRepository.findAll(pageable);

        return posts.map(PostResponse::new);
    }

public Page<PostResponse> getSortedPosts(Pageable pageable) {

    Page<Post> posts = postRepository.findAll(pageable);

    return posts.map(PostResponse::new);
 
    }
public PostAnalyticsResponse getPostAnalytics(Long postId) {

    long totalLikes = likeRepository.countByPostPostId(postId);

    long totalComments = commentRepository.countByPostPostId(postId);

    long totalShares = shareRepository.countByPostPostId(postId);

    return new PostAnalyticsResponse(
            postId,
            totalLikes,
            totalComments,
            totalShares
    );
}
}
