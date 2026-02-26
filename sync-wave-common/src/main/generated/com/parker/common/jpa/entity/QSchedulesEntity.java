package com.parker.common.jpa.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QSchedulesEntity is a Querydsl query type for SchedulesEntity
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QSchedulesEntity extends EntityPathBase<SchedulesEntity> {

    private static final long serialVersionUID = 930111648L;

    public static final QSchedulesEntity schedulesEntity = new QSchedulesEntity("schedulesEntity");

    public final QBaseInfoEntity _super = new QBaseInfoEntity(this);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdDateTime = _super.createdDateTime;

    //inherited
    public final StringPath createId = _super.createId;

    public final StringPath description = createString("description");

    public final DateTimePath<java.time.LocalDateTime> endDateTime = createDateTime("endDateTime", java.time.LocalDateTime.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> modifiedDateTime = _super.modifiedDateTime;

    //inherited
    public final StringPath modifiedId = _super.modifiedId;

    public final DateTimePath<java.time.LocalDateTime> startDateTime = createDateTime("startDateTime", java.time.LocalDateTime.class);

    public final StringPath title = createString("title");

    public final NumberPath<Long> userId = createNumber("userId", Long.class);

    public QSchedulesEntity(String variable) {
        super(SchedulesEntity.class, forVariable(variable));
    }

    public QSchedulesEntity(Path<? extends SchedulesEntity> path) {
        super(path.getType(), path.getMetadata());
    }

    public QSchedulesEntity(PathMetadata metadata) {
        super(SchedulesEntity.class, metadata);
    }

}

