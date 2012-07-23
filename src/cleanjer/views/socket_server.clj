(ns cleanjer.views.socket-server
  (:use noir-async.core)
  (:require [cheshire.core :as json]))

(def connections (atom #{}))

(defn attach-server [conn]
  (swap! connections conj conn))

(defn send-helo []
  ; notify all clients with "helo"
  (doseq [conn @connections]
    (async-push conn
		(json/generate-string {:type "helo" :data "Connection established"}))))

(defn send-result [res]
  ; notify all clients with a result (=vector)
  (doseq [conn @connections]
    (async-push conn
		(json/generate-string {:type "append_result" :data res}))))

(defn send-complete []
  (doseq [conn @connections]
    (async-push conn
		(json/generate-string {:type "complete"}))))
  
  

(defpage-async "/socket-server" {} conn
  (attach-server conn)
  (send-helo))
  ;(async-push conn "hallo welt"))
  ;(compare-and-set! connection @connection conn))