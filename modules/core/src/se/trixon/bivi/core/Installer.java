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

import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import org.openide.modules.ModuleInstall;
import org.openide.windows.WindowManager;
import se.trixon.almond.Xlog;
import se.trixon.almond.swing.NoTabsTabDisplayerUI;
import se.trixon.bivi.core.about.AboutInitializer;

public class Installer extends ModuleInstall {

    @Override
    public void restored() {
        AboutInitializer.init();
        SwingUtilities.invokeLater(() -> {
            UIManager.put("EditorTabDisplayerUI", NoTabsTabDisplayerUI.ID);
            UIManager.put("NbMainWindow.showCustomBackground", Boolean.TRUE);
            System.setProperty("netbeans.winsys.status_line.path", "se/trixon/bivi/core/windows/statusbar/se-trixon-bivi-core-windows-statusbar-StatusBar.instance");
        });
        
        WindowManager.getDefault().invokeWhenUIReady(() -> {
            Xlog.select();
            Bivi.log("Welcome");
        });

    }
}
