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
package se.trixon.bivi.browser.album.actions;

import org.openide.explorer.ExplorerManager;
import org.openide.nodes.Node;
import se.trixon.bivi.core.BiviGlobals;

/**
 *
 * @author Patrik Karlsson
 */
public abstract class NodeAction {

    protected ExplorerManager mAlbumExplorerManager = BiviGlobals.getAlbumExplorerManager();

    public Node getSelectedNode() {
        Node[] nodes = mAlbumExplorerManager.getSelectedNodes();
        Node node = null;
        
        if (nodes.length > 0) {
            node = nodes[0];
        }
        
        return node;
    }
}
