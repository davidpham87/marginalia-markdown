# materiala.markdown

Utilities for parsing code into markdown



??? tip "(ns)"
    ```clojure
    (ns materiala.markdown
      (:require
       [clojure.java.io]
       [clojure.string :as str]
       [clojure.edn]
       [clojure.tools.reader]
       [marginalia.parser :as p]))
    ```
## `drop-at`

Helper function to work with indices. Drop element from `coll` at indices
  `idx`.

```clojure
(drop-at idx coll)
```

??? tip "(defn)"
    ```clojure
    (defn drop-at
      [idx coll]
      (let [n (count coll)
            idx (sort (if (sequential? idx) idx [idx]))]
        (loop [indices idx
               iteration 0
               v coll]
          (if (and (seq indices) (< iteration n))
            (let [i (- (first indices) iteration)
                  v (vec v)]
              (recur (rest indices)
                     (inc iteration)
                     (concat
                      (subvec v 0 i)
                      (subvec v (inc i)))))
            (vec v)))))
    ```
## `path-to-doc`

```clojure
(path-to-doc fn)
```

??? tip "(defn)"
    ```clojure
    (defn path-to-doc [fn]
      {:ns (p/parse-ns (java.io.File. fn))
       :groups (p/parse-file fn)})
    ```
## `indent`

Indent string portion

```clojure
(indent s indent-level)
```

??? tip "(defn)"
    ```clojure
    (defn indent
      [s indent-level]
      (let [indent-space (str/join "" (repeat indent-level " "))]
        (str/join "\n" (map #(str indent-space %) (str/split s #"\n")))))
    ```
## `code-block`

Create code block from given string `s`

```clojure
(code-block s)
(code-block s indent-level)
```

??? tip "(defn)"
    ```clojure
    (defn code-block
      ([s] (code-block s 4))
      ([s indent-level]
       (indent (str "```clojure\n" s "\n" "```\n\n") indent-level)))
    ```
## `code-inline`

```clojure
(code-inline s)
```

??? tip "(defn)"
    ```clojure
    (defn code-inline [s] (str "`" s "`"))
    ```
## `render-valid-call`

```clojure
(render-valid-call valid-call)
```

??? tip "(defn)"
    ```clojure
    (defn render-valid-call [valid-call]
      (when valid-call
        (-> (str/join "\n" (map str valid-call))
            (str/replace #"," )
            (code-block 0))))
    ```
## `render-def-form`

```clojure
(render-def-form {:keys [docstring raw forms level verb valid-call] :or {level 2}})
```

??? tip "(defn)"
    ```clojure
    (defn render-def-form
      [{:keys [docstring raw forms level verb valid-call] :or {level 2}}]
      (str (str/join (repeat level "#")) " "
           (case verb
             'ns (second forms)
             (code-inline (second forms))) ;; keep the ear-muff variable to get md rendered
           "\n\n"
           (when docstring (str docstring "\n\n"))
           (render-valid-call valid-call)
           "\n\n"
           "??? tip \"(" verb ")\"\n"
           (code-block raw)
           "\n"))
    ```
## `render-code-form`



??? tip "(defmulti)"
    ```clojure
    (defmulti render-code-form (fn [m] (-> m :verb keyword)))
    ```
### render-code-form :default

```clojure
(render-code-form {:keys [docstring raw forms valid-call] :as m})
```

??? info "(defmethod)"
    ```clojure
    (defmethod render-code-form :default
      [{:keys [docstring raw forms valid-call] :as m}]
      (render-def-form m))
    ```
### render-code-form :ns

```clojure
(render-code-form {:keys [docstring raw forms valid-call] :as m})
```

??? info "(defmethod)"
    ```clojure
    (defmethod render-code-form :ns
      [{:keys [docstring raw forms valid-call] :as m}]
      (render-def-form (assoc m :level 1)))
    ```
### render-code-form :defmethod

```clojure
(render-code-form {:keys [docstring raw forms valid-call method-value verb] :as m})
```

??? info "(defmethod)"
    ```clojure
    (defmethod render-code-form :defmethod
      [{:keys [docstring raw forms valid-call method-value verb] :as m}]
      (str (str/join (repeat 3 "#")) " " (second forms) " " method-value "\n"
           "\n"
           (when docstring (str docstring "\n\n"))
           (render-valid-call valid-call)
           "\n\n"
           "??? info \"(" verb ")\"\n"
           (code-block raw 4)
           "\n"))
    ```
### render-code-form :comment

```clojure
(render-code-form {:keys [raw] :as m})
```

??? info "(defmethod)"
    ```clojure
    (defmethod render-code-form :comment
      [{:keys [raw] :as m}]
      (str "## Rich Comment\n\n"
           (code-block raw 0) "\n\n"))
    ```
## `render-form`



