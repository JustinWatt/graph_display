(ns graph-display.core
    (:require [reagent.core :as reagent]
              [re-frame.core :as re-frame]
              [cljsjs.d3]
              [graph-display.handlers]
              [graph-display.subs]
              [graph-display.routes :as routes]
              [graph-display.views :as views]
              [graph-display.config :as config]))

(when config/debug?
  (println "dev mode"))

(defn mount-root []
  (reagent/render [views/main-panel]
                  (.getElementById js/document "app")))

(defn ^:export init []
  (routes/app-routes)
  (re-frame/dispatch-sync [:initialize-db])
  (mount-root))
