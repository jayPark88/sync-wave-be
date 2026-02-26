package com.parker.common.jpa.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QPasswordResetTokenEntity is a Querydsl query type for PasswordResetTokenEntity
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QPasswordResetTokenEntity extends EntityPathBase<PasswordResetTokenEntity> {

    private static final long serialVersionUID = -399317145L;

    public static final QPasswordResetTokenEntity passwordResetTokenEntity = new QPasswordResetTokenEntity("passwordResetTokenEntity");

    public final DateTimePath<java.time.LocalDateTime> createdAt = createDateTime("createdAt", java.time.LocalDateTime.class);

    public final StringPath email = createString("email");

    public final DateTimePath<java.time.LocalDateTime> expiresAt = createDateTime("expiresAt", java.time.LocalDateTime.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath token = createString("token");

    public final BooleanPath used = createBoolean("used");

    public QPasswordResetTokenEntity(String variable) {
        super(PasswordResetTokenEntity.class, forVariable(variable));
    }

    public QPasswordResetTokenEntity(Path<? extends PasswordResetTokenEntity> path) {
        super(path.getType(), path.getMetadata());
    }

    public QPasswordResetTokenEntity(PathMetadata metadata) {
        super(PasswordResetTokenEntity.class, metadata);
    }

}

