(ns cljgae.database
    (:use
	  [clojure.contrib.datalog.database :only (make-database add-tuples remove-tuple add-tuple select)]))

;
(def db-schema
    (make-database
      (relation :forms [:id :title :order :method :target :success :fail :cancel :email])
      (index :forms :id)

      (relation :pages [:id :title :order :formid])
      (index :pages :id)

      (relation :sections [:id :title :order :formid :pageid :repeat])
      (index :sections :id)

      (relation :groups [:id :title :order :formid :pageid :sectionid :repeat])
      (index :groups :id)

      (relation :fieldsets [:id :title :order :formid :pageid :sectionid :repeat])
      (index :fieldsets :id)

	  (relation :questions [:id :title :order :formid :pageid :sectionid :groupid :fieldsetid :type :repeat])
      (index :questions :id)

	  (relation :question-types [:id :title :controller])
      (index :question-types :id)

	  (relation :answer-choices [:id :questionid :value])
      (index :answer-choices :id)

	  (relation :question-answers [:id :questionid :value :choiceid])
  	  (index :question-answers :id)))

;
(def db2
     (add-tuples db-schema
           [:forms :id 1     :title "Form 1" 	:order 1 :method nil :target nil :success nil :fail nil :cancel nil :email nil]

           [:pages :id 1     :title "Page 1"    :order 1 :formid 1]
           [:pages :id 2     :title "Page 2"    :order 2 :formid 1]

           [:sections :id 1  :title "Section 1" :pageid 1 :order 1 :formid nil :repeat 0]
           [:sections :id 2  :title "Section 2" :pageid 1 :order 2 :formid nil :repeat 0]
           [:sections :id 3  :title "Section 1" :pageid 2 :order 2 :formid nil :repeat 0]
           [:sections :id 4  :title "Section 2" :formid 1 :order 2 :pageid nil :repeat 0]

           [:groups :id 1    :title "Group 1"   :sectionid 1 :order 1 :repeat 0 :formid nil :pageid nil]
           [:groups :id 2    :title "Group 2"   :sectionid 1 :order 2 :repeat 0 :formid nil :pageid nil]
           [:groups :id 3    :title "Group 1"   :pageid 2 :order 1 :repeat 0 :sectionid nil :formid nil]
           [:groups :id 4    :title "Group 1"   :formid 1 :order 1 :repeat 0 :sectionid nil :pageid nil]
		   
		   [:fieldsets :id 1 :title "Fieldset 1"   :order 1 :formid nil :pageid nil :sectionid 1 :repeat 0]

           [:questions :id 1 :title "Question 1"    :groupid 1 :order 1 :type 1 :repeat 0 :fieldsetid nil :formid nil :pageid nil :sectionid nil]
           [:questions :id 2 :title "Question 2"    :groupid 1 :order 2 :type 1 :repeat 0 :fieldsetid nil :formid nil :pageid nil :sectionid nil]
           [:questions :id 3 :title "Question 3"    :groupid 2 :order 1 :type 1 :repeat 0 :fieldsetid nil :formid nil :pageid nil :sectionid nil]
           [:questions :id 4 :title "Question 4"    :groupid 3 :order 1 :type 1 :repeat 0 :fieldsetid nil :formid nil :pageid nil :sectionid nil]
           [:questions :id 5 :title "Question 5"    :groupid 4 :order 1 :type 1 :repeat 0 :fieldsetid nil :formid nil :pageid nil :sectionid nil]
           [:questions :id 6 :title "Question 6"    :sectionid 1 :order 1 :type 1 :repeat 0 :fieldsetid nil :formid nil :groupid nil :pageid nil]
           [:questions :id 7 :title "Question 7"    :pageid 1 :order 1 :type 1 :repeat 0 :fieldsetid nil :formid nil :groupid nil :sectionid nil]
           [:questions :id 8 :title "Question 8"    :formid 1 :order 1 :type 1 :repeat 0 :fieldsetid nil :groupid nil :pageid nil :sectionid nil]

		   [:question-types :id 1 :title "Textfield" :controller '(html [:input {:type "text"}])]))

(defn filter- [map key val]
  (filter #(= (key %) val) map))

(defn filter-- [map key val]
  (filter #(not= (key %) val) map))

; forms
(defn all-forms []
  (select db2 :forms {}))

(defn form [id]
  (nth (filter- (all-forms) :id (Integer/valueOf id)) 0 {}))

(defn add-form 
	([title order method target success fail cancel email]
		(add-form title order method target success fail cancel email (inc (count (all-forms)))))
	([title order method target success fail cancel email id]
	(def db2 
		(add-tuple db2 :forms 
			{:id (Integer/valueOf id)
			 :title title 
			 :order (Integer/valueOf order)
			 :method method 
			 :target target 
			 :success success 
			 :fail fail 
			 :cancel cancel 
			 :email email}))))

; can delete only if there is no answers, else put :status :deleted
(defn delete-form [id]
	(def db2
		(remove-tuple db2 :forms (form id))))

(defn edit-form [id title order method target success fail cancel email]
	(do (delete-form id)
		(add-form title order method target success fail cancel email id)))

(defn but-form [id]
  (nth (filter-- (all-forms) :id (Integer/valueOf id)) 0 {}))

; pages
(defn all-pages []
  (select db2 :pages {}))

(defn page [id]
  (nth (filter- (all-pages) :id (Integer/valueOf id)) 0 {}))

(defn but-page [id]
  (nth (filter-- (all-pages) :id (Integer/valueOf id)) 0 {}))

(defn add-page
	([title order formid] (add-page title order formid (inc (count (all-pages)))))
	([title order formid id]
		(def db2 
			(add-tuple db2 :pages 
				{:id (Integer/valueOf id) 
				 :title title 
				 :order (Integer/valueOf order) 
				 :formid (Integer/valueOf formid)}))))

; can delete only if there is no answers, else put :status :deleted
(defn delete-page [id]
	(def db2
		(remove-tuple db2 :pages (page id))))

(defn edit-page [id title order formid]
	(do (delete-page id)
		(add-page title order formid id)))

; sections
(defn all-sections []
  (select db2 :sections {}))

(defn section [id]
  (nth (filter- (all-sections) :id (Integer/valueOf id)) 0 {}))

(defn but-section [id]
  (nth (filter-- (all-sections) :id (Integer/valueOf id)) 0 {}))

(defn add-section [title order formid pageid]
	(def db2 
		(add-tuple db2 :sections 
			{:id (inc (count (all-sections)))
			 :title title 
			 :order (Integer/valueOf order) 
			 :formid (Integer/valueOf formid)
			 :pageid (Integer/valueOf pageid)
			 :repeat 1})))


(defn edit-section [id title order formid pageid])

(defn delete-section [id])

; groups
(defn all-groups []
  (select db2 :groups {}))

(defn group [id]
  (nth (filter- (all-groups) :id (Integer/valueOf id)) 0 {}))

(defn but-group [id]
  (nth (filter-- (all-groups) :id (Integer/valueOf id)) 0 {}))

(defn add-group [title order formid pageid sectionid]
	(def db2 
		(add-tuple db2 :groups 
			{:id (inc (count (all-groups))) 
			 :title title 
			 :order (Integer/valueOf order) 
			 :formid (Integer/valueOf formid)
			 :pageid (Integer/valueOf pageid)
			 :sectionid (Integer/valueOf sectionid)
			 :repeat 1})))

(defn edit-group [id title order formid pageid sectionid])

(defn delete-group [id])

; fieldsets
(defn all-fieldsets []
  (select db2 :fieldsets {}))

(defn fieldset [id]
  (nth (filter- (all-fieldsets) :id (Integer/valueOf id)) 0 {}))

(defn but-fieldset [id]
  (nth (filter-- (all-fieldsets) :id (Integer/valueOf id)) 0 {}))

(defn add-fieldset [title order formid pageid sectionid]
	(def db2 
		(add-tuple db2 :fieldsets 
			{:id (inc (count (all-fieldsets))) 
			 :title title 
			 :order (Integer/valueOf order) 
			 :formid (Integer/valueOf formid)
			 :pageid (Integer/valueOf pageid)
			 :sectionid (Integer/valueOf sectionid)
			 :repeat 1})))

(defn edit-fieldset [id title order formid pageid sectionid])

(defn delete-fieldset [id])

; questions TODO
(defn all-questions []
  (select db2 :questions {}))

(defn question [id]
  (nth (filter- (all-questions) :id (Integer/valueOf id)) 0 {}))

(defn add-question [title formid pageid sectionid groupid questiontype order])
(defn edit-question [id title formid pageid sectionid groupid questiontype order])
(defn delete-question [id])

; guestion types
(defn all-question-types []
  (select db2 :question-types {}))

(defn question-type [id]
  (nth (filter- (all-question-types) :id (Integer/valueOf id)) 0 {}))

(defn all-question-answers []
  (select db2 :question-answers {}))

(defn question-answers [questionid]
  (filter- (all-question-answers) :questionid (Integer/valueOf questionid)))

(defn question-answer [id]
  (nth (filter- (all-question-answers) :id (Integer/valueOf id)) 0 {}))

(defn division-data [type]
	(condp = type
	  "forms" 		(all-forms)
	  "pages" 		(all-pages)
	  "sections" 	(all-sections)
	  "groups" 		(all-groups)
	  "fieldsets" 	(all-fieldsets)
	  "questions" 	(all-questions)
	  "question-types" 	(all-question-types)
	  "question-answers" (all-question-answers)
	  (throw (IllegalArgumentException.
		(str "unsupported type " type)))))

(defn form-division [type id]
  (nth (filter- (division-data type) :id (Integer/valueOf id)) 0 {}))