(ns myapp3.core
  (:gen-class :extends javax.servlet.http.HttpServlet) ; for appengine
  (:use compojure.core
	  ;ring.adapter.jetty
      ring.util.servlet ; for appengine
  )
  (:require [compojure.route :as route]))

(defroutes example
  (GET "/" [] "<h1>!!! Hello World Wide Web !!!</h1>")
  (route/not-found "Page not found"))

;(run-jetty example {:port 8080})
(defservice example) ; for appengine