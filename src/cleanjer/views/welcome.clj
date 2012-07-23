(ns cleanjer.views.welcome
  (:require [cleanjer.views.common :as common])
  (:use [noir.core :only [defpage]]
        [hiccup.core :only [html]]))

(defpage "/" []
  (common/layout
   [:h1 "Cleanjer"]
   [:p (list
        "1. Enter the name of the column to be analyzed (copy from Excel)."
        [:br]
        "2. Drop the file to be analyzed."
        [:br]
        "3. See the result being generated here..."
        [:br])]))
