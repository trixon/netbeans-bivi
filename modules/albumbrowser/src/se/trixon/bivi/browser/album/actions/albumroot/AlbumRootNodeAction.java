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
package se.trixon.bivi.browser.album.actions.albumroot;

import se.trixon.bivi.browser.album.actions.NodeAction;
import se.trixon.bivi.db.api.AlbumRoot;
import se.trixon.bivi.browser.album.AlbumRootNode;

/**
 *
 * @author Patrik Karlsson
 */
public abstract class AlbumRootNodeAction extends NodeAction {

    public AlbumRoot getAlbumRoot() {
        return getSelectedNode().getAlbumRoot();
    }

    @Override
    public AlbumRootNode getSelectedNode() {
        return (AlbumRootNode) super.getSelectedNode();
    }
}
