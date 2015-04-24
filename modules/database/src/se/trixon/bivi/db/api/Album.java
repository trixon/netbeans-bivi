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

/**
 *
 * @author Patrik Karlsson
 */
public class Album {

    private long mAlbumRootId;
    private String mCaption;
    private String mCollection;
    private String mDate;
    private long mIcon;
    private long mId;
    private String mRelativePath;

    public Album() {
    }

    public long getAlbumRootId() {
        return mAlbumRootId;
    }

    public String getCaption() {
        return mCaption;
    }

    public String getCollection() {
        return mCollection;
    }

    public String getDate() {
        return mDate;
    }

    public long getIcon() {
        return mIcon;
    }

    public long getId() {
        return mId;
    }

    public String getRelativePath() {
        return mRelativePath;
    }

    public long getmId() {
        return mId;
    }

    public void setAlbumRootId(long albumRootId) {
        mAlbumRootId = albumRootId;
    }

    public void setCaption(String caption) {
        mCaption = caption;
    }

    public void setCollection(String collection) {
        mCollection = collection;
    }

    public void setDate(String date) {
        mDate = date;
    }

    public void setIcon(long icon) {
        mIcon = icon;
    }

    public void setId(long id) {
        mId = id;
    }

    public void setRelativePath(String relativePath) {
        mRelativePath = relativePath;
    }

    public void setmId(long id) {
        mId = id;
    }
}
