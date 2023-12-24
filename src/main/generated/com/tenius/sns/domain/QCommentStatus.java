package com.tenius.sns.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QCommentStatus is a Querydsl query type for CommentStatus
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QCommentStatus extends EntityPathBase<CommentStatus> {

    private static final long serialVersionUID = -1585713260L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QCommentStatus commentStatus = new QCommentStatus("commentStatus");

    public final QBaseEntity _super = new QBaseEntity(this);

    public final NumberPath<Long> cno = createNumber("cno", Long.class);

    public final QComment comment;

    public final BooleanPath hided = createBoolean("hided");

    public final BooleanPath liked = createBoolean("liked");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> modDate = _super.modDate;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> regDate = _super.regDate;

    public final StringPath uid = createString("uid");

    public final QUserInfo user;

    public QCommentStatus(String variable) {
        this(CommentStatus.class, forVariable(variable), INITS);
    }

    public QCommentStatus(Path<? extends CommentStatus> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QCommentStatus(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QCommentStatus(PathMetadata metadata, PathInits inits) {
        this(CommentStatus.class, metadata, inits);
    }

    public QCommentStatus(Class<? extends CommentStatus> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.comment = inits.isInitialized("comment") ? new QComment(forProperty("comment"), inits.get("comment")) : null;
        this.user = inits.isInitialized("user") ? new QUserInfo(forProperty("user"), inits.get("user")) : null;
    }

}

