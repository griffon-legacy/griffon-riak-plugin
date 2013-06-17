
Riak support
------------

Plugin page: [http://artifacts.griffon-framework.org/plugin/riak](http://artifacts.griffon-framework.org/plugin/riak)


The Riak plugin enables lightweight access to [Riak][1] datastores.
This plugin does NOT provide domain classes nor dynamic finders like GORM does.

Usage
-----
Upon installation the plugin will generate the following artifacts in `$appdir/griffon-app/conf`:

 * RiakConfig.groovy - contains the database definitions.
 * BootstrapRiak.groovy - defines init/destroy hooks for data to be manipulated during app startup/shutdown.

A new dynamic method named `withRiak` will be injected into all controllers,
giving you access to a `com.basho.riak.client.RiakClient` object, with which you'll be able
to make calls to the database. Remember to make all database calls off the EDT
otherwise your application may appear unresponsive when doing long computations
inside the EDT.

This method is aware of multiple databases. If no clientName is specified when calling
it then the default database will be selected. Here are two example usages, the first
queries against the default database while the second queries a database whose name has
been configured as 'internal'

    package sample
    class SampleController {
        def queryAllDatabases = {
            withRiak { clientName, client -> ... }
            withRiak('internal') { clientName, client -> ... }
        }
    }

This method is also accessible to any component through the singleton `griffon.plugins.riak.RiakConnector`.
You can inject these methods to non-artifacts via metaclasses. Simply grab hold of a particular metaclass and call
`RiakEnhancer.enhance(metaClassInstance, riakProviderInstance)`.

Configuration
-------------
### Dynamic method injection

The `withRiak()` dynamic method will be added to controllers by default. You can
change this setting by adding a configuration flag in `griffon-app/conf/Config.groovy`

    griffon.riak.injectInto = ['controller', 'service']

### Events

The following events will be triggered by this addon

 * RiakConnectStart[config, clientName] - triggered before connecting to the database
 * RiakConnectEnd[clientName, client] - triggered after connecting to the database
 * RiakDisconnectStart[config, clientName, client] - triggered before disconnecting from the database
 * RiakDisconnectEnd[config, clientName] - triggered after disconnecting from the database

### Multiple Stores

The config file `RiakConfig.groovy` defines a default client block. As the name
implies this is the client used by default, however you can configure named clients
by adding a new config block. For example connecting to a client whose name is 'internal'
can be done in this way

    databases {
        internal {
            url = 'http://localhost:8098/internal'
        }
    }

This block can be used inside the `environments()` block in the same way as the
default client block is used.

### Example

A trivial sample application can be found at [https://github.com/aalmiray/griffon_sample_apps/tree/master/persistence/riak][2]

Testing
-------
The `withRiak()` dynamic method will not be automatically injected during unit testing, because addons are simply not initialized
for this kind of tests. However you can use `RiakEnhancer.enhance(metaClassInstance, riakProviderInstance)` where 
`riakProviderInstance` is of type `griffon.plugins.riak.RiakProvider`. The contract for this interface looks like this

    public interface RiakProvider {
        Object withRiak(Closure closure);
        Object withRiak(String clientName, Closure closure);
        <T> T withRiak(CallableWithArgs<T> callable);
        <T> T withRiak(String clientName, CallableWithArgs<T> callable);
    }

It's up to you define how these methods need to be implemented for your tests. For example, here's an implementation that never
fails regardless of the arguments it receives

    class MyRiakProvider implements RiakProvider {
        Object withRiak(String clientName = 'default', Closure closure) { null }
        public <T> T withRiak(String clientName = 'default', CallableWithArgs<T> callable) { null }
    }

This implementation may be used in the following way

    class MyServiceTests extends GriffonUnitTestCase {
        void testSmokeAndMirrors() {
            MyService service = new MyService()
            RiakEnhancer.enhance(service.metaClass, new MyRiakProvider())
            // exercise service methods
        }
    }


[1]: http://riak.basho.com/
[2]: https://github.com/aalmiray/griffon_sample_apps/tree/master/persistence/riak

