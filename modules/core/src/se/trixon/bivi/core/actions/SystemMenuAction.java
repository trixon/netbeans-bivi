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

import java.awt.Component;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.awt.Actions;
import org.openide.awt.Mnemonics;
import se.trixon.almond.SwingHelper;
import se.trixon.almond.dictionary.Dict;

/**
 *
 * @author Patrik Karlsson <patrik@trixon.se>
 */
@ActionID(
        category = "System",
        id = "se.trixon.bivi.core.actions.SystemMenuAction"
)
@ActionRegistration(
        iconBase = "se/trixon/bivi/core/res/format-justify-fill.png",
        displayName = "#CTL_SystemMenuAction"
)
@ActionReferences({
    @ActionReference(path = "Toolbars/System", position = 9999)
})
public final class SystemMenuAction implements ActionListener {

    private final JPopupMenu mPopup = new JPopupMenu();

    public SystemMenuAction() {
        JMenu menu;
//            add(mPopup, "Window", "org.nbgames.core.StartPageTopComponent");
        add(mPopup, "Window", "org.netbeans.core.windows.actions.ToggleFullScreenAction");
        add(mPopup, "Window", "org.netbeans.core.windows.actions.ShowEditorOnlyAction");
        mPopup.add(new JSeparator());

        add(mPopup, "System", "org.netbeans.modules.autoupdate.ui.actions.PluginManagerAction");
        add(mPopup, "Window", "org.netbeans.modules.options.OptionsWindowAction");
        add(mPopup, "Window", "org.netbeans.core.windows.actions.ToolbarsListAction");
        //FIXME Does not populate...
        mPopup.add(new JSeparator());

        menu = new JMenu(Dict.SYSTEM.getString());
        add(menu, "Window", "org.netbeans.core.windows.actions.ResetWindowsAction");
        menu.add(new JSeparator());
        add(menu, "Window", "org.netbeans.core.io.ui.IOWindowAction");
        add(menu, "View", "org.netbeans.core.actions.LogAction");
        mPopup.add(menu);
        mPopup.add(new JSeparator());

        menu = new JMenu(Dict.HELP.getString());
        add(menu, "Help", "se.trixon.bivi.core.actions.HelpAction");
        add(menu, "System", "org.netbeans.modules.autoupdate.ui.actions.CheckForUpdatesAction");
        add(menu, "Help", "org.netbeans.core.actions.AboutAction");
        mPopup.add(menu);
        mPopup.add(new JSeparator());

        add(mPopup, "File", "se.trixon.almond.actions.QuitAction");

        Font font = menu.getFont().deriveFont(menu.getFont().getSize() + 3f);
        SwingHelper.setComponentsFont(mPopup, font);
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        Component component = (Component) actionEvent.getSource();

        mPopup.show(component, component.getWidth() - mPopup.getWidth(), component.getHeight());

        int x = component.getLocationOnScreen().x + component.getWidth() - mPopup.getWidth();
        int y = component.getLocationOnScreen().y + component.getHeight();

        mPopup.setLocation(x, y);
    }

    private void add(JComponent parentMenu, String category, String id) {
        Action a = Actions.forID(category, id);
        JMenuItem menu = new JMenuItem(a);
        Mnemonics.setLocalizedText(menu, a.getValue(Action.NAME).toString());
        parentMenu.add(menu);
    }
}
