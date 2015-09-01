(ns lol-achievements.worker.core
  (:require [lol-achievements.common.data-store :refer [set-player-data]]
            [lol-achievements.common.message-queue :refer [subscribe-player-data-requests]])
  (:gen-class))

(defn update-player-data [name]
  (set-player-data name {:visited true}))

(defn -main [& args]
  (subscribe-player-data-requests update-player-data))
