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
package se.trixon.bivi.core;

import java.util.ResourceBundle;
import javax.swing.SwingUtilities;
import org.openide.util.HelpCtx;
import org.openide.windows.TopComponent;
import se.trixon.almond.dialogs.Message;
import se.trixon.almond.dictionary.Dict;

/**
 *
 * @author Patrik Karlsson <patrik@trixon.se>
 */
public abstract class BiviTopComponent extends TopComponent {

    protected static final int TOOLBAR_ICON_SIZE = 32;
    protected ResourceBundle mBundle;
    protected String mHelpId = null;

    protected void displayHelp(final String helpId) {

        SwingUtilities.invokeLater(() -> {
            if (!new HelpCtx(helpId).display()) {
                Message.error(Dict.HELP_NOT_FOUND_TITLE.getString(), String.format(Dict.HELP_NOT_FOUND_MESSAGE.getString(), helpId));
            }
        });
    }
}
