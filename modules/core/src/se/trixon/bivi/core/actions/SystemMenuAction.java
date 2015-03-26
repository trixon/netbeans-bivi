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
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;
import javax.swing.SwingUtilities;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.awt.Actions;
import org.openide.awt.Mnemonics;
import org.openide.util.ImageUtilities;
import org.openide.util.actions.Presenter;
import se.trixon.almond.dictionary.Dict;

/**
 *
 * @author Patrik Karlsson <patrik@trixon.se>
 */
@ActionID(
        category = "System",
        id = "org.nbgames.core.actions.SystemMenuAction"
)
@ActionRegistration(
        displayName = "#CTL_SystemMenuAction", lazy = false
)
@ActionReferences({
    @ActionReference(path = "Toolbars/System", position = 9999),
    @ActionReference(path = "Shortcuts", name = "O-F10")
})
public final class SystemMenuAction extends AbstractAction implements Presenter.Toolbar {

    private final JButton mButton = new JButton();
    private final JPopupMenu mPopup = new JPopupMenu();

    public SystemMenuAction() {
        SwingUtilities.invokeLater(() -> {
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
        });

        mButton.setIcon(ImageUtilities.loadImageIcon("se/trixon/bivi/core/res/format-justify-fill24.png", false));
        mButton.addMouseListener(new MouseListener() {

            @Override
            public void mouseClicked(MouseEvent e) {
            }

            @Override
            public void mouseEntered(MouseEvent e) {
            }

            @Override
            public void mouseExited(MouseEvent e) {
            }

            @Override
            public void mousePressed(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON1) {
                    mPopup.show(mButton, mButton.getWidth() - mPopup.getWidth(), mButton.getHeight());

                    int x = mButton.getLocationOnScreen().x + mButton.getWidth() - mPopup.getWidth();
                    int y = mButton.getLocationOnScreen().y + mButton.getHeight();

                    mPopup.setLocation(x, y);
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
            }
        });
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
    }

    @Override
    public Component getToolbarPresenter() {

        return mButton;
    }

    private void add(JComponent parentMenu, String category, String id) {
        Action a = Actions.forID(category, id);
        JMenuItem menu = new JMenuItem(a);
        Mnemonics.setLocalizedText(menu, a.getValue(Action.NAME).toString());
        parentMenu.add(menu);
    }
}
