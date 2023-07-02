(ns june2023
  (:require [clojure.java.io :as io]
            [clojure.string :as str]
            [clojure.zip :as zip]))

(time (doseq [x (range 100)]
        (println x)))

(time (run! println (range 100)))

; (require '[pantomime.mime :as mime])
(require '[pantomime.core :as mime])

(with-open [o (io/output-stream "abe.txt")]
  (io/copy (byte-array [97 98 99]) o)
  (io/copy (byte-array [99 98 97]) o))

(defn new-index [i [g n]]
  (if (and n (< 1 n))
    [(+ n i) (- i) [g 0]]
    [ i (- i) [g n]]))

(let [a [[7 4]
         [8 3]
         [9 1]
         [10 1]
         [7 2]
         [11 nil]]]
  (map last (sort (map-indexed new-index a))))

(def maybe-keyvals
  [[:c (constantly nil)]
   [:d (constantly 5)]])

(let [m {:a 1 :b 2}]
  (reduce (fn [acc [k v]]
            (if-let [v-result (v)]
              (assoc acc k v-result)
              acc)) m maybe-keyvals))

(->> (conj
       [["src" "src/top/main" {"service.clj.template" "service.clj"}]
        ["dev" "" {"user_donut.clj" "user.clj"}]]
       ["dev" "" {"mulog.clj" "mulog.clj"}])
    (group-by first)
    vals)

#_(1 (2) (3) (4 (5)))
(def mz '(1 (2 3 (4 (5)))))

(def ma [{:value 1 :children [{2 {} 3 {} 4 {5 {}}}]}])
(-> (zip/zipper map? keys (fn [m c] (into m c)) ma) zip/down zip/down)

(-> (zip/seq-zip mz) zip/down zip/down)

    

