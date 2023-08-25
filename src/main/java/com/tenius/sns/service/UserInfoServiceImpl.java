package com.tenius.sns.service;

import com.tenius.sns.domain.UserInfo;
import com.tenius.sns.dto.UserInfoDTO;
import com.tenius.sns.repository.UserInfoRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@Transactional
@RequiredArgsConstructor
public class UserInfoServiceImpl implements UserInfoService {
    private final UserInfoRepository userInfoRepository;
    private final ModelMapper modelMapper;

    @Override
    public UserInfoDTO getUserInfo(String uid){
        UserInfo userInfo=userInfoRepository.findById(uid).orElseThrow();
        return modelMapper.map(userInfo, UserInfoDTO.class);
    }
}
