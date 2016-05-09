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

(defn find-neighbors [graph node]
  (node (:adj graph)))

(defn suggestion-list [graph nodes]
  (let [neighbors (mapcat (partial find-neighbors graph) nodes)
        no-fly    (incompatible neighbors)]
    (->> neighbors
         (filter #(> (second %) 0))
         (map (comp filter-negative (partial find-neighbors graph) first))
         (reduce #(merge-with + %1 %2) {})
         (filter #((complement contains?) (clojure.set/union no-fly nodes) (first %)))
         (sort-by second)
         reverse
         vec)))

(re-frame/register-sub
 :suggestion-list
 (fn [db]
   (reaction (suggestion-list (:graph @db) (:group @db)))))


(defn key-by-field [k coll]
  (into {} (map #((juxt k identity) %) coll)))

(defn index-nodes [nodeset]
  (key-by-field :name (map-indexed (fn [i d] {:index i :name d}) nodeset)))

(defn link [node-index adj d1 d2]
  (let [source (:index (d1 node-index))
        target (:index (d2 node-index))
        value (->> adj d1 d2)]
    {:source source :target target :value value
     :source-name d1 :target-name d2}))

(defn generate-links [graph node]
  (let [node-index (index-nodes (:nodeset graph))
        adj        (:adj graph)]
    (mapv #(link node-index adj node %) (keys (node adj)))))

(re-frame/register-sub
 :graph-links
 (fn [db]
   (let [graph (reaction (:graph @db))
         nodes (reaction (get-in @db [:graph :nodeset]))]
     (reaction (mapcat #(generate-links @graph %) @nodes)))))

(re-frame/register-sub
 :graph-nodes
 (fn [db]
   (let [nodes (reaction (get-in @db [:graph :nodeset]))]
     (reaction (vals (index-nodes @nodes))))))
