(ns selm
  (:require [selmer.filters :as f] 
            [selmer.parser :as s]))

(s/render 
  "b: {% if b%}{{b|double-format}}{% endif %}
c: {% if c%}{{c|double-format}}{% endif %}" 
  {:b nil :c 42})

(s/add-filter! :or-zero (fnil identity 0))
(s/add-filter! :or-zero-double (comp (f/get-filter :double-format) (fnil identity 0)))

(s/render
  "b: {{b|or-zero-double}}
c: {{c|or-zero|double-format}}"
  {:b nil :c 42})