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
package se.trixon.bivi.db;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import org.openide.util.Exceptions;
import se.trixon.almond.Xlog;
import se.trixon.bivi.db.api.Db;

/**
 *
 * @author Patrik Karlsson <patrik@trixon.se>
 */
public class TableCreator {

    public TableCreator(Connection conn) throws ClassNotFoundException, SQLException {
        Xlog.d(getClass(), "Creating tables");
        try (Statement statement = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE)) {
            statement.addBatch(getAlbumRoots());
            statement.addBatch(getAlbums());
            statement.addBatch("CREATE TRIGGER delete_albumroot AFTER DELETE ON AlbumRoots FOR EACH ROW CALL \"se.trixon.bivi.db.triggers.AfterDeleteOnAlbumRootsTrigger\"");
            statement.executeBatch();
            Xlog.d(getClass(), "Tables created");
        } catch (SQLException ex) {
            Xlog.d(getClass(), "Table creation failed.");
            Exceptions.printStackTrace(ex);
        }
    }

    private StringBuilder dropAndCreate(String tableName) {
        StringBuilder builder = new StringBuilder();

        builder.append(String.format("DROP TABLE IF EXISTS %s;", tableName));
        builder.append(String.format("CREATE TABLE %s", tableName));

        return builder;
    }

    private String getAlbumRoots() {
        StringBuilder builder = dropAndCreate(Db.AlbumRootsDef.TABLE_NAME);

        builder.append("(id IDENTITY PRIMARY KEY,");
        builder.append("label VARCHAR(255),");
        builder.append("status INTEGER NOT NULL,");
        builder.append("type INTEGER NOT NULL,");
        builder.append("identifier VARCHAR(255),");
        builder.append("specificPath VARCHAR(4096),");
        builder.append("UNIQUE(identifier, specificPath));");

        return builder.toString();
    }

    private String getAlbums() {
        StringBuilder builder = dropAndCreate(Db.AlbumsDef.TABLE_NAME);

        builder.append("(id IDENTITY PRIMARY KEY,");
        builder.append("albumRoot INTEGER NOT NULL,");
        builder.append("relativePath VARCHAR(4096) NOT NULL,");
        builder.append("date DATE,");
        builder.append("caption VARCHAR(255),");
        builder.append("collection VARCHAR(255),");
        builder.append("icon INTEGER,");
        builder.append("UNIQUE(albumRoot, relativePath));");

        return builder.toString();
    }
}
