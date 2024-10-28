package com.extremal.programming.repository;

import com.extremal.programming.entity.ListEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ListsRepository extends JpaRepository<ListEntity, Long> {

}
