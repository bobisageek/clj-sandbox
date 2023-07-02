(ns euler.p4
  (:require [clojure.string :as str]))

(defn palindrome? [n]
  (= (str n) (str/reverse (str n))))
             
(defn largest-palindrome-product [max-num]
  (let [palindrome-products
        (for [a (range max-num 0 -1)
              b (range max-num 0 -1)
              :let [p (* a b)]
              :when (palindrome? p)]
          p)]
    (apply max palindrome-products)))
