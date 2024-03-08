package com.tenius.sns.service;

import com.tenius.sns.dto.FollowDTO;
import com.tenius.sns.dto.PageResponseDTO;
import com.tenius.sns.exception.InputValueException;

public interface FollowService {
    void follow(String followerUid, String followeeUid) throws InputValueException;
    void unfollow(String followerUid, String followeeUid);
    PageResponseDTO<FollowDTO> readFollowerPage(String cursorUid, String uid, String myUid);
    PageResponseDTO<FollowDTO> readFollowingPage(String cursorUid, String uid, String myUid);
}
