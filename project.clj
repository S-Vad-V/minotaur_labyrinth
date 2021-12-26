(defproject mire "0.13.1"
  :description "A multiuser text adventure game/learning project."
  :main ^:skip-aot mire.server
  :dependencies [[org.clojure/clojure "1.10.3"]
				 [org.clojure/core.async "0.1.346.0-17112a-alpha"]
                 [server-socket "1.0.0"]])
