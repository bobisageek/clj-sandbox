(ns may2023
  (:require [clojure.string :as str]
            [clojure.string :as string]
            [clojure.math :as math]))

(defmacro my-thing [m]
  `(keys ~m))

(def default-db (atom {}))

(defmacro db-query
  [key & {:keys [db] :or {db 'default-db}}]
  `(get (deref ~db) ~key))                                  ;;;; db is an `atom`

(let [default-db (atom {})]
  (db-query 'painter default-db))

(macroexpand '(db-query 'painter :db default-db))
(macroexpand '(db-query 'painter default-db))

;; just a map
(def fred-map {:name "Fred" :hobby "skadooing"})

(type fred-map)
;; => clojure.lang.PersistentArrayMap

;; declare a record
(defrecord Person [name hobby])

;; make a record 'instance' from the map
(def fred-record (map->Person fred-map))

(type fred-record)
;; => may2023.Person

;; get hobby from map
(:hobby fred-map)

;; get hobby from record
(:hobby fred-record)


;; "update" (really create new) map with an additional field
(type (assoc fred-map :age 42))
;; => clojure.lang.PersistentArrayMap

;; "update" (really create new) record with an additional field
(type (assoc fred-record :age 42))
;; => may2023.Person
;; note that this record now has a 'field' that wasn't in the original definition

((juxt identity type) (assoc fred-record :age 42))

(take 2 (iterate inc Long/MAX_VALUE))
;; => Error: long overflow
(take 2 (iterate inc' Long/MAX_VALUE))
;; => (9223372036854775807 9223372036854775808N)

(take 2 (iterate unchecked-inc Long/MAX_VALUE))



#_(clojure.main/repl :eval identity :prompt #(println "read=>"))

#_(->> (ns-map (the-ns 'clojure.core))
       vals
       (filter var?)
       (map (juxt identity (comp :arglists meta)))
       (filter #(< 2 (count (second %)))))

((fn [x] (filter (empty? (rest x)))) [1 2 3 4 5])

(filter (empty? (rest [1 2 3 4 5])))

(doseq)