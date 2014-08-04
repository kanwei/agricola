(ns agricola.core
  (require [taoensso.timbre :refer :all]
           [criterium.core :as crit])
  (:gen-class))

(defn current-player [s]
  (:current-player s))

(def actions
  [{:name "Build Room"
    :req (fn [s] (>= (get-in s [:players (current-player s) :wood]) 2))
    :andor (fn [s])}

   {:name "Starting Player"
    :action (fn [s a] (assoc s :starting-player (current-player s)))
    :andor (fn [s])}

   {:name "Take 1 Grain"
    :action (fn [s a] (update-in s [:players (current-player s) :grain] inc))}

   {:name "Plow 1 Field"
    :action (fn [s a] (update-in s [:players (current-player s) :fields] conj {}))}

   {:name "Day Laborer"
    :action (fn [s a] (update-in s [:players (current-player s) :food] + 2))}

   {:name "3 Wood"
    :current 0
    :accumulate 3
    :action (fn [s a] (update-in s [:players (current-player s) :wood] + (:current a)))}

   {:name "1 Clay"
    :current 0
    :accumulate 1
    :action (fn [s a] (update-in s [:players (current-player s) :clay] + (:current a)))}

   {:name "1 Reed"
    :current 0
    :accumulate 1
    :action (fn [s a] (update-in s [:players (current-player s) :reed] + (:current a)))}

   {:name "Fishing"
    :current 0
    :accumulate 1
    :action (fn [s a] (update-in s [:players (current-player s) :food] + (:current a)))}

   {:name "Major/Minor Improvement"
    :req (constantly false)
    :stage 1}

   {:name "1 Sheep"
    :stage 1
    :current 0
    :accumulate 1
    :action (fn [s a] (update-in s [:players (current-player s) :sheep] + (:current a)))}

   {:name "Sow/Bake Bread"
    :req (constantly false)
    :stage 1}

   {:name "Fences"
    :stage 1
    :req (fn [s] (>= (get-in s [:players (current-player s) :wood]) 4))}

   {:name "1 Stone [1]"
    :stage 2
    :current 0
    :accumulate 1
    :action (fn [s a] (update-in s [:players (current-player s) :stone] + (:current a)))}

   {:name "Family Growth"
    :stage 2}

   {:name "Renovation"
    :stage 2}

   {:name "Take 1 Vegetable"
    :stage 3
    :action (fn [s a] (update-in s [:players (current-player s) :vegetable] inc))}

   {:name "1 Wild Boar"
    :stage 3
    :current 0
    :accumulate 1
    :action (fn [s a] (update-in s [:players (current-player s) :boar] + (:current a)))}

   {:name "1 Cattle"
    :stage 4
    :current 0
    :accumulate 1
    :action (fn [s a] (update-in s [:players (current-player s) :cattle] + (:current a)))}

   {:name "1 Stone [2]"
    :stage 4
    :current 0
    :accumulate 1
    :action (fn [s a] (update-in s [:players (current-player s) :stone] + (:current a)))}

   {:name "Plow 1 Field and/or Sow"
    :stage 5}

   {:name "Family Growth even without space"
    :stage 5}

   {:name "After Renovation Fences"
    :stage 6}
   ])

(defn random-rounds []
  (filter identity (flatten
                  (for [[round# rounds] (group-by :stage actions)]
  (if round#
    (shuffle rounds))))))

(defn setup-player [n]
  {:name n
   :rooms 2
   :workers 2
   :house-type :wood
   :food 3
   :pastures []
   :fields []
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

(defn accumulate-actions [actions]
  "Add resources to action spaces that accumulate"
  (mapv (fn [action]
         (if-let [resource-n (:accumulate action)]
           (assoc action :current (+ resource-n (:current action)))
           action))
          actions))

(defn add-next-action [s]
  (-> s
      (update-in [:actions] conj (first (:future-rounds s)))
      (update-in [:future-rounds] rest)))

(def round1 (update-in (add-next-action (setup)) [:actions] accumulate-actions))
round1

(defn pick-action [actions]
  (nth actions (rand-int (count actions))))

(defn perform-action [s action]
  (info "Performing" (:name action))
  (let [action-n (.indexOf (:actions s) action)]
    (-> ((:action action) s action)
        (assoc-in [:actions action-n :current] 0)
        (assoc-in [:actions action-n :occupied] true))))

(defn possible-actions [s]
  (filter (fn [action]
            (and (not (:occupied action))
                 (or (nil? (:req action))
                     ((:req action) s))))
          (:actions s)))

(possible-actions round1)

(nth (:actions round1) 5)

(possible-actions round1)
(:actions (possible-actions round1))
(count (possible-actions round1))

#_(crit/quick-bench
  (count (possible-actions (perform-action round1 (pick-action (possible-actions round1))))))


(defn play [s round-n]
  (if (>= round-n 14)
    [s round-n]
    (recur s (inc round-n)))
  )

(play {} 0)

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
