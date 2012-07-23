(ns cleanjer.server
  (:gen-class :main true) ; makes a jar working this way
  (:use aleph.http
        noir.core
        lamina.core)
  (:require [noir.server :as server]))

(server/load-views "src/cleanjer/views/")

(defn -main [& m]
  (let [mode (keyword (or (first m) :dev))
        port (Integer. (get (System/getenv) "PORT" "8843"))
        noir-handler (server/gen-handler {:mode mode})]
    (start-http-server
      (wrap-ring-handler noir-handler)
      {:port port :websocket true})
    (.exec (Runtime/getRuntime) "google-chrome http://127.0.0.1:8843")))