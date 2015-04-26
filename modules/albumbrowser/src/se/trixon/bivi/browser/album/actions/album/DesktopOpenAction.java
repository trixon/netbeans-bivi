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
package se.trixon.bivi.browser.album.actions.album;

import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import org.openide.awt.ActionID;
import org.openide.awt.ActionRegistration;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle.Messages;
import se.trixon.bivi.db.api.Album;
import se.trixon.bivi.browser.album.AlbumNode;

@ActionID(
        category = "Nodes/Album",
        id = "se.trixon.bivi.browser.album.actions.album.DesktopOpenAction"
)
@ActionRegistration(
        displayName = "#CTL_DesktopOpenAction"
)
@Messages("CTL_DesktopOpenAction=Open in File Manager123")
public final class DesktopOpenAction extends AlbumNodeAction implements ActionListener {

    @Override
    public void actionPerformed(ActionEvent e) {
        AlbumNode albumNode = getSelectedNode();
        Album album = albumNode.getAlbum();
        if (Desktop.isDesktopSupported()) {
            try {
                Desktop.getDesktop().open(album.getAbsoluteFile());
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
    }
}
