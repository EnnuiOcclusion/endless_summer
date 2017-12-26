(ns endless-summer-aframe.core
    (:require [reagent.core :as reagent :refer [atom]]))

(enable-console-print!)

(println "This text is printed from src/endless-summer-aframe/core.cljs. Go ahead and edit it and see reloading in action.")

;; define your app data so that it doesn't get over-written on reload

(defonce app-state (atom {:scene []}))

(def init-scene
  [:a-scene
   [:a-box {:position "-1 2.5 -3"
            :rotation "0 45 0"
            :color "#4CC3D9"}]])

(defn hello-world []
  [:div
   (:scene @app-state)])

(reagent/render-component [hello-world]
                          (. js/document (getElementById "app")))

(defn on-js-reload []
  ;; optionally touch your app-state to force rerendering depending on
  ;; your application
  ;; (swap! app-state update-in [:__figwheel_counter] inc)

  (swap! app-state #(assoc % :scene init-scene))
)
