package com.tenius.sns.dto;

import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper=true)
public class FollowDTO extends UserInfoDTO {
    private boolean isFollowed;
    @Builder(builderMethodName="followDTOBuilder")
    public FollowDTO(UserInfoDTO userInfoDTO, boolean isFollowed){
        super(userInfoDTO);
        this.isFollowed=isFollowed;
    }
}
