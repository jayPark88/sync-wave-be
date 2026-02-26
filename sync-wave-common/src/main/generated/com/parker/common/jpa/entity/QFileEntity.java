package com.parker.common.jpa.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QFileEntity is a Querydsl query type for FileEntity
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QFileEntity extends EntityPathBase<FileEntity> {

    private static final long serialVersionUID = -793304322L;

    public static final QFileEntity fileEntity = new QFileEntity("fileEntity");

    public final QBaseInfoEntity _super = new QBaseInfoEntity(this);

    public final NumberPath<Long> categorySeq = createNumber("categorySeq", Long.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdDateTime = _super.createdDateTime;

    //inherited
    public final StringPath createId = _super.createId;

    public final StringPath filePath = createString("filePath");

    public final NumberPath<Long> fileSeq = createNumber("fileSeq", Long.class);

    public final NumberPath<Long> fileSize = createNumber("fileSize", Long.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> modifiedDateTime = _super.modifiedDateTime;

    //inherited
    public final StringPath modifiedId = _super.modifiedId;

    public final StringPath name = createString("name");

    public QFileEntity(String variable) {
        super(FileEntity.class, forVariable(variable));
    }

    public QFileEntity(Path<? extends FileEntity> path) {
        super(path.getType(), path.getMetadata());
    }

    public QFileEntity(PathMetadata metadata) {
        super(FileEntity.class, metadata);
    }

}

