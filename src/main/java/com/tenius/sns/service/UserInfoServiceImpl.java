package com.tenius.sns.service;

import com.tenius.sns.domain.StorageFile;
import com.tenius.sns.domain.UserInfo;
import com.tenius.sns.dto.*;
import com.tenius.sns.exception.InputValueException;
import com.tenius.sns.repository.PostRepository;
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
    private final PostRepository postRepository;
    private final FileService fileService;

    @Override
    public UserInfoDTO read(String uid){
        UserInfo userInfo=userInfoRepository.findByIdWithProfileImage(uid).orElseThrow();
        return UserInfoService.entityToDTO(userInfo);
    }

    /**
     * 유저 페이지 (유저 정보, 게시글 /팔로워 /팔로우 수, 작성한 /좋아요 누른 글 목록) 조회
     * @param uid 조회할 유저의 id
     * @return 유저 페이지 반환
     */
    @Override
    public UserPageDTO readPage(String uid, String myUid){
        UserInfo userInfo=userInfoRepository.findByIdWithProfileImage(uid).orElseThrow();
        UserInfoDTO userInfoDTO=UserInfoService.entityToDTO(userInfo);
        long postCount=postRepository.countByWriterUid(uid);

        PageRequestDTO pageRequestDTO=PageRequestDTO.builder().uid(uid).build();
        PageResponseDTO<PostWithStatusDTO> postPage=postRepository.search(pageRequestDTO, myUid);

        UserPageDTO userPageDTO=UserPageDTO.builder()
                .userInfo(userInfoDTO)
                .postCount(postCount)
                .postPage(postPage)
                .build();
        return userPageDTO;
    }

    @Override
    public UserInfoDTO modify(String uid, UserInfoDTO userInfoDTO) throws Exception {
        UserInfo userInfo=userInfoRepository.findById(uid).orElseThrow();

        //이전 프로필 정보
        StorageFile beforeProfile=userInfo.getProfileImage();
        String beforeFilename=null;
        if(beforeProfile!=null){
            beforeFilename=FileService.getFileName(beforeProfile.getUuid(), beforeProfile.getFileName());
        }

        //유저 정보 변경
        StorageFile profileImage=null;
        String profileName=userInfoDTO.getProfileName();

        if(beforeFilename!=null && beforeFilename.equals(profileName)){
            profileImage=beforeProfile;
        }
        else if(profileName!=null && !profileName.equals(FileService.DEFAULT_PROFILE)){
            //새로운 프로필 사진으로 변경한 경우
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

        userInfo.changeNickname(userInfoDTO.getNickname());
        userInfo.changeIntro(userInfoDTO.getIntro());
        userInfo.changeProfileImage(profileImage);

        //변경사항 저장
        userInfo=userInfoRepository.save(userInfo);

        //새로운 프로필 사진으로 변경 성공했으면, 기존 프로필 파일 삭제
        //프로필 사진 변경이 없거나, 기본 프로필 사용하는 경우는 기존 파일 유지
        if(beforeProfile!=null && !beforeFilename.equals(profileName)) {
            fileService.remove(beforeFilename);
        }

        return UserInfoService.entityToDTO(userInfo);
    }
}
