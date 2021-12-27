(ns mire.mSpawns)

(def mSpawn (ref {}))

(defn load-spawn [mSpawn file]
  (let [spawn (read-string (slurp (.getAbsolutePath file)))]
    (conj mSpawn
          {(keyword (.getName file))
           {:name (keyword (.getName file))
            :desc (:desc spawn)
            :exits (ref (:exits spawn))
            :items (ref (or (:items spawn) #{}))
            :inhabitants (ref #{})}})))

(defn load-mSpawn
  "Given a dir, return a map with an entry corresponding to each file
  in it. Files should be maps containing spawn data."
  [mSpawn dir]
  (dosync
   (reduce load-spawn mSpawn
           (.listFiles (java.io.File. dir)))))

(defn add-mSpawn
  "Look through all the files in a dir for files describing mSpawn and add
  them to the mire.mSpawn/mSpawn map."
  [dir]
  (dosync
   (alter mSpawn load-mSpawn dir)))