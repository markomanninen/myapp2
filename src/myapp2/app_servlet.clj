(ns myapp2.app_servlet
  (:gen-class :extends javax.servlet.http.HttpServlet)
  (:use myapp2.core)
  (:use [appengine-magic.servlet :only [make-servlet-service-method]]))


(defn -service [this request response]
  ((make-servlet-service-method myapp2-app) this request response))
