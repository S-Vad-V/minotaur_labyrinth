(ns mire.minotaur
	(:require [Rooms :as rooms]
			  [mire.player :as player]))

(def ^:dynamic *current-room*)

(defn- move-between-refs
  "Move one instance of obj between from and to. Must call in a transaction."
  [obj from to]
  (alter from disj obj)
  (alter to conj obj))

(defn move
	(let [arr1 (keys @(:exits @minotaur/*current-room*))
		  arr2 (keys @(:exits @rooms/rooms arr1))
		  arr3 (keys @(:exits @rooms/rooms arr2))
		  pl1  (:inhabitants arr1)
		  pl2  (:inhabitants arr2)
		  pl3  (:inhabitants arr3)]
		(if pl1
			(do (move-between-refs minotaur
                            (:inhabitants *current-room*)
                            (:inhabitants pl1))
				(dosync
				(comment Нужна функция смерти игрока, пока только удаляем из комнаты)
				(commute (:inhabitants pl1) disj player/*name*)))
		 (if pl2
			(do (move-between-refs minotaur
                            (:inhabitants *current-room*)
                            (:inhabitants pl2))
				(dosync
				(comment Нужна функция смерти игрока, пока только удаляем из комнаты)
				(commute (:inhabitants pl1) disj player/*name*))))
		 (if pl3
			(do (move-between-refs minotaur
							(:inhabitants *current-room*)
							(:inhabitants pl2)))))))




	
	


	