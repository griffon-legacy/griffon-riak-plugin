/*
 * Copyright 2012 the original author or authors.
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

package griffon.plugins.riak

import griffon.util.CallableWithArgs
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * @author Andres Almiray
 */
final class RiakEnhancer {
    private static final Logger LOG = LoggerFactory.getLogger(RiakEnhancer)

    private RiakEnhancer() {}
    
    static void enhance(MetaClass mc, RiakProvider provider = RiakClientHolder.instance) {
        if(LOG.debugEnabled) LOG.debug("Enhancing $mc with $provider")
        mc.withRiak = {Closure closure ->
            provider.withRiak('default', closure)
        }
        mc.withRiak << {String clientName, Closure closure ->
            provider.withRiak(clientName, closure)
        }
        mc.withRiak << {CallableWithArgs callable ->
            provider.withRiak('default', callable)
        }
        mc.withRiak << {String clientName, CallableWithArgs callable ->
            provider.withRiak(clientName, callable)
        }
    }
}
