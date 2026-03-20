package com.example.revconnect.service;

import com.example.revconnect.dto.ProfileDTO;
import com.example.revconnect.entity.Profile;


public interface ProfileService {

    
    public ProfileDTO getProfile(Long userId);


    public ProfileDTO updateProfile(Long userId, Profile updatedProfile) ;
    
}