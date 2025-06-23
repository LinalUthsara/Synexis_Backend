package com.morphgen.synexis.service.serviceImpl;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.morphgen.synexis.dto.DesignDto;
import com.morphgen.synexis.dto.DesignViewDto;
import com.morphgen.synexis.dto.ProjectDesignAssetViewDto;
import com.morphgen.synexis.dto.ProjectDesignDto;
import com.morphgen.synexis.dto.ProjectDesignTableDto;
import com.morphgen.synexis.dto.ProjectDesignTableViewDto;
import com.morphgen.synexis.dto.ProjectDesignUpdateDto;
import com.morphgen.synexis.entity.BillOfQuantities;
import com.morphgen.synexis.entity.Design;
import com.morphgen.synexis.entity.ProjectDesign;
import com.morphgen.synexis.enums.BoqStatus;
import com.morphgen.synexis.enums.DesignStatus;
import com.morphgen.synexis.exception.DesignProcessingException;
import com.morphgen.synexis.exception.IllegalStatusTransitionException;
import com.morphgen.synexis.exception.InvalidInputException;
import com.morphgen.synexis.exception.InvalidStatusException;
import com.morphgen.synexis.exception.ProjectDesignNotFoundException;
import com.morphgen.synexis.repository.BillOfQuantitiesRepo;
import com.morphgen.synexis.repository.DesignRepo;
import com.morphgen.synexis.repository.ProjectDesignRepo;
import com.morphgen.synexis.service.ProjectDesignService;
import com.morphgen.synexis.utils.ProjectDesignUrlUtil;

@Service

public class ProjectDesignServiceImpl implements ProjectDesignService {

    @Autowired
    private ProjectDesignRepo projectDesignRepo;

    @Autowired
    private DesignRepo designRepo;

    @Autowired
    private BillOfQuantitiesRepo billOfQuantitiesRepo;

    private Design createDesign(MultipartFile deisgn, ProjectDesign projectDesign) throws IOException {
        
        Design pDesign = new Design();
        pDesign.setDesignName(deisgn.getOriginalFilename());
        pDesign.setDesignType(deisgn.getContentType());
        pDesign.setDesignSize(deisgn.getSize());
        pDesign.setDesignData(deisgn.getBytes());
        pDesign.setProjectDesign(projectDesign);
        return pDesign;
    }

    @Override
    @Transactional
    public void createProjectDesign(Long boqId, ProjectDesignDto projectDesignDto) {

        if(boqId == null){
            throw new InvalidInputException("BOQ Id cannot be empty!");
        }
        
        BillOfQuantities billOfQuantities = billOfQuantitiesRepo.findById(boqId)
                .orElseThrow(() -> new DesignProcessingException("BOQ ID: " + boqId + " is not found!"));

        if (billOfQuantities.getBoqStatus() != BoqStatus.SUBMITTED) {
            throw new InvalidStatusException("Adding project design requires submitted design assets!");
        }

        int currentCount = projectDesignRepo.countByBillOfQuantities_boqId(boqId);

        String projectDesignVersion = String.format("%03d", currentCount + 1);

        ProjectDesign projectDesign = new ProjectDesign();

        projectDesign.setProjectDesignVersion(projectDesignVersion);
        projectDesign.setBillOfQuantities(billOfQuantities);
        projectDesign.setProjectDesignStatus(projectDesignDto.getDesignStatus());

        List<Design> designs = new ArrayList<>();

        if (projectDesignDto.getDesigns() != null && !projectDesignDto.getDesigns().isEmpty()) {
    
            for (MultipartFile pDesign : projectDesignDto.getDesigns()) {
                if (pDesign != null) {
                    try {
                        Design design = createDesign(pDesign, projectDesign);
                        designs.add(design);
                    } catch (IOException e) {
                        throw new DesignProcessingException("Unable to process design. Please ensure the design is valid and try again!");
                    }
                }
            }
        }
        projectDesign.setDesigns(designs);

        projectDesignRepo.save(projectDesign);
    }

