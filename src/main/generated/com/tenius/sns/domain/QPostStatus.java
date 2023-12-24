package com.tenius.sns.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QPostStatus is a Querydsl query type for PostStatus
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QPostStatus extends EntityPathBase<PostStatus> {

    private static final long serialVersionUID = -1511446737L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QPostStatus postStatus = new QPostStatus("postStatus");

    public final QBaseEntity _super = new QBaseEntity(this);

    public final BooleanPath hided = createBoolean("hided");

    public final BooleanPath liked = createBoolean("liked");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> modDate = _super.modDate;

    public final NumberPath<Long> pno = createNumber("pno", Long.class);

    public final QPost post;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> regDate = _super.regDate;

    public final StringPath uid = createString("uid");

    public final QUserInfo user;

    public QPostStatus(String variable) {
        this(PostStatus.class, forVariable(variable), INITS);
    }

    public QPostStatus(Path<? extends PostStatus> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QPostStatus(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QPostStatus(PathMetadata metadata, PathInits inits) {
        this(PostStatus.class, metadata, inits);
    }

    public QPostStatus(Class<? extends PostStatus> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.post = inits.isInitialized("post") ? new QPost(forProperty("post"), inits.get("post")) : null;
        this.user = inits.isInitialized("user") ? new QUserInfo(forProperty("user"), inits.get("user")) : null;
    }

}

