# Extension

The idea of the rendering is to create the markdown string from the raw forms
and dispatching on the verb of the form (after keywordization [is a real name?])

``` clojure
(defmethod render-code-form :defmethod
  [{:keys [docstring raw forms valid-call method-value verb] :as m}]
  (str (str/join (repeat 3 "#")) " " (second forms) " " method-value "\n"
       "\n"
       (when docstring (str docstring "\n\n"))
       (render-valid-call valid-valid-call)
       "\n\n"
       "??? info \"(" verb ")\"\n"
       (code-block raw 4)
       "\n"))

(defmethod render-code-form :comment
  [{:keys [raw] :as m}]
  (str "## Comment \n\n"
       (code-block raw 0) "\n\n"))
```

The common top level pattern is the `(def-verb-symbol var-name [vector-of-args]
& body)`, which is default case render this case. However, the number of top
level forms is infinite thanks the power of macros (e.g. re-frame's
`reg-event-fx`). For these case, you can:

1. Make a pull request so that I include them in the codebase, if the top level
   form is common in Clojure (like `comment`).
2. Extend the multimethod `materiala.markdown/render-code-form` with your top
   level form and import them in your main file.

## Example

For a raw form `(defn hello [x] 3)`, the `render-code-form` multimethod gets
the following map as input

```clojure
{:forms (defn hello [x] 3) :verb defn :var hello
 :raw "(defn hello [x] 3)"}
```

(Note to self: nice place to make specs). Hence an example of extension is

``` clojure
;; in user/doc/extension.clj

(ns user.doc.extension
  (:require
   [materiala.markdown :as mm :refer (render-code-form code-block)]))

;; re-frame reg-event-fx follow this pattern

;; (reg-event-fx
;;  ::initialize
;;  event-fx-fn)

;; (reg-event-fx
;;  ::initialize
;;  (fn [cofx [_ & args]]))

(defmethod render-code-form ::register
  [{:keys [raw forms]}]
  (str "## Event: "
       (second forms)
       "\n\n"
       (if (symbol? (nth forms 2))
         (str "Dispatch-fn: " (nth forms 2))
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

;; in user/doc/ns.clj

(ns user.doc.ns
  (:require
   [materiala.core]
   [user.doc.extension]))

(defn -main [& args]
  (apply materiala.core/main args))
```

Then you can just call it (make sure `materiala` is on your classpath)

``` bash
clojure -m user.doc.ns src
```
