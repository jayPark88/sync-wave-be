package com.parker.common.jpa.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QNoticeEntity is a Querydsl query type for NoticeEntity
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QNoticeEntity extends EntityPathBase<NoticeEntity> {

    private static final long serialVersionUID = 773761946L;

    public static final QNoticeEntity noticeEntity = new QNoticeEntity("noticeEntity");

    public final QBaseInfoEntity _super = new QBaseInfoEntity(this);

    public final StringPath content = createString("content");

    public final StringPath createdBy = createString("createdBy");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdDateTime = _super.createdDateTime;

    //inherited
    public final StringPath createId = _super.createId;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final BooleanPath isActive = createBoolean("isActive");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> modifiedDateTime = _super.modifiedDateTime;

    //inherited
    public final StringPath modifiedId = _super.modifiedId;

    public final StringPath priority = createString("priority");

    public final StringPath title = createString("title");

    public QNoticeEntity(String variable) {
        super(NoticeEntity.class, forVariable(variable));
    }

    public QNoticeEntity(Path<? extends NoticeEntity> path) {
        super(path.getType(), path.getMetadata());
    }

    public QNoticeEntity(PathMetadata metadata) {
        super(NoticeEntity.class, metadata);
    }

}

