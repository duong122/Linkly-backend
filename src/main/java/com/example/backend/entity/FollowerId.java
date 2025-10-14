package com.example.backend.entity;

import lombok.*;

import java.io.Serializable;
import java.util.Objects;

/**
 * Composite key class cho Follower entity
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FollowerId implements Serializable {

    private User follower;
    private User following;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FollowerId that = (FollowerId) o;
        return Objects.equals(follower, that.follower) &&
               Objects.equals(following, that.following);
    }

    @Override
    public int hashCode() {
        return Objects.hash(follower, following);
    }
}