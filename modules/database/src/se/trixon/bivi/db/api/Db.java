/* 
 * Copyright 2015 Patrik Karlsson.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package se.trixon.bivi.db.api;

import com.healthmarketscience.sqlbuilder.dbspec.basic.DbColumn;
import com.healthmarketscience.sqlbuilder.dbspec.basic.DbSchema;
import com.healthmarketscience.sqlbuilder.dbspec.basic.DbSpec;
import com.healthmarketscience.sqlbuilder.dbspec.basic.DbTable;

/**
 *
 * @author Patrik Karlsson
 */
public enum Db {

    INSTANCE;
    private final DbSchema mSchema;
    private final DbSpec mSpec;

    private Db() {
        mSpec = new DbSpec();
        mSchema = mSpec.addDefaultSchema();

        init();
    }

    public DbSchema getSchema() {
        return mSchema;
    }

    public DbSpec getSpec() {
        return mSpec;
    }

    private void init() {
    }

    public enum AlbumRootsDef {

        INSTANCE;
        public static final String ID = "ID";
        public static final String IDENTIFIER = "identifier";
        public static final String LABEL = "label";
        public static final String SPECIFIC_PATH = "specificPath";
        public static final String STATUS = "status";
        public static final String TABLE_NAME = "AlbumRoots";
        public static final String TYPE = "type";

        private final DbColumn mId;
        private final DbColumn mIdentifier;
        private final DbColumn mLabel;
        private final DbColumn mSpecificPath;
        private final DbColumn mStatus;
        private final DbTable mTable;
        private final DbColumn mType;

        private AlbumRootsDef() {
            mTable = Db.INSTANCE.getSchema().addTable(TABLE_NAME);
            mId = mTable.addColumn(ID);
            mLabel = mTable.addColumn(LABEL);
            mStatus = mTable.addColumn(STATUS);
            mType = mTable.addColumn(TYPE);
            mSpecificPath = mTable.addColumn(SPECIFIC_PATH);
            mIdentifier = mTable.addColumn(IDENTIFIER);
        }

        public DbColumn getId() {
            return mId;
        }

        public DbColumn getIdentifier() {
            return mIdentifier;
        }

        public DbColumn getLabel() {
            return mLabel;
        }

        public DbColumn getSpecificPath() {
            return mSpecificPath;
        }

        public DbColumn getStatus() {
            return mStatus;
        }

        public DbTable getTable() {
            return mTable;
        }

        public DbColumn getType() {
            return mType;
        }
    }

    public enum AlbumsDef {

        INSTANCE;
        public static final String ALBUM_ROOT = "albumRoot";
        public static final String CAPTION = "caption";
        public static final String COLLECTION = "collection";
        public static final String DATE = "date";
        public static final String ICON = "icon";
        public static final String ID = "ID";
        public static final String RELATIVE_PATH = "relativePath";
        public static final String TABLE_NAME = "Albums";

        private final DbColumn mAlbumRoot;
        private final DbColumn mCaption;
        private final DbColumn mCollection;
        private final DbColumn mDate;
        private final DbColumn mIcon;
        private final DbColumn mId;
        private final DbColumn mRelativePath;
        private final DbTable mTable;

        private AlbumsDef() {
            mTable = Db.INSTANCE.getSchema().addTable(TABLE_NAME);
            mId = mTable.addColumn(ID);
            mAlbumRoot = mTable.addColumn(ALBUM_ROOT);
            mRelativePath = mTable.addColumn(RELATIVE_PATH);
            mDate = mTable.addColumn(DATE);
            mCaption = mTable.addColumn(CAPTION);
            mCollection = mTable.addColumn(COLLECTION);
            mIcon = mTable.addColumn(ICON);
        }

        public DbColumn getAlbumRoot() {
            return mAlbumRoot;
        }

        public DbColumn getCaption() {
            return mCaption;
        }

        public DbColumn getCollection() {
            return mCollection;
        }

        public DbColumn getDate() {
            return mDate;
        }

        public DbColumn getIcon() {
            return mIcon;
        }

        public DbColumn getId() {
            return mId;
        }

        public DbColumn getRelativePath() {
            return mRelativePath;
        }

        public DbTable getTable() {
            return mTable;
        }
    }
}
