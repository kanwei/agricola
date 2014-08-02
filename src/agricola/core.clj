(ns agricola.core
  (require [criterium.core :refer :all])
  (:gen-class))

(defn current-player [s]
  (:current-player s))

(def actions
  [{:name "Build Room"
    :req (fn [s] (>= (get-in s [(current-player s) :wood]) 2))
    :action (fn [s])
    :andor (fn [s])}
   {:name "Starting Player"
    :action (fn [s] s)
    :andor (fn [s])}
   {:name "Take 1 Grain"
    :action (fn [s] (update-in s [:players (current-player s) :grain] inc))}
   {:name "Plow 1 Field"
    :action (fn [s] (update-in s [:players (current-player s) :field] conj {}))}
   {:name "Day Laborer"
    :action (fn [s] (update-in s [:players (current-player s) :food] + 2))}
   {:name "3 Wood"
    :current 0
    :accumulate [3 :wood]
    :action (fn [s a] (update-in s [:players (current-player s) :wood] + (:current a)))}
   {:name "1 Clay"
    :current 0
    :accumulate [1 :clay]
    :action (fn [s a] (update-in s [:players (current-player s) :clay] + (:current a)))}
   {:name "1 Reed"
    :current 0
    :accumulate [1 :reed]
    :action (fn [s a] (update-in s [:players (current-player s) :reed] + (:current a)))}
   {:name "Fishing"
    :current 0
    :accumulate [1 :food]
    :action (fn [s a] (update-in s [:players (current-player s) :food] + (:current a)))}
   {:name "Major/Minor Improvement"
    :stage 1}
   {:name "1 Sheep"
    :stage 1
    :current 0
    :accumulate [1 :sheep]}
   {:name "Sow/Bake Bread"
    :stage 1}
   {:name "Fences"
    :stage 1}
   {:name "1 Stone [1]"
    :stage 2
    :accumulate [1 :stone]}
   {:name "Family Growth"
    :stage 2}
   {:name "Renovation"
    :stage 2}
   {:name "Take 1 Vegetable"
    :stage 3}
   {:name "1 Wild Boar"
    :stage 3
    :current 0
    :accumulate [1 :boar]}
   {:name "1 Cattle"
    :stage 4
    :current 0
    :accumulate [1 :cattle]}
   {:name "1 Stone [2]"
    :stage 4
    :current 0
    :accumulate [1 :stone]}
   {:name "Plow 1 Field and/or Sow"
    :stage 5}
   {:name "Family Growth without space"
    :stage 5}
   {:name "Renovation Fences"
    :stage 6}
   ])

(defn random-rounds []
  (filter identity (flatten
                  (for [[round# rounds] (group-by :stage actions)]
  (if round#
    (shuffle rounds))))))

(defn setup-player [n]
  {:name n
   :food 2
   :field []
   :grain 0
   :vegetable 0
   :wood 0
   :clay 0
   :reed 0
   :stone 0
   :sheep 0
   :cattle 0
   :boar 0
   })

(defn setup []
  {:actions
    (->> actions
         (filter #(nil? (:stage %)))
         vec)
   :future-rounds (random-rounds)
   :current-player 0
   :players [(setup-player "Kanwei")
             (setup-player "Lauren")]}

  )

#_(defn accumulate-actions [actions]
  (reduce (fn [new-actions action]
            (if-let [[resource-n resource-type] (:accumulate action)]
              (update-in new-actions [resource-type] + resource-n)
              new-actions))
          actions))

(defn accumulate-actions [actions]
  "Add resources to action spaces that accumulate"
  (mapv (fn [action]
         (if-let [[resource-n resource-type] (:accumulate action)]
           (assoc action :current (+ resource-n (:current action)))
           action))
          actions))

(defn add-next-action [s]
  (-> s
      (update-in [:actions] conj (first (:future-rounds s)))
      (update-in [:future-rounds] rest)))

(def round1 (update-in (add-next-action (setup)) [:actions] accumulate-actions))

(defn perform-action [s action-n]
  (let [action (nth (:actions s) action-n)]
    (-> ((:action action) s action)
        (assoc-in [:actions action-n :current] 0)
        (assoc-in [:actions action-n :occupied] true))))

(nth (:actions round1) 7)

(perform-action round1 7)


(def major-improvements
  [{:name "Well"
    :points 4}
   {:name "Fireplace [2]"
    :points 1}
   {:name "Fireplace [3]"
    :points 1}
   {:name "Hearth [4]"
    :points 1}
   {:name "Hearth [5]"
    :points 1}
   {:name "Basket Weaver"
    :points 2}
   {:name "Clay Oven"
    :points 2}
   {:name "Stone Oven"
    :points 3}
   {:name "Cabinetmaker's"
    :points 2}
   {:name "Pottery"
    :points 2}])

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println "Hello, World!"))
