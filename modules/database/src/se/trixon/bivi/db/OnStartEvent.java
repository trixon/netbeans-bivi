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

import java.io.File;
import se.trixon.bivi.db.api.DbManager;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.prefs.PreferenceChangeEvent;
import javax.swing.SwingUtilities;
import org.openide.modules.OnStart;
import se.trixon.almond.Xlog;
import se.trixon.bivi.db.api.AlbumRootManager;
import se.trixon.bivi.db.api.DbMonitor;
import se.trixon.bivi.db.options.DbOptions;

/**
 *
 * @author Patrik Karlsson <patrik@trixon.se>
 */
@OnStart
public class OnStartEvent implements Runnable, DbMonitor.DbEvent {

    private Connection mConn;
    private final DbManager mManager = DbManager.INSTANCE;
    private final DbOptions mOptions = DbOptions.INSTANCE;

    public OnStartEvent() {
        DbMonitor.INSTANCE.add(this);

        mOptions.getPreferences().addPreferenceChangeListener((PreferenceChangeEvent evt) -> {
            String key = evt.getKey();
            if (key.equalsIgnoreCase(mOptions.KEY_LIBRARY_MONITOR)) {
                if (mOptions.isLibraryMonitor()) {
                    initDirMonitors();
                } else {
                    DirMonitorManager.INSTANCE.stopAll();
                }
            }
        });
    }

    @Override
    public void onDbChanged() {
        initDirMonitors();
    }

    @Override
    public void onDbRootAlbumsChanged() {
        initDirMonitors();
    }

    @Override
    public void run() {
        SwingUtilities.invokeLater(() -> {
            Xlog.d(getClass(), "onStart");
            try {
                mConn = mManager.getConnection();
                initDirMonitors();
            } catch (ClassNotFoundException | SQLException ex) {
                Xlog.d(getClass(), "Connection failed");
                Xlog.d(getClass(), ex.getLocalizedMessage());
            }
        });
    }

    private synchronized void initDirMonitors() {
        DirMonitorManager.INSTANCE.stopAndRemoveOrphans();
        if (mOptions.isLibraryMonitor()) {
            Xlog.d(getClass(), "initDirMonitors()");
            ArrayList<File> roots = AlbumRootManager.INSTANCE.getRootsAsFiles();
            for (File root : roots) {
                try {
                    DirMonitorManager.INSTANCE.addAndStart(root.toPath());
                } catch (InstantiationException ex) {
                }
            }
        }
    }
}
