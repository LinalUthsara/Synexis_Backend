package com.morphgen.synexis.service.serviceImpl;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.morphgen.synexis.dto.AttachmentDto;
import com.morphgen.synexis.dto.AttachmentViewDto;
import com.morphgen.synexis.dto.CableBusbarDto;
import com.morphgen.synexis.dto.EnclosureDto;
import com.morphgen.synexis.dto.FloorDimensionDto;
import com.morphgen.synexis.dto.JobCreateDto;
import com.morphgen.synexis.dto.JobDto;
import com.morphgen.synexis.dto.JobSideDropViewDto;
import com.morphgen.synexis.dto.JobTableViewDto;
import com.morphgen.synexis.dto.JobViewDto;
import com.morphgen.synexis.dto.MaterialFinishDto;
import com.morphgen.synexis.dto.TechnicalSpecificationDto;
import com.morphgen.synexis.dto.WallDimensionDto;
import com.morphgen.synexis.entity.Attachment;
import com.morphgen.synexis.entity.CableBusbar;
import com.morphgen.synexis.entity.CostEstimation;
import com.morphgen.synexis.entity.Enclosure;
import com.morphgen.synexis.entity.FloorDimension;
import com.morphgen.synexis.entity.Job;
import com.morphgen.synexis.entity.MaterialFinish;
import com.morphgen.synexis.entity.TechnicalSpecification;
import com.morphgen.synexis.entity.WallDimension;
import com.morphgen.synexis.enums.Action;
import com.morphgen.synexis.enums.EstimationStatus;
import com.morphgen.synexis.enums.JobStatus;
import com.morphgen.synexis.exception.AttachmentNotFoundException;
import com.morphgen.synexis.exception.AttachmentProcessingException;
import com.morphgen.synexis.exception.CostEstimationNotFoundException;
import com.morphgen.synexis.exception.IllegalStatusTransitionException;
import com.morphgen.synexis.exception.ImageProcessingException;
import com.morphgen.synexis.exception.InvalidStatusException;
import com.morphgen.synexis.exception.JobNotFoundException;
import com.morphgen.synexis.repository.AttachmentRepo;
import com.morphgen.synexis.repository.CableBusbarRepo;
import com.morphgen.synexis.repository.CostEstimationRepo;
import com.morphgen.synexis.repository.EnclosureRepo;
import com.morphgen.synexis.repository.FloorDimensionRepo;
import com.morphgen.synexis.repository.JobRepo;
import com.morphgen.synexis.repository.MaterialFinishRepo;
import com.morphgen.synexis.repository.TechnicalSpecificationRepo;
import com.morphgen.synexis.repository.WallDimensionRepo;
import com.morphgen.synexis.service.ActivityLogService;
import com.morphgen.synexis.service.JobService;
import com.morphgen.synexis.utils.AttachmentUrlUtil;

@Service

public class JobServiceImpl implements JobService {

    @Autowired
    private JobRepo jobRepo;

    @Autowired
    private ActivityLogService activityLogService;

    @Autowired
    private CostEstimationRepo costEstimationRepo;

    @Autowired
    private TechnicalSpecificationRepo technicalSpecificationRepo;

    @Autowired
    private FloorDimensionRepo floorDimensionRepo;

    @Autowired
    private WallDimensionRepo wallDimensionRepo;

    @Autowired
    private EnclosureRepo enclosureRepo;

    @Autowired
    private CableBusbarRepo cableBusbarRepo;

    @Autowired
    private MaterialFinishRepo materialFinishRepo;

    @Autowired
    private AttachmentRepo attachmentRepo;



    private Attachment createAttachment(MultipartFile attachmentFile, Job job) throws IOException {
        
        Attachment attachment = new Attachment();
        attachment.setFilename(attachmentFile.getOriginalFilename());
        attachment.setFileType(attachmentFile.getContentType());
        attachment.setFileSize(attachmentFile.getSize());
        attachment.setFileData(attachmentFile.getBytes());
        attachment.setJob(job);
        return attachment;
    }

