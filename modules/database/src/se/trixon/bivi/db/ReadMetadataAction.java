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
import org.netbeans.api.progress.ProgressHandle;
import org.netbeans.api.progress.ProgressHandleFactory;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionRegistration;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle;
import org.openide.util.RequestProcessor;
import se.trixon.almond.Xlog;
import se.trixon.bivi.db.api.AlbumRoot;
import se.trixon.bivi.db.api.Db;
import se.trixon.bivi.db.api.Db.AlbumRootsDef;
import se.trixon.bivi.db.api.DbManager;

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
        });

        mProgressHandle.start();
        task.schedule(0);
    }

    @Override
    public void run() {
        ArrayList<AlbumRoot> roots = new ArrayList<>();
        AlbumRootsDef albumRoots = Db.AlbumRootsDef.INSTANCE;
        try {
            Connection conn = DbManager.INSTANCE.getConnection();
            try (Statement statement = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE)) {
                SelectQuery selectQuery = new SelectQuery()
                        .addColumns(albumRoots.getId(), albumRoots.getSpecificPath())
                        .validate();
                String sql = selectQuery.toString();
                Xlog.d(getClass(), sql);

                try (ResultSet rs = statement.executeQuery(selectQuery.toString())) {
                    while (rs.next()) {
                        int id = rs.getInt(Db.AlbumRootsDef.ID);
                        String specificPath = rs.getString(Db.AlbumRootsDef.SPECIFIC_PATH);
                        AlbumRoot albumRoot = new AlbumRoot();
                        albumRoot.setId(id);
                        albumRoot.setSpecificPath(specificPath);

                        Xlog.d(getClass(), id + ": " + specificPath);
                        roots.add(albumRoot);
                    }
                }
            }
        } catch (ClassNotFoundException | SQLException ex) {
            Exceptions.printStackTrace(ex);
        }

        try {
            visitRoots(roots);
        } catch (SQLException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    private void visitRoots(ArrayList<AlbumRoot> roots) throws SQLException {
        EnumSet<FileVisitOption> fileVisitOptions = EnumSet.of(FileVisitOption.FOLLOW_LINKS);
        mManager.beginTransaction();
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
        mManager.commitTransaction();
    }
}
