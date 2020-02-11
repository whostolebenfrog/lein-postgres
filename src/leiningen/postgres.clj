(ns leiningen.postgres
  (:require [leiningen.core.main :as main])
  (:import [com.opentable.db.postgres.embedded EmbeddedPostgres]
           [java.io File]
           (java.lang ProcessBuilder$Redirect)))

(defn- config-value
  "Get a value from project config or, optionally, use a default value."
  [project k & [default]]
  (get (project :postgres) k default))

(defn apply-server-config
  "Set additional config onto the server builder"
  [builder config]
  (doseq [[k v] config]
    (.setServerConfig builder (name k) v))
  builder)

(defn start-db
  "Start the database and configure available options"
  [project]
  (let [clean-data-directory? (get (project :postgres) :clean-data-directory)
        data-directory (get (project :postgres) :data-directory)
        server-config (get (project :postgres) :server-config)
        port (get (project :postgres) :port)
        databases (get (project :postgres) :databases)
        db (.start (cond-> (EmbeddedPostgres/builder)

                     port
                     (.setPort port)

                     clean-data-directory?
                     (.setCleanDataDirectory clean-data-directory?)

                     data-directory
                     (.setDataDirectory (File. ^String data-directory))

                     server-config
                     (apply-server-config server-config)

                     :always (doto
                               (.setErrorRedirector ProcessBuilder$Redirect/INHERIT)
                               (.setOutputRedirector ProcessBuilder$Redirect/INHERIT))))]
    (let [conn (.getConnection (.getPostgresDatabase db))
          statement (.createStatement conn)]
      (doseq [database databases]
        (let [create-str (str "CREATE DATABASE " database)]
          (println create-str)
          (.execute statement create-str))))
    db))

(defn postgres
  "Run postgres in memory"
  [project & args]
  (let []
    (println (str "lein-postgres: starting postgres instance"))
    (let [db (start-db project)
          port (.getPort db)]
      (println (str "lein-postgres: started on port: " port))
      (if (seq args)
        (try
          (main/apply-task (first args) project (rest args))
          (finally (println "lein-postgres: Closing embedded postgres") (.close db) "lein-postgres: closed"))
        (while true (Thread/sleep 5000))))))
