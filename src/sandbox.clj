(ns sandbox
  "FIXME: my new org.corfield.new/scratch project."
  (:require [clojure.string :as str]))

(defn exec
  "Invoke me with clojure -X sandbox/exec"
  [opts]
  (println "exec with" opts))

(defn -main
  "Invoke me with clojure -M -m sandbox"
  [& args]
  (println "-main with" args))


(re-seq #"\d{9}M" "987654321123456789M")
;; => ("123456789M")

(re-seq #"(\d*)\.?(\d{9})M" "987654321123456789M")
;; => (["987654321123456789M" "987654321" "123456789"])

(defn re-test [re s]
  (println "regex:" (.pattern re))
  (println "matched against:" s)
  (doseq [[whole-match & groups] (re-seq re s)]
    (println "whole match:" whole-match)
    (doseq [[group-num group-content] (map vector (range) groups)]
      (println "capture group" group-num ":" group-content))))


(defn flip-set-membership 
  "a helper function to swap set membership (if the element is there, 
  take it out, else add it"
  [sets el]
  (map #(if (% el) (disj % el) (conj % el)) sets))

;; demo
#_(flip-set-membership [#{1 2 3} #{4 5 6}] 3)
;; => (#{1 2} #{4 6 3 5})

(defn all-splits 
  "create all possible splits of coll between two sets"
  [coll]
  (let [flip-sets (comp set flip-set-membership)]
    (loop [acc #{#{#{} (set coll)}}
           [this-el & the-rest] coll]
      (if this-el
        (recur
          (into acc (map #(flip-sets % this-el) acc))
          the-rest)
        acc))))

;; demo
#_(all-splits #{1 2 3 4})
;; #{#{#{1 4 3 2} #{}}
;;  #{#{3} #{1 4 2}}
;;  #{#{1 4} #{3 2}}
;;  #{#{1 3 2} #{4}}
;;  #{#{2} #{1 4 3}}
;;  #{#{4 2} #{1 3}}
;;  #{#{4 3} #{1 2}}
;;  #{#{4 3 2} #{1}}}

(defn sums-in-opposite-set
  "check if the sum of set1 is a member of set2 and vice-versa"
  [set1 set2]
  (let [sum #(reduce + %)]
    (and (set1 (sum set2)) (set2 (sum set1)))))

;; demo
#_(sums-in-opposite-set? #{1 2} #{3 -2})
;; => 3 ; which is actually the result of (set2 (sum set1)), 
;;        but it's truthy, so that does the job
#_(sums-in-opposite-set? #{1 2} #{3 2})
;; => nil ; false because 3 + 2 = 5 and 5 isn't in #{1 2}

(defn do-the-thing [nums]
  (->> nums ;; take the collection of numbers that comes in
       all-splits ;; call all-splits on it to get a set of sets of sets
       (filter #(apply sums-in-opposite-set %)))) ;; filter to just the ones that match the condition

;; demo
#_(do-the-thing [1 2 -3 4 -5 6])
;; => ( #{#{4 -3} #{1 6 2 -5}} 
;;      #{#{1 -3 6} #{4 2 -5}})
;; returns a lazy sequence of sets of sets, so in this case
;; two splits of the input where the sum-cross-membership thing holds

(let [[zero one two & a-bunch-of-numbers] (take 20 (range))]
  (list (+ zero one two) (apply + a-bunch-of-numbers)))

(declare even)
(def ^:dynamic b 0)
(defn odd [x]
  (binding [b (inc b)]
    (if (= x 1) (do (println b) true)
                #(even (dec x)))))
(defn even [x]
  (binding [b (inc b)]
    (cond
      (neg? x) (do (println b) false)
      (zero? x) (do (println b) true)
      :else #(odd (dec x)))))

(defmulti frob :op)
(defmethod frob :default [x] (:subject x))
(defmethod frob :rev [s] (str/reverse (:subject s)))
(defmethod frob :str [x] (str (:subject x)))

(let [a [{:op :rev :subject "abc"}
         {:op :str :subject 21}
         {:op :unknown :subject [1 2 3]}
         [7 8]]]
  (transduce (comp (filter map?) (map frob)) conj a))


(map #(re-test % "abbbb") [#"a(b+?)b+$" #"a(b+)b+$"])