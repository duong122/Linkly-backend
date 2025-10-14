
package com.example.backend.mapper;

import com.example.backend.dto.response.PostResponse;
import com.example.backend.entity.Post;
import org.springframework.stereotype.Component;

@Component
public class PostMapper {
    
    public PostResponse toResponse(Post post, Long likeCount, Long commentCount, Boolean isLikedByCurrentUser) {
        return PostResponse.builder()
                .id(post.getId())
                .userId(post.getUser().getId())
                .username(post.getUser().getUsername())
                .userFullName(post.getUser().getFullName())
                .userAvatarUrl(post.getUser().getAvatarUrl())
                .caption(post.getCaption())
                .imageUrl(post.getImageUrl())
                .likeCount(likeCount)
                .commentCount(commentCount)
                .isLikedByCurrentUser(isLikedByCurrentUser)
                .createdAt(post.getCreatedAt())
                .updatedAt(post.getUpdatedAt())
                .build();
    }
    
    public PostResponse toResponse(Post post) {
        return toResponse(post, 0L, 0L, false);
    }
}