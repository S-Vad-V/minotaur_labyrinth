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

;; (def finishList (finishes/add-finishes "resources/finish/"))
;; (defn finish
;;   []
;;   ;; (println @player/*current-room*)
;;   ;; :when (= v @player/*current-room*)\
;;   (def b false)
;;   (println (str @player/*current-room*))
;;   ;; tln finishList)
;;   (doseq [[k v] finishList] (println (str (ref v)))))

;; Command functions

(defn look
  "Get a description of the surrounding environs and its contents."
  []
;;   (Thread/sleep 5000)

  (let [exitn ((:exits @player/*current-room*) (keyword "north"))
        exite ((:exits @player/*current-room*) (keyword "east"))
        exitw ((:exits @player/*current-room*) (keyword "west"))
        exitso ((:exits @player/*current-room*) (keyword "south"))

        targetn (@rooms/rooms exitn)
        targete (@rooms/rooms exite)
        targetw (@rooms/rooms exitw)
        targetso (@rooms/rooms exitso)]

    (if (= (:desc @player/*current-room*) "You are in the Safe Zone! Congratulations, you are winner!") ((println(:desc @player/*current-room*)) (Thread/sleep 5000) (System/exit 0)))

    (if (and (and exitn exite) (and exitw exitso))
      (do
        (str
         (:desc @player/*current-room*) "Ways from current room: " (keys @(:exits @player/*current-room*)) "\n"
         (str/join "\n" (map #(str " There is " % " here.\n") @(:items @player/*current-room*)))
         (str/join "\n" (map #(str "There is players " % " here.\n")  @(:inhabitants @player/*current-room*)))
         "\nExits from north way: " (keys @(:exits targetn))
         (str/join "\n" (map #(str " There is " % " here.\n") @(:items targetn)))
         (str/join "\n" (map #(str " There is players " % " here.\n")  @(:inhabitants targetn)))
         "\nExits from east way: " (keys @(:exits targete))
         (str/join "\n" (map #(str " There is " % " here.\n") @(:items targete)))
         (str/join "\n" (map #(str " There is players " % " here.\n")  @(:inhabitants targete)))
         "\nExits from west way: " (keys @(:exits targetw))
         (str/join "\n" (map #(str " There is " % " here.\n") @(:items targetw)))
         (str/join "\n" (map #(str " There is players " % " here.\n")  @(:inhabitants targetw)))
         "\nExits from south way: " (keys @(:exits targetso))
         (str/join "\n" (map #(str "There is " % " here.\n") @(:items targetso)))
         (str/join "\n" (map #(str " There is players " % " here.\n")  @(:inhabitants targetso)))))


      (if (and (and exitn exite)  exitw)

        (do
          (str
           (:desc @player/*current-room*) "Ways from current room: " (keys @(:exits @player/*current-room*))    "\n"
           (str/join "\n" (map #(str " There is " % " here.\n")
                               @(:items @player/*current-room*))) (str/join "\n" (map #(str "There is players " % " here.\n")  @(:inhabitants @player/*current-room*)))
           "\nExits from north way: " (keys @(:exits targetn))
           (str/join "\n" (map #(str "There is " % " here.\n")
                               @(:items targetn))) (str/join "\n" (map #(str " There is players " % " here.\n")  @(:inhabitants targetn)))
           "\nExits from east way: " (keys @(:exits targete)) (str/join "\n" (map #(str "There is " % " here.\n")
                                                                                  @(:items targete))) (str/join "\n" (map #(str " There is players " % " here.\n")  @(:inhabitants targete)))
           "\nExits from west: " (keys @(:exits targetw)) (str/join "\n" (map #(str "There is " % " here.\n")
                                                                              @(:items targetw))) (str/join "\n" (map #(str " There is players " % " here.\n")  @(:inhabitants targetw)))))

        (if (and (and exitn exite)  exitso)
          (do
            (str
             (:desc @player/*current-room*) "Ways from current room: " (keys @(:exits @player/*current-room*))
             (str/join "\n" (map #(str "There is " % " here.\n") @(:items @player/*current-room*)))
             (str/join "\n" (map #(str "There is players " % " here.\n")  @(:inhabitants @player/*current-room*)))
             "\nExits from north way: " (keys @(:exits targetn)) (str/join "\n" (map #(str "There is " % " here.\n") @(:items targetn)))
             (str/join "\n" (map #(str " There is players " % " here.\n")  @(:inhabitants targetn)))
             "\nExits from east way: " (keys @(:exits targete)) (str/join "\n" (map #(str "There is " % " here.\n") @(:items targete)))
             (str/join "\n" (map #(str " There is players " % " here.\n")  @(:inhabitants targete)))
             "\nExits from south way: " (keys @(:exits targetso)) (str/join "\n" (map #(str "There is " % " here.\n") @(:items targetso)))
             (str/join "\n" (map #(str " There is players " % " here.\n")  @(:inhabitants targetso)))))

          (if (and (and exitso exitw)  exite)

            (do
              (str
               (:desc @player/*current-room*) "Ways from current room: " (keys @(:exits @player/*current-room*))
               (str/join "\n" (map #(str "There is " % " here.\n") @(:items @player/*current-room*)))
               (str/join "\n" (map #(str "There is players " % " here.\n")  @(:inhabitants @player/*current-room*)))
               "\nExits from west way: " (keys @(:exits targetw)) (str/join "\n" (map #(str "There is " % " here.\n") @(:items targetw)))
               (str/join "\n" (map #(str " There is players " % " here.\n")  @(:inhabitants targetw)))
               "\nExits from east way: " (keys @(:exits targete)) (str/join "\n" (map #(str "There is " % " here.\n") @(:items targete)))
               (str/join "\n" (map #(str " There is players " % " here.\n")  @(:inhabitants targete)))
               "\nExits from south way: " (keys @(:exits targetso)) (str/join "\n" (map #(str "There is " % " here.\n") @(:items targetso)))
               (str/join "\n" (map #(str " There is players " % " here.\n")  @(:inhabitants targetso)))))


            (if (and (and exitn exitw)  exitso)
              (do
                (str
                 (:desc @player/*current-room*) "Ways from current room: " (keys @(:exits @player/*current-room*))
                 (str/join "\n" (map #(str "There is " % " here.\n") @(:items @player/*current-room*)))
                 (str/join "\n" (map #(str "There is players " % " here.\n")  @(:inhabitants @player/*current-room*)))
                 "\nExits from west way: " (keys @(:exits targetw))  (str/join "\n" (map #(str "There is " % " here.\n") @(:items targetw)))
                 (str/join "\n" (map #(str " There is players " % " here.\n")  @(:inhabitants targetw)))
                 "\nExits from north way: " (keys @(:exits targetn)) (str/join "\n" (map #(str "There is " % " here.\n") @(:items targetn)))
                 (str/join "\n" (map #(str " There is players " % " here.\n")  @(:inhabitants targetn)))
                 "\nExits from south way: " (keys @(:exits targetso)) (str/join "\n" (map #(str "There is " % " here.\n") @(:items targetso)))
                 (str/join "\n" (map #(str " There is players " % " here.\n")  @(:inhabitants targetso)))))

              (if (and exitn  exite)

                (do
                  (str
                   (:desc @player/*current-room*) "Ways from current room: " (keys @(:exits @player/*current-room*))
                   (str/join "\n" (map #(str "There is " % " here.\n") @(:items @player/*current-room*)))
                   (str/join "\n" (map #(str "There is players " % " here.\n")  @(:inhabitants @player/*current-room*)))
                   "\nExits from east way: " (keys @(:exits targete)) (str/join "\n" (map #(str "There is " % " here.\n") @(:items targete)))
                   (str/join "\n" (map #(str " There is players " % " here.\n")  @(:inhabitants targete)))
                   "\nExits from north way: " (keys @(:exits targetn)) (str/join "\n" (map #(str "There is " % " here.\n") @(:items targetn)))
                   (str/join "\n" (map #(str " There is players " % " here.\n")  @(:inhabitants targetn)))))

                (if (and exitn  exitso)

                  (do
                    (str
                     (:desc @player/*current-room*) "Ways from current room: " (keys @(:exits @player/*current-room*))
                     (str/join "\n" (map #(str "There is " % " here.\n") @(:items @player/*current-room*)))
                     (str/join "\n" (map #(str "There is players " % " here.\n")  @(:inhabitants @player/*current-room*)))
                     "\nExits from south way: " (keys @(:exits targetso))  (str/join "\n" (map #(str "There is " % " here.\n") @(:items targetso)))
                     (str/join "\n" (map #(str "There is players " % " here.\n")  @(:inhabitants targetso)))
                     "\nExits from north way: " (keys @(:exits targetn)) (str/join "\n" (map #(str "There is " % " here.\n") @(:items targetn)))
                     (str/join "\n" (map #(str "There is players " % " here.\n")  @(:inhabitants targetn)))))



                  (if (and exitn  exitw)
                    (do
                      (str
                       (:desc @player/*current-room*) "Ways from current room: " (keys @(:exits @player/*current-room*))
                       (str/join "\n" (map #(str "There is " % " here.\n") @(:items @player/*current-room*)))
                       (str/join "\n" (map #(str "There is players " % " here.\n")  @(:inhabitants @player/*current-room*)))
                       "\nExits from west way: " (keys @(:exits targetw))  (str/join "\n" (map #(str "There is " % " here.\n") @(:items targetw)))
                       (str/join "\n" (map #(str " There is players " % " here.\n")  @(:inhabitants targetw)))
                       "\nExits from north way: " (keys @(:exits targetn)) (str/join "\n" (map #(str "There is " % " here.\n") @(:items targetn)))
                       (str/join "\n" (map #(str " There is players " % " here.\n")  @(:inhabitants targetn)))))


                    (if  (and exite  exitso)

                      (do
                        (str
                         (:desc @player/*current-room*) "Ways from current room: " (keys @(:exits @player/*current-room*))
                         (str/join "\n" (map #(str "There is " % " here.\n") @(:items @player/*current-room*)))
                         (str/join "\n" (map #(str "There is players " % " here.\n")  @(:inhabitants @player/*current-room*)))
                         "\nExits from east way: " (keys @(:exits targete)) (str/join "\n" (map #(str "There is " % " here.\n") @(:items targete)))
                         (str/join "\n" (map #(str " There is players " % " here.\n")  @(:inhabitants targete)))
                         "\nExits from south way: " (keys @(:exits targetso)) (str/join "\n" (map #(str "There is " % " here.\n") @(:items targetso)))
                         (str/join "\n" (map #(str " There is players " % " here.\n")  @(:inhabitants targetso)))))

                      (if (and exite  exitw)

                        (do
                          (str
                           (:desc @player/*current-room*) "Ways from current room: " (keys @(:exits @player/*current-room*))
                           (str/join "\n" (map #(str "There is " % " here.\n") @(:items @player/*current-room*)))
                           (str/join "\n" (map #(str "There is players " % " here.\n")  @(:inhabitants @player/*current-room*)))
                           "\nExits from east way: " (keys @(:exits targete)) (str/join "\n" (map #(str "There is " % " here.\n") @(:items targete)))
                           (str/join "\n" (map #(str " There is players " % " here.\n")  @(:inhabitants targete)))
                           "\nExits from west way: " (keys @(:exits targetw)) (str/join "\n" (map #(str "There is " % " here.\n")	@(:items targetw)))
                           (str/join "\n" (map #(str " There is players " % " here.\n")  @(:inhabitants targetw)))))

                        (if (and exitso  exitw)

                          (do
                            (str
                             (:desc @player/*current-room*) "Ways from current room: " (keys @(:exits @player/*current-room*))
                             (str/join "\n" (map #(str "There is " % " here.\n") @(:items @player/*current-room*)))
                             (str/join "\n" (map #(str "There is players " % " here.\n")  @(:inhabitants @player/*current-room*)))
                             "\nExits from south way: " (keys @(:exits targetso))  (str/join "\n" (map #(str "There is " % " here.\n") @(:items targetso)))
                             (str/join "\n" (map #(str " There is players " % " here.\n")  @(:inhabitants targetso)))
                             "\nExits from west way: " (keys @(:exits targetw)) (str/join "\n" (map #(str "There is " % " here.\n") @(:items targetw)))
                             (str/join "\n" (map #(str " There is players " % " here.\n")  @(:inhabitants targetw)))))

                          (if exitn

                            (do
                              (str
                               (:desc @player/*current-room*) "Way from current room: " (keys @(:exits @player/*current-room*))
                               (str/join "\n" (map #(str "There is " % " here.\n") @(:items @player/*current-room*)))
                               (str/join "\n" (map #(str "There is players " % " here.\n")  @(:inhabitants @player/*current-room*)))
                               "\nExits from north way: " (keys @(:exits targetn)) (str/join "\n" (map #(str "There is " % " here.\n") @(:items targetn)))
                               (str/join "\n" (map #(str " There is players " % " here.\n")  @(:inhabitants targetn)))))


                            (if exite

                              (do
                                (str
                                 (:desc @player/*current-room*) "Way from current room: " (keys @(:exits @player/*current-room*))
                                 (str/join "\n" (map #(str "There is " % " here.\n") @(:items @player/*current-room*)))
                                 (str/join "\n" (map #(str "There is players " % " here.\n")  @(:inhabitants @player/*current-room*)))
                                 "\nExits from east way: " (keys @(:exits targete)) (str/join "\n" (map #(str "There is " % " here.\n") @(:items targete)))
                                 (str/join "\n" (map #(str " There is players " % " here.\n")  @(:inhabitants targete)))))

                              (if exitw

                                (do
                                  (str

                                   (:desc @player/*current-room*) "Way from current room: " (keys @(:exits @player/*current-room*))
                                   (str/join "\n" (map #(str "There is " % " here.\n") @(:items @player/*current-room*)))
                                   (str/join "\n" (map #(str "There is players " % " here.\n")  @(:inhabitants @player/*current-room*)))
                                   "\nExits from west way: " (keys @(:exits targetw)) (str/join "\n" (map #(str "There is " % " here.\n") @(:items targetw)))
                                   (str/join "\n" (map #(str " There is players " % " here.\n")  @(:inhabitants targetw)))))

                                (if exitso

                                  (do
                                    (str
                                     (:desc @player/*current-room*) "Way from current room: " (keys @(:exits @player/*current-room*))
                                     (str/join "\n" (map #(str "There is " % " here.\n") @(:items @player/*current-room*)))
                                     (str/join "\n" (map #(str "There is players " % " here.\n")  @(:inhabitants @player/*current-room*)))
                                     "\nExits from south way: " (keys @(:exits targetso)) (str/join "\n" (map #(str "There is " % " here.\n") @(:items targetso)))
                                     (str/join "\n" (map #(str " There is players " % " here.\n")  @(:inhabitants targetso)))))))))))))))))))))

)
  
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

(defn fight
  []
    (doseq [inhabitant
     (disj @(:inhabitants @player/*current-room*)
                             player/*name*)]
      (binding [*out* (player/streams inhabitant)]
      (println (str " You have been damaged. Your health has dropped."))
      (dosync
(def t (- (peek @player/*health*) @player/*damage*))
(alter player/*health* conj t  ))
(println(str "Your health " (peek @player/*health*)))
        )
        )
    (str "You hit another player " )
    )

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

;set_health
;set_damage

(defn health
  "See your hp."
  []
  (str "Your health: "
       (str/join "" (str (peek @player/*health*)))))

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

(defn say
  "Say something out loud and maybe someone will hear you."
  [& words]

  (let 			[
  					message (str/join " " words)

  					exitn ((:exits @player/*current-room*) (keyword "north"))
  					exite ((:exits @player/*current-room*) (keyword "east"))
  					exitw ((:exits @player/*current-room*) (keyword "west"))
  					exitso ((:exits @player/*current-room*) (keyword "south"))

  				  	targetn(@rooms/rooms exitn)
  					targete(@rooms/rooms exite)
  					targetw(@rooms/rooms exitw)
  					targetso(@rooms/rooms exitso)
  			]

     (do (if exitn
     (doseq [inhabitant (disj @(:inhabitants targetn) player/*name*)]
      (binding [*out* (player/streams inhabitant)]
        (println player/*name* " says from south: " message " " player/prompt)
        (println player/prompt)))   )

     (do (if exite
     (doseq [inhabitant (disj @(:inhabitants targete) player/*name*)]
      (binding [*out* (player/streams inhabitant)]
        (println player/*name* " says from west: " message " " player/prompt)
        (println player/prompt)))   )

     (do (if exitw
     (doseq [inhabitant (disj @(:inhabitants targetw) player/*name*)]
      (binding [*out* (player/streams inhabitant)]
        (println player/*name* " says from east: " message " " player/prompt)
        (println player/prompt)))   )

     (do (if exitso
     (doseq [inhabitant (disj @(:inhabitants targetso) player/*name*)]
      (binding [*out* (player/streams inhabitant)]
        (println player/*name* " says from north: " message " " player/prompt)
        (println player/prompt)))   )


     (doseq [inhabitant (disj @(:inhabitants @player/*current-room*) player/*name*)]
      (binding [*out* (player/streams inhabitant)]
        (println player/*name* " says : " message " " player/prompt)
        (println player/prompt)))
    ))))

    (str "You said: " message)
    ))


(defn help
  "Show available commands and what they do."
  []
  (str/join "\n" (map #(str (key %) ": " (:doc (meta (val %))))
                      (dissoc (ns-publics 'mire.commands)
                              'execute 'commands))))


(defn exit
  []
  "Drop all inventory and remove player from room and player list."
  (dosync
   (doseq [item @player/*inventory*]
     (discard item))
   (commute player/streams dissoc player/*name*)
   (commute (:inhabitants @player/*current-room*)
            disj player/*name*))
  )

;; Command data

(def commands {"move" move,
               "north" (fn [] (move :north)),
               "south" (fn [] (move :south)),
               "east" (fn [] (move :east)),
               "west" (fn [] (move :west)),
               "grab" grab
               "discard" discard
               "inventory" inventory
               "health" health
               "damage" damage
               "armor" armor
               "info" info
               "look" look
               "say" say
               "fight" fight
         	     "exit" exit
               "help" help})

;; Command handling

(defn execute
  "Execute a command that is passed to us."
  [input]
  (try (let [[command & args] (.split input " +")]
         (apply (commands command) args))
       (catch Exception e
         (.printStackTrace e (new java.io.PrintWriter *err*))
         "You can't do that!")))