??? tip "(defmulti)"
    ```clojure
    (defmulti render-form :type)
    ```
### render-form :default

```clojure
(render-form {:keys [docstring raw]})
```

??? info "(defmethod)"
    ```clojure
    (defmethod render-form :default [{:keys [docstring raw]}]
      (str docstring "\n\n" (code-block raw)))
    ```
This is a comment for testing comments in forms
=> (+ 2 2)

### render-form :comment

```clojure
(render-form {:keys [docstring raw forms] :as m})
```

??? info "(defmethod)"
    ```clojure
    (defmethod render-form :comment [{:keys [docstring raw forms] :as m}]
      (if (str/starts-with? raw "=>")
        (str "Result:" (code-block raw) "\n\n")
        (str raw "\n\n")))
    ```
### render-form :code



rely on two level dispatch to catch all possible verbs and extend it, to
keep it open the flat hierarchy is a closed system.

```clojure
(render-form m)
```

??? info "(defmethod)"
    ```clojure
    (defmethod render-form :code [m]
      (render-code-form m))
    ```
## `function-forms`

Retrived function calling forms.

  Inspired by clojure.core/fn and clojure.core/multimethod definition. Returns
  The valid function calls are returned. Expects valid code without docstrings.

```clojure
(function-forms name & sigs)
```

??? tip "(defn)"
    ```clojure
    (defn function-forms
      [name & sigs]
      (let [sigs (if (map? (first sigs)) (drop 1 sigs) sigs)
            sigs (if (vector? (first sigs)) (list sigs) sigs)]
        (mapv #(seq (into [name] (first %))) sigs)))
    ```
## `extended-raw->forms`







??? tip "(defmulti)"
    ```clojure
    (defmulti extended-raw->forms first)
    (defmethod extended-raw->forms :default [_] {})
    ```
### extended-raw->forms (quote defn)

```clojure
(extended-raw->forms forms)
```

??? info "(defmethod)"
    ```clojure
    (defmethod extended-raw->forms 'defn [forms]
      {:valid-call (apply function-forms (drop 1 forms))})
    ```
### extended-raw->forms (quote defmethod)

```clojure
(extended-raw->forms forms)
```

??? info "(defmethod)"
    ```clojure
    (defmethod extended-raw->forms 'defmethod [forms]
      {:valid-call (apply function-forms (second forms) (drop 3 forms))
       :method-value (nth forms 2)})
    ```
## `raw->forms`

```clojure
(raw->forms raw)
```

??? tip "(defn)"
    ```clojure
    (defn ^{:meta-data :true} raw->forms [raw]
      (binding [clojure.tools.reader/*read-eval* false]
        (let [forms (clojure.tools.reader/read-string raw)
              verb (first forms) name (second forms)]
          (merge {:forms forms :verb verb :var name}
                 (extended-raw->forms forms)))))
    ```
comment here

## `save-md`

Save markdown built from clojure source.

```clojure
(save-md filename options)
```

??? tip "(defn)"
    ```clojure
    (defn save-md
      [filename options]
      (let [target (:target options (str (second (re-find #"(.*)\.(\w+)$" filename)) ".md"))]
        (clojure.java.io/make-parents target)
        (when-not (:append options)
          (spit target ""))
        (doseq [{:keys [raw type] :as all} (p/parse-file filename)]
          (let [all (cond-> all
                      (and (= type :code) (:raw all)) (merge (raw->forms raw))
                      :always identity)]
            (spit target (render-form all) :append true)))))
    ```
## `multidoc!`

Generate an output file for each of provided namespace.

```clojure
(multidoc! docs files options)
```

??? tip "(defn)"
    ```clojure
    (defn multidoc!
      [docs files options]
      (doseq [filename files]
        (println filename)
        (let [file-ns (path-to-doc filename)
              target-filename (str docs "/" (str/replace (:ns file-ns) #"\." "/") ".md")]
          (save-md filename (assoc options :target target-filename)))))
    ```
## `uberdoc!`

```clojure
(uberdoc! docs files options)
```

??? tip "(defn)"
    ```clojure
    (defn uberdoc! [docs files options]
      (doseq [filename files]
        (save-md filename (assoc options :append true :target docs))))
    ```
## Rich Comment

```clojure
(comment
  ;; Rich comment
  (keyword (:verb (raw->forms "(rf/reg-sub
::hello
(fn [m] 3))")))
  (raw->forms "(defn hello {:pre (constantly true)} ([m] 3))")
  (p/parse "(defn hello \"hello\" {:pre (constantly true)} ([m] 3))")
  (raw->forms "(reg-sub
::hello
(fn [m] 3))")
  (raw->forms "(defmethod hello 3 [{:keys [a b]}] 3)")
  (function-forms '(def hello {:pre identity} [m] 3)))
```

