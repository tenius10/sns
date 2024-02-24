package com.tenius.sns.service;

import com.tenius.sns.domain.StorageFile;
import com.tenius.sns.domain.UserInfo;
import com.tenius.sns.dto.UserInfoDTO;
import com.tenius.sns.exception.InputValueException;
import com.tenius.sns.repository.UserInfoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Log4j2
@Service
@Transactional
@RequiredArgsConstructor
public class UserInfoServiceImpl implements UserInfoService {
    private final UserInfoRepository userInfoRepository;
    private final FileService fileService;

    @Override
    public UserInfoDTO read(String uid){
        UserInfo userInfo=userInfoRepository.findByIdWithProfileImage(uid).orElseThrow();
        return UserInfoService.entityToDTO(userInfo);
    }

    @Override
    public UserInfoDTO modify(String uid, UserInfoDTO userInfoDTO) throws Exception {
        UserInfo userInfo=userInfoRepository.findById(uid).orElseThrow();
        //이전 프로필 정보
        StorageFile beforeProfile=userInfo.getProfileImage();

        //유저 정보 변경
        StorageFile profileImage=null;
        String profileName=userInfoDTO.getProfileName();
        if(profileName!=null && !profileName.equals(FileService.DEFAULT_PROFILE)){
            boolean isImage=fileService.isImageFile(profileName);
            if(isImage){
                //DB에 storageFile 저장 (첨부파일은 이미 Storage 에 업로드됨)
                String[] arr=profileName.split(FileService.FILENAME_SEPARATOR);
                if(arr.length>1){
                    profileImage = StorageFile.builder()
                            .uuid(arr[0])
                            .fileName(arr[1])
                            .ord(0)
                            .uploader(userInfo)
                            .build();
                } else{
                    throw new InputValueException(InputValueException.ERROR.INVALID_FILE_NAME);
                }
            } else{
                //프로필 사진으로 들어온 파일이 이미지가 아니라면
                fileService.remove(profileName);
                throw new InputValueException(InputValueException.ERROR.UNSUPPORTED_MEDIA_TYPE);
            }
        }
        userInfo=new UserInfo(userInfo, userInfoDTO.getNickname(), profileImage);

        //변경사항 저장
        userInfo=userInfoRepository.save(userInfo);

        //프로필 변경 성공했으면, 기존 프로필 파일 삭제
        if(beforeProfile!=null) {
            fileService.remove(FileService.getFileName(beforeProfile.getUuid(), beforeProfile.getFileName()));
        }

        return UserInfoService.entityToDTO(userInfo);
    }
}
