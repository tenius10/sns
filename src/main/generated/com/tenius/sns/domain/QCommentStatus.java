package com.tenius.sns.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QCommentStatus is a Querydsl query type for CommentStatus
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QCommentStatus extends EntityPathBase<CommentStatus> {

    private static final long serialVersionUID = -1585713260L;

    public static final QCommentStatus commentStatus = new QCommentStatus("commentStatus");

    public final QBaseEntity _super = new QBaseEntity(this);

    public final NumberPath<Long> cno = createNumber("cno", Long.class);

    public final BooleanPath hided = createBoolean("hided");

    public final BooleanPath liked = createBoolean("liked");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> modDate = _super.modDate;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> regDate = _super.regDate;

    public final StringPath uid = createString("uid");

    public QCommentStatus(String variable) {
        super(CommentStatus.class, forVariable(variable));
    }

    public QCommentStatus(Path<? extends CommentStatus> path) {
        super(path.getType(), path.getMetadata());
    }

    public QCommentStatus(PathMetadata metadata) {
        super(CommentStatus.class, metadata);
    }

}

