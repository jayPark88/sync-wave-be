package com.parker.common.jpa.repository;

import com.parker.common.jpa.entity.NotificationsEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationsRepository extends JpaRepository<NotificationsEntity, String> {
}
