package com.parker.common.jpa.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QBaseInfoEntity is a Querydsl query type for BaseInfoEntity
 */
@Generated("com.querydsl.codegen.DefaultSupertypeSerializer")
public class QBaseInfoEntity extends EntityPathBase<BaseInfoEntity> {

    private static final long serialVersionUID = -734011679L;

    public static final QBaseInfoEntity baseInfoEntity = new QBaseInfoEntity("baseInfoEntity");

    public final DateTimePath<java.time.LocalDateTime> createdDateTime = createDateTime("createdDateTime", java.time.LocalDateTime.class);

    public final StringPath createId = createString("createId");

    public final DateTimePath<java.time.LocalDateTime> modifiedDateTime = createDateTime("modifiedDateTime", java.time.LocalDateTime.class);

    public final StringPath modifiedId = createString("modifiedId");

    public QBaseInfoEntity(String variable) {
        super(BaseInfoEntity.class, forVariable(variable));
    }

    public QBaseInfoEntity(Path<? extends BaseInfoEntity> path) {
        super(path.getType(), path.getMetadata());
    }

    public QBaseInfoEntity(PathMetadata metadata) {
        super(BaseInfoEntity.class, metadata);
    }

}

