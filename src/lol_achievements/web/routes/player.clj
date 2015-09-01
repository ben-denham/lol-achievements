(ns lol-achievements.web.routes.player
  (:require [compojure.core :refer [defroutes GET]]
            [ring.util.http-response :refer [ok]]
            [lol-achievements.common.data-store :refer [get-player-data]]
            [lol-achievements.common.message-queue :refer [publish-player-data-request]]))

(defn get-player [name]
  (if-let [player-data (get-player-data name)]
    (ok player-data)
    (do
      (publish-player-data-request name)
      (ok {}))))

(defroutes player-routes
  (GET "/player/:name" [name] (get-player name)))