    @Override
    public ProjectDesignTableViewDto viewProjectDesignTableByBoqId(Long boqId) {
        
        BillOfQuantities billOfQuantities = billOfQuantitiesRepo.findById(boqId)
        .orElseThrow(() -> new DesignProcessingException("BOQ ID: " + boqId + " is not found!"));
        
        List<ProjectDesign> designs = projectDesignRepo.findByBillOfQuantities_BoqIdOrderByProjectDesignIdDesc(boqId);

        ProjectDesignTableViewDto projectDesignTableViewDto = new ProjectDesignTableViewDto();

        projectDesignTableViewDto.setBoqId(billOfQuantities.getBoqId());
        projectDesignTableViewDto.setProjectName(billOfQuantities.getJob().getEstimation().getInquiry().getProjectName());

        List<ProjectDesignTableDto> projectDesignTableDtoList = designs.stream().map(design ->{

            ProjectDesignTableDto projectDesignTableDto = new ProjectDesignTableDto();

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MMMM-yyyy");

            projectDesignTableDto.setProjectDesignId(design.getProjectDesignId());
            projectDesignTableDto.setProjectDesignVersion(design.getProjectDesignVersion());
            projectDesignTableDto.setProjectDesignStatus(design.getProjectDesignStatus());
            projectDesignTableDto.setLastModifiedDate(design.getUpdatedAt().format(formatter));

            return projectDesignTableDto;
        }).collect(Collectors.toList());

        projectDesignTableViewDto.setProjectDesigns(projectDesignTableDtoList);
        
        return projectDesignTableViewDto;
    }

    @Override
    public ProjectDesignAssetViewDto viewProjectDesignByProjectDesignId(Long projectDesignId) {
        
        ProjectDesign projectDesign = projectDesignRepo.findById(projectDesignId)
        .orElseThrow(() -> new ProjectDesignNotFoundException("Project Design ID: " + projectDesignId + " is not found!"));

        ProjectDesignAssetViewDto projectDesignAssetViewDto = new ProjectDesignAssetViewDto();

        projectDesignAssetViewDto.setProjectDesignId(projectDesign.getProjectDesignId());
        projectDesignAssetViewDto.setBoqId(projectDesign.getBillOfQuantities().getBoqId());
        projectDesignAssetViewDto.setProjectDesignStatus(projectDesign.getProjectDesignStatus());

        if (projectDesign.getDesigns() != null){

            List<Design> designs = projectDesign.getDesigns();

            List<DesignViewDto> designViewDtoList = designs.stream().map(design ->{

                DesignViewDto designViewDto = new DesignViewDto();

                String designUrl = ProjectDesignUrlUtil.constructProjectDesignUrl(design.getDesignId());
                designViewDto.setDesignId(design.getDesignId());
                designViewDto.setDesignName(design.getDesignName());
                designViewDto.setDesignUrl(designUrl);
                
                return designViewDto;

            }).collect(Collectors.toList());

            projectDesignAssetViewDto.setDesignViewList(designViewDtoList); 
        }

        return projectDesignAssetViewDto;
    }

