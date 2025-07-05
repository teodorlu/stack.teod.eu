(ns ops
  (:require
   [babashka.fs :as fs]
   [babashka.process :as p])
  (:import (java.time LocalDate)))

(defn backup-to [folder]
  (fs/create-dirs folder)
  (p/shell {:in (str "get duratom.edn " folder "/duratom.edn")}
           "garden sftp"))

(comment

  (def archive (fs/file "archive" (str (LocalDate/now) "-" (System/currentTimeMillis))))
  (backup-to archive)

  )
