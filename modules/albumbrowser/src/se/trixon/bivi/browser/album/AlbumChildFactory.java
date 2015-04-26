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
package se.trixon.bivi.browser.album;

import java.beans.IntrospectionException;
import java.io.File;
import java.io.FilenameFilter;
import java.nio.file.Files;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.openide.nodes.ChildFactory;
import org.openide.nodes.Node;
import org.openide.util.Exceptions;
import se.trixon.bivi.db.api.Album;
import se.trixon.bivi.db.api.AlbumManager;
import se.trixon.bivi.db.api.AlbumRoot;
import se.trixon.bivi.db.api.AlbumRootManager;
import se.trixon.bivi.db.api.Db;

/**
 *
 * @author Patrik Karlsson
 */
public class AlbumChildFactory extends ChildFactory<Album> {

    public static final int PARENT_IS_ALBUM = 1;
    public static final int PARENT_IS_ROOT = 0;
    private final int mType;
    private AlbumRoot mAlbumRoot;
    private Album mAlbum;
    private final long albumRootId;
    private final String mRelativePath;
    private String mSpecificPath = "";
    private AlbumRootManager mAlbumRootManager = AlbumRootManager.INSTANCE;
    private AlbumManager mAlbumManager = AlbumManager.INSTANCE;
    private File mAbsoluteFile;

    public AlbumChildFactory(int parent, AlbumRoot albumRoot) {
        mType = parent;
        mAlbumRoot = albumRoot;
        albumRootId = mAlbumRoot.getId();
        mAbsoluteFile = albumRoot.getAbsoluteFile();
        mRelativePath = String.valueOf(IOUtils.DIR_SEPARATOR);

        try {
            mSpecificPath = mAlbumRootManager.getAbsoluteFile(albumRootId).getAbsolutePath();
        } catch (SQLException ex) {
//            Exceptions.printStackTrace(ex);
        }
    }

    public AlbumChildFactory(int parent, Album album) {
        mType = parent;
        mAlbum = album;
        albumRootId = album.getAlbumRootId();
        mRelativePath = album.getRelativePath();
        mAbsoluteFile = album.getAbsoluteFile();

        try {
            mSpecificPath = mAlbumRootManager.getAbsoluteFile(albumRootId).getAbsolutePath();
        } catch (SQLException ex) {
//            Exceptions.printStackTrace(ex);
        }
    }

    @Override
    protected boolean createKeys(List<Album> toPopulate) {
        File dir;
        if (mType == PARENT_IS_ROOT) {
            dir = new File(mAlbumRoot.getSpecificPath() + "/");
        } else {
            dir = mAlbum.getAbsoluteFile();
        }

        populateItems(albumRootId, dir, toPopulate);

        return true;
    }

    @Override
    protected Node createNodeForKey(Album key) {
        AlbumNode node = null;

        try {
            node = new AlbumNode(key);
        } catch (IntrospectionException ex) {
            Exceptions.printStackTrace(ex);
        }

        return node;
    }

    private void populateItems(long albumRootId, File dir, List<Album> toPopulate) {
        String[] directories = dir.list(new FilenameFilter() {
            @Override
            public boolean accept(File current, String name) {
                File f = new File(current, name);

                return f.isDirectory()
                        && !f.isHidden()
                        && !Files.isSymbolicLink(f.toPath());
            }
        });

        Arrays.sort(directories);

        for (String directory : directories) {
            Album album;

            String relativePath;
            relativePath = FilenameUtils.concat(mRelativePath, directory);
            relativePath = Db.INSTANCE.formatString(relativePath);

            try {
                if (!mAlbumManager.exists(albumRootId, relativePath)) {
                    mAlbumManager.insert(albumRootId, relativePath);
                }
                album = AlbumManager.INSTANCE.select(albumRootId, relativePath);
                toPopulate.add(album);
            } catch (SQLException ex) {
                //Album not found, and failed to insert new.
                Exceptions.printStackTrace(ex);
            }
        }
    }
}
