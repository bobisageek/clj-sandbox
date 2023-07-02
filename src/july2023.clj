(ns july2023)

(require '[clj-java-decompiler.core :refer [decompile]])
(decompile (^:once fn* [x y] (+ x y)))
(decompile (fn* [x y] (+ x y)))
