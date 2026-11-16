package com.homeservices.repository;

import com.homeservices.entity.ProviderProfile;
import com.homeservices.enums.ProviderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.EntityGraph;
import java.util.List;
import java.util.Optional;

public interface ProviderProfileRepository extends JpaRepository<ProviderProfile, Long> {
    Optional<ProviderProfile> findByUser_Id(Long userId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select p from ProviderProfile p where p.id = :id")
    Optional<ProviderProfile> findByIdForUpdate(@Param("id") Long id);
    @EntityGraph(attributePaths = {"user"})
    List<ProviderProfile> findByStatusOrderByCreatedAtDesc(ProviderStatus status);
    long countByStatus(ProviderStatus status);
}
