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

import com.healthmarketscience.sqlbuilder.BinaryCondition;
import com.healthmarketscience.sqlbuilder.DeleteQuery;
import com.healthmarketscience.sqlbuilder.InsertQuery;
import com.healthmarketscience.sqlbuilder.OrderObject;
import com.healthmarketscience.sqlbuilder.SelectQuery;
import com.healthmarketscience.sqlbuilder.UpdateQuery;
import java.io.File;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.Exceptions;
import se.trixon.almond.Xlog;

/**
 *
 * @author Patrik Karlsson
 */
public enum AlbumRootManager {

    INSTANCE;
    private final Db.AlbumRootsDef mAlbumRootsDef = Db.AlbumRootsDef.INSTANCE;
    private final DbManager mManager = DbManager.INSTANCE;

    private AlbumRootManager() {
    }

    public void delete(long id) throws ClassNotFoundException, SQLException {
        DeleteQuery deleteQuery = new DeleteQuery(mAlbumRootsDef.getTable())
                .addCondition(BinaryCondition.equalTo(mAlbumRootsDef.getId(), id))
                .validate();
        Xlog.d(getClass(), deleteQuery.toString());

        Connection conn = mManager.getConnection();
        try (Statement statement = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE)) {
            statement.execute(deleteQuery.toString());
        }
    }

    public void deleteAll() throws ClassNotFoundException, SQLException {
        DeleteQuery deleteQuery = new DeleteQuery(mAlbumRootsDef.getTable())
                .validate();
        Xlog.d(getClass(), deleteQuery.toString());

        Connection conn = mManager.getConnection();
        try (Statement statement = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE)) {
            statement.execute(deleteQuery.toString());
        }
    }

    public ArrayList<AlbumRoot> getRoots() {
        ArrayList<AlbumRoot> roots = new ArrayList<>();
        Db.AlbumRootsDef albumRoots = Db.AlbumRootsDef.INSTANCE;

        try {
            Connection conn = DbManager.INSTANCE.getConnection();
            try (Statement statement = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE)) {
                SelectQuery selectQuery = new SelectQuery()
                        .addAllTableColumns(albumRoots.getTable())
                        .addOrdering(albumRoots.getLabel(), OrderObject.Dir.ASCENDING)
                        .validate();
                String sql = selectQuery.toString();
                Xlog.d(getClass(), sql);

                try (ResultSet rs = statement.executeQuery(selectQuery.toString())) {
                    while (rs.next()) {
                        int id = rs.getInt(Db.AlbumRootsDef.ID);
                        String label = rs.getString(Db.AlbumRootsDef.LABEL);
                        String identifier = rs.getString(Db.AlbumRootsDef.IDENTIFIER);
                        String specificPath = rs.getString(Db.AlbumRootsDef.SPECIFIC_PATH);
                        int status = rs.getInt(Db.AlbumRootsDef.STATUS);
                        int type = rs.getInt(Db.AlbumRootsDef.TYPE);

                        AlbumRoot albumRoot = new AlbumRoot();
                        albumRoot.setId(id);
                        albumRoot.setIdentifier(identifier);
                        albumRoot.setLabel(label);
                        albumRoot.setSpecificPath(specificPath);
                        albumRoot.setStatus(status);
                        albumRoot.setType(type);

                        roots.add(albumRoot);
                    }
                }
            }
        } catch (ClassNotFoundException | SQLException ex) {
            Exceptions.printStackTrace(ex);
        }

        return roots;
    }

    public ArrayList<FileObject> getRootsAsFileObjects() {
        ArrayList<FileObject> fileObjects = new ArrayList<>();

        getRootsAsFiles().stream().map((file) -> FileUtil.toFileObject(file)).forEach((fileObject) -> {
            fileObjects.add(fileObject);
        });

        return fileObjects;
    }

    public ArrayList<File> getRootsAsFiles() {
        ArrayList<File> files = new ArrayList<>();

        getRoots().stream().map((root) -> new File(root.getSpecificPath())).forEach((file) -> {
            files.add(file);
        });

        return files;
    }

    public void insert(AlbumRoot albumRoot) throws ClassNotFoundException, SQLException {
        InsertQuery insertQuery = new InsertQuery(mAlbumRootsDef.getTable())
                .addColumn(mAlbumRootsDef.getLabel(), albumRoot.getLabel())
                .addColumn(mAlbumRootsDef.getStatus(), albumRoot.getStatus())
                .addColumn(mAlbumRootsDef.getType(), albumRoot.getType())
                .addColumn(mAlbumRootsDef.getIdentifier(), albumRoot.getIdentifier())
                .addColumn(mAlbumRootsDef.getSpecificPath(), albumRoot.getSpecificPath())
                .validate();
        Xlog.d(getClass(), insertQuery.toString());

        Connection conn = DbManager.INSTANCE.getConnection();
        try (Statement statement = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE)) {
            statement.execute(insertQuery.toString());
        }
    }

    public void update(AlbumRoot albumRoot) throws ClassNotFoundException, SQLException {
        UpdateQuery updateQuery = new UpdateQuery(mAlbumRootsDef.getTable())
                .addSetClause(mAlbumRootsDef.getLabel(), albumRoot.getLabel())
                .addSetClause(mAlbumRootsDef.getSpecificPath(), albumRoot.getSpecificPath())
                .addCondition(BinaryCondition.equalTo(mAlbumRootsDef.getId(), albumRoot.getId()))
                .validate();
        Xlog.d(getClass(), updateQuery.toString());

        Connection conn = DbManager.INSTANCE.getConnection();
        try (Statement statement = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE)) {
            statement.execute(updateQuery.toString());
        }
    }
}
