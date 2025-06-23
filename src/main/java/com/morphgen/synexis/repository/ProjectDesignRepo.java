package com.morphgen.synexis.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.morphgen.synexis.entity.ProjectDesign;
import com.morphgen.synexis.enums.DesignStatus;

@Repository

public interface ProjectDesignRepo extends JpaRepository<ProjectDesign, Long> {
    
    int countByBillOfQuantities_boqId(Long boqId);

    List<ProjectDesign> findByBillOfQuantities_BoqIdOrderByProjectDesignIdDesc(Long boqId);

    List<ProjectDesign> findByBillOfQuantities_BoqIdAndProjectDesignStatusNotOrderByProjectDesignIdDesc(Long boqId, DesignStatus designStatus);

    boolean existsByBillOfQuantities_BoqIdAndProjectDesignStatus(Long boqId, DesignStatus status);

}
