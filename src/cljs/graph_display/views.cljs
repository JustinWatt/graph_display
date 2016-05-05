(ns graph-display.views
  (:require [re-frame.core :as re-frame]
            [reagent.core :as reagent]))

;; home

(defn starter-list []
  (let [graph (re-frame/subscribe [:graph])]
    (fn []
      [:ul
       (map-indexed
        (fn [i d]
          ^{:key i}
          [:li
           {:on-click #(re-frame/dispatch [:add-to-group d])} (name d)]) (:nodeset @graph))])))


(defn home-panel []
  (let [graph (re-frame/subscribe [:graph])
        group (re-frame/subscribe [:group])
        suggestion-list (re-frame/subscribe [:suggestion-list])
        ]
    (fn []
      [:div.container
       [:div
         (if (empty? @group)
           [starter-list]
           [:ul
            (map-indexed
            (fn [i d]
              ^{:key i}
              [:li
               {:on-click #(re-frame/dispatch [:add-to-group (first d)])}
               (str (name (first d)) " " (second d))]) @suggestion-list)])]
       [:div (str @group)]])))

;; about

(defn about-panel []
  (fn []
    [:div "This is the About Page."
     [:div [:a {:href "#/"} "go to Home Page"]]]))

;; main

(defmulti panels identity)
(defmethod panels :home-panel [] [home-panel])
(defmethod panels :about-panel [] [about-panel])
(defmethod panels :default [] [:div])

(defn main-panel []
  (let [active-panel (re-frame/subscribe [:active-panel])]
    (fn []
      (panels @active-panel))))
