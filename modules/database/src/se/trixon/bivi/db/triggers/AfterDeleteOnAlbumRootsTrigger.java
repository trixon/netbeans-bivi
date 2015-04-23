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
package se.trixon.bivi.db.triggers;

import com.healthmarketscience.sqlbuilder.BinaryCondition;
import com.healthmarketscience.sqlbuilder.DeleteQuery;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import org.h2.tools.TriggerAdapter;
import se.trixon.almond.Xlog;
import se.trixon.bivi.db.api.Tables;

/**
 *
 * @author Patrik Karlsson
 */
public class AfterDeleteOnAlbumRootsTrigger extends TriggerAdapter {

    @Override
    public void fire(Connection conn, ResultSet oldRow, ResultSet newRow) throws SQLException {
        int id = oldRow.getInt(Tables.AlbumRoots.ID);
        DeleteQuery deleteQuery = new DeleteQuery(Tables.Albums._NAME)
                .addCondition(BinaryCondition.equalTo(Tables.Albums.ALBUM_ROOT, id))
                .validate();
        
        String sql = deleteQuery.toString().replace("'", "");
        Xlog.d(getClass(), sql);
        
        try (Statement statement = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE)) {
            statement.execute(sql);
        }
    }
}
