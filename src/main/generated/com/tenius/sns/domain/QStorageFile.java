package com.tenius.sns.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QStorageFile is a Querydsl query type for StorageFile
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QStorageFile extends EntityPathBase<StorageFile> {

    private static final long serialVersionUID = 1225817018L;

    public static final QStorageFile storageFile = new QStorageFile("storageFile");

    public final QBaseEntity _super = new QBaseEntity(this);

    public final StringPath fileName = createString("fileName");

    public final NumberPath<Long> fno = createNumber("fno", Long.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> modDate = _super.modDate;

    public final NumberPath<Integer> ord = createNumber("ord", Integer.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> regDate = _super.regDate;

    public final StringPath uuid = createString("uuid");

    public QStorageFile(String variable) {
        super(StorageFile.class, forVariable(variable));
    }

    public QStorageFile(Path<? extends StorageFile> path) {
        super(path.getType(), path.getMetadata());
    }

    public QStorageFile(PathMetadata metadata) {
        super(StorageFile.class, metadata);
    }

}

