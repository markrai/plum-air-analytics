package com.markrai.plumairanalytics.repository;

import com.markrai.plumairanalytics.model.Detector;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;

@Repository
@RepositoryRestResource
public interface DetectorRepository extends JpaRepository<Detector, Integer> {
}
