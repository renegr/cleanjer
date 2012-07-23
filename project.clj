(defproject cleanjer "0.1.0-SNAPSHOT"
            :description "Cleanjer - A CSV data cleansing application"
            :dependencies [[org.clojure/clojure "1.4.0"]
			   [org.clojure/data.csv "0.1.2"]
                           [noir-async "1.1.0-beta2"]
			   [noir "1.2.2"]
			   [hiccup "1.0.0"]
			   [cheshire "2.0.4"]]
            :main cleanjer.server)

