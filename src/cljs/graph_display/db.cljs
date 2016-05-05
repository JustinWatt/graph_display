(ns graph-display.db)

(def default-db
  {:group #{}
   :graph
   {:nodeset #{:Ellie :Shaw :Rogue :NewGuy :Rosy :BT :Henry :Fran :Kevin},
    :adj
    {:Fran {:Henry 3, :NewGuy 3, :BT 2, :Ellie 5, :Shaw 3, :Rosy -1},
     :Henry {:Fran 3,:NewGuy 2,:BT -1,:Ellie 1,:Shaw 5,:Rosy -1,:Rogue 2,:Kevin -1},
     :NewGuy {:Fran 3, :Henry 2, :BT -1, :Ellie 5, :Rosy 2, :Rogue 2},
     :BT {:Fran 2, :Henry -1, :NewGuy -1, :Ellie 2, :Rosy -1},
     :Ellie {:Fran 5, :Henry 1, :NewGuy 5, :BT 2, :Shaw -1, :Rogue 3, :Kevin 4},
     :Shaw {:Fran 3, :Henry 5, :Ellie -1, :Rosy 3, :Kevin 4},
     :Rosy {:Fran -1, :Henry -1, :NewGuy 2, :BT -1, :Shaw 3, :Rogue 5},
     :Rogue {:Henry 2, :NewGuy 2, :Ellie 3, :Rosy 5, :Kevin 1},
     :Kevin {:Henry -1, :Ellie 4, :Shaw 4, :Rogue 1}}}})
