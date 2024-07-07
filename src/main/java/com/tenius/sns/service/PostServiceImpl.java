package com.tenius.sns.service;

import com.tenius.sns.domain.*;
import com.tenius.sns.dto.*;
import com.tenius.sns.exception.InputValueException;
import com.tenius.sns.repository.CommentRepository;
import com.tenius.sns.repository.PostLikeRepository;
import com.tenius.sns.repository.PostRepository;
import com.tenius.sns.repository.UserInfoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Log4j2
@Transactional
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {
    private final PostRepository postRepository;
    private final PostLikeRepository postLikeRepository;
    private final CommentRepository commentRepository;
    private final UserInfoRepository userInfoRepository;
    private final FileService fileService;

    @Override
    public Long register(PostInputDTO postInputDTO, String myUid) throws InputValueException {
        // 작성자의 유저 정보 가져오기
        UserInfo userInfo=userInfoRepository.findById(myUid).orElseThrow();

        // Post 엔티티 생성
        Post post=Post.builder()
                .content(postInputDTO.getContent())
                .writer(userInfo)
                .build();

        // StorageFile 엔티티를 Post 엔티티에 추가
        List<String> fileNames=postInputDTO.getFileNames();
        if(fileNames!=null){
            for(String fileName: fileNames){
                String[] arr=fileName.split(FileService.FILENAME_SEPARATOR);
                if(arr.length>1) post.addFile(arr[0], arr[1]);
                else throw new InputValueException(InputValueException.ERROR.INVALID_FILE_NAME);
            }
        }

        // Post 엔티티를 DB에 저장
        Post result=postRepository.save(post);

        // 등록된 게시글의 ID 반환
        return result.getPno();
    }

    @Override
    public PostDTO readWithFiles(Long pno) {
        Post result=postRepository.findByIdWithFiles(pno).orElseThrow();
        return PostService.entityToDTO(result);
    }

    @Override
    public PostWithStatusDTO viewWithStatus(Long pno, String myUid){
        // 조회수 올리기
        Post post=postRepository.findById(pno).orElseThrow();
        post.changeViews(post.getViews()+1);
        postRepository.save(post);

        // 게시글 정보 가져오기
        PostWithStatusDTO result=postRepository.findByIdWithAll(pno, myUid).orElseThrow();

        return result;
    }

    @Override
    public Long modify(Long pno, PostInputDTO postInputDTO) throws Exception {
        // 이전 게시글 정보 가져오기
        Post post=postRepository.findByIdWithFiles(pno).orElseThrow();
        List<String> beforeFileNames=post.getFiles().stream()
                .map(file->FileService.getFileName(file.getUuid(), file.getFileName()))
                .collect(Collectors.toList());

        // 내용 수정
        post.changeContent(postInputDTO.getContent());
        post.clearFiles();

        // 새로운 이미지 추가
        List<String> fileNames=postInputDTO.getFileNames();
        if(fileNames!=null){
            for(String fileName: fileNames){
                String[] arr=fileName.split(FileService.FILENAME_SEPARATOR);
                if(arr.length>1) post.addFile(arr[0], arr[1]);
                else throw new InputValueException(InputValueException.ERROR.INVALID_FILE_NAME);
            }
        }

        // 변경사항 저장
        postRepository.save(post);

        // 수정 성공하고 나서 이전에 있던 이미지 삭제
        List<String> removeFileNames;
        if(fileNames!=null){
            removeFileNames=beforeFileNames.stream()
                    .filter((fileName)->!fileNames.contains(fileName))
                    .collect(Collectors.toList());
        } else{
            removeFileNames=beforeFileNames;
        }
        fileService.remove(removeFileNames);

        // 수정된 게시글의 ID 반환
        return pno;
    }

    @Override
    public void remove(Long pno) throws Exception {
        Post post=postRepository.findByIdWithFiles(pno).orElseThrow();
        PostDTO postDTO=PostService.entityToDTO(post);

        // 게시글 삭제 (DB 상에서, 게시글 삭제)
        postRepository.deleteById(pno);

        // 첨부파일 삭제 (스토리지 상에서, 파일 삭제)
        fileService.remove(postDTO.getFileNames());
    }

    @Override
    public PageResponseDTO<PostWithStatusDTO> readPage(PageRequestDTO pageRequestDTO, SearchOptionDTO searchOptionDTO, String myUid) {
        // 페이지 조회
        PageResponseDTO<PostWithStatusDTO> result=postRepository.search(pageRequestDTO, searchOptionDTO, myUid);
        return result;
    }

    @Override
    public boolean isPostWriter(Long pno, String myUid){
        // 게시글 정보 가져오기
        Post post=postRepository.findById(pno).orElseThrow();

        // 게시글 작성자와 myUid 를 비교하여 반환
        return post.getWriter().getUid().equals(myUid);
    }

    @Override
    public Long like(Long pno, String myUid){
        // 게시글 좋아요 정보를 DB에 저장
        PostLike postLike=PostLike.builder().pno(pno).uid(myUid).build();
        postLikeRepository.save(postLike);

        // 좋아요 누른 게시글의 ID 반환
        return pno;
    }

    @Override
    public Long unlike(Long pno, String myUid){
        // 게시글 좋아요 정보를 DB에서 삭제
        PostLikeKey postLikeKey=PostLikeKey.builder().pno(pno).uid(myUid).build();
        postLikeRepository.deleteById(postLikeKey);

        // 좋아요 취소한 게시글의 ID 반환
        return pno;
    }
}