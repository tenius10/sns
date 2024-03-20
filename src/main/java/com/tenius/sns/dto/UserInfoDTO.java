package com.tenius.sns.dto;

import com.tenius.sns.service.UserInfoService;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserInfoDTO {
    private String uid;
    @Size(min = UserInfoService.MIN_NICKNAME_LENGTH, max = UserInfoService.MAX_NICKNAME_LENGTH)
    private String nickname;
    private String profileName;
    @Size(max = UserInfoService.MAX_INTRO_LENGTH)
    @Builder.Default
    private String intro="";

    public UserInfoDTO(UserInfoDTO copy){
        this.uid=copy.uid;
        this.nickname=copy.nickname;
        this.profileName=copy.profileName;
        this.intro=copy.intro;
    }
}
