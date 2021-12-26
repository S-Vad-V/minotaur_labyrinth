(ns mire.minotaur
	(:require [Rooms :as rooms]
			  [mire.player :as player]))

(def ^:dynamic *current-room*)

(defn- move-between-refs
  "Move one instance of obj between from and to. Must call in a transaction."
  [obj from to]
  (alter from disj obj)
  (alter to conj obj))

(defn randMove
	(let [direct (rand-int 4)]
		(if direct == 0
		(do (move north))
		(if direct == 1
		(do (move east))
		(if direct == 2
		(do (move south))
		(do (move west)))))))

(defn move
  "\"♬ We gotta get out of this place... ♪\" Give a direction."
  [direction]
  (dosync
   (let [target-name ((:exits @minotaur/*current-room*) (keyword direction))
         target (@rooms/rooms target-name)]
     (if target
       (do
         (move-between-refs minotaur
                            (:inhabitants @minotaur/*current-room*)
                            (:inhabitants target))
         (ref-set minotaur/*current-room* target)
         )
       "You can't go that way."))))



	
	


	