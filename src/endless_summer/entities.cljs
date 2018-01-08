(ns endless-summer.entities)

(defn ground-plane
  ([]
   {:assets []
    :entities [[:a-plane {:rotation "-90 0 0"
                          :width "4"
                          :height "4"
                          :color "#7BC8A4"}]]})
  ([name position]
   (let [id (str name "_texture")]
     {:assets [[:img {:id id
                      :src (str "entities/" name "/texture.png")}]]
      :entities [[:a-plane {:rotation "-90 0 0"
                            :position (clojure.string/join " " position)
                            :width 4
                            :height 4
                            :material (str "src: #" id ";"
                                           "roughness: 0.99;"
                                           "metalness: 0.01;")}]]})))

(defn ground-plane-array
  [name position delta count]
  (mapv #(ground-plane name (mapv + position (mapv (partial * %) delta))) (range count)))

(defn assets-set [entities]
  "Creates a set of the assets used by given entities."
  (reduce #(apply conj %1 (:assets %2)) #{} entities))

(defn assets-vector [entities]
  (apply conj [:a-assets] (vec (assets-set entities))))

(defn entities-vector [entities]
  "Creates a vector of all the entities in a list of them."
  (reduce #(apply conj %1 (:entities %2)) [] entities))

(defn build-scene [entities]
  "Builds a scene vector out of a list of entity objects.
Entities take the form of {:assets :entities}.
Assets are treated as a set to prevent double loading"
  (let [assets (assets-vector entities)
        ents (entities-vector entities)]
    (apply conj [assets] ents)))

(defn get-postion [entity]
  (-> entity
      :entities
      first
      second
      :position
      (clojure.string/split #" ")))

(defn linear-array [entity delta count])

(def sand (ground-plane "simple_sand" "0 0 -4"))

(def sands
  (apply conj
         (ground-plane-array "simple_sand" [-4 0.1 -4] [4 0 0] 6)
         (ground-plane-array "patterned_sand" [-4 0.1 -8] [4 0 0] 6)))

(defn sands-scene []
  (build-scene sands))
