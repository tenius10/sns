package com.tenius.sns.service;

import com.tenius.sns.domain.User;
import com.tenius.sns.domain.UserInfo;
import com.tenius.sns.dto.UserInfoDTO;
import com.tenius.sns.dto.SignUpRequestDTO;
import com.tenius.sns.exception.InputValueException;
import com.tenius.sns.exception.TokenException;
import com.tenius.sns.repository.TokenBlacklistRepository;
import com.tenius.sns.repository.UserInfoRepository;
import com.tenius.sns.repository.UserRepository;

import com.tenius.sns.util.JwtUtil;
import com.tenius.sns.util.Util;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@Log4j2
@Transactional
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;
    private final UserInfoRepository userInfoRepository;
    private final TokenBlacklistRepository tokenBlacklistRepository;
    private final PasswordEncoder encoder;
    private final JwtUtil jwtUtil;
    private final Util util;

    /**
     * 회원가입
     * @param signUpRequestDTO 회원가입 정보
     * @return 유저 정보 반환
     */
    @Override
    public UserInfoDTO registerUser(SignUpRequestDTO signUpRequestDTO) throws InputValueException {
        //아이디, 이메일, 닉네임 중복 검사
        if (userRepository.existsByUsername(signUpRequestDTO.getUsername())) {
            throw new InputValueException(InputValueException.ERROR.DUPLICATE_USERNAME);
        }
        if (signUpRequestDTO.getEmail()!=null && !signUpRequestDTO.getEmail().isEmpty()) {
            if(userRepository.existsByEmail(signUpRequestDTO.getEmail()))
            throw new InputValueException(InputValueException.ERROR.DUPLICATE_EMAIL);
        }
        if(userInfoRepository.existsByNickname(signUpRequestDTO.getNickname())){
            throw new InputValueException(InputValueException.ERROR.DUPLICATE_NICKNAME);
        }

        //유저 계정 생성
        String uid= util.generateUid();
        User user=User.builder()
                .uid(uid)
                .username(signUpRequestDTO.getUsername())
                .password(encoder.encode(signUpRequestDTO.getPassword()))
                .email(signUpRequestDTO.getEmail())
                .build();
        UserInfo userInfo= UserInfo.builder()
                .uid(uid)
                .nickname(signUpRequestDTO.getNickname())
                .user(user)
                .build();
        user.initUserInfo(userInfo);

        //User, UserInfo 는 OneToOne 으로 연결되어 있기 때문에 하나만 저장
        User result=userRepository.save(user);

        return UserInfoService.entityToDTO(result.getUserInfo());
    }

    @Override
    public boolean isTokenInBlacklist(String token) throws TokenException {
        if(tokenBlacklistRepository.exists(token)){
            throw new TokenException(TokenException.TOKEN_ERROR.BLACKLISTED);
        }
        return false;
    }

    /**
     * 토큰을 블랙리스트에 등록
     * @param token 토큰
     * @param reason 블랙리스트 등록 이유
     */
    @Override
    public void registerTokenInBlacklist(String token, String reason){
        long expTime= jwtUtil.getExpirationFromJwtToken(token).getTime();
        long nowTime = System.currentTimeMillis();
        long diff = expTime - nowTime;

        tokenBlacklistRepository.save(token, reason, diff);
    }
}
