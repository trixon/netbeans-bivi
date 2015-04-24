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
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import org.openide.util.Exceptions;
import se.trixon.almond.Xlog;
import se.trixon.bivi.db.api.AlbumRootManager;

/**
 *
 * @author Patrik Karlsson
 */
public enum DirMonitorManager {

    INSTANCE;
    private final ArrayList<DirMonitor> mDirMonitors = new ArrayList<>();
    private final HashSet<String> mMonitoredDirs = new HashSet<>();

    public void addAndStart(Path dir) throws InstantiationException {
        if (!mMonitoredDirs.add(dir.toString())) {
            Xlog.d(getClass(), "Already monitoring " + dir);
            throw new InstantiationException(null);
        }

        new Thread(() -> {
            DirMonitor dirMonitor;
            try {
                dirMonitor = new DirMonitor(dir);
                mDirMonitors.add(dirMonitor);
                dirMonitor.start();
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            }
        }).start();
    }

    public void stopAll() {
        if (!mDirMonitors.isEmpty()) {
            Xlog.d(getClass(), "stopAllMonitors");
            
            mDirMonitors.stream().forEach((albumMonitor) -> {
                albumMonitor.stopMonitor();
            });
            
            mDirMonitors.clear();
            mMonitoredDirs.clear();
        }
    }

    public void stopAndRemoveOrphans() {
        ArrayList<File> roots = AlbumRootManager.INSTANCE.getRootsAsFiles();
        mDirMonitors.stream().forEach((albumMonitor) -> {
            Path dir = albumMonitor.getDir();
            boolean orphan = true;
            for (File root : roots) {
                if (dir.equals(root.toPath())) {
                    orphan = false;
                    break;
                }
            }

            if (orphan) {
                Xlog.d(getClass(), "orphan: " + dir);

                albumMonitor.stopMonitor();
                mMonitoredDirs.remove(dir.toString());
                mDirMonitors.remove(albumMonitor);
            }
        });
    }
}
