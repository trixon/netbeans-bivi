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

import com.healthmarketscience.sqlbuilder.BinaryCondition;
import com.healthmarketscience.sqlbuilder.CustomSql;
import com.healthmarketscience.sqlbuilder.FunctionCall;
import com.healthmarketscience.sqlbuilder.OrderObject;
import com.healthmarketscience.sqlbuilder.SelectQuery;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.swing.DefaultListModel;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle;
import se.trixon.almond.Xlog;
import se.trixon.almond.dialogs.Message;
import se.trixon.almond.dictionary.Dict;
import se.trixon.almond.icon.Pict;
import se.trixon.bivi.db.api.DbManager;
import se.trixon.bivi.db.api.AlbumRoot;
import se.trixon.bivi.db.api.AlbumRootManager;
import se.trixon.bivi.db.api.Db;

/**
 *
 * @author Patrik Karlsson <patrik@trixon.se>
 */
public class AlbumRootsPanel extends javax.swing.JPanel {

    private final Db.AlbumRootsDef mAlbumRootsDef = Db.AlbumRootsDef.INSTANCE;
    private final DbManager mManager = DbManager.INSTANCE;
    private final AlbumRootsOptionsPanelController mController;
    private final AlbumRootManager mAlbumRootManager = AlbumRootManager.INSTANCE;
    private final DbOptions mOptions = DbOptions.INSTANCE;
    private boolean mChanged;

    AlbumRootsPanel(AlbumRootsOptionsPanelController controller) {
        mController = controller;
        initComponents();
        init();
    }

    public boolean hasChanged() {
        return mChanged;
    }

    private void dbDelete(AlbumRoot albumRoot) throws ClassNotFoundException, SQLException {
        mManager.beginTransaction();

        if (albumRoot == null) {
            mAlbumRootManager.deleteAll();
        } else {
            mAlbumRootManager.delete(albumRoot.getId());
        }

        mChanged = true;
        mController.changed();
    }

    private boolean hasDuplicate(AlbumRoot albumRoot, boolean update) throws ClassNotFoundException, SQLException {
        SelectQuery selectQuery = new SelectQuery()
                .addCustomColumns(new CustomSql(FunctionCall.countAll() + " AS ROW_COUNT"))
                .addFromTable(mAlbumRootsDef.getTable())
                .addCondition(BinaryCondition.equalTo(mAlbumRootsDef.getIdentifier(), albumRoot.getIdentifier()))
                .addCondition(BinaryCondition.equalTo(mAlbumRootsDef.getSpecificPath(), albumRoot.getSpecificPath()));

        if (update) {
            selectQuery.addCondition(BinaryCondition.notEqualTo(mAlbumRootsDef.getId(), albumRoot.getId()));
        }

        selectQuery.validate();
        Xlog.d(getClass(), selectQuery.toString());

        int rowCount = 0;

        Connection conn = DbManager.INSTANCE.getConnection();
        try (Statement statement = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
                ResultSet rs = statement.executeQuery(selectQuery.toString())) {
            rs.first();
            rowCount = rs.getInt("ROW_COUNT");
        } catch (SQLException ex) {
            Exceptions.printStackTrace(ex);
        }

        boolean hasDuplicate = rowCount > 0;
        if (hasDuplicate) {
            Message.warning("Not Distinct path/drive", "Try again");
        }

        return hasDuplicate;
    }

    private boolean dbInsert(AlbumRoot albumRoot) throws ClassNotFoundException, SQLException {
        if (hasDuplicate(albumRoot, false)) {
            return false;
        }

        mManager.beginTransaction();
        mAlbumRootManager.insert(albumRoot);

        mChanged = true;
        mController.changed();
        return true;
    }

