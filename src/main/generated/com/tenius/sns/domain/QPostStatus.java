package com.tenius.sns.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QPostStatus is a Querydsl query type for PostStatus
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QPostStatus extends EntityPathBase<PostStatus> {

    private static final long serialVersionUID = -1511446737L;

    public static final QPostStatus postStatus = new QPostStatus("postStatus");

    public final QBaseEntity _super = new QBaseEntity(this);

    public final BooleanPath hided = createBoolean("hided");

    public final BooleanPath liked = createBoolean("liked");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> modDate = _super.modDate;

    public final NumberPath<Long> pno = createNumber("pno", Long.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> regDate = _super.regDate;

    public final StringPath uid = createString("uid");

    public QPostStatus(String variable) {
        super(PostStatus.class, forVariable(variable));
    }

    public QPostStatus(Path<? extends PostStatus> path) {
        super(path.getType(), path.getMetadata());
    }

    public QPostStatus(PathMetadata metadata) {
        super(PostStatus.class, metadata);
    }

}

