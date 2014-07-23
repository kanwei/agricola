(ns agricola.core
  (:gen-class))

(def actions
  [{:name "Build Room"
    :action (fn [state])
    :andor (fn [state])}
   {:name "Starting Player"
    :action (fn [state])
    :andor (fn [state])}
   {:name "Take 1 Grain"
    :action (fn [state] (update-in state [:grain] inc))}
   {:name "Plow 1 Field"
    :action (fn [state] (update-in state [:field] conj {}))}
   {:name "Day Laborer"
    :action (fn [state] (update-in state [:food] + 2))}
   {:name "3 Wood"
    :accumulate [3 :wood]}
   {:name "1 Clay"
    :accumulate [1 :clay]}
   {:name "1 Reed"
    :accumulate [1 :reed]}
   {:name "Fishing"
    :accumulate [1 :food]}
   {:name "Major/Minor Improvement"
    :stage 1}
   {:name "1 Sheep"
    :stage 1
    :accumulate [1 :sheep]}
   {:name "Sow/Bake Bread"
    :stage 1}
   {:name "Fences"
    :stage 1}
   {:name "1 Stone [1]"
    :stage 2
    :accumulate [2 :stone]}
   {:name "Family Growth"
    :stage 2}
   {:name "Renovation"
    :stage 2}
   {:name "Take 1 Vegetable"
    :stage 3}
   {:name "1 Wild Boar"
    :stage 3
    :accumulate [1 :boar]}
   {:name "1 Cattle"
    :stage 4
    :accumulate [1 :cattle]}
   {:name "1 Stone [2]"
    :stage 4
    :accumulate [1 :stone]}
   {:name "Plow 1 Field and/or Sow"
    :stage 5}
   {:name "Family Growth without space"
    :stage 5}
   {:name "Renovation Fences"
    :stage 6}
   ])

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println "Hello, World!"))
