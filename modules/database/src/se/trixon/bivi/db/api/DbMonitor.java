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

import java.util.HashSet;

/**
 *
 * @author Patrik Karlsson
 */
public enum DbMonitor {

    INSTANCE;
    private final HashSet<DbEvent> mDbEvents;

    private DbMonitor() {
        mDbEvents = new HashSet<>();
    }

    public boolean add(DbEvent event) {
        return mDbEvents.add(event);
    }

    public void dbChanged() {
        mDbEvents.stream().forEach((dbEvent) -> {
            try {
                dbEvent.onDbChanged();
            } catch (Exception e) {
                // nvm
            }
        });
    }
    public void dbRootAlbumsChanged() {
        mDbEvents.stream().forEach((dbEvent) -> {
            try {
                dbEvent.onDbRootAlbumsChanged();
            } catch (Exception e) {
                // nvm
            }
        });
    }

    public interface DbEvent {

        void onDbChanged();

        void onDbRootAlbumsChanged();
    }
}
