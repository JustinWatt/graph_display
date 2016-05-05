(ns graph-display.subs
    (:require-macros [reagent.ratom :refer [reaction]])
    (:require [re-frame.core :as re-frame]))

(re-frame/register-sub
 :active-panel
 (fn [db _]
   (reaction (:active-panel @db))))

(re-frame/register-sub
 :graph
 (fn [db]
   (reaction (:graph @db))))

(re-frame/register-sub
 :group
 (fn [db]
   (reaction (:group @db))))

(defn incompatible [group]
  (->> group
       (filter #((comp neg? second) %))
       (map first)
       set))

(defn filter-negative [m]
  (into {} (filter (fn [[k v]] (>= v 0))) m))

(defn suggestion-list [graph nodes]
  (let [neighbors (mapcat #(% (:adj graph)) nodes)
        no-fly (incompatible neighbors)]
    (->> neighbors
         (filter #(> (second %) 0))
         (map (comp filter-negative #(% (:adj graph)) first))
         (reduce #(merge-with + %1 %2) {})
         (filter #((complement contains?) (clojure.set/union no-fly nodes) (first %)))
         (sort-by second)
         reverse
         vec)))

(re-frame/register-sub
 :suggestion-list
 (fn [db]
   (reaction (suggestion-list (:graph @db) (:group @db)))))

