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
import org.openide.nodes.BeanNode;

/**
 *
 * @author Patrik Karlsson
 */
public class AlbumRootNode extends BeanNode {

    public AlbumRootNode(AlbumRoot bean) throws IntrospectionException {
        super(bean);
        setDisplayName(bean.getLabel());
        setShortDescription(bean.getSpecificPath());
        
        //setIconBaseWithExtension("org/fully/qualified/name/myicon.png");
    }

}
