package com.morphgen.synexis.service;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.morphgen.synexis.dto.ProjectDesignAssetViewDto;
import com.morphgen.synexis.dto.ProjectDesignDto;
import com.morphgen.synexis.dto.ProjectDesignTableViewDto;
import com.morphgen.synexis.dto.ProjectDesignUpdateDto;
import com.morphgen.synexis.entity.ProjectDesign;
import com.morphgen.synexis.enums.DesignStatus;

@Service

public interface ProjectDesignService {
    
    void createProjectDesign(Long boqId, ProjectDesignDto projectDesignDto);

    ProjectDesignTableViewDto viewProjectDesignTableByBoqId(Long boqId);
    ProjectDesignTableViewDto viewProjectDesignApprovalTable(Long boqId);

    ProjectDesign updateProjectDesign(Long projectDesignId, ProjectDesignUpdateDto projectDesignUpdateDto);

    ProjectDesign handleProjectDesign(Long projectDesignId, DesignStatus designStatus);

    ProjectDesignAssetViewDto viewProjectDesignByProjectDesignId(Long projectDesignId);
    ResponseEntity<byte[]> viewProjectDesign(Long designId, String disposition);

}