    private void dbSelect() {
        DefaultListModel model = new DefaultListModel<>();

        try {
            Connection conn = DbManager.INSTANCE.getConnection();
            try (Statement statement = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE)) {
                SelectQuery selectQuery = new SelectQuery()
                        .addAllTableColumns(mAlbumRootsDef.getTable())
                        .addOrdering(mAlbumRootsDef.getLabel(), OrderObject.Dir.ASCENDING)
                        .validate();
                Xlog.d(getClass(), selectQuery.toString());

                try (ResultSet rs = statement.executeQuery(selectQuery.toString())) {
                    while (rs.next()) {
                        int id = rs.getInt(Db.AlbumRootsDef.ID);
                        String label = rs.getString(Db.AlbumRootsDef.LABEL);
                        String identifier = rs.getString(Db.AlbumRootsDef.IDENTIFIER);
                        String specificPath = rs.getString(Db.AlbumRootsDef.SPECIFIC_PATH);
                        int status = rs.getInt(Db.AlbumRootsDef.STATUS);
                        int type = rs.getInt(Db.AlbumRootsDef.TYPE);

                        AlbumRoot albumRoot = new AlbumRoot();
                        albumRoot.setId(id);
                        albumRoot.setIdentifier(identifier);
                        albumRoot.setLabel(label);
                        albumRoot.setSpecificPath(specificPath);
                        albumRoot.setStatus(status);
                        albumRoot.setType(type);

                        model.addElement(albumRoot);
                    }
                }
            }
        } catch (ClassNotFoundException | SQLException ex) {
            Exceptions.printStackTrace(ex);
        }

