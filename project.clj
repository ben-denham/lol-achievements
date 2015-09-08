(defproject lol-achievements "0.1.0-SNAPSHOT"

  :description "FIXME: write description"
  :url "http://example.com/FIXME"

  :dependencies [[org.clojure/clojure "1.7.0"]
                 [org.clojure/core.async "0.1.346.0-17112a-alpha"]
                 [selmer "0.9.0"]
                 [com.taoensso/timbre "4.1.1"]
                 [com.taoensso/tower "3.0.2"]
                 [markdown-clj "0.9.69"]
                 [environ "1.0.0"]
                 [compojure "1.4.0"]
                 [ring-webjars "0.1.1"]
                 [ring/ring-defaults "0.1.5"]
                 [ring-ttl-session "0.1.1"]
                 [ring "1.4.0"
                  :exclusions [ring/ring-jetty-adapter]]
                 [metosin/ring-middleware-format "0.6.0"]
                 [metosin/ring-http-response "0.6.3"]
                 [bouncer "0.3.3"]
                 [prone "0.8.2"]
                 [org.clojure/tools.nrepl "0.2.10"]
                 [org.webjars/bootstrap "3.3.5"]
                 [org.webjars/jquery "2.1.4"]
                 [org.immutant/web "2.0.2"]
                 [com.taoensso/carmine "2.11.1"]
                 [com.novemberain/langohr "3.3.0"]
                 [clj-http "2.0.0"]]

  :min-lein-version "2.0.0"
  :uberjar-name "lol-achievements.jar"
  :jvm-opts ["-server"]

  :main lol-achievements.core

  :plugins [[lein-environ "1.0.0"]]
  :profiles
  {:uberjar       [:project/base :project/uberjar]
   :dev           [:project/base :project/dev :profiles/dev]
   :test          [:project/base :project/test :profiles/test]
   :project/base {:env {:riot-api-config
                        {:api-url "https://oce.api.pvp.net/"
                         :region "oce"
                         :requests-per-minute 50
                         :workers-count 1}}}
   :project/uberjar {:omit-source true
                     :env {:production true}
                     :aot :all}
   :project/dev  {:dependencies [[ring/ring-mock "0.2.0"]
                                 [ring/ring-devel "1.4.0"]
                                 [pjstadig/humane-test-output "0.7.0"]]


                  :repl-options {:init-ns lol-achievements.core}
                  :injections [(require 'pjstadig.humane-test-output)
                               (pjstadig.humane-test-output/activate!)]
                  ;;when :nrepl-port is set the application starts the nREPL server on load
                  :env {:dev        true
                        :port       3000
                        :nrepl-port 7000}}
   :project/test {:env {:test       true
                        :port       3001
                        :nrepl-port 7001}}
   :profiles/dev {}
   :profiles/test {}})