    @Override
    @Transactional
    public Job createJob(JobCreateDto jobDto) {
        
        CostEstimation estimation = costEstimationRepo.findById(jobDto.getEstimationId())
        .orElseThrow(() -> new CostEstimationNotFoundException("Cost Estimation ID: " + jobDto.getEstimationId() + " is not found!"));

        if (estimation.getEstimationStatus() != EstimationStatus.ACCEPTED) {
            throw new InvalidStatusException("Job registration requires an approved estimation!");
        }

        Optional<Job> existingEstimationId = jobRepo.findByEstimation(estimation);
            if(existingEstimationId.isPresent()){
                throw new DataIntegrityViolationException("A Job under the Quotation: " + estimation.getQuotationVersion() + " already exists!");
            }

        Job job = new Job();

        job.setEstimation(estimation);
        job.setJobDeliveryTime(jobDto.getJobDeliveryTime());
        job.setJobDeliveryPoint(jobDto.getJobDeliveryPoint());
        job.setConsultant(jobDto.getConsultant());
        job.setContractor(jobDto.getContractor());
        job.setSubContractor(jobDto.getSubContractor());
        job.setGrossProfit(jobDto.getGrossProfit());
        job.setPaymentType(jobDto.getPaymentType());

        job.setInvoiceType(jobDto.getInvoiceType());

        List<Attachment> jobAttachments = new ArrayList<>();

        if (jobDto.getAttachments() != null && !jobDto.getAttachments().isEmpty()) {
    
            for (MultipartFile attachmentFile : jobDto.getAttachments()) {
                if (attachmentFile != null) {
                    try {
                        Attachment attachment = createAttachment(attachmentFile, job);
                        jobAttachments.add(attachment);
                    } catch (IOException e) {
                        throw new ImageProcessingException("Unable to process image. Please ensure the image is valid and try again!");
                    }
                }
            }
        }
        job.setAttachments(jobAttachments);

        TechnicalSpecificationDto specDto = jobDto.getSpecificationDto();
        if (specDto != null) {
            TechnicalSpecification spec = new TechnicalSpecification();
            spec.setFloorMounting(specDto.getFloorMounting());
            spec.setWallMounting(specDto.getWallMounting());
            spec.setRemarks(specDto.getRemarks());

            if (specDto.getFloorDimensionDto() != null) {
                FloorDimension floorDim = mapFloorDimension(specDto.getFloorDimensionDto());
                floorDimensionRepo.save(floorDim);
                spec.setFloorDimension(floorDim);
            }

            if (specDto.getWallDimensionDto() != null) {
                WallDimension wallDim = mapWallDimension(specDto.getWallDimensionDto());
                wallDimensionRepo.save(wallDim);
                spec.setWallDimension(wallDim);
            }

            if (specDto.getEnclosureDto() != null) {
                Enclosure enclosure = mapEnclosure(specDto.getEnclosureDto());
                enclosureRepo.save(enclosure);
                spec.setEnclosure(enclosure);
            }

            if (specDto.getCableBusbarDto() != null) {
                CableBusbar cable = mapCableBusbar(specDto.getCableBusbarDto());
                cableBusbarRepo.save(cable);
                spec.setCableBusbar(cable);
            }

            if (specDto.getMaterialFinishDto() != null) {
                MaterialFinish material = mapMaterialFinish(specDto.getMaterialFinishDto());
                materialFinishRepo.save(material);
                spec.setMaterialFinish(material);
            }

            technicalSpecificationRepo.save(spec);
            job.setSpecification(spec);
        }

        Job newJob = jobRepo.save(job);

        activityLogService.logActivity(
            "Job", 
            newJob.getJobId(),
            estimation.getInquiry().getProjectName(),
            Action.CREATE, 
            "Created Estimation: " + estimation.getInquiry().getProjectName());

            return newJob;
    }

    private FloorDimension mapFloorDimension(FloorDimensionDto dto) {
        FloorDimension fd = new FloorDimension();
        fd.setFloorFramework(dto.getFloorFramework());
        fd.setFloorBaseFrame(dto.getFloorBaseFrame());
        fd.setFloorPartition(dto.getFloorPartition());
        fd.setFloorDoor(dto.getFloorDoor());
        fd.setFloorMountingPlate(dto.getFloorMountingPlate());
        fd.setFloorEscutcheon(dto.getFloorEscutcheon());
        fd.setFloorCoveringPanels(dto.getFloorCoveringPanels());
        fd.setFloorTopCover(dto.getFloorTopCover());
        fd.setFloorTopCoverMaterial(dto.getFloorTopCoverMaterial());
        fd.setFloorBottomPlate(dto.getFloorBottomPlate());
        fd.setFloorBottomPlateMaterial(dto.getFloorBottomPlateMaterial());
        return fd;
    }

    private WallDimension mapWallDimension(WallDimensionDto dto) {
        WallDimension wd = new WallDimension();
        wd.setWallLid(dto.getWallLid());
        wd.setWallShelf(dto.getWallShelf());
        wd.setWallDoor(dto.getWallDoor());
        wd.setWallCoverPlate(dto.getWallCoverPlate());
        wd.setWallMountingPlate(dto.getWallMountingPlate());
        wd.setWallTopGlandPlate(dto.getWallTopGlandPlate());
        wd.setWallTopGlandPlateMaterial(dto.getWallTopGlandPlateMaterial());
        wd.setWallBottomGlandPlate(dto.getWallBottomGlandPlate());
        wd.setWallBottomGlandPlateMaterial(dto.getWallBottomGlandPlateMaterial());
        return wd;
    }

