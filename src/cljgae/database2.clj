(ns cljgae.database
    (:use 
	  [hiccup.core]
	  [clojure.contrib.datalog.database :only (make-database add-tuples)]))

;
(def db-schema
    (make-database
      (relation :forms [:id :title])
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
           [:forms :id 1     :title "Form 1"]

           [:pages :id 1     :title "Page 1"    :formid 1 :order 1]
           [:pages :id 2     :title "Page 2"    :formid 1 :order 2]

           [:sections :id 1  :title "Section 1" :pageid 1 :order 1 :formid nil :repeat 0]
           [:sections :id 2  :title "Section 2" :pageid 1 :order 2 :formid nil :repeat 0]
           [:sections :id 3  :title "Section 1" :pageid 2 :order 2 :formid nil :repeat 0]
           [:sections :id 4  :title "Section 2" :formid 1 :order 2 :pageid nil :repeat 0]

           [:groups :id 1    :title "Group 1"   :sectionid 1 :order 1 :repeat 0 :formid nil :pageid nil]
           [:groups :id 2    :title "Group 2"   :sectionid 1 :order 2 :repeat 0 :formid nil :pageid nil]
           [:groups :id 3    :title "Group 1"   :pageid 2 :order 1 :repeat 0 :sectionid nil :formid nil]
           [:groups :id 4    :title "Group 1"   :formid 1 :order 1 :repeat 0 :sectionid nil :pageid nil]
		   
           [:questions :id 1 :title "Sameer"    :groupid 1 :order 1 :type 1 :repeat 0 :fieldsetid nil :formid nil :pageid nil :sectionid nil]
           [:questions :id 2 :title "Lilian"    :groupid 1 :order 2 :type 1 :repeat 0 :fieldsetid nil :formid nil :pageid nil :sectionid nil]
           [:questions :id 3 :title "Li"        :groupid 2 :order 1 :type 1 :repeat 0 :fieldsetid nil :formid nil :pageid nil :sectionid nil]
           [:questions :id 4 :title "Fred"      :groupid 3 :order 1 :type 1 :repeat 0 :fieldsetid nil :formid nil :pageid nil :sectionid nil]
           [:questions :id 5 :title "Brenda"    :groupid 4 :order 1 :type 1 :repeat 0 :fieldsetid nil :formid nil :pageid nil :sectionid nil]
           [:questions :id 6 :title "Miki"      :sectionid 1 :order 1 :type 1 :repeat 0 :fieldsetid nil :formid nil :groupid nil :pageid nil]
           [:questions :id 7 :title "Albert"    :pageid 1 :order 1 :type 1 :repeat 0 :fieldsetid nil :formid nil :groupid nil :sectionid nil]
           [:questions :id 8 :title "Albert"    :formid 1 :order 1 :type 1 :repeat 0 :fieldsetid nil :groupid nil :pageid nil :sectionid nil]

		   [:question-types :id 1 :title "Textfield" :controller '(html [:input {:type "text"}])]))

(defonce db (atom
    {:id 1

       ; form database setup
       :forms [{:id 1 :title "Form 1"}]

       ; divisions
       :pages [{:id 1 :title "Page 1" :formid 1 :order 1}
               {:id 2 :title "Page 2" :formid 1 :order 2}]

       :sections [{:id 1 :title "Section 1" :pageid 1 :order 1}
                  {:id 2 :title "Section 2" :pageid 1 :order 2}
                  {:id 3 :title "Section 1" :pageid 2 :order 2}
                  {:id 4 :title "Section 2" :formid 1 :order 2}]
                  ; if there are several pages, then this section shows on every page because its a direct child of form!

       :groups [{:id 1 :title "Group 1" :sectionid 1 :order 1}
                {:id 2 :title "Group 2" :sectionid 1 :order 2}
                {:id 3 :title "Group 1" :pageid 2 :order 1}
                {:id 4 :title "Group 1" :formid 1 :order 1}]
                ; if there are several pages, then this groups shows on every page because its a direct child of form!
       
       :fieldsets [] ; acts like groups or actually any division, but are made of fieldset and legend tags
       
       ; questions -> widgets -> -> question types -> controllers. one can define whole 
       ; controller / sanitize / validation routine per question
       ; but its easier to use predefined question types for each question
       :questions [{:id 1 :title "Question 1" :groupid 1 :order 1 :type 1}
                   {:id 2 :title "Question 2" :groupid 1 :order 2 :type 1}
                   {:id 3 :title "Question 3" :groupid 2 :order 1 :type 1}
                   {:id 4 :title "Question 4" :groupid 3 :order 1 :type 1}
                   {:id 5 :title "Question 5" :groupid 4 :order 1 :type 1}
                   {:id 6 :title "Question 6" :sectionid 1 :order 1 :type 1}
                   {:id 7 :title "Question 7" :pageid 1 :order 1 :type 1}
                   {:id 8 :title "Question 8" :formid 1 :order 1 :type 1}]
     
       :question-types [{:id 1 :title "Textfield" :controller '(html [:input {:type "text"}])}]
       
       ; question answers
       :question-answers []
    
       :users []
       :roles #{:admin :user}
       :regions #{{:id 1 :value "North America"}
                  {:id 2 :value "South America"}
                  {:id 3 :value "Europe"}
                  {:id 4 :value "Australia"}}
       :languages #{{:id 1 :name "Clojure"}
                    {:id 2 :name "Ruby"}
                    {:id 3 :name "Java"}
                    {:id 4 :name "C/C++"}
                    {:id 5 :name "Go"}}}))



(defn filter- [map key val]
  (filter #(= (key %) val) map))

(defn add-form [title order]
	(swap! (all-forms) assoc {:id 2 :title title :order order})) ; return formid

(defn edit-form [id title order]
	(atom (form id) :title title :order order))

(defn delete-form [id])

(defn all-forms []
  (:forms @db))

(defn form [id]
  (filter- (all-forms) :id (Integer/valueOf id)))

(defn add-page [title formid order]) ; return formid
(defn edit-page [id title formid order])
(defn delete-page [id])

(defn all-pages []
  (:pages @db))

(defn page [id]
  (filter- (all-pages) :id (Integer/valueOf id)))

(defn add-section [title formid pageid order]) ; return pageid
(defn edit-section [id title formid pageid order])
(defn delete-section [id])

(defn all-sections []
  (:sections @db))

(defn section [id]
  (filter- (all-sections) :id (Integer/valueOf id)))

(defn add-group [title formid pageid sectionid order]) ; return groupid
(defn edit-group [id title formid pageid sectionid order])
(defn delete-group [id])

(defn all-groups []
  (:groups @db))

(defn group [id]
  (filter- (all-groups) :id (Integer/valueOf id)))

(defn add-fieldset [title formid pageid sectionid order]) ; return groupid
(defn edit-fieldset [id title formid pageid sectionid order])
(defn delete-fieldset [id])

(defn all-fieldsets []
  (:fieldsets @db))

(defn fieldset [id]
  (filter- (all-fieldsets) :id (Integer/valueOf id)))

(defn add-question [title formid pageid sectionid groupid questiontype order]) ; return groupid
(defn edit-question [id title formid pageid sectionid groupid questiontype order])
(defn delete-question [id])

(defn all-questions []
  (:questions @db))

(defn question [id]
  (filter- (all-questions) :id (Integer/valueOf id)))

(defn all-question-types []
  (:question-types @db))

(defn question-type [id]
  (filter- (all-question-types) :id (Integer/valueOf id)))

; https://github.com/brentonashworth/sandbar-examples/blob/master/forms/src/sandbar/examples/database.clj

(defn all-question-answers []
  (:question-answers @db))

(defn question-answers [questionid]
  (filter- (all-question-answers) :questionid (Integer/valueOf questionid)))

(defn question-answer [id]
  (filter- (all-question-answers) :id (Integer/valueOf id)))

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