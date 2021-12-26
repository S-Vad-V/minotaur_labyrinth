(ns mire.minotaur
	(:require [mire.rooms :as rooms]
			  [mire.player :as player]))

(def ^:dynamic *current-room*)
(def ^:dynamic *name*)

(defn- move-between-refs
  "Move one instance of obj between from and to. Must call in a transaction."
  [obj from to]
  (alter from disj obj)
  (alter to conj obj))

(defn walk
  [direction]
  (dosync
   (let [target-name ((:exits @*current-room*) (keyword direction))
         target (@rooms/rooms target-name)]
     (if target
       (do
		 (let [victim ((:inhabitants target))]
		 (cond (= victim @player/*name*) (ref-set @player/*health* 0)))
         (move-between-refs *name*
                            (:inhabitants @*current-room*)
                            (:inhabitants target))
         (ref-set *current-room* target) 
       )))))

(defn randMove
	[]
	(let [direct (rand-int 4)]
		(if (= direct 0)
		(try (walk 'north'))
		(if (= direct 1)
		(try (walk 'east'))
		(if (= direct 2)
		(try (walk 'south'))
		(try (walk 'west')))))))


	
	


	