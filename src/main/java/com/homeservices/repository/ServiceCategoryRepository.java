package com.homeservices.repository;

import com.homeservices.entity.ServiceCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface ServiceCategoryRepository extends JpaRepository<ServiceCategory, Long> {
    List<ServiceCategory> findByActiveTrueOrderByNameAsc();
    Optional<ServiceCategory> findBySlug(String slug);
    Optional<ServiceCategory> findByIdAndActiveTrue(Long id);
}
