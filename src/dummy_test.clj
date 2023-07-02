(ns dummy-test
  (:require [clojure.test :as test :refer [deftest testing is]]))

(deftest thing
         (is (= 2 (+ 1 2))))
         