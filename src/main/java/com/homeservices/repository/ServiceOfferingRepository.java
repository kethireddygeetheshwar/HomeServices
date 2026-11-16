package com.homeservices.repository;

import com.homeservices.entity.ServiceOffering;
import com.homeservices.enums.ProviderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ServiceOfferingRepository extends JpaRepository<ServiceOffering, Long> {

    @EntityGraph(attributePaths = {"category"})
    List<ServiceOffering> findByProvider_IdAndActiveTrueOrderByCreatedAtDesc(Long providerId);

    /**
     * Load the public catalogue using only stable, mandatory predicates.
     * Optional search filters are applied in MarketplaceService so that
     * NULL/blank query parameters cannot accidentally exclude every row.
     */
    @Query("""
        select s from ServiceOffering s
        join fetch s.provider p
        join fetch p.user u
        join fetch s.category c
        where s.active = true and p.status = :status and c.active = true
        order by s.createdAt desc
        """)
    List<ServiceOffering> findApprovedActiveServices(@Param("status") ProviderStatus status);

    @Query("""
        select s from ServiceOffering s
        join fetch s.provider p
        join fetch p.user u
        join fetch s.category c
        where s.id = :id and s.active = true and p.status = :status and c.active = true
        """)
    Optional<ServiceOffering> findPublicById(@Param("id") Long id,
                                               @Param("status") ProviderStatus status);

    long countByActiveTrue();
}
