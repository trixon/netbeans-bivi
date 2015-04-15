/*
 * Copyright 2015 pata.
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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.util.NbBundle;
import se.trixon.bivi.db.api.DbManager;

@ActionID(
        category = "Browse",
        id = "se.trixon.bivi.db.DbSelectAction"
)
@ActionRegistration(
        displayName = "#CTL_DbSelectAction"
)
@ActionReferences({
    @ActionReference(path = "Menu/Browse",
            position = 4999,
            separatorBefore = 2499),
    @ActionReference(path = "Shortcuts", name = "D-D")
})
public final class DbSelectAction implements ActionListener {

    @Override
    public void actionPerformed(ActionEvent e) {
        String title = NbBundle.getMessage(getClass(), "CTL_DbSelectAction");
        DbSelectPanel dbSelectPanel = new DbSelectPanel();
        DialogDescriptor d = new DialogDescriptor(dbSelectPanel, title, true, (ActionEvent actionEvent) -> {
        });

        dbSelectPanel.setDialogDescriptor(d);

        Object retval = DialogDisplayer.getDefault().notify(d);
        if (retval == NotifyDescriptor.OK_OPTION) {
            DbManager.INSTANCE.setPath(new File(dbSelectPanel.getPath()));
        }
    }
}
