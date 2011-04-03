(ns appengine-magic.app_servlet
  (:gen-class :extends javax.servlet.http.HttpServlet)
  (:use appengine-magic.core)
  (:use [appengine-magic.servlet :only [make-servlet-service-method]]))


(defn -service [this request response]
  ((make-servlet-service-method appengine-magic-app) this request response))
