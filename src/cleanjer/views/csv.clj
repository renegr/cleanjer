(ns cleanjer.views.csv
  (:require [cleanjer.views.common :as common]
	    [cheshire.core :as json]
	    [csv-stat.analyzer :as analyzer]
	    [string-matcher.core :as sm]
	    [cleanjer.views.socket-server :as socket])
	    ;[noir.response :as response])
  (:use [noir.core :only [defpage]]
        [hiccup.core :only [html]]
	[ring.util.codec :only [url-decode]]))

(defn closest-match [fname colname]
  (let [csvdata (analyzer/load-data fname)
	colnum  (analyzer/find-pos colname (analyzer/header csvdata))
	coldata (analyzer/column-data (analyzer/body csvdata) colnum)
        dictionary (analyzer/uniq coldata)
	matcher (sm/new-string-matcher dictionary)]
    (future
     (do
       (doseq [w dictionary]
	 (let [w2 (sm/match-string matcher w)
	       rating (sm/letter-pair-similarity w w2)]
	   (socket/send-result [w w2 rating])))
       (socket/send-complete)))))

(defn parse-int [s]
  (Integer. (re-find #"[0-9]*" s)))

(defpage [:post "/parse"] {:keys [csvfile colname]}
  (closest-match (.getPath (csvfile :tempfile)) (url-decode colname))
  (json/generate-string {:status :success}))