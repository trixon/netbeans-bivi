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
public class Tables {

    public class AlbumRoots {

        public static final String ID = "ID";
        public static final String IDENTIFIER = "identifier";
        public static final String LABEL = "label";
        public static final String SPECIFIC_PATH = "specificPath";
        public static final String STATUS = "status";
        public static final String TYPE = "type";
        public static final String _NAME = "AlbumRoots";
    }

    public class Albums {

        public static final String _NAME = "Albums";
        public static final String ID = "ID";
        public static final String ALBUM_ROOT = "albumRoot";
        public static final String RELATIVE_PATH = "relativePath";
        public static final String DATE = "date";
        public static final String CAPTION = "caption";
        public static final String COLLECTION = "collection";
        public static final String ICON = "icon";
    }
}
