package com.parker.common.jpa.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QNotificationsEntity is a Querydsl query type for NotificationsEntity
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QNotificationsEntity extends EntityPathBase<NotificationsEntity> {

    private static final long serialVersionUID = 1113262924L;

    public static final QNotificationsEntity notificationsEntity = new QNotificationsEntity("notificationsEntity");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath message = createString("message");

    public final DateTimePath<java.time.LocalDateTime> notificationTime = createDateTime("notificationTime", java.time.LocalDateTime.class);

    public final NumberPath<Long> scheduleId = createNumber("scheduleId", Long.class);

    public QNotificationsEntity(String variable) {
        super(NotificationsEntity.class, forVariable(variable));
    }

    public QNotificationsEntity(Path<? extends NotificationsEntity> path) {
        super(path.getType(), path.getMetadata());
    }

    public QNotificationsEntity(PathMetadata metadata) {
        super(NotificationsEntity.class, metadata);
    }

}

