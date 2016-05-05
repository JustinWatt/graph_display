(ns graph-display.handlers
    (:require [re-frame.core :as re-frame]
              [graph-display.db :as db]))

(re-frame/register-handler
 :initialize-db
 (fn  [_ _]
   db/default-db))

(re-frame/register-handler
 :set-active-panel
 (fn [db [_ active-panel]]
   (assoc db :active-panel active-panel)))

(defn update-vals [m v f]
  (reduce #(update-in %1 [%2] f) m v))

(re-frame/register-handler
 :add-to-group
 (fn [db [_ dog]]
   (update db :group conj dog)))
