# lein-postgres

A lein plugin that starts up an "embedded" postgres instance to be used when running tests / during development.

In effect it works as a wrapper around [OpenTable's embedded postgres JUnit helper](https://github.com/opentable/otj-pg-embedded).

## Usage

Add `[lein-postgres "0.1.1"]` to the plugins vector in your project.clj file.

A postgres instance can then be started with `lein postgres`

This will start a postgres instance and wait until you send it kill command with `ctrl+c`

You can also start a postgres instance and run other lein tasks by passing them as a second argument.

`lein postgres test`

This will start postgres, run your tests (which presumably depend on postgres) before closing postgres and cleaning up.

## Config

You can configure lein-postgres with a config map in your project.clj

```clojure
(defproject  "1.0.0-SNAPSHOT"
  :plugins [[lein-postgres "0.1.1"]]
  :postgres {:port 12345 ;optional, defaults to a random free port
             :clean-data-directory true ;optional, defaults to true - should we cleanup the data directory on close
             :data-directory "/tmp/embeddedpostgres" ;optional, sets the temporary data directory
             :server-config {:max_connections "300"}} ;optional, allows you to set additional server config options
```

See [here](https://github.com/opentable/otj-pg-embedded/blob/master/src/main/java/com/opentable/db/postgres/embedded/EmbeddedPostgreSQL.java) for more information on additional server-config options.

## Thanks

Thanks to OpenTable for the excellent embedded postgres plugin and Joe Littlejohn for [lein-embongo](https://github.com/joelittlejohn/lein-embongo) which was used as a base for some of the code here.

## License

Copyright © 2015 Ben Griffiths

Distributed under the Eclipse Public License - just like Clojure.
