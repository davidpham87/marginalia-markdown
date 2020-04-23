# marginalia-md.markdown

Utilities for parsing code into markdown



??? tip "(ns)"
    ```clojure
    (ns marginalia-md.markdown
      (:require
       [clojure.java.io]
       [clojure.string :as str]
       [clojure.edn]
       [clojure.tools.reader]
       [marginalia.parser :as p]))
    ```
## path-to-doc

```clojure
(path-to-doc fn)
```

??? tip "(defn)"
    ```clojure
    (defn path-to-doc [fn]
      {:ns (p/parse-ns (java.io.File. fn))
       :groups (p/parse-file fn)})
    ```
## indent

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
## code-block

Create code block from given string `s`



??? tip "(defn-)"
    ```clojure
    (defn- code-block
      ([s] (code-block s 4))
      ([s indent-level]
       (indent (str "```clojure\n" s "\n" "```\n\n") indent-level)))
    ```
## render-def-form

```clojure
(render-def-form {:keys [docstring raw forms level verb valid-call], :or {level 2}})
```

??? tip "(defn)"
    ```clojure
    (defn render-def-form [{:keys [docstring raw forms level verb valid-call] :or {level 2}}]
      (str (str/join (repeat level "#")) " " (second forms)
           "\n\n"
           (when docstring (str docstring "\n\n"))
           (when valid-call
             (code-block (str/join "\n" (map str valid-call)) 0))
           "\n\n"
           "??? tip \"(" verb ")\"\n"
           (code-block raw)
           "\n"))
    ```
## render-form



??? tip "(defmulti)"
    ```clojure
    (defmulti render-form
      (fn [{:keys [type verb]}]
        (case type
          :comment [:comment]
          :code [:code verb]
          :default)))
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
### render-form [:code (quote ns)]

```clojure
(render-form {:keys [docstring raw forms valid-call], :as m})
```

??? info "(defmethod)"
    ```clojure
    (defmethod render-form [:code 'ns] [{:keys [docstring raw forms valid-call] :as m}]
      (render-def-form (assoc m :level 1)))
    ```
### render-form [:code (quote def)]

```clojure
(render-form {:keys [docstring raw forms valid-call], :as m})
```

??? info "(defmethod)"
    ```clojure
    (defmethod render-form [:code 'def] [{:keys [docstring raw forms valid-call] :as m}]
      (render-def-form m))
    ```
### render-form [:code (quote defn)]

```clojure
(render-form {:keys [docstring raw forms valid-call], :as m})
```

??? info "(defmethod)"
    ```clojure
    (defmethod render-form [:code 'defn] [{:keys [docstring raw forms valid-call] :as m}]
      (render-def-form m))
    ```
### render-form [:code (quote defn-)]

```clojure
(render-form {:keys [docstring raw forms valid-call], :as m})
```

??? info "(defmethod)"
    ```clojure
    (defmethod render-form [:code 'defn-] [{:keys [docstring raw forms valid-call] :as m}]
      (render-def-form m))
    ```
### render-form [:code (quote defmacro)]

```clojure
(render-form {:keys [docstring raw forms valid-call], :as m})
```

??? info "(defmethod)"
    ```clojure
    (defmethod render-form [:code 'defmacro] [{:keys [docstring raw forms valid-call] :as m}]
      (render-def-form m))
    ```
### render-form [:code (quote defmulti)]

```clojure
(render-form {:keys [docstring raw forms valid-call], :as m})
```

??? info "(defmethod)"
    ```clojure
    (defmethod render-form [:code 'defmulti] [{:keys [docstring raw forms valid-call] :as m}]
      (render-def-form m))
    ```
### render-form [:code (quote defmethod)]

```clojure
(render-form {:keys [docstring raw forms verb method-value valid-call], :as m})
```

??? info "(defmethod)"
    ```clojure
    (defmethod render-form [:code 'defmethod]
      [{:keys [docstring raw forms verb method-value valid-call] :as m}]
      (str (str/join (repeat 3 "#")) " " (second forms) " " method-value "\n"
           "\n"
           (when docstring (str docstring "\n\n"))
           (when valid-call
             (code-block (str/join "\n" (map str valid-call)) 0))
           "\n\n"
           "??? info \"(" verb ")\"\n"
           (code-block raw 4)
           "\n"))
    ```
### render-form [:comment]

```clojure
(render-form {:keys [docstring raw forms], :as m})
```

??? info "(defmethod)"
    ```clojure
    (defmethod render-form [:comment] [{:keys [docstring raw forms] :as m}]
      (if (str/starts-with? raw "=>")
        (str "Result:" (code-block raw))
        (str raw "\n\n")))
    ```
This is a comment

Result:    ```clojure
    => (+ 2 2)
    ```## function-forms

Retrived function calling forms.

  Inspired by clojure.core/fn and clojure.core/multimethod definition. Returns
  The valid function calls are returned. Expects valid code without docstrings.



??? tip "(defn-)"
    ```clojure
    (defn- function-forms
      [name & sigs]
      (let [sigs (if (vector? (first sigs)) (list sigs) sigs)]
        (mapv #(seq (into [name] (first %))) sigs)))
    ```
## raw->forms

```clojure
(raw->forms raw)
```

??? tip "(defn)"
    ```clojure
    (defn ^{:meta-data :true} raw->forms [raw]
      (binding [clojure.tools.reader/*read-eval* false]
        (let [forms (clojure.tools.reader/read-string raw)
              multimethod? (= (first forms) 'defmethod)
              verb (first forms)
              name (second forms)]
          {:forms forms
           :verb verb
           :var name
           :valid-call
           (condp = verb
             'defn (apply function-forms (drop 1 forms))
             'defmethod (apply function-forms (second forms) (drop 3 forms))
             nil)
           :method-value (when multimethod? (nth forms 2))})))
    ```
comment here

## save-md

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
## multidoc!

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
## uberdoc!

```clojure
(uberdoc! docs files options)
```

??? tip "(defn)"
    ```clojure
    (defn uberdoc! [docs files options]
      (doseq [filename files]
        (save-md filename (assoc options :append true :target docs))))
    ```


    ```clojure
    (comment
      (function-forms 'hello '([x y] 3) '([x] 3))
      (raw->forms '(defn hello ([x y] 3) ([x] 3)))
      (raw->forms "(defn hello [x] 3)")
      (raw->forms "(defn hello ([x] 3) ([x y] 3))")
      (raw->forms "(defmethod hello 3 [{:keys [a b]}] 3)")
      (raw->forms "(def hello 3)"))
    ```