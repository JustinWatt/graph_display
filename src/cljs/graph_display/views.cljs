(ns graph-display.views
  (:require [re-frame.core :as re-frame]
            [reagent.core :as reagent]))

;; home

(defn dog-icon [dog-name]
  [:img {:style {:margin 5 :height 60 :width 60 :border-radius 50 }
         :src (str "imgs/" (name dog-name) ".png")}])

(defn starter-list []
  (let [graph (re-frame/subscribe [:graph])]
    (fn []
      [:div
       [:h4 "Let's get started!"]
      [:ul
       (map-indexed
        (fn [i d]
          ^{:key i}
          [:li
           {:on-click #(re-frame/dispatch [:add-to-group d])
            :style {:list-style-type "none"}}
           [dog-icon (name d)]
           (name d)
           ]) (:nodeset @graph))]])))

(defn d3-inner [nodes links]
  (reagent/create-class
   {:reagent-render (fn [] [:div [:svg {:width 700 :height 800}]])

    :component-did-mount
    (fn []
      (let [d3data #js{:nodes (clj->js nodes)
                       :links (clj->js links)}
            color (js/d3.scale.category20)

            force (.. js/d3.layout
                      force
                      (charge -370)
                      (linkDistance 280)
                      (size (array 700 800)))

            link (.. js/d3
                     (select "svg")
                     (selectAll ".link")
                     (data (.-links d3data))
                     enter
                     (append "line")
                     (attr "class" "link")
                     (attr "stroke" (fn [d]
                                      (if (neg? (.-value d))
                                        "red" "black")))
                     (style "stroke-width" 4))

            node (.. js/d3
                     (select "svg")
                     (selectAll ".node")
                     (data (.-nodes d3data))
                     enter
                     (append "circle")
                     (attr "class" "node")
                     (attr "r" 20)
                     (attr "stroke" "black")
                     (attr "stroke-width" 3)
                     (style "fill" (fn [d] (color (.-group d))))
                     (call (.drag force)))]
        (js/console.log d3data)

        (.. force
            (nodes (.-nodes d3data))
            (links (.-links d3data))
            start)


        (.. node
            (append "image")
            (attr "xlink:href", "https://github.com/favicon.ico")
            (attr "x" -8)
            (attr "y" -8)
            (attr "width" 16)
            (attr "height" 16))

        (js/console.log d3data)

        (.. node
            (append "text")
            (attr "dx" 12)
            (attr "dy" ".35em")
            (text (fn [d]
                    (js/console.log d)
                    (.-name d))))
        (.. force
            (on "tick" (fn []
                         (.. link
                             (attr "x1" (fn [d] (.. d -source -x )))
                             (attr "y1" (fn [d] (.. d -source -y)))
                             (attr "x2" (fn [d] (.. d -target -x)))
                             (attr "y2" (fn [d] (.. d -target -y))))
                         (.. node
                             (attr "cx" (fn [d] (.-x d)))
                             (attr "cy" (fn [d] (.-y d)))))))))

    :component-did-update (fn [this]
                            (js/console.log "sup"))}))

(defn d3-outer []
  (let [nodes (re-frame/subscribe [:graph-nodes])
        links (re-frame/subscribe [:graph-links])]
    (fn []
      [d3-inner @nodes @links])))

(defn chosen-display [chosen]
  [:ul
   (map-indexed
    (fn [i d-name]
      ^{:key i}
      [:li
       {:on-click #(re-frame/dispatch [:remove-from-group d-name])
        :style {:list-style-type "none"}}
       [dog-icon d-name]
       (name d-name)]) chosen)])

(defn home-panel []
  (let [graph (re-frame/subscribe [:graph])
        group (re-frame/subscribe [:group])
        links (re-frame/subscribe [:graph-links])
        nodes (re-frame/subscribe [:graph-nodes])
        suggestion-list (re-frame/subscribe [:suggestion-list])]
    (fn []
      [:div.container {:style {:display "flex" :flex-direction "row"
                               :justify-content "flex-start"
                               :align-items "flex-start"}}
       [:div
        (if (empty? @group)
          [starter-list]
          [:div
           (if (empty? @suggestion-list)
             [:h4 "Empty"]
             [:h4 "Suggestions"])
           [:ul
           (map-indexed
            (fn [i [d-name value]]
              ^{:key i}
              [:li
               {:on-click #(re-frame/dispatch [:add-to-group d-name])
                :style {:list-style-type "none"}}
               [dog-icon d-name]
               (str (name d-name) " " value)]) @suggestion-list)]])]
       [:div {:style {:margin-left "100"}}
        [:h4 "Group!"]
        [chosen-display @group]]
       [d3-outer]])))

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
