(ns apr2023
  (:require [clojure.string :as str]))


(let [foo [{:at1 "Hello1" :at2 "World1"} {:at1 "Hello2" :at2 "World2"}]]
  (->> (map #(find % :at1) foo)
       (filter #(= (val %) "Hello2"))))

(let [s "https://www.google.com"
      protocol-ind (+ 3 (str/index-of s "://"))
      domain-ind (str/index-of s "." protocol-ind)]
  (subs s protocol-ind domain-ind))

(re-find #"(?<=://).*?(?=\.mydomain)" "https://blib.blub.mydomain.com")

(let [digits #"[-+]?\d+" ;regex for an integer
      operator #"[-+*/]" ; an operator
      spaces #"\s+" ; one or more whitespace characters
      integer-op (str/join spaces [digits operator digits]) ; integer op integer separated by spaces
      r (re-pattern integer-op)] ; turn it into a regex 
  (map #(re-find r %)
       ["43 + 34" "-1 + 6" "a + b"]))