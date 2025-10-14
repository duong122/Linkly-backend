
package com.example.backend.repository;

import com.example.backend.entity.Follower;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.example.backend.entity.FollowerId;

@Repository
public interface FollowerRepository extends JpaRepository<Follower, FollowerId> {
    
    Long countByFollowingId(Long followingId);
    
    Long countByFollowerId(Long followerId);
    
    Boolean existsByFollowerIdAndFollowingId(Long followerId, Long followingId);

    
}