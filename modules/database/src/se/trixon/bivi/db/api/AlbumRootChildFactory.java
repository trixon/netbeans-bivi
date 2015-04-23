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

import com.healthmarketscience.sqlbuilder.OrderObject;
import com.healthmarketscience.sqlbuilder.SelectQuery;
import java.beans.IntrospectionException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import org.openide.nodes.ChildFactory;
import org.openide.nodes.Node;
import org.openide.util.Exceptions;
import se.trixon.almond.Xlog;

/**
 *
 * @author Patrik Karlsson
 */
public class AlbumRootChildFactory extends ChildFactory<AlbumRoot> {

    @Override
    protected boolean createKeys(List<AlbumRoot> toPopulate) {
        try {
            Connection conn = DbManager.INSTANCE.getConnection();
            try (Statement statement = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE)) {
                Db.AlbumRootsDef albumRoots = Db.AlbumRootsDef.INSTANCE;
                SelectQuery selectQuery = new SelectQuery()
                        .addAllTableColumns(albumRoots.getTable())
                        .addOrdering(albumRoots.getLabel(), OrderObject.Dir.ASCENDING)
                        .validate();
                Xlog.d(getClass(), selectQuery.toString());

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

                        toPopulate.add(albumRoot);
                    }
                }
            }
        } catch (ClassNotFoundException | SQLException ex) {
            Exceptions.printStackTrace(ex);
        }

        return true;
    }

    @Override
    protected Node createNodeForKey(AlbumRoot key) {
        AlbumRootNode node = null;

        try {
            node = new AlbumRootNode(key);
        } catch (IntrospectionException ex) {
            Exceptions.printStackTrace(ex);
        }

        return node;
    }
}
