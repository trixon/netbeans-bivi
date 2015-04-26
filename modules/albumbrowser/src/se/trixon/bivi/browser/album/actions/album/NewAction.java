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
package se.trixon.bivi.browser.album.actions.album;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import org.openide.*;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.util.NbBundle.Messages;
import se.trixon.bivi.db.api.Album;

@ActionID(
        category = "Album",
        id = "se.trixon.bivi.browser.album.actions.album.NewAction"
)
@ActionRegistration(
        displayName = "#CTL_NewAction"
)
@ActionReferences({
    @ActionReference(path = "Menu/Album", position = 0),
    @ActionReference(path = "Bivi/NodeMenu/Album", position = 0, separatorAfter = 1)
})

@Messages("CTL_NewAction=New...")
public final class NewAction implements ActionListener {

    private final Album mContext;

    public NewAction(Album context) {
        mContext = context;
    }

    @Override
    public void actionPerformed(ActionEvent ev) {
        NotifyDescriptor.InputLine msg = new NotifyDescriptor.InputLine("New album", "Album");
        DialogDisplayer.getDefault().notify(msg);
    }
}
