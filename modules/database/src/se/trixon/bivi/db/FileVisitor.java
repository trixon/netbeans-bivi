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
import java.nio.file.FileVisitResult;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import se.trixon.bivi.db.api.AlbumManager;
import se.trixon.bivi.db.api.AlbumRoot;

/**
 *
 * @author Patrik Karlsson <patrik@trixon.se>
 */
public class FileVisitor extends SimpleFileVisitor<Path> {

    private final String mDate;
    private boolean mInterrupted;
    private PathMatcher mPathMatcher;
    private final AlbumRoot mAlbumRoot;
    private static final SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd");

    FileVisitor(AlbumRoot root) {
        mAlbumRoot = root;
        mDate = sDateFormat.format(Calendar.getInstance().getTime());
    }

    public boolean isInterrupted() {
        return mInterrupted;
    }

    @Override
    public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
        if (Thread.interrupted()) {
            mInterrupted = true;
            return FileVisitResult.TERMINATE;
        }

        String relativePath = StringUtils.removeStart(dir.toString(), mAlbumRoot.getSpecificPath());
        if (StringUtils.isEmpty(relativePath)) {
            relativePath = String.valueOf(IOUtils.DIR_SEPARATOR);
        }

        AlbumManager.INSTANCE.insert(mAlbumRoot.getId(), relativePath, mDate);

        return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
        //Xlog.d(getClass(), file.toString());

        return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult visitFileFailed(Path file, IOException exc) {
        return FileVisitResult.CONTINUE;
    }
}
