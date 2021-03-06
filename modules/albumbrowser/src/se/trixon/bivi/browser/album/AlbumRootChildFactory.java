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
package se.trixon.bivi.browser.album;

import java.beans.IntrospectionException;
import java.util.List;
import org.openide.nodes.ChildFactory;
import org.openide.nodes.Node;
import org.openide.util.Exceptions;
import se.trixon.bivi.db.api.AlbumRoot;
import se.trixon.bivi.db.api.AlbumRootManager;

/**
 *
 * @author Patrik Karlsson
 */
public class AlbumRootChildFactory extends ChildFactory<AlbumRoot> {

    @Override
    protected boolean createKeys(List<AlbumRoot> toPopulate) {
        toPopulate.addAll(AlbumRootManager.INSTANCE.getRoots());

        return true;
    }

    @Override
    protected Node createNodeForKey(AlbumRoot key) {
        AlbumRootNode node = null;

        try {
            node = new AlbumRootNode(key);
        } catch (IntrospectionException ex) {
            Exceptions.printStackTrace(ex);
        }

        return node;
    }
}