    @Override
    public ResponseEntity<byte[]> viewProjectDesign(Long designId, String disposition) {
        
        Design design = designRepo.findById(designId)
        .orElseThrow(() -> new ProjectDesignNotFoundException("Design ID: " + designId + " is not found or has no file data!"));

        String mimeType = design.getDesignType() != null ? design.getDesignType() : "application/octet-stream";

        String originalDesignName = design.getDesignName();

        String contentDisposition;
        try {

            String encodedFilename = java.net.URLEncoder.encode(originalDesignName, "UTF-8")
                                .replace("+", "%20");
        
        contentDisposition = disposition + "; filename=\"" + originalDesignName + "\"; filename*=UTF-8''" + encodedFilename;
        } catch (Exception e) {

            contentDisposition = disposition + "; filename=\"" + originalDesignName + "\"";
        }
        
        return ResponseEntity.ok()
            .contentType(MediaType.parseMediaType(mimeType))
            .header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition)
            .header(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, "Content-Disposition")
            .body(design.getDesignData());
    }

    @Override
    public ProjectDesignTableViewDto viewProjectDesignApprovalTable(Long boqId) {
        
        BillOfQuantities billOfQuantities = billOfQuantitiesRepo.findById(boqId)
        .orElseThrow(() -> new DesignProcessingException("BOQ ID: " + boqId + " is not found!"));
        
        List<ProjectDesign> designs = projectDesignRepo.findByBillOfQuantities_BoqIdAndProjectDesignStatusNotOrderByProjectDesignIdDesc(boqId, DesignStatus.DRAFT);

        ProjectDesignTableViewDto projectDesignTableViewDto = new ProjectDesignTableViewDto();

        projectDesignTableViewDto.setBoqId(billOfQuantities.getBoqId());
        projectDesignTableViewDto.setProjectName(billOfQuantities.getJob().getEstimation().getInquiry().getProjectName());

        List<ProjectDesignTableDto> projectDesignTableDtoList = designs.stream().map(design ->{

            ProjectDesignTableDto projectDesignTableDto = new ProjectDesignTableDto();

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MMMM-yyyy");

            projectDesignTableDto.setProjectDesignId(design.getProjectDesignId());
            projectDesignTableDto.setProjectDesignVersion(design.getProjectDesignVersion());
            projectDesignTableDto.setProjectDesignStatus(design.getProjectDesignStatus());
            projectDesignTableDto.setLastModifiedDate(design.getUpdatedAt().format(formatter));

            return projectDesignTableDto;
        }).collect(Collectors.toList());

        projectDesignTableViewDto.setProjectDesigns(projectDesignTableDtoList);
        
        return projectDesignTableViewDto;
    }

    @Override
    @Transactional
    public ProjectDesign updateProjectDesign(Long projectDesignId, ProjectDesignUpdateDto projectDesignUpdateDto) {
        
        ProjectDesign projectDesign = projectDesignRepo.findById(projectDesignId)
        .orElseThrow(() -> new ProjectDesignNotFoundException("Project Design ID: " + projectDesignId + " is not found!"));

        if (projectDesign.getProjectDesignStatus() != DesignStatus.DRAFT){
            throw new InvalidStatusException("Cannot update a project design that has already been submitted!");
        }

        projectDesign.setProjectDesignStatus(projectDesignUpdateDto.getDesignStatus());

        if (projectDesignUpdateDto.getDesigns() != null){

            List<Design> existingDesigns = projectDesign.getDesigns();

            if (existingDesigns == null){

                existingDesigns = new ArrayList<>();
            }

            Map<Long, Design> existingMap = existingDesigns.stream()
                .filter(dsgn -> dsgn.getDesignId() != null)
                .collect(Collectors.toMap(Design::getDesignId, dsgn ->dsgn));

            List<Design> updatedDesigns = new ArrayList<>();

            for (DesignDto design : projectDesignUpdateDto.getDesigns()){
                Long designId = design.getDesignId();
                MultipartFile newDesign = design.getDesign();

                if (designId != null && existingMap.containsKey(designId)){

                    Design existingDesign = existingMap.get(designId);

                    if (newDesign != null && !newDesign.isEmpty()){

                        try{
                            existingDesign.setDesignName(newDesign.getOriginalFilename());
                            existingDesign.setDesignType(newDesign.getContentType());
                            existingDesign.setDesignSize(newDesign.getSize());
                            existingDesign.setDesignData(newDesign.getBytes());
                            updatedDesigns.add(existingDesign);
                            existingMap.remove(designId);
                        }
                        catch (IOException e){

                            throw new DesignProcessingException("Unable to process design. Please ensure the design is valid and try again!");
                        }
                    }
                }
                else {

                    if (newDesign != null && !newDesign.isEmpty()) {

                        try{

                            Design newPDesign = createDesign(newDesign, projectDesign);
                            updatedDesigns.add(newPDesign);
                        }
                        catch (IOException e){

                            throw new DesignProcessingException("Unable to process design. Please ensure the design is valid and try again!");
                        }
                    }
                }
            }

            for (Design leftover : existingMap.values()) {
                designRepo.delete(leftover);
            }

            projectDesign.setDesigns(updatedDesigns);
        }

        return projectDesign;
    }

    @Override
    public ProjectDesign handleProjectDesign(Long projectDesignId, DesignStatus designStatus) {
        
        ProjectDesign projectDesign = projectDesignRepo.findById(projectDesignId)
        .orElseThrow(() -> new ProjectDesignNotFoundException("Project Design ID: " + projectDesignId + " is not found!"));

        DesignStatus existingStatus = projectDesign.getProjectDesignStatus();

        boolean isValidTransition = false;

        switch (existingStatus) {
            case SUBMITTED:
                isValidTransition = (designStatus == DesignStatus.ACCEPTED || designStatus == DesignStatus.REJECTED);
                break;
            case ACCEPTED:
                isValidTransition = (designStatus == DesignStatus.REJECTED);
                break;
            case REJECTED:
                isValidTransition = (designStatus == DesignStatus.ACCEPTED);
                break;
            default:
                throw new InvalidStatusException("Unknown existing status: " + existingStatus);
        }

        if (!isValidTransition) {
            throw new IllegalStatusTransitionException("Transition from " + existingStatus + " to " + designStatus + " is not valid!");
        }

        if (designStatus == DesignStatus.ACCEPTED) {
        Long boqId = projectDesign.getBillOfQuantities().getBoqId();
        boolean alreadyAcceptedExists = projectDesignRepo.existsByBillOfQuantities_BoqIdAndProjectDesignStatus(boqId, DesignStatus.ACCEPTED);

        if (alreadyAcceptedExists && existingStatus != DesignStatus.ACCEPTED) {
            throw new IllegalStatusTransitionException(
                "Another project design under BOQ Version " + projectDesign.getBillOfQuantities().getBoqVersion() + " is already ACCEPTED.");
        }
    }

        projectDesign.setProjectDesignStatus(designStatus);

        ProjectDesign updatedProjectDesign = projectDesignRepo.save(projectDesign);

        return updatedProjectDesign;
    }
    
}
