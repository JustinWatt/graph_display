(ns graph-display.core
  (:require [clojure.math.combinatorics :as c]
            [loom
             [alg :as la]
             [graph :as graph]
             [io  :as io]]))


(def dogs
  [:Fran
   :Henry
   :NewGuy
   :BT
   :Ellie
   :Shaw
   :Rosy
   :Rogue
   :Kevin])

(defn dog-pairs [dogs]
  (c/combinations dogs 2))

(defn create-record [acc [d1 d2]]
  (let [weight (rand-nth (range -1 6))]
    (if (not= weight 0)
      (conj acc {:id-a d1 :id-b d2 :weight weight})
      acc)))

(defn create-db [dogs]
  (reduce create-record [] (dog-pairs dogs)))

(def records
  [{:id-a :Fran   :id-b :Henry  :weight 3}
   {:id-a :Fran   :id-b :NewGuy :weight 3}
   {:id-a :Fran   :id-b :BT     :weight 2}
   {:id-a :Fran   :id-b :Ellie  :weight 5}
   {:id-a :Fran   :id-b :Shaw   :weight 3}
   {:id-a :Fran   :id-b :Rosy   :weight -1}
   {:id-a :Henry  :id-b :NewGuy :weight 2}
   {:id-a :Henry  :id-b :BT     :weight -1}
   {:id-a :Henry  :id-b :Ellie  :weight 1}
   {:id-a :Henry  :id-b :Shaw   :weight 5}
   {:id-a :Henry  :id-b :Rosy   :weight -1}
   {:id-a :Henry  :id-b :Rogue  :weight 2}
   {:id-a :Henry  :id-b :Kevin  :weight -1}
   {:id-a :NewGuy :id-b :BT     :weight -1}
   {:id-a :NewGuy :id-b :Ellie  :weight 5}
   {:id-a :NewGuy :id-b :Rosy   :weight 2}
   {:id-a :NewGuy :id-b :Rogue  :weight 2}
   {:id-a :BT     :id-b :Ellie  :weight 2}
   {:id-a :BT     :id-b :Rosy   :weight -1}
   {:id-a :Ellie  :id-b :Shaw   :weight -1}
   {:id-a :Ellie  :id-b :Rogue  :weight 3}
   {:id-a :Ellie  :id-b :Kevin  :weight 4}
   {:id-a :Shaw   :id-b :Rosy   :weight 3}
   {:id-a :Shaw   :id-b :Kevin  :weight 4}
   {:id-a :Rosy   :id-b :Rogue  :weight 5}
   {:id-a :Rogue  :id-b :Kevin  :weight 1}])

(defn incompatible-dogs [group]
  (->> group
       (filter (comp neg? :weight))
       (map (comp set vals #(select-keys % [:id-a :id-b])))))

(defn create-nodes [db]
  (mapv (comp vec vals) db))

(def g2 (apply graph/weighted-graph (create-nodes records)))

(def g3 (apply graph/weighted-graph (create-nodes (filter (comp pos? :weight) records))))

(defn incompatible [group]
  (->> group
       (filter #((comp neg? second) %))
       (map first)
       set))

(defn suggestion-list [graph nodes]
  (let [neighbors (mapcat #(% (:adj graph)) nodes)
        no-fly (incompatible neighbors)]
    (->> neighbors
         (filter #(>= (second %) 0))
         (map first)
         (map #(% (:adj graph)))
         (reduce #(merge-with + %1 %2) {})
         (filter #((complement contains?) no-fly (first %)))
         (filter #((complement contains?) nodes  (first %)))
         (sort-by second)
         reverse
         vec)))

(defn render-graph-images [graph]
  (->> graph
       la/maximal-cliques
       (map #(c/combinations % 2))
       (map #(apply graph/weighted-graph %))
       (map io/view)
       dorun))
