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

import java.beans.IntrospectionException;
import java.util.List;
import org.openide.nodes.ChildFactory;
import org.openide.nodes.Node;
import org.openide.util.Exceptions;

/**
 *
 * @author Patrik Karlsson
 */
public class AlbumChildFactory extends ChildFactory<Album> {

    public static final int PARENT_IS_ALBUM = 1;
    public static final int PARENT_IS_ROOT = 0;
    private final int mType;

    public AlbumChildFactory(int parent) {
        mType = parent;
    }

    @Override
    protected boolean createKeys(List<Album> toPopulate) {
        if (mType == PARENT_IS_ROOT) {
            Album a = new Album();
            a.setCaption("caption 0");
            a.setRelativePath("/the/complete/path/");
            toPopulate.add(a);
            toPopulate.add(a);
            toPopulate.add(a);
        } else {
            Album a = new Album();
            a.setCaption("caption 1");
            a.setRelativePath("/sub/sub/");
            toPopulate.add(a);
            toPopulate.add(a);
            toPopulate.add(a);
        }

        return true;
    }

    @Override
    protected Node createNodeForKey(Album key) {
        AlbumNode node = null;

        try {
            node = new AlbumNode(key);
        } catch (IntrospectionException ex) {
            Exceptions.printStackTrace(ex);
        }

        return node;
    }
}
