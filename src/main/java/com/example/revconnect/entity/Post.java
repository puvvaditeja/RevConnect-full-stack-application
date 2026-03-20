package com.example.revconnect.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long postId;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    
    @Column
    private Long viewCount = 0L;
    
    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;
    
    @Column
    private Boolean pinned = false;
    
    @Column(length = 500)
    private String productName;

    @Column(length = 1000)
    private String productLink;
    
    @Column
    private LocalDateTime scheduledAt;
   
    private String imageUrl;

    
    @ManyToOne
    @JoinColumn(name = "original_post_id")
    private Post originalPost;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Like> likes;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Share> shares;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @Column
    private String hashtag;

    private String content;

    private LocalDateTime createdAt;
    
    @Column(length = 500)
    private String ctaLabel;   // button text

    @Column(length = 1000)
    private String ctaLink;    // button URL

    public Post() {}

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

	public Long getPostId() {
		return postId;
	}

	public void setPostId(Long postId) {
		this.postId = postId;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getHashtag() {
		return hashtag;
	}

	public void setHashtag(String hashtag) {
		this.hashtag = hashtag;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}
	public Boolean getPinned() {
	    return pinned;
	}

	public void setPinned(Boolean pinned) {
	    this.pinned = pinned;
	}

	public LocalDateTime getScheduledAt() {
	    return scheduledAt;
	}

	public void setScheduledAt(LocalDateTime scheduledAt) {
	    this.scheduledAt = scheduledAt;
	}
	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}
	
	public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    
	  public Post getOriginalPost() {
	        return originalPost;
	    }

	    public void setOriginalPost(Post originalPost) {
	        this.originalPost = originalPost;
	    }
	    
	    public String getCtaLabel() {
	        return ctaLabel;
	    }

	    public void setCtaLabel(String ctaLabel) {
	        this.ctaLabel = ctaLabel;
	    }

	    public String getCtaLink() {
	        return ctaLink;
	    }

	    public void setCtaLink(String ctaLink) {
	        this.ctaLink = ctaLink;
	    }
	    public Product getProduct() {
	        return product;
	    }

	    public void setProduct(Product product) {
	        this.product = product;
	    }
	    
	    public String getProductName() {
	        return productName;
	    }

	    public void setProductName(String productName) {
	        this.productName = productName;
	    }

	    public String getProductLink() {
	        return productLink;
	    }

	    public void setProductLink(String productLink) {
	        this.productLink = productLink;
	    }
	    public Long getViewCount() {
	        return viewCount;
	    }

	    public void setViewCount(Long viewCount) {
	        this.viewCount = viewCount;
	    }
}