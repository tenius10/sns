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
    public String registerUser(SignUpRequestDTO signUpRequestDTO) throws InputValueException {
        // 아이디, 이메일, 닉네임 중복 검사
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

        // 유저 계정 생성
        String uid= util.generateUid();
        // 중복되지 않는 uid 인지 확인하는 코드가 들어가면 좋겠다.
        // 어지간해서는 안 겹치겠지만 겹쳤을 때 위험하니까.

        // 패스워드의 복잡성을 검사하는 코드도 필요하고
        // 이메일 형식 검사와
        // 실제로 존재하는 본인 이메일인지 확인하는 기능도 필요

        UserInfo userInfo= UserInfo.builder()
                .uid(uid)
                .nickname(signUpRequestDTO.getNickname())
                .build();

        User user=User.builder()
                .username(signUpRequestDTO.getUsername())
                .password(encoder.encode(signUpRequestDTO.getPassword()))
                .email(signUpRequestDTO.getEmail())
                .userInfo(userInfo)
                .build();

        // User, UserInfo 는 OneToOne 으로 연결되어 있기 때문에 하나만 저장
        User result = userRepository.save(user);

        // 등록에 성공한 유저 ID 반환
        return result.getUserInfo().getUid();
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
