(ns materiala.extensions.re-frame-test
  "Example namespace for rendering custom macros"
  (:require
   [re-frame.core :as rf :refer (reg-event-fx reg-sub)]
   [materiala.markdown :as mm]
   [materiala.extensions.re-frame]
   [clojure.test :as t :refer (deftest are is)]))

(rf/reg-event-fx
 ::initialize
 (fn [cofx _]
   (println "Hello with namespace keywords!")
   cofx))

(rf/reg-sub
 ::all ;; not really good engineering
 (fn [db] db))

(reg-event-fx
 :initialize
 (fn [cofx _]
   (println "Hello without namespace!")
   cofx))

(reg-sub
 :all ;; not really good engineering
 (fn [db] db))

(reg-sub
 ::dispath-fn
 println)

(deftest re-frame-simple-register
  (are [raw markdown]
      (= markdown (-> (mm/raw->forms raw)
                      (assoc :type :code :raw raw)
                      mm/render-form))
    "(reg-sub
:all ;; not really good engineering
(fn [db] db))"
    "## Event: `:all`\n\n```clojure\n(reg-sub\n:all ;; not really good engineering\n(fn [db] db))\n```\n\n"

    "(reg-sub
::all ;; not really good engineering
dispatch-all-fn)"
    "## Event: `:materiala.extensions.re-frame/all`\n\nDispatch-fn: `dispatch-all-fn`\n\n"))
