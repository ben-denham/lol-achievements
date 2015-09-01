(ns lol-achievements.common.data-store
  (:require [taoensso.carmine :as car :refer [wcar]]
            [environ.core :refer [env]]))

(def redis-conn-spec
  (if-let [redis-url (env :redis-url)]
    {:url redis-url}
    {:host "redis" :port 6379}))

(def redis-conn
  {:pool {} :spec redis-conn-spec})

(defmacro wcar* [& body] `(car/wcar redis-conn ~@body))

(defn get-player-data [name]
  (wcar* (car/get name)))

(defn set-player-data [name data]
  (wcar* (car/set name data)))
