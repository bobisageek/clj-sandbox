(ns key-exchange
  (:require [clojure.java.io :as io])
  (:import (java.net ServerSocket)
           (java.security KeyPairGenerator SecureRandom)))

(def server-port 5000)

(defn gen-key-pair []
  (let [kpg (doto (KeyPairGenerator/getInstance "RSA")
              (.initialize 2048 (SecureRandom.)))
        key-pair (.generateKeyPair kpg)]
    ((juxt #(.. % getPublic getPublicExponent)
           #(.. % getPrivate getPrivateExponent))
     key-pair)))

(defn gen-shared-secret [^BigInteger private-key ^BigInteger public-key]
  (let [prime (biginteger 23)]
    (.modPow public-key private-key prime)))

(defn start-tcp-server []
  (with-open [srv-sock (doto (ServerSocket. server-port)
                         ((fn [_] (println "Server listening on" server-port))))
              clnt-sock (doto (.accept srv-sock)
                          ((fn [sock] (println "Client connected:" (.. sock getInetAddress getHostAddress)))))
              w (io/writer (.getOutputStream clnt-sock))
              r (io/reader (.getInputStream clnt-sock))]
    (let [[apub apriv] (gen-key-pair)
          amsg (str apub "\n")
          _ (doto w (.write amsg) .flush)
          _ (println "sent alice:" amsg)
          bpub (-> (.readLine r) biginteger)
          _ (println "receive bob:" bpub)
          [ashared bshared] (map #(apply gen-shared-secret %)
                                 [[apriv bpub] [bpub apub]])
          _ (println "alice shared:" ashared)
          _ (println "bob shared:" bshared)]
      (println (if (== ashared bshared)
                 "shared match"
                 "no shared match")))))

(def main start-tcp-server)

#_(main)