package com.markrai.plumairanalytics.repository;

import com.markrai.plumairanalytics.model.Metrics;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;

@Repository
@RepositoryRestResource
public interface MetricsRepository extends JpaRepository<Metrics, Integer> {
    // custom query methods can be added here if needed
}
