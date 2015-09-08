(ns lol-achievements.common.message-queue
  (:require [langohr.core :as mq]
            [langohr.channel :as lch]
            [langohr.queue :as lq]
            [langohr.consumers :as lc]
            [langohr.basic :as lb]
            [environ.core :refer [env]]))

(def ^:private amqp-url (get (System/getenv) "CLOUDAMQP_URL" "amqp://guest:guest@rabbitmq:5672"))
(def ^{:private true :const true} default-exchange-name "")

(defn- get-connection []
  "Get a connection to Rabbitmq."
  (mq/connect {:uri amqp-url}))
(defn- get-channel [connection]
  "Get a channel for the given Rabbitmq connection."
  (lch/open connection))

(defn- close [connection channel]
  "Close the given Rabbitmq connection and channel."
  (mq/close channel)
  (mq/close connection))

(defn- publish [queue-name payload & properties]
  "Publish a message (with the given payload and message properties)
  to the Rabbitmq queue named by queue-name."
  (let [connection (get-connection)
        channel (get-channel connection)]
    (lb/publish channel default-exchange-name queue-name
                payload properties)
    (close connection channel)))

(defn- subscribe [queue-name handler]
  "Subscribe handler to be called for all messages received from the
  Rabbitmq queue named by queue-name."
  (let [connection (get-connection)
        channel (get-channel connection)]
    (lq/declare channel queue-name {:exclusive false :auto-delete true})
    (lc/subscribe channel queue-name handler {:auto-ack true})
    (.addShutdownHook (Runtime/getRuntime)
                      (Thread. #(close connection channel)))))

;; Helper functions for the player data requests queue.

(def player-data-requests-queue-name "lol-achievements.player-data-requests")

(defn publish-player-data-request [name]
  "Publish a player data request with the given player name."
  (publish player-data-requests-queue-name name
           :content-type "text/plain"
           :type "player-data-request"))

(defn subscribe-player-data-requests [handler]
  "Subscribe a handler to be called with the name of all
  player-data-requests."
  (subscribe player-data-requests-queue-name
             (fn [_ _ payload]
               (let [name (String. payload "UTF-8")]
                 (handler name)))))
