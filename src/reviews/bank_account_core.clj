(ns reviews.bank-account-core
  (:require [clojure.java.io :as io]
            [fake-logging :as log]
            [clojure.string :as str])
  (:gen-class)
  (:import (java.io StringReader)))

(def accounts (atom [100 100]))
(def current-line (atom 1)) ; text lines begin from 1, not 0

(defn reset-state! []
  (reset! accounts [100 100])
  (reset! current-line 1))

;; in a production system I'd probably pass the line number around, but 
;; this'll do in the micro 


(defn die [msg]
  (log/error (format "[Line %d] %s" @current-line msg))
  #_(System/exit 1)) ; I don't want my repl to quit

;; I'd probably use a dynamic binding to push my error handling in, but that's a topic for 
;; another day (https://www.youtube.com/watch?v=zp0OEDcAro0)


(defn collect-bank-id
  "Takes a string and parses it as an integer.
   Then, it checks whether it is a valid index of the accounts vector.
   Returns the index if it was valid, otherwise nil."
  [text]
  (let [bank-id (dec (Integer/parseInt text))] ; expects one str element which is converted to int
    (when (contains? @accounts bank-id) ; check if bank-id is a valid index to accounts
      bank-id)))

(defn parse-check
  "Return the account-id from the string line. If it is invalid then (die) is called."
  [line]
  (if-let [account-id (collect-bank-id (first line))]
      account-id
      (die (format "No Account With That ID Exists On The System!"))))

;; changed to if-let

(defn eval-check
  "Takes a string and converts it into an integer, specifically a valid index of the accounts vector.
   Then, prints the id (index + 1) and balance of that account."
  [account-id]
  (when account-id ; this is here to not execute after a die call without exiting
    (printf "Check Account Balance:\n\tId: %d\n\tBalance: %d\n" (inc account-id) (nth @accounts account-id))))

(defn parse-transfer
  "Takes a string and parses out a transfer source, destination and amount. Returns it in a map."
  [line]
  (let [parse-order [collect-bank-id collect-bank-id parse-long] 
        ; parse-long is in clojure.core and, in this case, more ergonomic than Integer/parseInt (long story)
        [src dest amount] (map (fn [parser value] (parser value)) parse-order line)]
    (cond
      (not (and src dest)) (die "Invalid account ID(s)")
      (not amount) (die "Invalid amount")
      :else {:src src :dest dest :amount amount})))

;; used map and destructuring to parse all the things
;; call die if anything doesn't parse

(defn eval-transfer
  "Takes a sequence of at least three strings, parses them into two indexes for the accounts vector
   and an amount for the amount of money that need to be transefered from the first to the second.
   Then does it."
  [transfer-info]
  (let [{:keys [src dest amount]} transfer-info
        deref-accounts @accounts]
    (when transfer-info ; bail if we called die
      (if (>= (nth deref-accounts src) (nth deref-accounts dest)) ; check that the source has enough money 
        (do
          (swap! accounts update src - amount)
          (swap! accounts update dest + amount))
        (die (format "Failed Transfer Transaction!\n\tCause: Not Enough Money In %d" (inc src)))))))

(defn do-transaction
  "Takes a line, trims it, splits it with a space delimiter and checks the first character.
   Depending on the character a different transaction will be performed"
  [line]
  (let [[command & remaining-info] (str/split (str/trim line) #" ")]
    (case command
      ("" "#") nil ; skip whitespace and comments
      "0" (eval-check (parse-check remaining-info)) ; transaction is a check
      "1" (eval-transfer (parse-transfer remaining-info)) ; transaction is a transfer 
      (die (format "Illegal Transaction Syntax!\n\tEncountered: %s" command)))))

(defn begin-transaction [file]
  (with-open [reader (io/reader file)]
    (doseq [line (line-seq reader)] ; loop over every line in the file
      (do-transaction line) ; perform transaction first, with the current line being defaulted to 1
      (swap! current-line inc)))) ; increment current line (2..3..)

(defn -main [& _]
  (let [bank-file (io/file "resources/bank.txt")]
    (if (.exists bank-file)
      (begin-transaction bank-file)
      (die "File 'bank.lst' Not Found!"))))

(defn mock-main [bank-file-str]
  (reset-state!) ; reset the state for repeated runs
  (with-out-str
    (begin-transaction (StringReader. bank-file-str))))

#_ (mock-main
     "
     # 0 - check, syntax: 0 <src>
     # 1 - transfer, syntax: 1 <src> <dest> <amount>
     
     0 1
     0 2
     0 3
     1 1 2 50
     1 2 1 30
     1 1 3 20
     1 1 2 money
     0 1
     0 2")

;; I can make a bunch of these commented `mock-main` calls to run in the REPL
;; and test my functions, or I could even throw this mock-main function
;; into a test namespace and use it to test basically my whole program
;; i.e. mock objects are borderline a scam