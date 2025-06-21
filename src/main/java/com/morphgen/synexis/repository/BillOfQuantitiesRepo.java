package com.morphgen.synexis.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.morphgen.synexis.entity.BillOfQuantities;
import com.morphgen.synexis.enums.BoqStatus;

@Repository

public interface BillOfQuantitiesRepo extends JpaRepository<BillOfQuantities, Long> {
    
    int countByJob_JobId(Long jobId);

    List<BillOfQuantities> findByJob_JobIdOrderByBoqIdDesc(Long jobId);

    List<BillOfQuantities> findByJob_JobIdAndBoqStatusOrderByBoqIdDesc(Long jobId, BoqStatus boqStatus);

}
