(ns euler.p5)

(defn group-by-reduce [keyf valf coll]
  (reduce (fn [acc v]
            (let [fk (keyf v)]
              (update acc fk valf v)))
          {} coll))
                  

#_ (group-by-reduce identity (fn [cur-acc _this-val] (inc (or cur-acc 0))) [1 2 3 4 5 6 2 3 5])

(defn next-factor [n candidate]
  (if (< n candidate)
    [n candidate]
    (let [candidates (iterate inc candidate)
          factor (first (filter #(zero? (mod n %)) candidates))]
      [(/ n factor) factor])))

(defn factor-map [n]
  (loop [n n
         candidate 2
         acc {}]
    (if (= 1 quotient)
      acc
      (recur (quotient-and-factor)))))


(macroexpand '(when 'a 'b 'c))