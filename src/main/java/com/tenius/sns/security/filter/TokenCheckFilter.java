package com.tenius.sns.security.filter;

import com.tenius.sns.exception.TokenException;
import com.tenius.sns.security.UserDetailsServiceImpl;
import com.tenius.sns.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Log4j2
@RequiredArgsConstructor
public class TokenCheckFilter extends OncePerRequestFilter {
    private final JwtUtil jwtUtil;
    private final UserDetailsServiceImpl userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        log.info("Token Check Filter.........");

        try{
            String accessToken= jwtUtil.getAccessTokenFromCookies(request);

            if(accessToken!=null && jwtUtil.validateToken(accessToken)){
                String username= jwtUtil.getUserNameFromJwtToken(accessToken);
                UserDetails userDetails=userDetailsService.loadUserByUsername(username);

                UsernamePasswordAuthenticationToken authentication= new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }
        catch(TokenException e){
            log.error("TokenException: "+e.getMessage());
            e.sendResponseError(response);
        }

        filterChain.doFilter(request, response);
    }
}
