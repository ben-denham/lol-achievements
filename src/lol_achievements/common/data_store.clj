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
  "Get the player data stored in Redis for a given player name."
  (wcar* (car/get name)))

(defn set-player-data [name data]
  "Set the player data stored in Redis for a given player name."
  (wcar* (car/set name data)))
