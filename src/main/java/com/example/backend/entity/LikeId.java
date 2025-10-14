package com.example.backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LikeId implements Serializable {
    
    @Column(name = "user_id")
    private Long userId;
    
    @Column(name = "post_id")
    private Long postId;
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LikeId likeId = (LikeId) o;
        return Objects.equals(userId, likeId.userId) && 
               Objects.equals(postId, likeId.postId);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(userId, postId);
    }
}