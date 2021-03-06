(ns lol-achievements.web.handler
  (:require [compojure.core :refer [defroutes routes wrap-routes]]
            [lol-achievements.web.layout :refer [error-page]]
            [lol-achievements.web.routes.home :refer [home-routes]]
            [lol-achievements.web.routes.player :refer [player-routes]]
            [lol-achievements.web.middleware :as middleware]
            [compojure.route :as route]
            [taoensso.timbre :as timbre]
            [taoensso.timbre.appenders.3rd-party.rotor :as rotor]
            [selmer.parser :as parser]
            [environ.core :refer [env]]))

(defn init
  "init will be called once when
   app is deployed as a servlet on
   an app server such as Tomcat
   put any initialization code here"
  []

  (timbre/merge-config!
    {:level     (if (env :dev) :trace :info)
     :appenders {:rotor (rotor/rotor-appender
                          {:path "lol_achievements.log"
                           :max-size (* 512 1024)
                           :backlog 10})}})

  (if (env :dev) (parser/cache-off!))
  (timbre/info (str
                 "\n-=[lol-achievements started successfully"
                 (when (env :dev) " using the development profile")
                 "]=-")))

(defn destroy
  "destroy will be called when your application
   shuts down, put any clean up code here"
  []
  (timbre/info "lol-achievements is shutting down...")
  (timbre/info "shutdown complete!"))

(def app-routes
  (routes
   (wrap-routes (routes
                 #'home-routes
                 #'player-routes) middleware/wrap-csrf)
   (route/not-found
    (:body
     (error-page {:status 404
                  :title "page not found"})))))

(def app (middleware/wrap-base #'app-routes))
