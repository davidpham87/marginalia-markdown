(ns materiala.extensions.re-frame
  "Example of extension not perfect parsing of re-frame forms"
  (:require
   [materiala.markdown :as mm :refer (render-code-form code-block code-inline)]))

(derive :rf/reg-event-fx ::register)
(derive :rf/reg-sub ::register)

(defmethod render-code-form ::register
  [{:keys [raw forms]}]
  (str "## " (code-inline (name (first forms)))": "
       (code-inline (second forms))
       "\n\n"
       (if (symbol? (nth forms 2))
         (str "Dispatch-fn: " (code-inline (nth forms 2)))
         ;; here we could have a better rendering by leveraing the code as data
         ;; by showing how it the event can be called
         (code-block raw 0))
       "\n\n"))

;; no shortcut here, have to do it
(defmethod render-code-form :reg-sub
  [m]
  (render-code-form (assoc m :verb ::register)))

(defmethod render-code-form :reg-event-fx
  [m]
  (render-code-form (assoc m :verb ::register)))


(comment
  (let [raw "(reg-sub
                  ::all ;; not really good engineering
                  dispatch-all-fn)"]
    (-> (mm/raw->forms raw)
        (assoc :type :code :raw raw)
        mm/render-form)))
