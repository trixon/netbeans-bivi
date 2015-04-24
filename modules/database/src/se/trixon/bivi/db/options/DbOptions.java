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

import java.util.prefs.Preferences;
import org.openide.util.NbPreferences;

/**
 *
 * @author Patrik Karlsson <patrik@trixon.se>
 */
public enum DbOptions {

    INSTANCE;
    public final String KEY_LIBRARY_MONITOR = "library_monitor";
    public final String KEY_LIBRARY_UPDATE_ON_START = "library_updateonstart";
    private final boolean DEFAULT_LIBRARY_MONITOR = false;
    private final boolean DEFAULT_LIBRARY_UPDATE_ON_START = false;
    private final Preferences mPreferences;

    private DbOptions() {
        mPreferences = NbPreferences.forModule(this.getClass());
    }

    public Preferences getPreferences() {
        return mPreferences;
    }

    public boolean isLibraryMonitor() {
        return mPreferences.getBoolean(KEY_LIBRARY_MONITOR, DEFAULT_LIBRARY_MONITOR);
    }

    public boolean isLibraryUpdateOnStart() {
        return mPreferences.getBoolean(KEY_LIBRARY_UPDATE_ON_START, DEFAULT_LIBRARY_UPDATE_ON_START);
    }

    public void setLibraryMonitor(boolean value) {
        mPreferences.putBoolean(KEY_LIBRARY_MONITOR, value);
    }

    public void setLibraryUpdateOnStart(boolean value) {
        mPreferences.putBoolean(KEY_LIBRARY_UPDATE_ON_START, value);
    }
}