    private Enclosure mapEnclosure(EnclosureDto dto) {
        Enclosure e = new Enclosure();
        e.setSurfaceTypeOutdoor(dto.getSurfaceTypeOutdoor());
        e.setSurfaceTypeIndoor(dto.getSurfaceTypeIndoor());
        e.setFlushTypeOutdoor(dto.getFlushTypeOutdoor());
        e.setFlushTypeIndoor(dto.getFlushTypeIndoor());
        e.setFreestandingTypeOutdoor(dto.getFreestandingTypeOutdoor());
        e.setFreestandingTypeIndoor(dto.getFreestandingTypeIndoor());
        e.setLidTypeOutdoor(dto.getLidTypeOutdoor());
        e.setLidTypeIndoor(dto.getLidTypeIndoor());
        e.setOutdoorWallOutdoor(dto.getOutdoorWallOutdoor());
        e.setOutdoorWallIndoor(dto.getOutdoorWallIndoor());
        e.setFeederPillarOutdoor(dto.getFeederPillarOutdoor());
        e.setFeederPillarIndoor(dto.getFeederPillarIndoor());
        return e;
    }

    private CableBusbar mapCableBusbar(CableBusbarDto dto) {
        CableBusbar cb = new CableBusbar();
        cb.setBusbarIncoming(dto.getBusbarIncoming());
        cb.setBusbarOutgoing(dto.getBusbarOutgoing());
        cb.setTopIncoming(dto.getTopIncoming());
        cb.setTopOutgoing(dto.getTopOutgoing());
        cb.setBottomIncoming(dto.getBottomIncoming());
        cb.setBottomOutgoing(dto.getBottomOutgoing());
        cb.setLeftIncoming(dto.getLeftIncoming());
        cb.setLeftOutgoing(dto.getLeftOutgoing());
        cb.setRightIncoming(dto.getRightIncoming());
        cb.setRightOutgoing(dto.getRightOutgoing());
        cb.setRearTopIncoming(dto.getRearTopIncoming());
        cb.setRearTopOutgoing(dto.getRearTopOutgoing());
        cb.setRearBottomIncoming(dto.getRearBottomIncoming());
        cb.setRearBottomOutgoing(dto.getRearBottomOutgoing());
        return cb;
    }

    private MaterialFinish mapMaterialFinish(MaterialFinishDto dto) {
        MaterialFinish mf = new MaterialFinish();
        mf.setSheetMaterial(dto.getSheetMaterial());
        mf.setPaintingThickness(dto.getPaintingThickness());
        mf.setPrimer(dto.getPrimer());
        mf.setPowderCoating(dto.getPowderCoating());
        mf.setSurfaceType(dto.getSurfaceType());
        mf.setHDG(dto.getHDG());
        mf.setColor(dto.getColor());
        mf.setCustomColor(dto.getCustomColor());
        return mf;
    }

