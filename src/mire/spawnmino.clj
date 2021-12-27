(ns mire.spawnmino)

(def spawnmino (ref {}))

(defn load-spawnmino [spawnmino file]
  (let [spawnmino (read-string (slurp (.getAbsolutePath file)))]
    (conj spawnmino
          {(keyword (.getName file))
           {:name (keyword (.getName file))
            :desc (:desc spawnmino)
            :exits (ref (:exits spawnmino))
            :items (ref (or (:items spawnmino) #{}))
            :inhabitants (ref #{})}})))

(defn load-spawnmino
  "Given a dir, return a map with an entry corresponding to each file
  in it. Files should be maps containing spawnmino data."
  [spawnmino dir]
  (dosync
   (reduce load-spawnmino spawnmino
           (.listFiles (java.io.File. dir)))))

(defn add-spawnmino
  "Look through all the files in a dir for files describing spawnmino and add
  them to the mire.spawnmino/spawnmino map."
  [dir]
  (dosync
   (alter spawnmino load-spawnmino dir)))