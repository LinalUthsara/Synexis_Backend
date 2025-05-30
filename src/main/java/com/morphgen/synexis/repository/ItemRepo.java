package com.morphgen.synexis.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.morphgen.synexis.entity.Item;

@Repository

public interface ItemRepo extends JpaRepository<Item, Long> {
    
}
