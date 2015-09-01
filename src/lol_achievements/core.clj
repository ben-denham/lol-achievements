(ns lol-achievements.core
  (:require [lol-achievements.web.core]
            [lol-achievements.worker.core])
  (:gen-class))

(defn -main [& args]
  "Starts the web server and worker server."
  (lol-achievements.web.core/-main)
  (lol-achievements.worker.core/-main))
