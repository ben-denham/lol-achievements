(ns lol-achievements.common.message-queue
  (:require [langohr.core :as mq]
            [langohr.channel :as lch]
            [langohr.queue :as lq]
            [langohr.consumers :as lc]
            [langohr.basic :as lb]
            [environ.core :refer [env]]
            [lol-achievements.common.util :refer [wrap-cache dec-to-zero]]))

(def ^:private amqp-url (get (System/getenv) "CLOUDAMQP_URL" "amqp://guest:guest@rabbitmq:5672"))
(def ^{:private true :const true} default-exchange-name "")

;; Atoms to store the connection and channel that are in use.
(def ^:private connection (atom nil))
(def ^:private channel (atom nil))
;; A counter to track usage of the message queue connection and
;; channel.
(def ^:private mq-usage (atom 0))

;; Connection and channel getters that cache.
(def ^:private get-connection (wrap-cache connection #(mq/connect {:uri amqp-url})))
(def ^:private get-channel (wrap-cache channel #(lch/open (get-connection))))

;; Return a channel, and increments the usage counter.
(defn- open []
  (swap! mq-usage inc)
  (get-channel))

;; Decrements the usage counter, and closes the connection and channel
;; if there are no more users.
(defn- close []
  (swap! mq-usage dec-to-zero)
  (when (zero? @mq-usage)
    (do (when-let [chan @channel]
          (mq/close @channel)
          (reset! channel nil))
        (when-let [conn @connection]
          (mq/close conn)
          (reset! connection nil)))))

(defn- publish [queue-name payload & properties]
  (let [chan (open)]
    (lb/publish chan default-exchange-name queue-name
                payload properties)
    (close)))

(defn- subscribe [queue-name handler]
  (let [chan (open)]
    (lq/declare chan queue-name {:exclusive false :auto-delete true})
    (lc/subscribe chan queue-name handler {:auto-ack true})
    (.addShutdownHook (Runtime/getRuntime) (Thread. close))))

(def player-data-requests-queue-name "lol-achievements.player-data-requests")

(defn publish-player-data-request [name]
  (publish player-data-requests-queue-name name
           :content-type "text/plain"
           :type "player-data-request"))

(defn subscribe-player-data-requests [handler]
  (subscribe player-data-requests-queue-name
             (fn [_ _ payload]
               (let [name (String. payload "UTF-8")]
                 (handler name)))))
