(ns cljgae.core
  (:gen-class :extends javax.servlet.http.HttpServlet) ; for appengine
  (:use
	  [hiccup.core]
	  [compojure.core]
	  [compojure.handler]
	  ;[ring.adapter.jetty] ; for repl wont work together with appengine!
      [ring.util.servlet :only [defservice]] ; for appengine
	  ;[ring.util.response :only [redirect]]
	  ;[ring.middleware.stacktrace]
	  [ring.middleware.keyword-params]
	  [ring.middleware.params]
	  [ring.middleware.nested-params]
	  [ring.handler.dump])
	  ;[swank.swank] ; for swank server
  (:require
	  [compojure.route :as route]
	  [cljgae.session :as ses]
	  [cljgae.middleware :as mdw]))

; using ring provided session bind
(defn view-tasks2 [session]
  (str "tasks: " (:tasks session)))

(defn add-task2 [task]
  {:body (str "Adding: " task " " (html [:a {:href "/tasks2"} "tasks"]))
   :session {:tasks task}})

; using own user session store bind
(defn view-tasks [request]
  (str "tasks: " (ses/session-get request :tasks)))

(defn add-task [request task]
  (ses/session-set! request :tasks (merge (get (ses/session-bind request) :tasks []) task)))

(defn counter [request]
	(ses/session-set! request :count 
		(inc (get (ses/session-bind (ses/sid-get request)) :count 0))))

(defroutes index
  
  (ANY "*"					request (counter request) :next)

  ;(GET "/swank" 			request (str (start-repl 4006)))

  (GET "/session" 		    request (handle-dump request))

  (GET "/session/:key" 	    request (str (ses/session-get request (:key (:params request)))))

  (GET "/session/:key/:val" request (str (ses/session-set! request (:key (:params request)) (:val (:params request)))))

  (GET "/tasks" 			request (view-tasks request))

  (GET "/atask/:task"		request (add-task request (:task (:params request))))

  (GET "/tasks2" 			{session :session} (view-tasks2 session))

  (GET "/atask2/:task"		[task] (add-task2 task))

  (GET "/hello" 			[] "<h1>Hello dynamic world!</h1>")

  (route/not-found "Page not found"))

;(decorate app index
(def app (->  index
			  wrap-keyword-params
              wrap-nested-params
              wrap-params
			  mdw/wrap-request-logging
			  ses/wrap-session-bind))

(defservice app)