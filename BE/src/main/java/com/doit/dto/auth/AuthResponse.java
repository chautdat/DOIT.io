package com.doit.dto.auth;

import com.doit.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {
    
    private String accessToken;
    private String refreshToken;
    private String tokenType;
    private String expiresIn;
    private UserInfo user;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserInfo {
        private String id;
        private String email;
        private String fullName;
        private String avatarUrl;
        private String role;
        private Double currentBand;
        private Double targetBand;
        private Boolean placementTestCompleted;
    }
    
    public static AuthResponse of(String accessToken, String refreshToken, Long expiresIn, User user) {
        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(String.valueOf(expiresIn))
                .user(UserInfo.builder()
                        .id(user.getId())
                        .email(user.getEmail())
                        .fullName(user.getFullName())
                        .avatarUrl(user.getAvatarUrl())
                        .role(user.getRole().name())
                        .currentBand(user.getCurrentBand())
                        .targetBand(user.getTargetBand())
                        .placementTestCompleted(user.getPlacementTestCompleted())
                        .build())
                .build();
    }
}
