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
import javax.swing.Action;
import org.openide.nodes.BeanNode;
import org.openide.nodes.Children;
import org.openide.util.Utilities;
import org.openide.util.lookup.Lookups;
import se.trixon.bivi.db.api.Album;

/**
 *
 * @author Patrik Karlsson
 */
public class AlbumNode extends BeanNode {

    public AlbumNode(Album bean) throws IntrospectionException {
        super(bean, Children.create(new AlbumChildFactory(AlbumChildFactory.PARENT_IS_ALBUM, bean), true),Lookups.singleton(bean));
        setDisplayName(bean.getName());
        setShortDescription(bean.getCaption());

        setIconBaseWithExtension("se/trixon/bivi/core/res/folder16.png");
    }

    @Override
    public Action[] getActions(boolean context) {
        List<? extends Action> actions = Utilities.actionsForPath("Bivi/NodeMenu/Album");
        return actions.toArray(new Action[actions.size()]);
    }

    public Album getAlbum() {
        return (Album) getBean();
    }

    @Override
    public Action getPreferredAction() {
        return null;
    }
}
