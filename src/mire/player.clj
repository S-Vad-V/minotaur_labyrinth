(ns mire.player)

(def ^:dynamic *current-room*)
(def ^:dynamic *inventory*)
(def ^:dynamic *health*)
(def ^:dynamic *damage*)
(def ^:dynamic *name*)
(def ^:dynamic *command*)

(def prompt "> ")
(def streams (ref {}))
(def players (ref {}))

(defn carrying? [thing]
  (some #{(keyword thing)} @*inventory*))
