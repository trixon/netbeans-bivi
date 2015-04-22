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
@OptionsPanelController.ContainerRegistration(id = "Album",
        position = 0,
        categoryName = "#OptionsCategory_Name_Album", 
        iconBase = "se/trixon/bivi/db/options/server-database.png", 
        keywords = "#OptionsCategory_Keywords_Album", 
        keywordsCategory = "Album")
package se.trixon.bivi.db.options;

import org.netbeans.spi.options.OptionsPanelController;
