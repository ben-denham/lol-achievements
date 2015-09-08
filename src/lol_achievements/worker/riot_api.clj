(ns lol-achievements.worker.riot-api
  (:require [environ.core :refer [env]]
            [clj-http.client :as http]
            [clojure.data.json :refer [read-json]]
            [lol-achievements.common.util :refer [throttler]]))

(def ^:private config (env :riot-api-config))

(def ^:private base-url (str (:api-url config) "api/lol/" (:region config) "/"))

(def ^:private requests-per-node-per-minute
  (/ (:requests-per-minute config) (:workers-count config)))

(def ^:private delay-between-requests
  (int (Math/ceil (/ 60000 requests-per-node-per-minute))))

(def ^:private api-throttler (throttler delay-between-requests))

(def fetch-player-data
  (api-throttler
   (fn [name]
     (-> (http/get (str base-url "v1.4/summoner/by-name/" name)
                   {:query-params {"api_key" (:api-key config)}})
         (:body)
         (read-json)
         (vals)
         (first)))))
