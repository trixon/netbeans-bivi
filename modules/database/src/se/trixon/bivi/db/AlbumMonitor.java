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

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import org.openide.util.Exceptions;
import se.trixon.almond.Xlog;

/**
 *
 * @author Patrik Karlsson
 */
public class AlbumMonitor {

    private static final ArrayList<AlbumMonitor> sAlbumMonitors = new ArrayList<>();
    public static void stopAllMonitors() {
        if (!sAlbumMonitors.isEmpty()) {
            Xlog.d(AlbumMonitor.class, "stopAllMonitors");
        }
        sAlbumMonitors.stream().forEach((albumMonitor) -> {
            albumMonitor.stopMonitor();
        });
        sAlbumMonitors.clear();
    }

    @SuppressWarnings(value = "unchecked")
    static <T> WatchEvent<T> cast(WatchEvent<?> event) {
        return (WatchEvent<T>) event;
    }
    
    private final Path mDir;
    private boolean mMonitor = false;
    private final Map<WatchKey, Path> mWatchKeys;
    private final WatchService mWatchService;
    private volatile Thread processingThread;

    public AlbumMonitor(Path dir) throws IOException {
        mWatchService = FileSystems.getDefault().newWatchService();
        mWatchKeys = new HashMap<>();
        mDir = dir;
        Xlog.d(getClass(), "Start monitor " + dir);
        registerAll(dir);
        Xlog.d(getClass(), "Start monitor " + dir + "...done!");

        mMonitor = true;
        sAlbumMonitors.add(this);
    }

    public void startMonitor() {
        processingThread = new Thread(() -> {
            processEvents();
        });

        processingThread.start();
    }

    public void stopMonitor() {
        Thread thread = processingThread;
        if (thread != null) {
            Xlog.d(getClass(), "Stop monitor " + mDir);
            mWatchKeys.clear();
            try {
                mWatchService.close();
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            }

            thread.interrupt();
        }
    }

    private void register(Path dir) throws IOException {
        WatchKey watchKey = dir.register(mWatchService,
                StandardWatchEventKinds.ENTRY_CREATE,
                StandardWatchEventKinds.ENTRY_DELETE,
                StandardWatchEventKinds.ENTRY_MODIFY);

        if (mMonitor) {
            Path prev = mWatchKeys.get(watchKey);
            if (prev == null) {
                String message = String.format("register: %s", dir);
                Xlog.d(getClass(), message);
            } else {
                if (!dir.equals(prev)) {
                    String message = String.format("update: %s -> %s", prev, dir);
                    Xlog.d(getClass(), message);
                }
            }
        }

        mWatchKeys.put(watchKey, dir);
    }

    private void registerAll(final Path start) throws IOException {
        Files.walkFileTree(start, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                register(dir);
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
                return FileVisitResult.CONTINUE;
            }

        });
    }

    void processEvents() {
        for (;;) {

            WatchKey watchKey;
            try {
                watchKey = mWatchService.take();
            } catch (InterruptedException ex) {
                return;
            }

            Path dir = mWatchKeys.get(watchKey);
            if (dir == null) {
                String message = "WatchKey not recognized!!";
                Xlog.d(getClass(), message);
                continue;
            }

            for (WatchEvent<?> event : watchKey.pollEvents()) {
                WatchEvent.Kind kind = event.kind();

                if (kind == StandardWatchEventKinds.OVERFLOW) {
                    Xlog.d(getClass(), "OVERFLOW");
                    continue;
                }

                WatchEvent<Path> watchEvent = cast(event);
                Path name = watchEvent.context();
                Path child = dir.resolve(name);

                String message = String.format("%s: %s", event.kind().name(), child);
                Xlog.d(getClass(), message);

                if (kind == StandardWatchEventKinds.ENTRY_CREATE) {
                    try {
                        if (Files.isDirectory(child, LinkOption.NOFOLLOW_LINKS)) {
                            registerAll(child);
                        }
                    } catch (IOException ex) {
                        Exceptions.printStackTrace(ex);
                    }
                }
            }

            boolean valid = watchKey.reset();
            if (!valid) {
                mWatchKeys.remove(watchKey);

                if (mWatchKeys.isEmpty()) {
                    break;
                }
            }
        }
    }

}
