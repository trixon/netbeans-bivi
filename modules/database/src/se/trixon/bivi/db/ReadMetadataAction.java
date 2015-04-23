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
import se.trixon.bivi.db.api.Db;
import se.trixon.bivi.db.api.Db.AlbumRootsDef;

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
        });

        mProgressHandle.start();
        task.schedule(0);
    }

    @Override
    public void run() {
        AlbumRootsDef albumRoots = Db.AlbumRootsDef.INSTANCE;
        SelectQuery selectQuery = new SelectQuery()
                .addColumns(albumRoots.getId(), albumRoots.getSpecificPath())
                .validate();
        String sql = selectQuery.toString();
        Xlog.d(getClass(), sql);
        try {
            Thread.sleep(4000);
        } catch (InterruptedException ex) {
            Exceptions.printStackTrace(ex);
        }
    }
}
