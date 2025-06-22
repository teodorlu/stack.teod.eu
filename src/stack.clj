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

(defn render-writer [{:keys [message]}]
  [:form {:hx-post "/push" :hx-swap "outerHTML"}
   message
   [:textarea {:name "writer" :rows 10}]
   [:button {:type "submit"} "Save"]])

(defn concatv [& args]
  (into [] cat args))

(defn teodor? [req]
  (= (username req) "teodorlu"))

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
     (when (teodor? req)
       (render-writer {}))
     (map render-note (concatv (:stack @impulse/state) weeknotes/archive)))))

(def ok-messages ["Got it!" "At your service!" "Rubber shoes in motion."
                  "Sir! Yes, SIR!" "Double time." "Stack ready."])

(defn stack-push [{:as req :keys [params]}]
  (when (teodor? req)
    (when-let [text (get params "writer")]
      (impulse/swap-in! impulse/state
                        [:stack]
                        (fnil conj ())
                        {:text text
                         :timestamp (str (java.time.Instant/now))
                         :uuid (random-uuid)})))
  (render-writer {:message (rand-nth ok-messages)}))

(def routes
  [["/" #'index]
   ["/push" {:post #'stack-push}]])

(defn start! [opts]
  (impulse/start! #'routes opts))
