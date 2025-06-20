package com.morphgen.synexis.service;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.morphgen.synexis.dto.BoqDesignDto;
import com.morphgen.synexis.dto.BoqDto;
import com.morphgen.synexis.dto.BoqTableViewDto;
import com.morphgen.synexis.dto.BoqViewDto;
import com.morphgen.synexis.dto.CustomerDesignAssetViewDto;
import com.morphgen.synexis.entity.BillOfQuantities;

@Service

public interface BillOfQuantitiesService {
    
    BillOfQuantities createBOQ(BoqDto boqDto);

    BoqTableViewDto viewBoqByJobId(Long jobId);

    BoqViewDto viewBoqById(Long boqId);

    BillOfQuantities updateBOQ(Long boqId, BoqDto boqDto);

    void addCustomerDesign(Long boqId, BoqDesignDto boqDesignDto);
    CustomerDesignAssetViewDto viewCustomerDesignByBoqId(Long boqId);
    ResponseEntity<byte[]> viewCustomerDesign(Long cDesignId, String disposition);

}
