# materiala.extensions.re-frame-test

Example namespace for rendering custom macros



??? tip  "(`ns`)"

    ```clojure
    (ns materiala.extensions.re-frame-test
      (:require
       [re-frame.core :as rf :refer (reg-event-fx reg-sub)]
       [materiala.markdown :as mm]
       [materiala.extensions.re-frame]
       [clojure.test :as t :refer (deftest are is)]))
    ```

## `reg-event-fx`: `:user/initialize`

```clojure
(rf/reg-event-fx
 ::initialize
 (fn [cofx _]
   (println "Hello with namespace keywords!")
   cofx))
```

## `reg-sub`: `:user/all`

```clojure
(rf/reg-sub
 ::all ;; not really good engineering
 (fn [db] db))
```

## `reg-event-fx`: `:initialize`

```clojure
(reg-event-fx
 :initialize
 (fn [cofx _]
   (println "Hello without namespace!")
   cofx))
```

## `reg-sub`: `:all`

```clojure
(reg-sub
 :all ;; not really good engineering
 (fn [db] db))
```

## `reg-sub`: `:user/dispath-fn`

Dispatch-fn: `println`

## `re-frame-simple-register`



??? tip  "(`deftest`)"

    ```clojure
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
    ```

