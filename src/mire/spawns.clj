(ns mire.spawns)

(def spawns (ref {}))

(defn load-spawn [spawns file]
  (let [spawn (read-string (slurp (.getAbsolutePath file)))]
    (conj spawns
          {(keyword (.getName file))
           {:name (keyword (.getName file))
            :desc (:desc spawn)
            :exits (ref (:exits spawn))
            :items (ref (or (:items spawn) #{}))
            :inhabitants (ref #{})}})))

(defn load-spawns
  "Given a dir, return a map with an entry corresponding to each file
  in it. Files should be maps containing spawn data."
  [spawns dir]
  (dosync
   (reduce load-spawn spawns
           (.listFiles (java.io.File. dir)))))

(defn add-spawns
  "Look through all the files in a dir for files describing spawns and add
  them to the mire.spawns/spawns map."
  [dir]
  (dosync
   (alter spawns load-spawns dir)))