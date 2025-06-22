(ns weeknotes
  (:require [clojure.string :as str]))

(comment
  (str/split (-> archive first :text) #"\n" 2)

  )

(def archive
  [{:text "I DAG STARTER EN NY UKE!",
    :uuid #uuid "d9d4834a-df20-4503-ad5b-f88d13a9fb17",
    :timestamp "2024-09-23T07:22:56.186950328Z"}
   {:text
    "Clerk is a tool for creating better feedback loops.\n\nWhat's good feedback?\n\n- feedback latency: 100 ms? 10 minutes? 3 days?\n- feedback width: does it cover the whole thing, or just a slice?\n- feedback visualization: is it a data dump, or does it show what you want to see?\n",
    :uuid #uuid "9411e859-d9e7-4ebd-82cf-8e9bc01bad85",
    :timestamp "2024-09-23T11:14:01.533167565Z"}
   {:text
    "- how are you selling?\n- when does a company start needing your product?\n- when a company uses your product for success, how does that use look?\n\nThese are questions I didn't have good enough answers for.",
    :uuid #uuid "1116f5f4-51d8-4843-8b31-5f80e69e576e",
    :timestamp "2024-09-23T12:19:23.770881169Z"}
   {:text
    "First build the whole system, then tweak.\n\nAlan Kay did it in the 80s.\nYou can now.\n\nDon't get stuck in a local optimum.\nWhat matters is the performance of the system _as a whole_.\nDoes it solve what it's aimed to solve?\nAnd is it aimed at the right problem?\n\nTweaking is something different, but tweak AFTER you've made a full system.\n\n---\n\nHow does \"First build the whole system, then tweak\" relate to expand and contract in design thinking?\n\nFrankly, I think it maybe doesn't.\nDesign thinking is about how you treat unknowns.\n\"First build the whole system, then tweak\" is about treating knowns.\nGet the knowns out of the way, so you can focus on the unknowns.",
    :uuid #uuid "f264a912-9e08-4242-87da-781771721b08",
    :timestamp "2024-09-23T15:57:00.363055816Z"}
   {:text
    "Første BJJ-trening på LENGE.\n\nDet gjør vondt i fingrene. Eller, vondt? De har blitt brukt.\nJeg kjenner det i skuldrene. Jeg kjenner det i ryggen.\n\nSærlig ryggen. Trenger de små stabilitetsmusklene.",
    :uuid #uuid "91c42e47-43d5-483a-89b9-02b707243745",
    :timestamp "2024-09-23T20:36:34.823489179Z"}
   {:text
    "Data can reduce coupling between components in your system.\n\nBefore: components take arguments, which they then call.\nFor example to apply a theme to HTML components.\nEach component must take a color theme as input, which it uses to produce the correct HTML.\n\nAfter: theme is a one-step data transformation.\nComponents return data describing HTML, with markers where data should be interpolated later.\n\nThis change lets us skip one argument from all components, meaning we don't have to pass around the theme.",
    :uuid #uuid "a3ee8def-4f59-424a-b98a-a062d01aba0c",
    :timestamp "2024-09-23T20:56:28.035333435Z"}
   {:text "It's going to be nice to see Sindre again! ",
    :uuid #uuid "1017b2ba-6887-4c51-85c1-26a042961c52",
    :timestamp "2024-09-24T05:39:52.805280173Z"}
   {:text
    "Two nights after Monday's BJJ practice, I feel more sore than yesterday. I'm sore in my /jaw/, I didn't think that was even possible 🤔\n\nI didn't practice yesterday (Tuesday), instead washed some clothes, ate dinner and enjoyed an episode of Band of Brothers.",
    :uuid #uuid "6d718050-9f7a-4256-b4b8-be00b4e448eb",
    :timestamp "2024-09-25T07:21:53.812782580Z"}
   {:text "Har vært på Kodekamp :) ",
    :uuid #uuid "72fdbf7b-9761-45b3-90a4-9d85c7d8e139",
    :timestamp "2024-09-26T16:27:04.962152033Z"}
   {:text
    "Had a nice walk + chat with Ben. Sun is nice. Sincere conversation is nice. 🙏☀️",
    :uuid #uuid "b36ad97f-04a7-4c53-b464-1dff67cf7c4e",
    :timestamp "2024-10-01T08:40:45.381655713Z"}
   {:text
    "I cherish teamwork driven by mutual understanding, and a drive to aid.\n\nLet's help each other move forward! Let's keep the goal in mind when we take steps!\n\nThat is what I want to do.",
    :uuid #uuid "b39f9bd4-4747-42bd-8431-6add36e1e40d",
    :timestamp "2024-10-02T08:10:19.809012942Z"}
   {:text
    "TO LEARN IS TO COMPRESS\n=======================\n\nTo learn is to compress. More knowledge fits now in the same space.\n\nAs you compress concepts, your chunks will change. The weight of your concept will stay mostly the same, but the weight of the _reach_ of the concept will increase. One unit of work will produce more value.\n\nKeeping your chunk size small as you learn is essential. If the chunk size grows too large, your learning will slow. What about someone else's chunks? Perhaps you try work at their chunk size, and fail with a bang, making zero progress. Or you may refuse to work at their chunk size, sticking to your own. In that case, their chunk size may not make sense to you.",
    :uuid #uuid "e606814e-fa2a-4ce4-828b-42cbc4edefa5",
    :timestamp "2024-10-08T16:28:26.249354120Z"}
   {:text
    "Har funnet problemer med Hiccup. Løser Replicant biffen?\n\nSer sånn ut, i alle fall foreløpig???",
    :uuid #uuid "b3a11b8a-84c7-4366-a8bd-1638f55c6982",
    :timestamp "2024-10-09T07:48:32.207439313Z"}
   {:text
    "Enhetstesting for byggebransjen\n\nJeg tror det som trengs er masse innsats for å lage et \"corpus\" av automatiserte tester gitt et konsept.\n\nSå trenger man beskrivelser av det konseptet!",
    :uuid #uuid "35f5f0bd-3e82-48d7-853d-f0c0b3112143",
    :timestamp "2024-10-09T14:37:11.464934306Z"}
   {:text
    "Feedback bør være:\n\n1. Rask - ikke vente lenge\n2. Bred - dekke det vi bryr oss om\n3. Effektiv - mulig å forstå når vi får den.\n\nMens jeg leser https://parenteser.mattilsynet.io/flaskehals/, innser jeg at dette stemmer 100 % for profilering / tuning også.\n\nChristian sier det selv, (time ,,,) er smal, mens en god flamegraph er bred nok.\n\n> Det er lett å bare slenge en (time ,,,) rundt koden, og se hva vi får. Denne tilnærmingen gir oss et tall og kan være nyttig for en før og etter-sammenligning. Men det er også alt.\n> \n> Hvis det er én ting jeg har lært av Goldratt så er det at lokale optimaliseringer er meningsløse om man ikke har noen formening om hvor flaskehalsene i systemet befinner seg.",
    :uuid #uuid "adf0bd89-d362-4935-9660-3385aa9be512",
    :timestamp "2024-10-10T11:20:21.611595140Z"}
   {:text
    "A sudden awareness of the bones in my body\n\nEach movement\nMy breath\nThe length of my steps\n\nIn some sense predetermined. In some sense, a a choice I make.\n\nGoing with the flow means allowing things in motion to keep moving AND letting impulses alter the direction.",
    :uuid #uuid "53ab56bc-e519-43da-ae5f-255d67780ba1",
    :timestamp "2024-10-11T10:34:11.356285535Z"}
   {:text
    "1. Datomic i mikrobloggeriet var en braksuksess!\n2. Jeg vil lage file watcher som kun kjører når GARDEN_GIT_REVISION ikke er tilstede\n3. Jeg vil lage en matte-poc med codemirror som kjører i en nettleser med ekstremt kjapp deploy, helst nobuild.",
    :uuid #uuid "2e9c380a-c169-494e-a40a-e9c1927265c1",
    :timestamp "2024-12-22T11:59:43.916931479Z"}
   {:text
    "Three modes when creating:\n\n- expand\n- select \n- reduce wip / cleanup\n",
    :uuid #uuid "f64ed53e-b57e-4b89-96bf-6d2f3fda6232",
    :timestamp "2025-05-03T09:37:28.860823053Z"}
   {:text "I wonder if anyone else have written weeknotes.",
    :uuid #uuid "a73836d3-2183-4951-8225-599425f73adb",
    :timestamp "2025-06-22T14:45:18.173298295Z"}])
