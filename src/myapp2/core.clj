(ns myapp2.core
  (:require [appengine-magic.core :as ae]))


(defn myapp2-app-handler [request]
  {:status 200
   :headers {"Content-Type" "text/plain"}
   :body "Hello, world!"})


(ae/def-appengine-app myapp2-app #'myapp2-app-handler)