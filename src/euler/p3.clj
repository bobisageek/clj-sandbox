(ns euler.p3)

(defn next-factor [n candidate]
  (if (zero? (mod n candidate))
    candidate
    (recur n (inc candidate))))

#_ (next-factor 13195 2)

(defn lpf [n]
  (loop [n n
         a 2]
    (let [nf (next-factor n a)]
      (if (= nf n)
        n
        (recur (/ n nf) nf)))))

#_ (lpf 13195)