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

import com.healthmarketscience.sqlbuilder.SelectQuery;
import com.healthmarketscience.sqlbuilder.dbspec.basic.DbTable;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.ResourceBundle;
import org.apache.commons.io.FilenameUtils;
import org.netbeans.api.progress.ProgressHandle;
import org.netbeans.api.progress.ProgressHandleFactory;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionRegistration;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle;
import org.openide.util.RequestProcessor;
import se.trixon.almond.Xlog;
import se.trixon.bivi.db.api.AlbumManager;
import se.trixon.bivi.db.api.AlbumRoot;
import se.trixon.bivi.db.api.AlbumRootManager;
import se.trixon.bivi.db.api.Db;
import se.trixon.bivi.db.api.Db.AlbumRootsDef;
import se.trixon.bivi.db.api.Db.AlbumsDef;
import se.trixon.bivi.db.api.DbManager;
import se.trixon.bivi.db.api.DbMonitor;

@ActionID(
        category = "Album",
        id = "se.trixon.bivi.db.ReadMetadataAction"
)
@ActionRegistration(
        displayName = "#CTL_ReadMetadataAction"
)
@ActionReference(path = "Menu/Album", position = 3333)
public final class ReadMetadataAction implements ActionListener, Runnable {

    private final ResourceBundle mBundle;
    private final DbManager mManager = DbManager.INSTANCE;
    private ProgressHandle mProgressHandle;
    private final RequestProcessor mRequestProcessor = new RequestProcessor("interruptible tasks", 1, true);

    public ReadMetadataAction() {
        mBundle = NbBundle.getBundle(getClass());
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        final RequestProcessor.Task task = mRequestProcessor.create(this);
        mProgressHandle = ProgressHandleFactory.createHandle(mBundle.getString("CTL_ReadMetadataAction"), task);
        task.addTaskListener((org.openide.util.Task task1) -> {
            mProgressHandle.finish();
            Xlog.d(getClass(), "done");
            DbMonitor.INSTANCE.dbRootAlbumsChanged();
        });

        mProgressHandle.start();
        task.schedule(0);
    }

    @Override
    public void run() {
        ArrayList<AlbumRoot> roots = AlbumRootManager.INSTANCE.getRoots();
        if (!roots.isEmpty()) {
            try {
                mManager.beginTransaction();
                removeNonExistingAlbumRoots(roots);
                removeNonExistingAlbums();
                visitRoots(roots);
                mManager.commitTransaction();
            } catch (SQLException | ClassNotFoundException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
    }

    private void removeNonExistingAlbumRoots(ArrayList<AlbumRoot> roots) {
        for (AlbumRoot albumRoot : roots) {
            File file = new File(albumRoot.getSpecificPath());
            if (!file.isDirectory()) {
                try {
                    AlbumRootManager.INSTANCE.delete(albumRoot.getId());
                } catch (ClassNotFoundException | SQLException ex) {
                    Exceptions.printStackTrace(ex);
                }
            }
        }
    }

    private void removeNonExistingAlbums() throws ClassNotFoundException, SQLException {
        Connection conn = DbManager.INSTANCE.getConnection();
        try (Statement statement = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE)) {
            AlbumsDef albumDefs = Db.AlbumsDef.INSTANCE;
            AlbumRootsDef albumRootsDef = Db.AlbumRootsDef.INSTANCE;
            DbTable album = albumDefs.getTable();
            DbTable albumRoots = albumRootsDef.getTable();
            SelectQuery selectQuery = new SelectQuery()
                    .addColumns(albumRootsDef.getSpecificPath(), albumDefs.getRelativePath(), albumDefs.getId())
                    .addJoin(SelectQuery.JoinType.LEFT_OUTER, album, albumRoots, albumDefs.getAlbumRoot(), albumRootsDef.getId())
                    .validate();
            String sql = selectQuery.toString();
            Xlog.d(getClass(), sql);

            try (ResultSet rs = statement.executeQuery(sql)) {
                while (rs.next()) {
                    long id = rs.getLong(Db.AlbumsDef.ID);
                    String specificPath = rs.getString(Db.AlbumRootsDef.SPECIFIC_PATH);
                    String relativePath = rs.getString(Db.AlbumsDef.RELATIVE_PATH);
                    String path = FilenameUtils.normalize(specificPath + relativePath);
                    File file = new File(path);
                    if (!file.isDirectory()) {
                        Xlog.d(getClass(), file.getAbsolutePath());
                        AlbumManager.INSTANCE.delete(id);
                    }
                }
            }
        }
    }

    private void visitRoots(ArrayList<AlbumRoot> roots) throws SQLException {
        EnumSet<FileVisitOption> fileVisitOptions = EnumSet.of(FileVisitOption.FOLLOW_LINKS);
        for (AlbumRoot root : roots) {
            File file = new File(root.getSpecificPath());
            if (file.isDirectory()) {
                FileVisitor fileVisitor = new FileVisitor(root);
                try {
                    Files.walkFileTree(file.toPath(), fileVisitOptions, Integer.MAX_VALUE, fileVisitor);
                } catch (IOException ex) {
                    Exceptions.printStackTrace(ex);
                }
            }
        }
    }
}
