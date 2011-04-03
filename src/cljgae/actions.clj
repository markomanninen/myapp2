(ns cljgae.actions
  (:use
	[cljgae.session :only [flash]]
	[ring.util.response :only [redirect]])
  (:require
	[cljgae.database :as db]))

; general delete
(defn form-delete [request]
	(let [params (:params request) id (:id params) title (:title params) type (:type params)]
		(condp = type
		  "forms"		(db/delete-form id)
		  "pages" 		(db/delete-page id)
		  "sections" 	(db/delete-section id)
		  "groups" 		(db/delete-group id)
		  "fieldsets" 	(db/delete-fieldset id)
		  "questions" 	(db/delete-question id)
  	  	  (throw (IllegalArgumentException. (str "unsupported type " type))))
		(flash request (str type " deleted"))
		(redirect (str "/" type))))

; forms
(defn form-add [request action]
	(db/add-form (:title (:params request))
				 (:order (:params request))
				 (:method (:params request))
				 (:target (:params request))
				 (:success (:params request))
				 (:fail (:params request))
				 (:cancel (:params request))
				 (:email (:params request)))
	(flash request "Form added")
	(redirect action))

(defn form-edit [request action]
	(db/edit-form (:id (:params request))
				  (:title (:params request))
				  (:order (:params request))
				  (:method (:params request))
				  (:target (:params request))
				  (:success (:params request))
				  (:fail (:params request))
				  (:cancel (:params request))
				  (:email (:params request)))
	(flash request "Form edited")
	(redirect action))

; divisions
(defn form-division-add [request]
	(let [params (:params request) type (:type params) title (:title params) order (:order params)]
		(condp = type
		  "pages" 		(db/add-page title order (params :formid))
		  "sections" 	(db/add-section title order (params :formid) (params :pageid))
		  "groups" 		(db/add-group title order (params :formid) (params :pageid) (params :sectionid))
		  "fieldsets" 	(db/add-fieldset title order (params :formid) (params :pageid) (params :sectionid))
  	  	  (throw (IllegalArgumentException. (str "unsupported type " type))))
		(flash request (str "Division [" type "] added"))
		(redirect (str "/" type))))

(defn form-division-edit [request]
	(let [params (:params request) type (:type params) id (:id params) title (:title params) order (:order params)]
		(condp = type
		  "pages" 		(db/edit-page id title order (params :formid))
		  "sections" 	(db/edit-section id title order (params :formid) (params :pageid))
		  "groups" 		(db/edit-group id title order (params :formid) (params :pageid) (params :sectionid))
		  "fieldsets" 	(db/edit-fieldset id title order (params :formid) (params :pageid) (params :sectionid))
		  (throw (IllegalArgumentException. (str "unsupported type " type))))
		(flash request (str "Division [" type "] edited"))
		(redirect (str "/" type "/" id))))

; questions TODO
(defn form-question-add [request action]
	(:title (:params request)))

(defn form-question-edit [request action]
	(:title (:params request)))

(defn form-question-type-add [request action]
	(:title (:params request)))

(defn form-question-type-edit [request action]
	(:title (:params request)))

(defn form-question-answer-add [request action]
	(:title (:params request)))

(defn form-question-answer-edit [request action]
	(:title (:params request)))