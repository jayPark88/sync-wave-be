package com.parker.common.jpa.repository;

import com.parker.common.jpa.entity.FileEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FileRepository extends JpaRepository<FileEntity, Long> {
}
