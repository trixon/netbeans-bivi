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
import se.trixon.bivi.db.api.AlbumRoot;

/**
 *
 * @author Patrik Karlsson
 */
public class AlbumRootNode extends BeanNode {

    public AlbumRootNode(AlbumRoot bean) throws IntrospectionException {
        super(bean, Children.create(new AlbumChildFactory(AlbumChildFactory.PARENT_IS_ROOT, bean), true));
        setDisplayName(bean.getLabel());
        setShortDescription(bean.getSpecificPath());

        setIconBaseWithExtension("se/trixon/bivi/core/res/folder-image16.png");
    }

    @Override
    public Action[] getActions(boolean context) {
        List<? extends Action> actions = Utilities.actionsForPath("Actions/Nodes/AlbumRoot");
        return actions.toArray(new Action[actions.size()]);
    }

    public AlbumRoot getAlbumRoot() {
        return (AlbumRoot) getBean();
    }

    @Override
    public Action getPreferredAction() {
        return null;
    }
}
