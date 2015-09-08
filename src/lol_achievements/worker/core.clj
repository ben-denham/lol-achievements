(ns lol-achievements.worker.core
  (:require [lol-achievements.common.data-store :refer [set-player-data]]
            [lol-achievements.common.message-queue :refer [subscribe-player-data-requests]]
            [lol-achievements.worker.riot-api :refer [fetch-player-data]])
  (:gen-class))

(defn update-player-data [name]
  "Fetches the latest player data for the given name and updates the
  data store."
  (set-player-data name (fetch-player-data name)))

(defn -main [& args]
  (subscribe-player-data-requests update-player-data))
