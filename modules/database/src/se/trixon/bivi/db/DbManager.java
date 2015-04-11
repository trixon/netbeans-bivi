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
package se.trixon.bivi.db;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.prefs.Preferences;
import org.apache.commons.io.FileUtils;
import org.openide.util.NbPreferences;
import se.trixon.almond.Xlog;

/**
 *
 * @author Patrik Karlsson <patrik@trixon.se>
 */
public enum DbManager {

    INSTANCE;
    public final String KEY_PATH = "path";
    private final String DB_FILENAME = "bivi";
    private final String DEFAULT_PATH = FileUtils.getUserDirectoryPath();
    private Connection mConnection = null;
    private boolean mEmpty = false;
    private final Preferences mPreferences;

    private DbManager() {
        mPreferences = NbPreferences.forModule(this.getClass());
    }

    public void closeConnection() throws SQLException {
        if (mConnection != null) {
            Xlog.d(getClass(), "closeConnection");
            mConnection.close();
            mConnection = null;
        }
    }

    public Connection getConnection() throws ClassNotFoundException, SQLException {
        File dbFile = new File(getPathAsFile(), DB_FILENAME + ".h2.db");
        if (!dbFile.exists()) {
            mEmpty = true;
            Xlog.d(getClass(), "Database does not exist, creating " + dbFile.getAbsolutePath());
        }

        if (mConnection == null) {
            Class.forName("org.h2.Driver");
            String jdbcUrl = String.format("jdbc:h2:%s/%s", getPath(), DB_FILENAME);
            Xlog.d(getClass(), "Establishing connection: " + jdbcUrl);
            mConnection = DriverManager.getConnection(jdbcUrl, "sa", "");
        }

        return mConnection;
    }

    public String getPath() {
        return mPreferences.get(KEY_PATH, DEFAULT_PATH);
    }

    public File getPathAsFile() {
        return new File(mPreferences.get(KEY_PATH, DEFAULT_PATH));
    }

    public Preferences getPreferences() {
        return mPreferences;
    }

    public boolean isEmpty() {
        return mEmpty;
    }

    public void setEmpty(boolean empty) {
        mEmpty = empty;
    }

    public void setPath(File value) {
        mPreferences.put(KEY_PATH, value.getAbsolutePath());
    }

}
