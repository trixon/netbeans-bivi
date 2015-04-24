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
package se.trixon.bivi.db.options;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.sql.SQLException;
import javax.swing.JComponent;
import javax.swing.SwingUtilities;
import org.netbeans.spi.options.OptionsPanelController;
import org.openide.util.Exceptions;
import org.openide.util.HelpCtx;
import org.openide.util.Lookup;
import se.trixon.bivi.db.api.DbManager;
import se.trixon.bivi.db.api.DbMonitor;

@OptionsPanelController.SubRegistration(
        location = "Album",
        position = 0,
        displayName = "#AdvancedOption_DisplayName_AlbumRoots",
        keywords = "#AdvancedOption_Keywords_AlbumRoots",
        keywordsCategory = "Album/AlbumRoots"
)
public final class AlbumRootsOptionsPanelController extends OptionsPanelController {

    private boolean mChanged;
    private AlbumRootsPanel mPanel;
    private final PropertyChangeSupport mPcs = new PropertyChangeSupport(this);
    private final DbManager mManager = DbManager.INSTANCE;

    @Override
    public void addPropertyChangeListener(PropertyChangeListener l) {
        mPcs.addPropertyChangeListener(l);
    }

    @Override
    public void applyChanges() {
        SwingUtilities.invokeLater(() -> {
            try {
                getPanel().store();
                if (getPanel().hasChanged()) {
                    DbMonitor.INSTANCE.dbRootAlbumsChanged();
                }
                mChanged = false;
            } catch (SQLException ex) {
                Exceptions.printStackTrace(ex);
            }
        });
    }

    @Override
    public void cancel() {
        try {
            mManager.rollbackTransaction();
        } catch (SQLException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    @Override
    public JComponent getComponent(Lookup masterLookup) {
        return getPanel();
    }

    @Override
    public HelpCtx getHelpCtx() {
        return null; // new HelpCtx("...ID") if you have a help set
    }

    @Override
    public boolean isChanged() {
        return mChanged;
    }

    @Override
    public boolean isValid() {
        return getPanel().valid();
    }

    @Override
    public void removePropertyChangeListener(PropertyChangeListener l) {
        mPcs.removePropertyChangeListener(l);
    }

    @Override
    public void update() {
        try {
            getPanel().load();
            mChanged = false;
        } catch (SQLException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    private AlbumRootsPanel getPanel() {
        if (mPanel == null) {
            mPanel = new AlbumRootsPanel(this);
        }
        return mPanel;
    }

    void changed() {
        if (!mChanged) {
            mChanged = true;
            mPcs.firePropertyChange(OptionsPanelController.PROP_CHANGED, false, true);
        }
        mPcs.firePropertyChange(OptionsPanelController.PROP_VALID, null, null);
    }
}
