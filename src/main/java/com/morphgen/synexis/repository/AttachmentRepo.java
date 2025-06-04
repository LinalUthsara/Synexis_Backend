package com.morphgen.synexis.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.morphgen.synexis.entity.Attachment;

@Repository

public interface AttachmentRepo extends JpaRepository<Attachment, Long> {
    
}
