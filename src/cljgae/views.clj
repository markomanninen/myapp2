(ns cljgae.views
  (:use 
		[hiccup.core]
        [hiccup.page-helpers]
        [hiccup.form-helpers]
		[cljgae.database :only [all-forms all-pages all-sections all-groups all-fieldsets division-data]]
		[cljgae.session :only [flash]])
  (:require [clojure.contrib.str-utils2 :as s]))

;;;;;;;;;;;;;;;;;;;;;;;;;;;
; division views:
; - forms (special)
; - pages (parent: form)
; - sections (parent: form, page)
; - groups (parent: form, page, section)
; - fieldsets (parent: form, page, section)
; other views:
; - questions (parent: form, page, section, group, fieldset)
; - question types
; - question answers
;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn with-label [name text & rest]
	(html [:label {:for name :accesskey (s/take text 1)} text] rest [:br]))

(defn with-fieldset [legend & rest] 
	(html [:fieldset [:legend legend] rest]))

; base layout
(defn layout [request title content]
	(let [message (flash request)]
    (html5
      [:head
        [:title title]
        (include-js "/media/script.js")
        (include-css "/media/style.css")]
      [:body
        [:div {:class "global-container"}
		  (if (not= nil message)
			  [:div.flash message])
          content]])))

;
(defn form-post [action & content]
    (html [:form {:action action :method "post"} content]))

;
(defn form-get [action & content]
    (html [:form {:action action :method "get"} content]))

;
(defn division-select [type id divisions selected]
	(with-label id type (drop-down id (map (fn [x] [(get x :title) (get x :id)]) divisions) selected)))

(defn with-empty-option [option-map]
	(merge option-map [{:id 0 :title ""}]))

; if type page list forms, if type section list forms and page, if type group list list forms, pages and sections
(defn division-inputs 
	[type form-option-selected page-option-selected section-option-selected fieldset-option-selected group-option-selected]
	(let [Type (s/capitalize type)]
	(condp = type
	  "forms" 		(str "")
	  "pages" 		(html 
						(division-select "Forms" :formid (all-forms) form-option-selected))
	  "sections" 	(html 
						(division-select "Forms" :formid (with-empty-option (all-forms)) form-option-selected)
						(division-select "Pages" :pageid (with-empty-option (all-pages)) page-option-selected))
	  "groups" 		(html 
						(division-select "Forms" :formid (with-empty-option (all-forms)) form-option-selected)
						(division-select "Pages" :pageid (with-empty-option (all-pages)) page-option-selected)
						(division-select "Sections" :sectionid (with-empty-option (all-sections)) section-option-selected))
	  "fieldsets" 	(html 
						(division-select "Forms" :formid (with-empty-option (all-forms)) form-option-selected)
						(division-select "Pages" :pageid (with-empty-option (all-pages)) page-option-selected)
						(division-select "Sections" :sectionid (with-empty-option (all-sections)) section-option-selected))
	  "questions" 	(html 
						(division-select "Forms" :formid (with-empty-option (all-forms)) form-option-selected)
						(division-select "Pages" :pageid (with-empty-option (all-pages)) page-option-selected)
						(division-select "Sections" :sectionid (with-empty-option (all-sections)) section-option-selected)
						(division-select "Groups" :groupid (with-empty-option (all-groups)) group-option-selected)
						(division-select "Fieldsets" :fieldsetid (with-empty-option (all-fieldsets)) fieldset-option-selected))
	  (throw (IllegalArgumentException.
	  	(str "unsupported type " type))))))

;
(defn division-list [type]
	[:ul.items 
		(map 
			(fn [x] [:li [:a {:href (str "/" type "/" (get x :id))} (get x :title)] " [" [:a  {:href (str "/" type "/delete/" (get x :id))} "delete"] "]"])
		  	(division-data type))])

(defn division-extra [type params]
	(condp = type
		"forms" (do (html [:div
						(with-label :method "Method" (text-field :method (:method params)))
						(with-label :target "Target" (text-field :target (:target params)))
						(with-fieldset "Optional"
							(with-label :success "Success url" (text-field :success (:success params)))
							(with-label :fail "Fail ulr" (text-field :fail (:fail params)))
							(with-label :cancel "Cancel url" (text-field :cancel (:cancel params)))
							(with-label :email "Email" (text-field :email (:email params))))]))
		"questions" (str "")
		(str "")))

(defn fieldset-legend [type id]
	(str (s/butlast (s/capitalize type) 1) 
		 (if (not= "" id)
		 	 (str " [id: " id "]"))))

(defn division [type action params]
    (html
      [:h2 type]
      [:div {:class "add-division"}
        (form-post action
		  (with-fieldset (fieldset-legend type (:id params))
              (hidden-field :type type)
		      (hidden-field :id (:id params))
			  (with-label :title "Name" (text-field :title (:title params)))
			  (division-inputs type (:formid params) (:pageid params) (:sectionid params) (:fieldsetid params) (:groupid params))
			  (with-label :order "Order" (text-field {:size 4} :order (:order params)))
		  	  (division-extra type params)
	          (submit-button "submit")))]
      [:hr]
      (division-list type)))

;
(defn division-menu []
	[:ul.menu (map (fn [x] [:li [:a {:href (str "/" (get x :id))} (get x :title)]])
		[{:id "forms" :title "forms"}
		 {:id "pages" :title "pages"}
		 {:id "sections" :title "sections"}
		 {:id "groups" :title "groups"}
		 {:id "fieldsets" :title "fieldsets"}
		 {:id "questions" :title "questions"}])])

; views
;
(defn forms-admin [request action form-params]
    (layout request "forms" 
      (html
        [:div {:class "page-container"}
		  (division-menu)
          (division "forms" action form-params)])))

;
(defn form-divisions-admin [request type action form-params]
    (layout request type 
      (html
        [:div {:class "page-container"}
		  (division-menu)
          (division type action form-params)])))

;
(defn form-questions-admin [request action form-params]
    (layout request "questions" 
      (html
        [:div {:class "page-container"}
		  (division-menu)
          (division "questions" action form-params)])))

;
(defn form-question-types-admin [request action form-params]
    (layout request "question-types" 
      (html
        [:div {:class "page-container"}
		  (division-menu)
          (division "questions-types" action form-params)])))

;
(defn form-question-answers-admin [request action form-params]
    (layout request "question-answers" 
      (html
        [:div {:class "page-container"}
		  (division-menu)
          (division "question-answers" action form-params)])))