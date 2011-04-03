(ns cljgae.controllers
	;(:use)
	(:require
	  [cljgae.views :as v]
	  [cljgae.database :as db]))

; forms
(defn form-admin [request action id]
	(let [m (db/form id)]
		(v/forms-admin request action
			{:title (get m :title "")
			 :order (get m :order "")
			 :id id
			 :method (get m :method "")
			 :target (get m :target "")
			 :success (get m :success "")
			 :fail (get m :fail "")
			 :cancel (get m :cancel "")
			 :email (get m :email "")})))

(defn forms-admin [request action]
	(v/forms-admin request action {:id "" :title "" :order (inc (count (db/all-forms)))}))

; divisions
(defn form-division-admin [request type action id]
	(let [m (db/form-division type id)]
		(v/form-divisions-admin request type action 
			{:title (get m :title)
			 :order (get m :order)
			 :id (get m :id)
			 :formid (get m :formid)
			 :pageid (get m :pageid)
			 :sectionid (get m :sectionid)})))
		
(defn form-divisions-admin [request type action]
	(v/form-divisions-admin request type action 
		{:id ""
		 :title ""
		 :order (inc (count (db/division-data type)))}))

; questions
(defn form-question-admin [request action id]
	(let [m (db/question id)]
		(v/form-questions-admin request action {:title (get m :title) :order (get m :order) :id (get m :id)})))

(defn form-questions-admin [request action]
	(v/form-questions-admin request action {:id "" :title "" :order (inc (count (db/all-questions)))}))

; question types
(defn form-question-type-admin [request action id]
	(let [m (db/question-type id)]
		(v/form-question-types-admin request action {:title (get m :title) :order (get m :order) :id (get m :id)})))

(defn form-question-types-admin [request action]
	(v/forms-admin request request action {:id "" :title "" :order (inc (count (db/all-question-types)))}))

; question answers
(defn form-question-answer-admin [request action id]
	(let [m (db/question-answer id)]
		(v/form-question-answers-admin request action {:title (get m :title) :order (get m :order) :id (get m :id)})))

(defn form-question-answers-admin [request action]
	(v/forms-admin request action {:id "" :title "" :order (inc (count (db/all-question-answers)))}))