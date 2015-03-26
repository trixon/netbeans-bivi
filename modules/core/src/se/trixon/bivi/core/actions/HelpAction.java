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
package se.trixon.bivi.core.actions;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.SwingUtilities;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionRegistration;
import org.openide.util.HelpCtx;
import se.trixon.almond.dialogs.Message;
import se.trixon.almond.dictionary.Dict;

@ActionID(
        category = "Help",
        id = "se.trixon.bivi.core.actions.HelpAction"
)
@ActionRegistration(
        displayName = "#CTL_HelpAction"
)
@ActionReference(path = "Shortcuts", name = "F1")
public final class HelpAction implements ActionListener {

    @Override
    public void actionPerformed(ActionEvent e) {
        String helpId = "se.trixon.bivi.core.about";
        SwingUtilities.invokeLater(() -> {
            if (!new HelpCtx(helpId).display()) {
                Message.error(Dict.HELP_NOT_FOUND_TITLE.getString(), String.format(Dict.HELP_NOT_FOUND_MESSAGE.getString(), helpId));
            }
        });
    }
}
