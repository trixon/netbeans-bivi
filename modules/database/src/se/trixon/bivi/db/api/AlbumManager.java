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
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import se.trixon.almond.Xlog;
import se.trixon.bivi.db.api.Db.AlbumsDef;

/**
 *
 * @author Patrik Karlsson
 */
public enum AlbumManager {

    INSTANCE;
    private final AlbumsDef mAlbumsDef = Db.AlbumsDef.INSTANCE;
    private final DbManager mManager = DbManager.INSTANCE;

    private AlbumManager() {
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
}
