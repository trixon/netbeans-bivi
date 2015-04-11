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

import java.io.File;
import java.util.prefs.Preferences;
import org.apache.commons.io.FileUtils;
import org.openide.util.NbPreferences;

/**
 *
 * @author Patrik Karlsson <patrik@trixon.se>
 */
public enum DbOptions {

    INSTANCE;
    private final String KEY_PATH = "path";
    private final String DEFAULT_PATH = FileUtils.getUserDirectoryPath();
    private final Preferences mPreferences;

    private DbOptions() {
        mPreferences = NbPreferences.forModule(this.getClass());
    }

    public File getPath() {
        return new File(mPreferences.get(KEY_PATH, DEFAULT_PATH));
    }

    public void setPath(File value) {
        mPreferences.put(KEY_PATH, value.getAbsolutePath());
    }
}
