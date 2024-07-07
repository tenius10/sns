package com.tenius.sns.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QCommentLike is a Querydsl query type for CommentLike
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QCommentLike extends EntityPathBase<CommentLike> {

    private static final long serialVersionUID = -439857223L;

    public static final QCommentLike commentLike = new QCommentLike("commentLike");

    public final QBaseEntity _super = new QBaseEntity(this);

    public final NumberPath<Long> cno = createNumber("cno", Long.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> modDate = _super.modDate;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> regDate = _super.regDate;

    public final StringPath uid = createString("uid");

    public QCommentLike(String variable) {
        super(CommentLike.class, forVariable(variable));
    }

    public QCommentLike(Path<? extends CommentLike> path) {
        super(path.getType(), path.getMetadata());
    }

    public QCommentLike(PathMetadata metadata) {
        super(CommentLike.class, metadata);
    }

}

