(ns n-test
  (:require
    [clojure.test :refer [is deftest]]))

(defrecord A [b])

(defmacro m [a]
  (-> a meta :tag symbol resolve))

(comment
  ; The following returns 'n.A' that is a java.lang.Class.
  (m ^A (A. 5)))

(deftest m-test
  ; Returns true in REPL.
  ; Fails in test runner.
  (let [x (macroexpand '(n-test/m ^A (A. 5)))]
    ; Prints in REPL: n_test.A java.lang.Class
    ; Prints in test-runner: nil nil
    (println x (type x))
    (is (= n_test.A (macroexpand '(n-test/m ^A (A. 5)))))))