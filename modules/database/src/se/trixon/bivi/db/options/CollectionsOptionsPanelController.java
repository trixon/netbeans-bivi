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
import javax.swing.JComponent;
import javax.swing.SwingUtilities;
import org.netbeans.spi.options.OptionsPanelController;
import org.openide.util.HelpCtx;
import org.openide.util.Lookup;

@OptionsPanelController.SubRegistration(
        location = "Database",
        displayName = "#AdvancedOption_DisplayName_Collections",
        keywords = "#AdvancedOption_Keywords_Collections",
        keywordsCategory = "Database/Collections",
        position = 2
)
@org.openide.util.NbBundle.Messages({"AdvancedOption_DisplayName_Collections=Collections", "AdvancedOption_Keywords_Collections=Collections"})
/**
 *
 * @author Patrik Karlsson <patrik@trixon.se>
 */
public final class CollectionsOptionsPanelController extends OptionsPanelController {

    private boolean mChanged;
    private CollectionsPanel mPanel;
    private final PropertyChangeSupport mPcs = new PropertyChangeSupport(this);

    @Override
    public void addPropertyChangeListener(PropertyChangeListener l) {
        mPcs.addPropertyChangeListener(l);
    }

    @Override
    public void applyChanges() {
        SwingUtilities.invokeLater(() -> {
            getPanel().store();
            mChanged = false;
        });
    }

    @Override
    public void cancel() {
        // need not do anything special, if no changes have been persisted yet
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
        getPanel().load();
        mChanged = false;
    }

    private CollectionsPanel getPanel() {
        if (mPanel == null) {
            mPanel = new CollectionsPanel(this);
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
