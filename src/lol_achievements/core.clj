(ns lol-achievements.core
  (:require [lol-achievements.web.core])
  (:gen-class))

(defn -main [& args]
  "Starts the web server and worker server."
  (lol-achievements.web.core/-main))
