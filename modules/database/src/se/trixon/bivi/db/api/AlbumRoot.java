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
package se.trixon.bivi.db.api;

import java.io.File;

/**
 *
 * @author Patrik Karlsson <patrik@trixon.se>
 */
public class AlbumRoot {

    private long mId;
    private String mIdentifier = "";
    private String mLabel = "";
    private String mSpecificPath = "";
    private int mStatus = 0;
    private int mType = 1;

    public AlbumRoot() {
    }

    public AlbumRoot(String label, String specificPath) {
        mLabel = label;
        mSpecificPath = specificPath;
    }

    public long getId() {
        return mId;
    }

    public String getIdentifier() {
        return mIdentifier;
    }

    public String getLabel() {
        return mLabel;
    }

    public String getSpecificPath() {
        return mSpecificPath;
    }

    public int getStatus() {
        return mStatus;
    }

    public int getType() {
        return mType;
    }

    public boolean isValid() {
        File specificPath = new File(mSpecificPath);
        boolean valid = mLabel.isEmpty() == false && specificPath.isDirectory();
        return valid;
    }

    public void setId(long id) {
        mId = id;
    }

    public void setIdentifier(String identifier) {
        mIdentifier = identifier;
    }

    public void setLabel(String label) {
        mLabel = label;
    }

    public void setSpecificPath(String specificPath) {
        mSpecificPath = specificPath;
    }

    public void setStatus(int status) {
        mStatus = status;
    }

    public void setType(int type) {
        mType = type;
    }

    @Override
    public String toString() {
        return "<html><b>" + mLabel + "</b><br />" + mSpecificPath;
    }
}
