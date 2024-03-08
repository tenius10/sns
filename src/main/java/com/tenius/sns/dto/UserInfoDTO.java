package com.tenius.sns.dto;

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
    @Size(min=2, max=10)
    private String nickname;
    private String profileName;
    @Size(max=100)
    @Builder.Default
    private String intro="";

    public UserInfoDTO(UserInfoDTO copy){
        this.uid=copy.uid;
        this.nickname=copy.nickname;
        this.profileName=copy.profileName;
        this.intro=copy.intro;
    }
}
