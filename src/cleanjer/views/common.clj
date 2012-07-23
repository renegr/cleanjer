(ns cleanjer.views.common
  (:use [noir.core :only [defpartial]]
        [hiccup.page-helpers :only [include-css include-js html5]]))

(defpartial layout [& content]
            (html5
              [:head
               [:title "Cleanjer"]
               (include-css "/css/cleanjer.css")
	       (include-css "/css/jquery.notice.css")]
	      [:body
	       (list
		[:div.control
		 (list
		  [:div.configure
		   (list
		    "Spalten Name:"
		    [:br]
		    [:input {:name "colname" :type "text" :size 10 :value "1"}])]
		  [:div.dropzone "Drop a file here"])]

                [:div.container content] 
                (include-js "http://ajax.googleapis.com/ajax/libs/jquery/1.7.1/jquery.min.js")
		(include-js "/js/jquery.filedrop.js")
		(include-js "/js/jquery.json-2.3.min.js")
		(include-js "/js/jquery.websocket-0.0.1.js")
		(include-js "/js/jquery.notice.js")
		(include-js "/js/app.js"))]))
		
