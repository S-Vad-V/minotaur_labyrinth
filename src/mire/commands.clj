(ns mire.commands
  (:require [clojure.string :as str]
            [mire.rooms :as rooms]
            [mire.player :as player]))

(defn- move-between-refs
  "Move one instance of obj between from and to. Must call in a transaction."
  [obj from to]
  (alter from disj obj)
  (alter to conj obj))

(defn- del-from-ref
  [obj from]
  (alter from disj obj))
  
;; Command functions

(defn look
  "Get a description of the surrounding environs and its contents."
  []
  (str (:desc @player/*current-room*)
       "\nExits: " (keys @(:exits @player/*current-room*)) "\n"
       (str/join "\n" (map #(str "There is " % " here.\n")
                           @(:items @player/*current-room*)))))

(defn move
  "\"♬ We gotta get out of this place... ♪\" Give a direction."
  [direction]
  (dosync
   (let [target-name ((:exits @player/*current-room*) (keyword direction))
         target (@rooms/rooms target-name)]
     (if target
       (do
         (move-between-refs player/*name*
                            (:inhabitants @player/*current-room*)
                            (:inhabitants target))
         (ref-set player/*current-room* target)
         (look))
       "You can't go that way."))))

(defn grab
  "Pick something up."
  [thing]
  (dosync
  	(if (rooms/room-contains? @player/*current-room* thing)
  		(if (= thing "heal")
          		(do (del-from-ref (keyword thing)
                       (:items @player/*current-room*))
              		(print "You healed. Your HP: ")
	     		(ref-set player/*health* 100))
	   	(if (not(player/carrying? thing))
	     		(do (move-between-refs (keyword thing)
		        (:items @player/*current-room*)
		         player/*inventory*)
		 	(println "You picked up the " thing ".")
		 		(if (= thing "sword")
		   		(do (print "Your damage now: ")
		   		(ref-set player/*damage* (+ @player/*damage* 50)))
			   		(if (= thing "tt")
			   		(do (print "Your damage now: ")
			   		(ref-set player/*damage* (+ @player/*damage* 100)))
			   			(if (= thing "potato")
			   			(if (not(player/carrying? "glad"))
			   			(do (print "Your armor now: ")
		   				(ref-set player/*armor* 50))
		   					(do (move-between-refs (keyword thing)
		   					player/*inventory*
							(:items @player/*current-room*))
		   					(str "You can not wear 2 armors at the same time. You dropped " thing ".")))
					   			(if (= thing "glad")
					   			(if (not(player/carrying? "potato"))
					   			(do (print "Your armor now: ")
				   				(ref-set player/*armor* 100))
				   					(do (move-between-refs (keyword thing)
				   					player/*inventory*
									(:items @player/*current-room*))
				   					(str "You can not wear 2 armors at the same time. You dropped " thing ".")))
					   				)))))
      			(str "You already have " thing ".")))
     	(str "There isn't any " thing " here."))))

(defn discard
  "Put something down that you're carrying."
  [thing]
  (dosync
   (if (player/carrying? thing)
     (do (move-between-refs (keyword thing)
                            player/*inventory*
                            (:items @player/*current-room*))
         (str "You dropped the " thing ".")
         (if (= thing "sword") 
         (do (print "Your damage now: ")
      	 (ref-set player/*damage* (- @player/*damage* 50)))
         	(if (= thing "tt") 
         	(do (print "Your damage now: ")
      	 	(ref-set player/*damage* (- @player/*damage* 100)))
         		(if (= thing "potato") 
		 	(do (del-from-ref (keyword thing)
                       (:items @player/*current-room*))
		 	(print "Your armor now: ")
	      	 	(ref-set player/*armor* 0))
		 		(if (= thing "glad") 
			 	(do (del-from-ref (keyword thing)
                       	(:items @player/*current-room*))
			 	(print "Your armor now: ")
		      	 	(ref-set player/*armor* 0))	      	 	
         			)))))
     (str "You're not carrying a " thing "."))))

(defn inventory
  "See what you've got."
  []
  (str "You are carrying:\n"
       (str/join "\n" (seq @player/*inventory*))))
       
(defn health
  "See your hp."
  []
  (str @player/*health*))
  
(defn damage
  "See your damage."
  []
  (str @player/*damage*))

(defn armor
  "See your armor."
  []
  (str @player/*armor*))

(defn info
 "See all info"
 []
 (print "Your health is: ")
 (println (str @player/*health*))
 (print "Your armor is: ")
 (println (str @player/*armor*))
 (print "Your damage is: ")
 (str @player/*damage*))

(defn detect
  "If you have the detector, you can see which room an item is in."
  [item]
  (if (@player/*inventory* :detector)
    (if-let [room (first (filter #((:items %) (keyword item))
                                 (vals @rooms/rooms)))]
      (str item " is in " (:name room))
      (str item " is not in any room."))
    "You need to be carrying the detector for that."))

(defn say
  "Say something out loud so everyone in the room can hear."
  [& words]
  (let [message (str/join " " words)]
    (doseq [inhabitant (disj @(:inhabitants @player/*current-room*)
                             player/*name*)]
      (binding [*out* (player/streams inhabitant)]
        (println message)
        (println player/prompt)))
    (str "You said " message)))

(defn help
  "Show available commands and what they do."
  []
  (str/join "\n" (map #(str (key %) ": " (:doc (meta (val %))))
                      (dissoc (ns-publics 'mire.commands)
                              'execute 'commands))))

;; Command data

(def commands {"move" move,
               "north" (fn [] (move :north)),
               "south" (fn [] (move :south)),
               "east" (fn [] (move :east)),
               "west" (fn [] (move :west)),
               "grab" grab
               "discard" discard
               "inventory" inventory
               "detect" detect
               "look" look
               "say" say
               "help" help
               "health" health
               "damage" damage
               "armor" armor
               "info" info})

;; Command handling

(defn execute
  "Execute a command that is passed to us."
  [input]
  (try (let [[command & args] (.split input " +")]
         (apply (commands command) args))
       (catch Exception e
         (.printStackTrace e (new java.io.PrintWriter *err*))
         "You can't do that!")))
