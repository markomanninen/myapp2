(ns cljgae.servlet
  (:gen-class :extends javax.servlet.http.HttpServlet) ; for appengine
  (:use compojure.core
	ring.util.servlet)
  (:require [appengine-magic.core :as ae]))

(defroutes cljgae-app-handler
  (GET "/" req
       {:status 200
        :headers {"Content-Type" "text/plain"}
        :body "Hello, world!"})
  (GET "/hello/:name" [name]
       {:status 200
        :headers {"Content-Type" "text/plain"}
        :body (format "Hello, %s!" name)})
  (ANY "*" _
       {:status 200
        :headers {"Content-Type" "text/plain"}
        :body "not found"}))

(ae/def-appengine-app cljgae-app #'cljgae-app-handler)