package com.parker.common.jpa.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QTodosEntity is a Querydsl query type for TodosEntity
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QTodosEntity extends EntityPathBase<TodosEntity> {

    private static final long serialVersionUID = -869781359L;

    public static final QTodosEntity todosEntity = new QTodosEntity("todosEntity");

    public final QBaseInfoEntity _super = new QBaseInfoEntity(this);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdDateTime = _super.createdDateTime;

    //inherited
    public final StringPath createId = _super.createId;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> modifiedDateTime = _super.modifiedDateTime;

    //inherited
    public final StringPath modifiedId = _super.modifiedId;

    public final StringPath status = createString("status");

    public final StringPath task = createString("task");

    public final NumberPath<Long> userId = createNumber("userId", Long.class);

    public QTodosEntity(String variable) {
        super(TodosEntity.class, forVariable(variable));
    }

    public QTodosEntity(Path<? extends TodosEntity> path) {
        super(path.getType(), path.getMetadata());
    }

    public QTodosEntity(PathMetadata metadata) {
        super(TodosEntity.class, metadata);
    }

}

