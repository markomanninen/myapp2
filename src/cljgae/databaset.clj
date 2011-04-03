(ns cljgae.databaset
    (:use
	  [clojure.contrib.datalog.database :only (make-database add-tuples remove-tuple add-tuple select)]))

(def db-schema
    (make-database
      (relation :forms [:id :title])
      (index :forms :id)))

(def db2 (add-tuples db-schema [:forms :id 1 :title "Form 1"]))

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
	([title] (add-form title (inc (count (all-forms)))))
	([title id]
		(def db2 
			(add-tuple db2 :forms 
				{:id (Integer/valueOf id)
				 :title title}))))

(defn delete-form [id]
	(def db2
		(remove-tuple db2 :forms (form id))))

(defn edit-form [id title]
	(do (delete-form id)
		(add-form title id)))

(defn but-form [id]
  (filter-- (all-forms) :id (Integer/valueOf id)))