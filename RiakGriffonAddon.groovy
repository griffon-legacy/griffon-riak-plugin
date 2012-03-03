/*
 * Copyright 2010-2012 the original author or authors.
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

import griffon.core.GriffonClass
import griffon.core.GriffonApplication
import griffon.plugins.riak.RiakConnector
import griffon.plugins.riak.RiakEnhancer

/**
 * @author Andres Almiray
 */
class RiakGriffonAddon {
    void addonInit(GriffonApplication app) {
        ConfigObject config = RiakConnector.instance.createConfig(app)
        RiakConnector.instance.connect(app, config)
    }

    void addonPostInit(GriffonApplication app) {
        def types = app.config.griffon?.riak?.injectInto ?: ['controller']
        for(String type : types) {
            for(GriffonClass gc : app.artifactManager.getClassesOfType(type)) {
                RiakEnhancer.enhance(gc.metaClass)
            }
        }
    }

    def events = [
        ShutdownStart: { app ->
            ConfigObject config = RiakConnector.instance.createConfig(app)
            RiakConnector.instance.disconnect(app, config)
        }
    ]
}
