(ns endless-summer-aframe.core
  (:require [reagent.core :as reagent :refer [atom]]))

(enable-console-print!)

(println "This text is printed from src/endless-summer-aframe/core.cljs. Go ahead and edit it and see reloading in action.")

;; define your app data so that it doesn't get over-written on reload

(defonce app-state (atom {:scene []}))

(defn kabob-case-to-camel-case
  [input]
  (let [words (clojure.string/split input #"-")
        [first-word & rest-words] words]
    (str first-word (apply str (map (partial clojure.string/capitalize) rest-words)))))

(defn map-to-string
  [input]
  (reduce-kv #(str (kabob-case-to-camel-case (name %2)) ": " %3 ";" %1) "" input))

(defn map-to-string-map
  [input]
  (reduce-kv #(assoc %1 (name %2) (map-to-string %3)) {} input))

(defn spin
  ([duration to]
   [:a-animation
    {:attribute "rotation"
     :dur duration
     :direction "alternate"
     :easing "linear"
     :to to
     :repeat "indefinite"}]))

(defn query-for-object
  [id]
  (js/document.querySelector id))

(defn object-entries
  "Returns a clj vector of the keys and values of a js object"
  [object]
  (js->clj (js/Object.entries object)))

(defn query-entries
  [id]
  (object-entries (query-for-object id)))

(defn cannon-world []
  (.. (query-for-object "#scene") -systems -physics -driver -world))

(defn user-click []
  (let [el (query-for-object "#player")]
    (el.body.applyImpulse
     (js/CANNON.Vec3. 0 30 0)
     (js/CANNON.Vec3. 0 0 0))))

(defn register-component
  [component-name init-function]
  (js/AFRAME.registerComponent component-name
                               #js {"init" init-function}))

(def init-scene
  [:a-scene#scene {:onClick user-click
                   :physics "debug: true;"}
   [:a-entity (map-to-string-map {:camera {:user-height 1.6}
                                  :look-controls {:enabled true}
                                  :wasd-controls {:enabled false}})]
   [:a-sphere#player {"dynamic-body" "mass: 5"
                      :position "0 4 -4"
                      :color "#4CC3D9"}
    #_(spin 10000 "0 360 0")]
   [:a-plane#ground {
                     "static-body" "shape: auto;"
                     :position "0 0.1 -4"
                     :rotation "-90 0 0"
                     :width "4"
                     :height "4"
                     :color "#7BC8A4"}
    #_[:a-torus {:position "0 10 4"
               :rotation "90 0 0"}]]
   [:a-entity (map-to-string-map {:environment {:preset "forest"}})]])

(defn hello-world []
  [:div
   (:scene @app-state)])

(defn setup-physics []
  (let [world (cannon-world)
        player (query-for-object "#player")
        ground (query-for-object "#ground")
        player-material (js/CANNON.Material.)
        ground-material (js/CANNON.Material.)
        contact-material (js/CANNON.ContactMaterial. ground-material player-material #js {:friction 0.0 :restitution 0.9})]
    (set! (.. ground -body -material) ground-material)
    (set! (.. player -body -material) player-material)
    (. world addContactMaterial contact-material)))

(defn init-app-state []
  (swap! app-state #(assoc % :scene init-scene)))

(defn initial-setup []
  (init-app-state)
  )

(defn post-reagent-render []
  (let [scene (query-for-object "#scene")]
    (. scene addEventListener "loaded" #(setup-physics))))

(defonce startup (initial-setup))

(reagent/render-component [hello-world]
                          (. js/document (getElementById "app")))
(reagent/after-render post-reagent-render)

(defn on-js-reload []
  ;; optionally touch your app-state to force rerendering depending on
  ;; your application
  ;; (swap! app-state update-in [:__figwheel_counter] inc)

  (init-app-state))
