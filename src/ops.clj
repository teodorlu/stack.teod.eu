(ns ops
  (:require
   [babashka.fs :as fs]
   [babashka.process :as p]
   [clojure.string :as str])
  (:import (java.time LocalDate)))

(def trim+newline #(str (str/trim %) \n))

(defn backup! [notes folder]
  (when (empty? notes)
    (throw (ex-info "Notes carry *why* we did a backup just now. Empty notes are not allowed."
                    {:notes notes})))
  (fs/create-dirs folder)
  (spit (fs/file folder "NOTES.txt") (trim+newline notes))
  (p/shell {:in (str "get duratom.edn " folder "/duratom.edn")}
           "garden sftp"))

(comment

  ;; Run this from a local REPL to backup.
  ;; Archive is written to archive/ with a date and timestamp.
  ;; The intent is to check backups into Git.
  (def archive (fs/file "archive" (str (LocalDate/now) "-" (System/currentTimeMillis))))
  (backup! "New backup after joining weeknotes into impulse/state" archive)

  )
