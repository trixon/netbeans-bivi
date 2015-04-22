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

import se.trixon.bivi.db.api.DbManager;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.prefs.PreferenceChangeEvent;
import org.openide.modules.OnStart;
import org.openide.windows.WindowManager;
import se.trixon.almond.Xlog;

/**
 *
 * @author Patrik Karlsson <patrik@trixon.se>
 */
@OnStart
public class OnStartEvent implements Runnable {

    private Connection mConn;
    private final DbManager mManager = DbManager.INSTANCE;

    @Override
    public void run() {
        mManager.getPreferences().addPreferenceChangeListener((PreferenceChangeEvent evt) -> {
            if (evt.getKey().equalsIgnoreCase(mManager.KEY_PATH)) {
                initDb();
            }
        });

        WindowManager.getDefault().invokeWhenUIReady(() -> {
            Xlog.d(getClass(), "onStart");
            initDb();
        });
    }

    private void initDb() {
        try {
            mManager.closeConnection();
            mConn = mManager.getConnection();
            if (mManager.isEmpty()) {
                TableCreator tableCreator = new TableCreator();
            }
        } catch (ClassNotFoundException | SQLException ex) {
            Xlog.d(getClass(), "Connection failed");
            Xlog.d(getClass(), ex.getLocalizedMessage());
        }
    }
}
