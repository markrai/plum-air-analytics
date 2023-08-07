package com.markrai.plumairanalytics.repository;

import com.markrai.plumairanalytics.model.EcobeeToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface EcobeeTokenRepository extends JpaRepository<EcobeeToken, Long> {
    @Query("SELECT e FROM EcobeeToken e")
    EcobeeToken findEcobeeToken();

    @Query("SELECT e FROM EcobeeToken e JOIN FETCH e.detector")
    EcobeeToken findEcobeeTokenFetchDetector();
}
