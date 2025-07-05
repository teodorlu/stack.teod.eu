(ns stack
  (:require [clojure.string :as str]
            [impulse]
            [nextjournal.garden-id :as garden-id :refer [username displayname email]]
            [weeknotes]))

(defn render-note [{:keys [text uuid timestamp]}]
  (let [[header body] (-> text
                          (str/split #"\n" 2))]
    [:div ;; {:style {:margin-top "2rem"}}
     [:p [:strong header]]
     [:p {:style {:padding-left "1rem"}}
      (some->> body str/trim str/split-lines (interpose [:br]))]]))

(defn render-writer [message]
  [:form {:hx-post "/push" :hx-swap "outerHTML"
          :hx-target "#writer-and-notes"}
   message
   [:textarea {:name "writer" :rows 10}]
   [:button {:type "submit"} "Save"]])

(defn teodor? [req]
  (= (username req) "teodorlu"))

(defn render-writer-and-notes
  ([req] (render-writer-and-notes req nil))
  ([req message]
   [:div#writer-and-notes
    (when (teodor? req)
      (render-writer message))
    (map render-note (:stack @impulse/state))]))

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
     (render-writer-and-notes req))))

;; https://www.reddit.com/r/redalert2/comments/1eifo7p/red_alert_2_quotes_test/
(def ok-messages ["High Speed, Low Drag."
                  "Got it!" "At your service!" "Rubber shoes in motion."
                  "Sir! Yes, SIR!" "Double time." "Stack ready." "Moving out."
                  "Airship ready!" "I've got the knowlege." "Release the swarm!"
                  "It's gonna be a silent spring." "Scorched earth."
                  "High speed, low drag." "Woof!"])

(defn stack-push [{:as req :keys [params]}]
  (when (teodor? req)
    (when-let [text (get params "writer")]
      (impulse/swap-in! impulse/state
                        [:stack]
                        (fnil conj ())
                        {:text text
                         :timestamp (str (java.time.Instant/now))
                         :uuid (random-uuid)})))
  (render-writer-and-notes req (rand-nth ok-messages)))

(def routes
  [["/" #'index]
   ["/push" {:post #'stack-push}]])

(defn start! [opts]
  (impulse/start! #'routes opts))
