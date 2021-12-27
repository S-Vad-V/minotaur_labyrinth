(ns mire.server
  (:require [clojure.java.io :as io]
            [server.socket :as socket]
            [mire.player :as player]
            [mire.commands :as commands]
            [mire.rooms :as rooms]
            [mire.spawns :as spawns]
            [mire.mSpawns :as mSpawns]
            [mire.minotaur :as minotaur]
            [clojure.core.async :as a :refer [thread <! timeout]]))

(defn- cleanup []
  "Drop all inventory and remove player from room and player list."
  (dosync
   (doseq [item @player/*inventory*]
     (commands/discard item))
   (commute player/streams dissoc player/*name*)
   (commute (:inhabitants @player/*current-room*)
            disj player/*name*)))

(defn- get-unique-player-name [name]
  (if (@player/streams name)
    (do (print "That name is in use; try again: ")
        (flush)
        (recur (read-line)))
    name))
(defn- mire-handle-client [in out]
  (binding [*in* (io/reader in)
            *out* (io/writer out)
            *err* (io/writer System/err)]
    #_{:clj-kondo/ignore [:inline-def]}
    (def spawnsList (spawns/add-spawns "resources/spawn/"))
    #_{:clj-kondo/ignore [:inline-def]}
    (def strt (get spawnsList (rand-nth (keys spawnsList))))
    (println strt)
    (print "\nWhat is your name? ") (flush)
    (binding [player/*name* (get-unique-player-name (read-line))
              player/*current-room* (ref strt)
              player/*inventory*  (ref #{});(ref #{})
              player/*health* (ref [100])
              player/*damage* (ref 5)
              player/*armor* (ref 10)]
      ;; (println @player/*current-room*)
      (dosync
       (commute (:inhabitants @player/*current-room*) conj player/*name*)
       (commute player/streams assoc player/*name* *out*)) ;player/players

      (println (commands/look)) (print player/prompt) (flush)

      (try (loop [input (read-line)]
             (when input
               (if (= input "exit")
                 (do (println "You are leave! Goodbye!") (cleanup)
                     (throw (Exception. "You are leave! Goodbye!")))
                 (do (println (commands/execute input))
                     (.flush *err*)
                     (print player/prompt) (flush)))
               (if (<= @player/*health* 0)
                 (do (println "You are dead!") (cleanup)
                     (throw (Exception. "You are dead!")))
                 (do (println (commands/execute input))
                     (.flush *err*)
                     (print player/prompt) (flush)
                     (recur (read-line))))))
           (finally (cleanup))))))

(defn- minos-rising []
  (def spawnsminoList (mSpawns/add-mSpawn "resources/minotaur_spawn/"))
#_{:clj-kondo/ignore [:inline-def]}
(def strt (get spawnsminoList (rand-nth (keys spawnsminoList))))
  (binding [minotaur/*current-room* (ref strt)
            minotaur/*name* (ref 'Minos')]
    (dosync (commute (:inhabitants @minotaur/*current-room*) conj minotaur/*name*))
    (thread
      (try
        (while true minotaur/randMove)))))

(defn -main
  ([port dir]
   (rooms/add-rooms dir)
   (defonce server (socket/create-server (Integer. port) mire-handle-client))
   (println "Launching Mire server on port" port)
   (minos-rising))
  ([port] (-main port "resources/rooms"))
  ([] (-main 3333)))
