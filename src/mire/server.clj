(ns mire.server
  (:require [clojure.java.io :as io]
            [server.socket :as socket]
            [mire.player :as player]
            [mire.commands :as commands]
            [mire.rooms :as rooms]
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

    ;; We have to nest this in another binding call instead of using
    ;; the one above so *in* and *out* will be bound to the socket
    (print "\nWhat is your name? ") (flush)
    (binding [player/*name* (get-unique-player-name (read-line))
              player/*current-room* (ref (@rooms/rooms :start))
              player/*inventory*  (ref #{:mantle :lighter :wall :penknife :sandals});(ref #{})
              player/*health* (ref 100)
              player/*damage* (ref 5)]
      (dosync
       (commute (:inhabitants @player/*current-room*) conj player/*name*)
       (commute player/streams assoc player/*name* *out*)) ;player/players

      (println (commands/look)) (print player/prompt) (flush)

      (try (loop [input (read-line)]
             (when input
               (if (<= @player/*health* 0)
                 (do (println "You are dead!")(cleanup)
                   (throw (Exception. "You are dead!")))
                 (do (println (commands/execute input))
                 (.flush *err*)
                 (print player/prompt) (flush)
                 (recur (read-line)))
               )
             ))
           (finally (cleanup))))))

(defn- minos-rising []
  (binding[minotaur/*current-room* (ref (@rooms/rooms :2-24))
		   minotaur/*name* (ref 'Minos')]
  (dosync(commute (:inhabitants @minotaur/*current-room*) conj minotaur/*name*))
  (thread 
    (try 
	(while true minotaur/randMove
	(<! (timeout 5000)))))))

(defn -main
  ([port dir]
     (rooms/add-rooms dir)
     (defonce server (socket/create-server (Integer. port) mire-handle-client))
     (println "Launching Mire server on port" port)
	 (minos-rising))
  ([port] (-main port "resources/rooms"))
  ([] (-main 3333))
  )
