package com.parker.common.jpa.listener;

import com.parker.common.jpa.entity.BaseInfoEntity;
import com.parker.common.util.security.SecurityUtil;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;

import java.util.Optional;

public class BaseEntityListener {
    @PrePersist
    void onSave(BaseInfoEntity entity) {
        Optional<String> userId = SecurityUtil.getCurrentUserName();
        if (userId.isPresent()) {
            entity.setCreateId(userId.get());
            entity.setModifiedId(userId.get());
        }
    }

    @PreUpdate
    void onUpdate(BaseInfoEntity entity) {
        Optional<String> userId = SecurityUtil.getCurrentUserName();

        if (userId.isPresent()) {
            entity.setModifiedId(userId.get());
        }
    }
}
