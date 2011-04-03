(ns cljgae.core
  (:gen-class :extends javax.servlet.http.HttpServlet) ; for appengine
  (:use
	  [hiccup.core]
	  [compojure.core]
	  [compojure.handler]
	  ;[ring.adapter.jetty] ; for repl wont work together with appengine classes!
      [ring.util.servlet :only [defservice]] ; for appengine
	  ;[ring.util.response :only [redirect]]
	  ;[ring.middleware.stacktrace]
	  [ring.middleware.keyword-params]
	  [ring.middleware.params]
	  [ring.middleware.nested-params]
	  ;[swank.swank] ; for swank server, interactively modify dev app engine from repl purpose! TODO
	  ;[ring.middleware.cookies]
  	  ;[ring.middleware.session]
	  ;[ring.middleware.flash]
	  [ring.handler.dump])
  (:require
	  [compojure.route :as route]
	  ; simple actions and views can be called from routes directly
	  ; but when complex logic like database operations are required, then use controllers
	  [cljgae.session :as ses]
	  [cljgae.middleware :as mdw]
	  [cljgae.database :as db]
	  [cljgae.controllers :as c]
	  [cljgae.actions :as a]
	  [cljgae.views :as v]))

; using ring provided session bind
(defn view-tasks2 [session]
  (str "tasks: " (:tasks session)))

(defn add-task2 [task]
  {:body (str "Adding: " task " " (html [:a {:href "/tasks2"} "tasks"]))
   :session {:tasks task}})
   ;:session {:tasks (merge (get get-session :tasks []) task)}})

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


  ; form-admin id/name, title, order, action, target, method, ...
  (GET "/forms" 			request (c/forms-admin request "/forms"))
  (GET "/forms/:id" 		request (c/form-admin request (str "/forms/" (:id (:params request))) (:id (:params request))))

  ; forms add and edit
  (POST "/forms" 			request (a/form-add request "/forms"))
  (POST "/forms/:id" 		request (a/form-edit request (str "/forms/" (:id (:params request)))))

  ; form-divisions-admin
  (GET "/pages" 			request (c/form-divisions-admin request "pages" "/division_add"))
  (GET "/pages/:id" 		request (c/form-division-admin request "pages" "/division_edit" (:id (:params request))))

  (GET "/sections" 			request (c/form-divisions-admin request "sections" "/division_add"))
  (GET "/sections/:id" 		request (c/form-division-admin request "sections" "/division_edit" (:id (:params request))))

  (GET "/groups" 			request (c/form-divisions-admin request "groups" "/division_add"))
  (GET "/groups/:id" 		request (c/form-division-admin request "groups" "/division_edit" (:id (:params request))))

  (GET "/fieldsets" 		request (c/form-divisions-admin request "fieldsets" "/division_add"))
  (GET "/fieldsets/:id" 	request (c/form-division-admin request "fieldsets" "/division_edit" (:id (:params request))))

  ; form-questions-admin id/name, type + all same fields than type has for overloading
  (GET "/questions" 		request (c/form-questions-admin request "/question_add"))
  (GET "/questions/:id" 	request (c/form-question-admin request "/question_edit" (:id (:params request))))

  ; form-question-types-admin title, tip, validation, sanitazion, required, default values
  (GET "/qtypes"			request (c/form-question-types-admin request "/qtype_add"))
  (GET "/qtypes/:id" 		request (c/form-question-type-admin request "/qtype_edit" (:id (:params request))))

  ; form-question-answers-admin TODESIGN
  (GET "/qanswers/:questionid"	request (c/form-question-answers-admin request "/qanswer_add" (:questionid (:params request))))
  (GET "/qanswer/:id" 			request (c/form-question-answer-admin request "/qanswer_edit" (:id (:params request))))

  ; form divisions add and edit
  (POST "/division_add" 		request (a/form-division-add request))
  (POST "/division_edit" 		request (a/form-division-edit request))

  (GET "/:type/delete/:id"		request (a/form-delete request))

  ; form questions add and edit
  (POST "/question_add" 		request (a/form-question-add request "questions"))
  (POST "/question_edit" 		request (a/form-question-edit request (str "/questions/" (:id (:params request)))))

  ; form question types add and edit
  (POST "/qtype_add" 			request (a/form-question-type-add request "qtypes"))
  (POST "/qtype_edit" 			request (a/form-question-type-edit request (str "/qtypes/" (:id (:params request)))))

  ; form question answers add and edit TODESIGN
  (POST "/qanswer_add" 			request (a/form-question-answer-add request (str "/qanswers/" (:questionid (:params request)))))
  (POST "/qanswer_edit" 		request (a/form-question-answer-edit request (str "/qanswer/" (:id (:params request)))))

  (route/not-found "Page not found"))

;(decorate app index
(def app (->  index
			  wrap-keyword-params
              wrap-nested-params
              wrap-params
			  mdw/wrap-request-logging
			  ses/wrap-session-bind
			  ;wrap-stacktrace
			))

(defservice app)