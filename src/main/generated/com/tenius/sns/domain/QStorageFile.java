package com.tenius.sns.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QStorageFile is a Querydsl query type for StorageFile
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QStorageFile extends EntityPathBase<StorageFile> {

    private static final long serialVersionUID = 1225817018L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QStorageFile storageFile = new QStorageFile("storageFile");

    public final QBaseEntity _super = new QBaseEntity(this);

    public final StringPath fileName = createString("fileName");

    public final NumberPath<Long> fno = createNumber("fno", Long.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> modDate = _super.modDate;

    public final NumberPath<Integer> ord = createNumber("ord", Integer.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> regDate = _super.regDate;

    public final QUserInfo uploader;

    public final StringPath uuid = createString("uuid");

    public QStorageFile(String variable) {
        this(StorageFile.class, forVariable(variable), INITS);
    }

    public QStorageFile(Path<? extends StorageFile> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QStorageFile(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QStorageFile(PathMetadata metadata, PathInits inits) {
        this(StorageFile.class, metadata, inits);
    }

    public QStorageFile(Class<? extends StorageFile> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.uploader = inits.isInitialized("uploader") ? new QUserInfo(forProperty("uploader"), inits.get("uploader")) : null;
    }

}

