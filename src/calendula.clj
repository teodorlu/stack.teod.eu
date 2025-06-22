(ns calendula
  (:require [clojure.string :as str]
            [impulse]
            [nextjournal.garden-email :as garden-email]
            [nextjournal.garden-id :as garden-id :refer [username displayname email]]
            [ring.util.codec :as codec]
            [weeknotes])
  (:import (java.time LocalDate DayOfWeek)
           (java.time.temporal TemporalAdjusters)))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; configuration

(def availability {:monday [{:start 9 :end 10}
                            {:start 12 :end 15}]
                   :wednesday [{:start 8 :end 10}
                               {:start 12 :end 14}]})

(def owner-email (or (System/getenv "GARDEN_OWNER_EMAIL") "my-email@example.com"))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; date stuff

(defn now [] (str (LocalDate/now)))

(defn before? [a b]
  (.isBefore (LocalDate/parse a) (LocalDate/parse b)))

(defn start-of-week [date]
  (str (.with (LocalDate/parse date) (TemporalAdjusters/previous DayOfWeek/MONDAY))))

(defn offset-week [date offset]
  (str (.plusWeeks (LocalDate/parse date) offset)))

(defn dates-of-week [start-date]
  (map (fn [offset] (str (.plusDays (LocalDate/parse start-date) offset))) (range 7)))

(defn date->keyword [day]
  (keyword (str/lower-case (str (.getDayOfWeek (LocalDate/parse day))))))

(defn free? [availability day hour]
  (and (some (fn [{:keys [start end]}] (<= start hour end)) (availability (date->keyword day)))
       (not (get-in @impulse/state [:appointments day hour]))))

(comment
  (before? "2024-09-16" "2024-09-17") ;=> true
  (before? "2024-09-17" "2024-09-16") ;=> false
  (dates-of-week (start-of-week (now)))
  (start-of-week (now))
  (offset-week (start-of-week (now)) 1)
  (dates-of-week (offset-week (start-of-week (now)) 1))
  (date->keyword "2024-01-01") ;=> :monday
  (free? {:monday [{:start 12 :end 15}]} "2024-01-01" 15))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; the app!

(defn render-day [req day]
  [:div {:id (str "date-" day)}
   [:strong (str (name (date->keyword day)))]
   [:span " (" day ")"]
   (for [hour (mapcat #(range (:start %) (:end %)) (availability (date->keyword day)))
         :let [status (cond (free? availability day hour) :free
                            (= (get-in @impulse/state [:appointments day hour]) (email req)) :mine
                            :else nil)]
         :when status]              ; either free or booked by the current user
     [:div {:hx-post (str (if (= status :free)
                            "/book-appointment?"
                            "/unbook-appointment?")
                          (codec/form-encode {:day day :hour hour}))
            :hx-target (str "#date-" day)}
      [:input {:type "checkbox"
               :checked (= status :mine)}]
      (str hour ":00")])])

(defn render-week [req start-of-week]
  (let [today (now)]
    [:div#week
     [:div.grid
      (for [date (dates-of-week start-of-week)
            :when (and (availability (date->keyword date))
                       (not (before? date today)))]
        (render-day req date))]
     [:div {:role "group" :style "margin-top: 1em"}
      (if (before? start-of-week today)
        [:button.outline {:disabled true} "← prev week"]
        [:button.outline {:hx-post "/prev" :hx-target "#week"} "← prev week"])
      [:button.outline {:hx-post "/next" :hx-target "#week"} "next week →"]]]))

(defn render-note [{:keys [text uuid timestamp]}]
  (let [[header body] (-> text
                          (str/split #"\n" 2))]
    [:div ;; {:style {:margin-top "2rem"}}
     [:p [:strong header]]
     [:p {:style {:padding-left "1rem"}}
      (some->> body str/trim str/split-lines (interpose [:br]))]]))

(defn render-writer [{:keys [message]}]
  [:form {:hx-post "/push" :hx-swap "outerHTML"}
   message
   [:textarea {:name "writer" :rows 10}]
   [:button {:type "submit"} "Save"]])

(defn concatv [& args]
  (into [] cat args))

(defn index [req]
  (let [uname (username req)]
    (impulse/page
     {:title "Stack"}
     [:nav
      [:ul
       [:li "📚" [:strong "Stack"]]]
      [:ul
       [:li (if uname
              [:span (str uname " ") [:a {:href garden-id/logout-uri} "(logout)"]]
              [:a {:href garden-id/login-uri} "login"])]]]
     (when uname (render-writer {}))
     (map render-note (concatv (:stack @impulse/state) weeknotes/archive)))))

(defn book-appointment [{:as req :keys [params]}]
  (let [{:strs [day hour]} params]
    ;; XXX race condition if multiple users are booking appointments at the same time
    (swap! impulse/state assoc-in [:appointments (str day) (parse-long hour)] (email req))
    (garden-email/send-email! {:to {:email owner-email}
                               :subject (format "%s (%s) booked appointment at %s %s:00" (displayname req) (email req) day hour)})
    (garden-email/send-email! {:to {:email (email req)}
                               :subject (format "You booked an appointment at %s %s:00" day hour)})
    (render-day req day)))

(defn unbook-appointment [{:as req :keys [params]}]
  (let [{:strs [day hour]} params]
    (swap! impulse/state update-in [:appointments (str day)] dissoc (parse-long hour))
    (garden-email/send-email! {:to {:email owner-email}
                               :subject (format "%s (%s) has canceled their appointment at %s %s:00" (displayname req) (email req) day hour)})
    (garden-email/send-email! {:to {:email (email req)}
                               :subject (format "You cancelled an appointment at %s %s:00" day hour)})
    (render-day req day)))

(defn update-week [offset req]
  (render-week req (impulse/swap-in! impulse/state
                                     [:user-state (username req) :week]
                                     (fnil offset-week (start-of-week (now)))
                                     offset)))

(def ok-messages ["Got it!" "At your service!" "Rubber shoes in motion."
                  "Sir! Yes, SIR!" "Double time." "Stack ready."])

(defn stack-push [{:as req :keys [params]}]
  (when-let [text (get params "writer")]
    (impulse/swap-in! impulse/state
                      [:stack]
                      (fnil conj [])
                      {:text text
                       :timestamp (str (java.time.Instant/now))
                       :uuid (random-uuid)}))
  (render-writer {:message (rand-nth ok-messages)}))

(def routes
  [["/" #'index]
   ["/push" {:post #'stack-push}]
   ["/book-appointment" {:post #'book-appointment}]
   ["/unbook-appointment" {:post #'unbook-appointment}]
   ["/next" {:post (partial update-week 1)}]
   ["/prev" {:post (partial update-week -1)}]])

(defn start! [opts]
  (impulse/start! #'routes opts))
