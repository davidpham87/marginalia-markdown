# materiala.extensions.re-frame

Example of extension not perfect parsing of re-frame forms



??? tip  "(`ns`)"

    ```clojure
    (ns materiala.extensions.re-frame
      (:require
       [materiala.markdown :as mm :refer (render-code-form code-block code-inline)]))
    ```

## `:rf/reg-event-fx`







??? tip  "(`derive`)"

    ```clojure
    (derive :rf/reg-event-fx ::register)
    (derive :rf/reg-sub ::register)
    ```

### render-code-form :user/register

```clojure
(render-code-form {:keys [raw forms]})
```

??? info  "(`defmethod`)"

    ```clojure
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
    ```
### render-code-form :reg-sub



no shortcut here, have to do it

```clojure
(render-code-form m)
```

??? info  "(`defmethod`)"

    ```clojure
    (defmethod render-code-form :reg-sub
      [m]
      (render-code-form (assoc m :verb ::register)))
    ```
### render-code-form :reg-event-fx

```clojure
(render-code-form m)
```

??? info  "(`defmethod`)"

    ```clojure
    (defmethod render-code-form :reg-event-fx
      [m]
      (render-code-form (assoc m :verb ::register)))
    ```
## Rich Comment

```clojure
(comment
  (let [raw "(reg-sub
                  ::all ;; not really good engineering
                  dispatch-all-fn)"]
    (-> (mm/raw->forms raw)
        (assoc :type :code :raw raw)
        mm/render-form)))
```

