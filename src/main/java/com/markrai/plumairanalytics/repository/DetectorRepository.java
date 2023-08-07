package com.markrai.plumairanalytics.repository;

import com.markrai.plumairanalytics.model.Detector;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RepositoryRestResource
public interface DetectorRepository extends JpaRepository<Detector, Integer> {
    Optional<Detector> findByType(String type);

}

