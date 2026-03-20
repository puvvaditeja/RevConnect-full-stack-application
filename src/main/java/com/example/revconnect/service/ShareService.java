package com.example.revconnect.service;

import java.util.List;

import com.example.revconnect.dto.ShareTargetDTO;

public interface ShareService {

   
    public String sharePost(Long userId, Long postId);
    
    public String sharePostToUser(Long sharerId, Long postId, Long recipientId, String caption);
    
    public List<ShareTargetDTO> getShareTargets(Long sharerId, Long postId);
    
}
