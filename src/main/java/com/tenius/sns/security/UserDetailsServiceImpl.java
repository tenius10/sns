package com.tenius.sns.security;

import com.tenius.sns.domain.User;
import com.tenius.sns.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.security.Principal;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    @Autowired
    UserRepository userRepository;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user=userRepository.findByUsername(username)
                .orElseThrow(()->new UsernameNotFoundException("User Not Found with username: " + username));
        return UserDetailsImpl.build(user);
    }

    /**
     * Principal에서 Uid를 추출하는 함수
     * (@PreAuthorize에서 Principal을 UserDetailsImpl로 형변환할 수 없어서 함수로 분리)
     * @param principal
     * @return uid
     */
    public String getUidFromPrincipal(Principal principal){
        return ((UserDetailsImpl)principal).getUid();
    }
}
