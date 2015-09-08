(ns lol-achievements.web.routes.player
  (:require [compojure.core :refer [defroutes GET]]
            [ring.util.http-response :refer [ok]]
            [lol-achievements.common.data-store :refer [get-player-data]]
            [lol-achievements.common.message-queue :refer [publish-player-data-request]]
            [environ.core :refer [env]]))

(defn get-player [req]
  (let [name (get-in req [:params :name])
        refresh? (nil? (get-in req [:params :refresh]))
        player-data (get-player-data name)]
    ;; Force a refresh if there is no stored player data, or if the
    ;; site is in dev mode and a refresh parameter was provided.
    (when (or (and refresh? (:dev env)) (nil? player-data))
      (publish-player-data-request name))
    (ok (or player-data {}))))

(defroutes player-routes
  (GET "/player/:name" req (get-player req)))