        list.setModel(model);
    }

    private boolean dbUpdate(AlbumRoot albumRoot) throws ClassNotFoundException, SQLException {
        if (hasDuplicate(albumRoot, true)) {
            return false;
        }

        mManager.beginTransaction();
        mAlbumRootManager.update(albumRoot);

        mChanged = true;
        mController.changed();
        return true;
    }

    private void editAlbumRoot(AlbumRoot albumRoot) throws SQLException, ClassNotFoundException {

        albumRoot = requestAlbumRoot(albumRoot, Dict.EDIT.getString());
        if (albumRoot != null) {
            if (!dbUpdate(albumRoot)) {
                editAlbumRoot(albumRoot);
            }
        }

        dbSelect();
    }

    private AlbumRoot requestAlbumRoot(AlbumRoot albumRoot, String title) {
        AlbumRootPanel albumRootPanel = new AlbumRootPanel();
        DialogDescriptor d = new DialogDescriptor(albumRootPanel, title, true, (ActionEvent e) -> {
        });

        albumRootPanel.setDialogDescriptor(d);
        albumRootPanel.setAlbumRoot(albumRoot);

        Object retval = DialogDisplayer.getDefault().notify(d);
        if (retval == NotifyDescriptor.OK_OPTION) {
            AlbumRoot editedAlbumRoot = albumRootPanel.getAlbumRoot();
            if (editedAlbumRoot.isValid()) {
                return editedAlbumRoot;
            }
        }

        return null;
    }

    private void addAlbumRoot(AlbumRoot albumRoot) throws SQLException, ClassNotFoundException {
        if (albumRoot == null) {
            albumRoot = new AlbumRoot();
        }

        albumRoot = requestAlbumRoot(albumRoot, Dict.ADD.getString());
        if (albumRoot != null) {
            if (!dbInsert(albumRoot)) {
                addAlbumRoot(albumRoot);
            }
        }

        dbSelect();
    }

    private AlbumRoot getSelectedItem() {
        return (AlbumRoot) list.getSelectedValue();
    }

    private void init() {
        final int ICON_SIZE = 24;
        addButton.setIcon(Pict.Actions.LIST_ADD.get(ICON_SIZE));
        editButton.setIcon(Pict.Actions.DOCUMENT_EDIT.get(ICON_SIZE));
        removeButton.setIcon(Pict.Actions.LIST_REMOVE.get(ICON_SIZE));
        removeAllButton.setIcon(Pict.Actions.EDIT_DELETE.get(ICON_SIZE));

        list.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                if (evt.getButton() == MouseEvent.BUTTON1 && evt.getClickCount() == 2) {
                    try {
                        editAlbumRoot(getSelectedItem());
                    } catch (SQLException | ClassNotFoundException ex) {
                        Exceptions.printStackTrace(ex);
                    }
                }
            }
        });
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        toolBar = new javax.swing.JToolBar();
        addButton = new javax.swing.JButton();
        editButton = new javax.swing.JButton();
        removeButton = new javax.swing.JButton();
        removeAllButton = new javax.swing.JButton();
        scrollPane = new javax.swing.JScrollPane();
        list = new javax.swing.JList();
        footerPanel = new javax.swing.JPanel();
        updateLabel = new javax.swing.JLabel();
        updateOnStartCheckBox = new javax.swing.JCheckBox();
        monitorCheckBox = new javax.swing.JCheckBox();

        setPreferredSize(new java.awt.Dimension(200, 100));
        setLayout(new java.awt.BorderLayout());

        toolBar.setFloatable(false);
        toolBar.setRollover(true);

        addButton.setToolTipText(Dict.ADD.getString());
        addButton.setFocusable(false);
        addButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        addButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        addButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addButtonActionPerformed(evt);
            }
        });
        toolBar.add(addButton);

        editButton.setToolTipText(Dict.EDIT.getString());
        editButton.setFocusable(false);
        editButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        editButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        editButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                editButtonActionPerformed(evt);
            }
        });
        toolBar.add(editButton);

        removeButton.setToolTipText(Dict.REMOVE.getString());
        removeButton.setFocusable(false);
        removeButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        removeButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        removeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removeButtonActionPerformed(evt);
            }
        });
        toolBar.add(removeButton);

        removeAllButton.setToolTipText(Dict.REMOVE_ALL.getString());
        removeAllButton.setFocusable(false);
        removeAllButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        removeAllButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        removeAllButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removeAllButtonActionPerformed(evt);
            }
        });
        toolBar.add(removeAllButton);

        add(toolBar, java.awt.BorderLayout.PAGE_START);

        scrollPane.setViewportView(list);

        add(scrollPane, java.awt.BorderLayout.CENTER);

        updateLabel.setFont(updateLabel.getFont().deriveFont(updateLabel.getFont().getStyle() | java.awt.Font.BOLD));
        org.openide.awt.Mnemonics.setLocalizedText(updateLabel, Dict.AUTOMATIC_UPDATING.getString());

        org.openide.awt.Mnemonics.setLocalizedText(updateOnStartCheckBox, org.openide.util.NbBundle.getMessage(AlbumRootsPanel.class, "AlbumRootsPanel.updateOnStartCheckBox.text")); // NOI18N
        updateOnStartCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                updateOnStartCheckBoxActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(monitorCheckBox, org.openide.util.NbBundle.getMessage(AlbumRootsPanel.class, "AlbumRootsPanel.monitorCheckBox.text")); // NOI18N
        monitorCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                monitorCheckBoxActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout footerPanelLayout = new javax.swing.GroupLayout(footerPanel);
        footerPanel.setLayout(footerPanelLayout);
        footerPanelLayout.setHorizontalGroup(
            footerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(footerPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(footerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(updateLabel)
                    .addGroup(footerPanelLayout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addGroup(footerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(monitorCheckBox)
                            .addComponent(updateOnStartCheckBox))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        footerPanelLayout.setVerticalGroup(
            footerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(footerPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(updateLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(updateOnStartCheckBox)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(monitorCheckBox)
                .addGap(0, 0, 0))
        );

        add(footerPanel, java.awt.BorderLayout.PAGE_END);
    }// </editor-fold>//GEN-END:initComponents

    private void addButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addButtonActionPerformed
        try {
            addAlbumRoot(null);
        } catch (SQLException | ClassNotFoundException ex) {
            Exceptions.printStackTrace(ex);
        }
    }//GEN-LAST:event_addButtonActionPerformed

    private void editButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_editButtonActionPerformed
        try {
            AlbumRoot selectedItem = getSelectedItem();
            if (selectedItem != null) {
                editAlbumRoot(selectedItem);
            }
        } catch (SQLException | ClassNotFoundException ex) {
            Exceptions.printStackTrace(ex);
        }
    }//GEN-LAST:event_editButtonActionPerformed

    private void removeButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_removeButtonActionPerformed
        if (getSelectedItem() != null) {
            NotifyDescriptor d = new NotifyDescriptor(
                    NbBundle.getMessage(this.getClass(), "AlbumRootsPanel.message.remove", getSelectedItem().getLabel()),
                    NbBundle.getMessage(this.getClass(), "AlbumRootsPanel.title.remove"),
                    NotifyDescriptor.OK_CANCEL_OPTION,
                    NotifyDescriptor.WARNING_MESSAGE,
                    null,
                    null);
            Object retval = DialogDisplayer.getDefault().notify(d);

            if (retval == NotifyDescriptor.OK_OPTION) {
                try {
                    dbDelete(getSelectedItem());
                    dbSelect();
                } catch (ClassNotFoundException | SQLException ex) {
                    Exceptions.printStackTrace(ex);
                }
            }
        }
    }//GEN-LAST:event_removeButtonActionPerformed

    private void removeAllButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_removeAllButtonActionPerformed
        DefaultListModel model = (DefaultListModel) list.getModel();
        if (!model.isEmpty()) {
            NotifyDescriptor d = new NotifyDescriptor(
                    NbBundle.getMessage(this.getClass(), "AlbumRootsPanel.message.removeAll"),
                    NbBundle.getMessage(this.getClass(), "AlbumRootsPanel.title.removeAll"),
                    NotifyDescriptor.OK_CANCEL_OPTION,
                    NotifyDescriptor.WARNING_MESSAGE,
                    null,
                    null);
            Object retval = DialogDisplayer.getDefault().notify(d);

            if (retval == NotifyDescriptor.OK_OPTION) {
                try {
                    dbDelete(null);
                    dbSelect();
                } catch (ClassNotFoundException | SQLException ex) {
                    Exceptions.printStackTrace(ex);
                }
            }
        }
    }//GEN-LAST:event_removeAllButtonActionPerformed

    private void updateOnStartCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_updateOnStartCheckBoxActionPerformed
        mController.changed();
    }//GEN-LAST:event_updateOnStartCheckBoxActionPerformed

    private void monitorCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_monitorCheckBoxActionPerformed
        mController.changed();
    }//GEN-LAST:event_monitorCheckBoxActionPerformed

    void load() throws SQLException {
        mChanged = false;
        monitorCheckBox.setSelected(mOptions.isLibraryMonitor());
        updateOnStartCheckBox.setSelected(mOptions.isLibraryUpdateOnStart());
        dbSelect();
    }

    void store() throws SQLException {
        mOptions.setLibraryMonitor(monitorCheckBox.isSelected());
        mOptions.setLibraryUpdateOnStart(updateOnStartCheckBox.isSelected());
        mManager.commitTransaction();
    }

    boolean valid() {
        // TODO check whether form is consistent and complete
        return true;
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addButton;
    private javax.swing.JButton editButton;
    private javax.swing.JPanel footerPanel;
    private javax.swing.JList list;
    private javax.swing.JCheckBox monitorCheckBox;
    private javax.swing.JButton removeAllButton;
    private javax.swing.JButton removeButton;
    private javax.swing.JScrollPane scrollPane;
    private javax.swing.JToolBar toolBar;
    private javax.swing.JLabel updateLabel;
    private javax.swing.JCheckBox updateOnStartCheckBox;
    // End of variables declaration//GEN-END:variables
}
