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
import com.healthmarketscience.sqlbuilder.CustomSql;
import com.healthmarketscience.sqlbuilder.DeleteQuery;
import com.healthmarketscience.sqlbuilder.FunctionCall;
import com.healthmarketscience.sqlbuilder.InsertQuery;
import com.healthmarketscience.sqlbuilder.SelectQuery;
import com.healthmarketscience.sqlbuilder.dbspec.basic.DbTable;
import java.io.File;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import org.apache.commons.io.FilenameUtils;
import org.openide.util.Exceptions;
import se.trixon.almond.Xlog;
import se.trixon.bivi.db.api.Db.AlbumRootsDef;
import se.trixon.bivi.db.api.Db.AlbumsDef;

/**
 *
 * @author Patrik Karlsson
 */
public enum AlbumManager {

    INSTANCE;
    private final DbTable mAlbumRootTable;
    private final AlbumRootsDef mAlbumRootsDef = Db.AlbumRootsDef.INSTANCE;
    private final DbTable mAlbumTable;
    private final AlbumsDef mAlbumsDef = Db.AlbumsDef.INSTANCE;
    private final DbManager mManager = DbManager.INSTANCE;

    private AlbumManager() {
        mAlbumTable = mAlbumsDef.getTable();
        mAlbumRootTable = mAlbumRootsDef.getTable();
    }

    public void delete(long id) throws ClassNotFoundException, SQLException {
        DeleteQuery deleteQuery = new DeleteQuery(mAlbumsDef.getTable())
                .addCondition(BinaryCondition.equalTo(mAlbumsDef.getId(), id))
                .validate();
        Xlog.d(getClass(), deleteQuery.toString());

        Connection conn = mManager.getConnection();
        try (Statement statement = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE)) {
            statement.execute(deleteQuery.toString());
        }
    }

    public void insert(Album album) {
        insert(album.getAlbumRootId(), album.getRelativePath(), album.getDate());
    }

    public void insert(long albumRoot, String relativePath) {
        String date;
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        date = dateFormat.format(Calendar.getInstance().getTime());
        insert(albumRoot, relativePath, date);
    }

    public void insert(long albumRoot, String relativePath, String date) {
        InsertQuery insertQuery = new InsertQuery(mAlbumsDef.getTable())
                .addColumn(mAlbumsDef.getAlbumRoot(), albumRoot)
                .addColumn(mAlbumsDef.getRelativePath(), relativePath)
                .addColumn(mAlbumsDef.getDate(), date)
                .validate();

        String sql = insertQuery.toString();

        Connection conn;
        try {
            conn = DbManager.INSTANCE.getConnection();
            try (Statement statement = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE)) {
                statement.execute(sql);
                Xlog.d(getClass(), sql);
            }
        } catch (ClassNotFoundException | SQLException ex) {
            //Exceptions.printStackTrace(ex);
        }
    }

    public boolean exists(long albumRoot, String relativePath) throws SQLException {
        SelectQuery selectQuery = new SelectQuery()
                .addCustomColumns(new CustomSql(FunctionCall.countAll() + " AS ROW_COUNT"))
                .addFromTable(mAlbumTable)
                .addCondition(BinaryCondition.equalTo(mAlbumsDef.getId(), albumRoot))
                .addCondition(BinaryCondition.equalTo(mAlbumsDef.getRelativePath(), relativePath))
                .validate();
        String sql = selectQuery.toString();
        Xlog.d(getClass(), sql);

        int rowCount = 0;
        try {
            Connection conn = DbManager.INSTANCE.getConnection();
            try (Statement statement = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
                    ResultSet rs = statement.executeQuery(selectQuery.toString())) {
                rs.first();
                rowCount = rs.getInt("ROW_COUNT");
            } catch (SQLException ex) {
                Exceptions.printStackTrace(ex);
            }
        } catch (ClassNotFoundException | SQLException ex) {
            return false;
        }

        return rowCount > 0;
    }

    public Album select(long albumRoot, String relativePath) throws SQLException {
        SelectQuery selectQuery = new SelectQuery()
                .addAllTableColumns(mAlbumTable)
                .addColumns(mAlbumRootsDef.getSpecificPath())
                .addJoin(SelectQuery.JoinType.INNER, mAlbumTable, mAlbumRootTable, mAlbumsDef.getAlbumRoot(), mAlbumRootsDef.getId())
                .addCondition(BinaryCondition.equalTo(mAlbumRootsDef.getId(), albumRoot))
                .addCondition(BinaryCondition.equalTo(mAlbumsDef.getRelativePath(), relativePath))
                .validate();
        String sql = selectQuery.toString();
        Xlog.d(getClass(), sql);

        Album album = null;

        try {
            Connection conn = DbManager.INSTANCE.getConnection();
            try (Statement statement = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY)) {
                try (ResultSet rs = statement.executeQuery(sql)) {
                    album = getAlbum(rs);
                }
            }
        } catch (ClassNotFoundException | SQLException ex) {
            Exceptions.printStackTrace(ex);
        }

        if (album == null) {
            throw new SQLException("Album not found");
        }

        return album;
    }

    private Album getAlbum(ResultSet rs) {
        Album album = null;
        try {
            while (rs.next()) {
                long id = rs.getLong(AlbumsDef.ID);
                long albumRoot = rs.getLong(AlbumsDef.ALBUM_ROOT);
                String relativePath = rs.getString(AlbumsDef.RELATIVE_PATH);
                String date = rs.getString(AlbumsDef.DATE);
                String caption = rs.getString(AlbumsDef.CAPTION);
                String collection = rs.getString(AlbumsDef.COLLECTION);
                long icon = rs.getLong(AlbumsDef.ICON);

                String specificPath = rs.getString(AlbumRootsDef.SPECIFIC_PATH);
                String path = FilenameUtils.normalize(specificPath + relativePath);
                File absoluteFile = new File(path);

                album = new Album();
                album.setId(id);
                album.setCaption(caption);
                album.setName(FilenameUtils.getName(relativePath));
                album.setCollection(collection);
                album.setDate(date);
                album.setIcon(icon);
                album.setRelativePath(relativePath);
                album.setAlbumRootId(albumRoot);
                album.setAbsoluteFile(absoluteFile);
            }
        } catch (SQLException ex) {
            Exceptions.printStackTrace(ex);
        }

        return album;
    }
}
