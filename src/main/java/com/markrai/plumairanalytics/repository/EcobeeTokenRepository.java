package com.markrai.plumairanalytics.repository;

import com.markrai.plumairanalytics.model.EcobeeToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface EcobeeTokenRepository extends JpaRepository<EcobeeToken, Long> {
    @Query("SELECT e FROM EcobeeToken e")
    EcobeeToken findEcobeeToken();

    @Query("SELECT t FROM EcobeeToken t ORDER BY t.id DESC")
    EcobeeToken findTopByOrderByIdDesc();

    @Modifying
    @Query("DELETE FROM EcobeeToken t WHERE t.id NOT IN (SELECT MAX(t2.id) FROM EcobeeToken t2)")
    void deleteAllExceptLatest();
}