    @Override
    public ResponseEntity<byte[]> viewAttachment(Long attachmentId, String disposition) {
        
        Attachment attachment = attachmentRepo.findById(attachmentId)
        .orElseThrow(() -> new AttachmentNotFoundException("Attachment ID: " + attachmentId + " is not found or has no file data!"));

        String mimeType = attachment.getFileType() != null ? attachment.getFileType() : "application/octet-stream";

        String originalAttachmentName = attachment.getFilename();

        String contentDisposition;
        try {

            String encodedFilename = java.net.URLEncoder.encode(originalAttachmentName, "UTF-8")
                                .replace("+", "%20");
        
        contentDisposition = disposition + "; filename=\"" + originalAttachmentName + "\"; filename*=UTF-8''" + encodedFilename;
        } catch (Exception e) {

            contentDisposition = disposition + "; filename=\"" + originalAttachmentName + "\"";
        }
        
        return ResponseEntity.ok()
            .contentType(MediaType.parseMediaType(mimeType))
            .header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition)
            .header(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, "Content-Disposition")
            .body(attachment.getFileData());
    }

    @Override
    public List<JobTableViewDto> viewJobTable() {
        
        List<Job> jobs = jobRepo.findAllByOrderByJobIdDesc();

        List<JobTableViewDto> jobTableViewDtoList = jobs.stream().map(job -> {

            JobTableViewDto jobTableViewDto = new JobTableViewDto();

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MMMM-yyyy");

            jobTableViewDto.setJobId(job.getJobId());
            jobTableViewDto.setProjectName(job.getEstimation().getInquiry().getProjectName());
            jobTableViewDto.setQuotationVersion(job.getEstimation().getQuotationVersion());
            jobTableViewDto.setCustomerName(job.getEstimation().getInquiry().getCustomer().getCustomerPrefix() + " " + job.getEstimation().getInquiry().getCustomer().getCustomerFirstName() + " " + job.getEstimation().getInquiry().getCustomer().getCustomerLastName());
            jobTableViewDto.setJobReturnDate(job.getEstimation().getInquiry().getProjectReturnDate().format(formatter));
            jobTableViewDto.setJobStatus(job.getJobStatus());

            return jobTableViewDto;

        }).collect(Collectors.toList());

        return jobTableViewDtoList;
    }

    @Override
    public List<JobSideDropViewDto> viewJobSideDrop() {
        
        List<Job> jobs = jobRepo.findAllByOrderByJobIdDesc();

        List<JobSideDropViewDto> jobSideDropViewDtoList = jobs.stream().map(job -> {

            JobSideDropViewDto jobSideDropViewDto = new JobSideDropViewDto();

            jobSideDropViewDto.setJobId(job.getJobId());
            jobSideDropViewDto.setProjectName(job.getEstimation().getInquiry().getProjectName());
            jobSideDropViewDto.setQuotationVersion(job.getEstimation().getQuotationVersion());
            jobSideDropViewDto.setProjectType(job.getEstimation().getInquiry().getProjectType());

            return jobSideDropViewDto;

        }).collect(Collectors.toList());

        return jobSideDropViewDtoList;
    }

    @Override
    public JobViewDto viewJobById(Long jobId) {
        
        Job job = jobRepo.findById(jobId)
        .orElseThrow(() -> new JobNotFoundException("Job ID: " + jobId + " is not found!"));

        JobViewDto jobViewDto = new JobViewDto();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MMMM-yyyy");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("hh:mm a");


        jobViewDto.setJobId(job.getJobId());
        jobViewDto.setProjectName(job.getEstimation().getInquiry().getProjectName());
        jobViewDto.setQuotationVersion(job.getEstimation().getQuotationVersion());
        jobViewDto.setSalesPersonName(job.getEstimation().getInquiry().getSalesPerson().getEmployeePrefix() + " " + job.getEstimation().getInquiry().getSalesPerson().getEmployeeFirstName() + " " + job.getEstimation().getInquiry().getSalesPerson().getEmployeeLastName());
        jobViewDto.setEstimatorName(job.getEstimation().getInquiry().getEstimator().getEmployeePrefix() + " " + job.getEstimation().getInquiry().getEstimator().getEmployeeFirstName() + " " + job.getEstimation().getInquiry().getEstimator().getEmployeeLastName());
        jobViewDto.setCustomerName(job.getEstimation().getInquiry().getCustomer().getCustomerPrefix() + " " + job.getEstimation().getInquiry().getCustomer().getCustomerFirstName() + " " + job.getEstimation().getInquiry().getCustomer().getCustomerLastName());
        jobViewDto.setCustomerAddress(job.getEstimation().getInquiry().getCustomer().getCustomerAddress().getAddressLine1() + " " + job.getEstimation().getInquiry().getCustomer().getCustomerAddress().getAddressLine2() + " " + job.getEstimation().getInquiry().getCustomer().getCustomerAddress().getCity() + " [" + job.getEstimation().getInquiry().getCustomer().getCustomerAddress().getZipCode() + "]" );
        jobViewDto.setProjectType(job.getEstimation().getInquiry().getProjectType());
        jobViewDto.setJobStatus(job.getJobStatus());
        jobViewDto.setJobNumber(String.format("%05d", job.getJobId()));
        jobViewDto.setJobDeliveryDate(job.getEstimation().getInquiry().getProjectReturnDate().format(formatter));
        jobViewDto.setJobDeliveryTime(job.getJobDeliveryTime().format(timeFormatter));
        jobViewDto.setJobDeliveryPoint(job.getJobDeliveryPoint());
        jobViewDto.setConsultant(job.getConsultant());
        jobViewDto.setContractor(job.getContractor());
        jobViewDto.setSubContractor(job.getSubContractor());
        jobViewDto.setGrossProfit(job.getGrossProfit());
        jobViewDto.setPaymentType(job.getPaymentType());
        jobViewDto.setInvoiceType(job.getInvoiceType());
        jobViewDto.setCustomInvoiceType(job.getCustomInvoiceType());
        jobViewDto.setEstimationId(job.getEstimation().getEstimationId());

        List<Attachment> attachments = job.getAttachments();


        if (attachments != null && !attachments.isEmpty()){

            List<AttachmentViewDto> attachmentViewDtoList = attachments.stream().map(attachment -> {

            AttachmentViewDto attachmentViewDto = new AttachmentViewDto();

            String attachmentUrl = AttachmentUrlUtil.constructAttachmentUrl(attachment.getId());
            attachmentViewDto.setAttachmentId(attachment.getId());
            attachmentViewDto.setAttachmentName(attachment.getFilename());
            attachmentViewDto.setAttachmentUrl(attachmentUrl);

            return attachmentViewDto;

        }).collect(Collectors.toList());

        jobViewDto.setAttachmentDtoList(attachmentViewDtoList);

        }
        
        if (job.getSpecification() != null) {
        TechnicalSpecification spec = job.getSpecification();
        TechnicalSpecificationDto specDto = new TechnicalSpecificationDto();
        specDto.setSpecificationId(spec.getSpecificationId());
        specDto.setFloorMounting(spec.getFloorMounting());
        specDto.setWallMounting(spec.getWallMounting());
        specDto.setRemarks(spec.getRemarks());

        if (spec.getFloorDimension() != null) {
            FloorDimensionDto floorDto = new FloorDimensionDto();
            floorDto.setFloorDimensionId(spec.getFloorDimension().getFloorDimensionId());
            floorDto.setFloorFramework(spec.getFloorDimension().getFloorFramework());
            floorDto.setFloorBaseFrame(spec.getFloorDimension().getFloorBaseFrame());
            floorDto.setFloorPartition(spec.getFloorDimension().getFloorPartition());
            floorDto.setFloorDoor(spec.getFloorDimension().getFloorDoor());
            floorDto.setFloorMountingPlate(spec.getFloorDimension().getFloorMountingPlate());
            floorDto.setFloorEscutcheon(spec.getFloorDimension().getFloorEscutcheon());
            floorDto.setFloorCoveringPanels(spec.getFloorDimension().getFloorCoveringPanels());
            floorDto.setFloorTopCover(spec.getFloorDimension().getFloorTopCover());
            floorDto.setFloorTopCoverMaterial(spec.getFloorDimension().getFloorTopCoverMaterial());
            floorDto.setFloorBottomPlate(spec.getFloorDimension().getFloorBottomPlate());
            floorDto.setFloorBottomPlateMaterial(spec.getFloorDimension().getFloorBottomPlateMaterial());
            specDto.setFloorDimensionDto(floorDto);
        }

        if (spec.getWallDimension() != null) {
            WallDimensionDto wallDto = new WallDimensionDto();
            wallDto.setWallDimensionId(spec.getWallDimension().getWallDimensionId());
            wallDto.setWallLid(spec.getWallDimension().getWallLid());
            wallDto.setWallShelf(spec.getWallDimension().getWallShelf());
            wallDto.setWallDoor(spec.getWallDimension().getWallDoor());
            wallDto.setWallCoverPlate(spec.getWallDimension().getWallCoverPlate());
            wallDto.setWallMountingPlate(spec.getWallDimension().getWallMountingPlate());
            wallDto.setWallTopGlandPlate(spec.getWallDimension().getWallTopGlandPlate());
            wallDto.setWallTopGlandPlateMaterial(spec.getWallDimension().getWallTopGlandPlateMaterial());
            wallDto.setWallBottomGlandPlate(spec.getWallDimension().getWallBottomGlandPlate());
            wallDto.setWallBottomGlandPlateMaterial(spec.getWallDimension().getWallBottomGlandPlateMaterial());
            specDto.setWallDimensionDto(wallDto);
        }

        if (spec.getEnclosure() != null) {
            EnclosureDto enclosureDto = new EnclosureDto();
            enclosureDto.setEnclosureId(spec.getEnclosure().getEnclosureId());
            enclosureDto.setSurfaceTypeOutdoor(spec.getEnclosure().getSurfaceTypeOutdoor());
            enclosureDto.setSurfaceTypeIndoor(spec.getEnclosure().getSurfaceTypeIndoor());
            enclosureDto.setFlushTypeOutdoor(spec.getEnclosure().getFlushTypeOutdoor());
            enclosureDto.setFlushTypeIndoor(spec.getEnclosure().getFlushTypeIndoor());
            enclosureDto.setFreestandingTypeOutdoor(spec.getEnclosure().getFreestandingTypeOutdoor());
            enclosureDto.setFreestandingTypeIndoor(spec.getEnclosure().getFreestandingTypeIndoor());
            enclosureDto.setLidTypeOutdoor(spec.getEnclosure().getLidTypeOutdoor());
            enclosureDto.setLidTypeIndoor(spec.getEnclosure().getLidTypeIndoor());
            enclosureDto.setOutdoorWallOutdoor(spec.getEnclosure().getOutdoorWallOutdoor());
            enclosureDto.setOutdoorWallIndoor(spec.getEnclosure().getOutdoorWallIndoor());
            enclosureDto.setFeederPillarOutdoor(spec.getEnclosure().getFeederPillarOutdoor());
            enclosureDto.setFeederPillarIndoor(spec.getEnclosure().getFeederPillarIndoor());
            specDto.setEnclosureDto(enclosureDto);
        }

        if (spec.getCableBusbar() != null) {
            CableBusbarDto cableDto = new CableBusbarDto();
            cableDto.setCableBusbarId(spec.getSpecificationId());
            cableDto.setBusbarIncoming(spec.getCableBusbar().getBusbarIncoming());
            cableDto.setBusbarOutgoing(spec.getCableBusbar().getBusbarOutgoing());
            cableDto.setTopIncoming(spec.getCableBusbar().getTopIncoming());
            cableDto.setTopOutgoing(spec.getCableBusbar().getTopOutgoing());
            cableDto.setBottomIncoming(spec.getCableBusbar().getBottomIncoming());
            cableDto.setBottomOutgoing(spec.getCableBusbar().getBottomOutgoing());
            cableDto.setLeftIncoming(spec.getCableBusbar().getLeftIncoming());
            cableDto.setLeftOutgoing(spec.getCableBusbar().getLeftOutgoing());
            cableDto.setRightIncoming(spec.getCableBusbar().getRightIncoming());
            cableDto.setRightOutgoing(spec.getCableBusbar().getRightOutgoing());
            cableDto.setRearTopIncoming(spec.getCableBusbar().getRearTopIncoming());
            cableDto.setRearTopOutgoing(spec.getCableBusbar().getRearTopOutgoing());
            cableDto.setRearBottomIncoming(spec.getCableBusbar().getRearBottomIncoming());
            cableDto.setRearBottomOutgoing(spec.getCableBusbar().getRearBottomOutgoing());
            specDto.setCableBusbarDto(cableDto);
        }

        if (spec.getMaterialFinish() != null) {
            MaterialFinishDto materialDto = new MaterialFinishDto();
            materialDto.setMaterialFinishId(spec.getMaterialFinish().getMaterialFinishId());
            materialDto.setSheetMaterial(spec.getMaterialFinish().getSheetMaterial());
            materialDto.setPaintingThickness(spec.getMaterialFinish().getPaintingThickness());
            materialDto.setPrimer(spec.getMaterialFinish().getPrimer());
            materialDto.setPowderCoating(spec.getMaterialFinish().getPowderCoating());
            materialDto.setSurfaceType(spec.getMaterialFinish().getSurfaceType());
            materialDto.setHDG(spec.getMaterialFinish().getHDG());
            materialDto.setColor(spec.getMaterialFinish().getColor());
            materialDto.setCustomColor(spec.getMaterialFinish().getCustomColor());
            specDto.setMaterialFinishDto(materialDto);
        }

        jobViewDto.setSpecificationDto(specDto);
    }

        return jobViewDto;

    }

    @Override
    @Transactional
    public Job updateJob(Long jobId, JobDto jobDto) {
        
        Job existingJob = jobRepo.findById(jobId)
        .orElseThrow(() -> new JobNotFoundException("Job ID: " + jobId + " is not found!"));

        Job job = updateJobFields(existingJob, jobDto);

        if (jobDto.getAttachments() != null){

            updateAttachments(existingJob, jobDto.getAttachments());
        }
        return job;
    }

    private void updateAttachments(Job existingJob, List<AttachmentDto> attachments) {
        
        List<Attachment> updatedAttachments = processAttachments(existingJob, attachments);

        existingJob.setAttachments(updatedAttachments);
            
        jobRepo.save(existingJob);

    }


    private List<Attachment> processAttachments(Job existingJob, List<AttachmentDto> attachmentDtos) {

        List<Attachment> existingAttachments = existingJob.getAttachments();
            
        if (existingAttachments == null) {

            existingAttachments = new ArrayList<>();
        }

         Map<Long, Attachment> existingMap = existingAttachments.stream()
            .filter(att -> att.getId() != null)
            .collect(Collectors.toMap(Attachment::getId, att -> att));

        List<Attachment> updatedAttachments = new ArrayList<>();

        for (AttachmentDto attachment : attachmentDtos) {
            Long attachmentId = attachment.getAttachmentId();
            MultipartFile newFile = attachment.getAttachment();

            if (attachmentId != null && existingMap.containsKey(attachmentId)) {
            
                Attachment existingAttachment = existingMap.get(attachmentId);
                
                if (newFile !=null && !newFile.isEmpty()){

                    try {
                        
                    existingAttachment.setFilename(newFile.getOriginalFilename());
                    existingAttachment.setFileType(newFile.getContentType());
                    existingAttachment.setFileData(newFile.getBytes());
                    existingAttachment.setFileSize(newFile.getSize());
                    updatedAttachments.add(existingAttachment);
                    existingMap.remove(attachmentId);
                    } 
                    catch (IOException e) {
                        throw new AttachmentProcessingException("Unable to process attachment. Please ensure the attachment is valid and try again!");
                    }
                }   
            } 
            else {
            
                if (newFile != null && !newFile.isEmpty()) {
                    
                    try {
                        Attachment newAttachment = createAttachment(newFile, existingJob);
                        updatedAttachments.add(newAttachment);
                    } 
                    catch (IOException e) {
                        throw new AttachmentProcessingException("Unable to process attachment. Please ensure the attachment is valid and try again!");
                    }
                }
            }
        }

        for (Attachment leftover : existingMap.values()) {
            attachmentRepo.delete(leftover);
        }
        
        return updatedAttachments;

    }

    private Job updateJobFields(Job existingJob, JobDto jobDto){

        existingJob.setJobDeliveryTime(jobDto.getJobDeliveryTime());
        existingJob.setJobDeliveryPoint(jobDto.getJobDeliveryPoint());
        existingJob.setConsultant(jobDto.getConsultant());
        existingJob.setContractor(jobDto.getContractor());
        existingJob.setSubContractor(jobDto.getSubContractor());
        existingJob.setGrossProfit(jobDto.getGrossProfit());
        existingJob.setPaymentType(jobDto.getPaymentType());
        existingJob.setInvoiceType(jobDto.getInvoiceType());
        existingJob.setCustomInvoiceType(jobDto.getCustomInvoiceType());


        TechnicalSpecificationDto specDto = jobDto.getSpecificationDto();

        if (specDto != null) {
            updateTechnicalSpecification(existingJob, specDto);
        }

        return jobRepo.save(existingJob);

    }

    private void updateTechnicalSpecification(Job existingJob, TechnicalSpecificationDto specDto) {

        TechnicalSpecification spec = existingJob.getSpecification();
        if (spec == null || (specDto.getSpecificationId() != null && !spec.getSpecificationId().equals(specDto.getSpecificationId()))) {
            spec = technicalSpecificationRepo.findById(specDto.getSpecificationId()).orElse(new TechnicalSpecification());
        }

        spec.setFloorMounting(specDto.getFloorMounting());
        spec.setWallMounting(specDto.getWallMounting());
        spec.setRemarks(specDto.getRemarks());

        // Floor Dimension
        if (specDto.getFloorDimensionDto() != null) {
            FloorDimensionDto fdDto = specDto.getFloorDimensionDto();
            FloorDimension fd = spec.getFloorDimension();
            if (fd == null || (fdDto.getFloorDimensionId() != null && !fd.getFloorDimensionId().equals(fdDto.getFloorDimensionId()))) {
                fd = floorDimensionRepo.findById(fdDto.getFloorDimensionId()).orElse(new FloorDimension());
            }
            fd.setFloorFramework(fdDto.getFloorFramework());
            fd.setFloorBaseFrame(fdDto.getFloorBaseFrame());
            fd.setFloorPartition(fdDto.getFloorPartition());
            fd.setFloorDoor(fdDto.getFloorDoor());
            fd.setFloorMountingPlate(fdDto.getFloorMountingPlate());
            fd.setFloorEscutcheon(fdDto.getFloorEscutcheon());
            fd.setFloorCoveringPanels(fdDto.getFloorCoveringPanels());
            fd.setFloorTopCover(fdDto.getFloorTopCover());
            fd.setFloorTopCoverMaterial(fdDto.getFloorTopCoverMaterial());
            fd.setFloorBottomPlate(fdDto.getFloorBottomPlate());
            fd.setFloorBottomPlateMaterial(fdDto.getFloorBottomPlateMaterial());
            floorDimensionRepo.save(fd);
            spec.setFloorDimension(fd);
        }

        // Wall Dimension
        if (specDto.getWallDimensionDto() != null) {
            WallDimensionDto wdDto = specDto.getWallDimensionDto();
            WallDimension wd = spec.getWallDimension();
            if (wd == null || (wdDto.getWallDimensionId() != null && !wd.getWallDimensionId().equals(wdDto.getWallDimensionId()))) {
                wd = wallDimensionRepo.findById(wdDto.getWallDimensionId()).orElse(new WallDimension());
            }
            wd.setWallLid(wdDto.getWallLid());
            wd.setWallShelf(wdDto.getWallShelf());
            wd.setWallDoor(wdDto.getWallDoor());
            wd.setWallCoverPlate(wdDto.getWallCoverPlate());
            wd.setWallMountingPlate(wdDto.getWallMountingPlate());
            wd.setWallTopGlandPlate(wdDto.getWallTopGlandPlate());
            wd.setWallTopGlandPlateMaterial(wdDto.getWallTopGlandPlateMaterial());
            wd.setWallBottomGlandPlate(wdDto.getWallBottomGlandPlate());
            wd.setWallBottomGlandPlateMaterial(wdDto.getWallBottomGlandPlateMaterial());
            wallDimensionRepo.save(wd);
            spec.setWallDimension(wd);
        }

        // Enclosure
        if (specDto.getEnclosureDto() != null) {
            EnclosureDto dto = specDto.getEnclosureDto();
            Enclosure enc = spec.getEnclosure();
            if (enc == null || (dto.getEnclosureId() != null && !enc.getEnclosureId().equals(dto.getEnclosureId()))) {
                enc = enclosureRepo.findById(dto.getEnclosureId()).orElse(new Enclosure());
            }
            enc.setSurfaceTypeOutdoor(dto.getSurfaceTypeOutdoor());
            enc.setSurfaceTypeIndoor(dto.getSurfaceTypeIndoor());
            enc.setFlushTypeOutdoor(dto.getFlushTypeOutdoor());
            enc.setFlushTypeIndoor(dto.getFlushTypeIndoor());
            enc.setFreestandingTypeOutdoor(dto.getFreestandingTypeOutdoor());
            enc.setFreestandingTypeIndoor(dto.getFreestandingTypeIndoor());
            enc.setLidTypeOutdoor(dto.getLidTypeOutdoor());
            enc.setLidTypeIndoor(dto.getLidTypeIndoor());
            enc.setOutdoorWallOutdoor(dto.getOutdoorWallOutdoor());
            enc.setOutdoorWallIndoor(dto.getOutdoorWallIndoor());
            enc.setFeederPillarOutdoor(dto.getFeederPillarOutdoor());
            enc.setFeederPillarIndoor(dto.getFeederPillarIndoor());
            enclosureRepo.save(enc);
            spec.setEnclosure(enc);
        }

        // Cable Busbar
        if (specDto.getCableBusbarDto() != null) {
            CableBusbarDto dto = specDto.getCableBusbarDto();
            CableBusbar cb = spec.getCableBusbar();
            if (cb == null || (dto.getCableBusbarId() != null && !cb.getCableBusbarId().equals(dto.getCableBusbarId()))) {
                cb = cableBusbarRepo.findById(dto.getCableBusbarId()).orElse(new CableBusbar());
            }
            cb.setBusbarIncoming(dto.getBusbarIncoming());
            cb.setBusbarOutgoing(dto.getBusbarOutgoing());
            cb.setTopIncoming(dto.getTopIncoming());
            cb.setTopOutgoing(dto.getTopOutgoing());
            cb.setBottomIncoming(dto.getBottomIncoming());
            cb.setBottomOutgoing(dto.getBottomOutgoing());
            cb.setLeftIncoming(dto.getLeftIncoming());
            cb.setLeftOutgoing(dto.getLeftOutgoing());
            cb.setRightIncoming(dto.getRightIncoming());
            cb.setRightOutgoing(dto.getRightOutgoing());
            cb.setRearTopIncoming(dto.getRearTopIncoming());
            cb.setRearTopOutgoing(dto.getRearTopOutgoing());
            cb.setRearBottomIncoming(dto.getRearBottomIncoming());
            cb.setRearBottomOutgoing(dto.getRearBottomOutgoing());
            cableBusbarRepo.save(cb);
            spec.setCableBusbar(cb);
        }

        // Material Finish
        if (specDto.getMaterialFinishDto() != null) {
            MaterialFinishDto dto = specDto.getMaterialFinishDto();
            MaterialFinish mf = spec.getMaterialFinish();
            if (mf == null || (dto.getMaterialFinishId() != null && !mf.getMaterialFinishId().equals(dto.getMaterialFinishId()))) {
                mf = materialFinishRepo.findById(dto.getMaterialFinishId()).orElse(new MaterialFinish());
            }
            mf.setSheetMaterial(dto.getSheetMaterial());
            mf.setPaintingThickness(dto.getPaintingThickness());
            mf.setPrimer(dto.getPrimer());
            mf.setPowderCoating(dto.getPowderCoating());
            mf.setSurfaceType(dto.getSurfaceType());
            mf.setHDG(dto.getHDG());
            mf.setColor(dto.getColor());
            mf.setCustomColor(dto.getCustomColor());
            materialFinishRepo.save(mf);
            spec.setMaterialFinish(mf);
        }

        technicalSpecificationRepo.save(spec);
        existingJob.setSpecification(spec);
    }

        

    @Override
    public void deleteJob(Long jobId) {
        
        Job job = jobRepo.findById(jobId)
        .orElseThrow(() -> new JobNotFoundException("Job ID: " + jobId + " is not found!"));

        job.setJobStatus(JobStatus.CLOSED);

        jobRepo.save(job);
    }

    @Override
    public Job handleJob(Long jobId, JobStatus jobStatus) {

        Job job = jobRepo.findById(jobId)
        .orElseThrow(() -> new JobNotFoundException("Job ID: " + jobId + " is not found!"));

        JobStatus existingStatus = job.getJobStatus();

        boolean isValidTransition = false;

        switch (existingStatus){

            case PENDING:
                isValidTransition = (jobStatus == JobStatus.APPROVED_BY_SALESMANAGER || jobStatus == JobStatus.REJECTED_BY_SALESMANAGER);
                break;

            case APPROVED_BY_SALESMANAGER:
                isValidTransition = (jobStatus == JobStatus.APPROVED || jobStatus == JobStatus.REJECTED_BY_ACCOUNTANT);
                break;

            case REJECTED_BY_SALESMANAGER:
            case REJECTED_BY_ACCOUNTANT:
            case APPROVED:            
            case READY_FOR_DESIGNING:
            case READY_FOR_PRODUCTION:
            case ONGOING:
            case BLOCKED:
            case WAITING_FOR_MATERIALS:
            case COMPLETED:
            case CLOSED:
            isValidTransition = false;
            break;
        }

        if (!isValidTransition) {
            throw new IllegalStatusTransitionException("Transition from " + existingStatus + " to " + jobStatus + " is not valid!");
        }

        job.setJobStatus(jobStatus);

        Job updatedJob = jobRepo.save(job);

        return updatedJob;
    }

    @Override
    public List<JobTableViewDto> viewJobTableForDesign() {
        
        List<Job> jobs = jobRepo.findByJobStatusOrderByJobIdDesc(JobStatus.READY_FOR_DESIGNING);

        List<JobTableViewDto> jobTableViewDtoList = jobs.stream().map(job -> {

            JobTableViewDto jobTableViewDto = new JobTableViewDto();

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MMMM-yyyy");

            jobTableViewDto.setJobId(job.getJobId());
            jobTableViewDto.setProjectName(job.getEstimation().getInquiry().getProjectName());
            jobTableViewDto.setQuotationVersion(job.getEstimation().getQuotationVersion());
            jobTableViewDto.setCustomerName(job.getEstimation().getInquiry().getCustomer().getCustomerPrefix() + " " + job.getEstimation().getInquiry().getCustomer().getCustomerFirstName() + " " + job.getEstimation().getInquiry().getCustomer().getCustomerLastName());
            jobTableViewDto.setJobReturnDate(job.getEstimation().getInquiry().getProjectReturnDate().format(formatter));
            jobTableViewDto.setJobStatus(job.getJobStatus());

            return jobTableViewDto;

        }).collect(Collectors.toList());

        return jobTableViewDtoList;
    }

}
