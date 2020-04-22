# marginalia-md.markdown

Utilities for parsing code into markdown

???+ tip "(ns)"
    ```clojure
    (ns marginalia-md.markdown
      (:require
       [clojure.java.io]
       [clojure.string :as str]
       [clojure.tools.reader]
       [marginalia.parser :as p]))
    ```

## path-to-doc

???+ tip "(defn)"
    ```clojure
    (defn path-to-doc [fn]
      {:ns (p/parse-ns (java.io.File. fn))
       :groups (p/parse-file fn)})
    ```

## code-block

Create code block from given string `s`

???+ tip "(defn-)"
    ```clojure
    (defn- code-block
      ([s] (code-block s 4))
      ([s indent]
       (let [indent-space (str/join "" (repeat indent " "))]
         (str indent-space "```clojure\n"
              (str/join "\n" (map #(str indent-space %) (str/split s #"\n")))
              "\n"
              indent-space "```\n\n"))))
    ```

## render-def-form

???+ tip "(defn)"
    ```clojure
    (defn render-def-form [{:keys [docstring raw forms level verb] :or {level 2}}]
      (str (str/join (repeat level "#")) " " (second forms)
           "\n\n"
           (when docstring (str docstring "\n\n"))
           "???+ tip \"(" verb ")\"\n"
           (code-block raw)))
    ```

## render-form

???+ tip "(defmulti)"
    ```clojure
    (defmulti render-form
      (fn [{:keys [type verb]}]
        (case type
          :comment [:comment]
          :code [:code verb]
          :default)))
    ```

### render-form :default

???+ info "(defmethod)"
    ```clojure
    (defmethod render-form :default [{:keys [docstring raw]}]
      (str docstring "\n\n" (code-block raw)))
    ```

### render-form [:code ns]

???+ info "(defmethod)"
    ```clojure
    (defmethod render-form [:code 'ns] [{:keys [docstring raw forms] :as m}]
      (render-def-form (assoc m :level 1)))
    ```

### render-form [:code def]

???+ info "(defmethod)"
    ```clojure
    (defmethod render-form [:code 'def] [{:keys [docstring raw forms] :as m}]
      (render-def-form m))
    ```

### render-form [:code defn]

???+ info "(defmethod)"
    ```clojure
    (defmethod render-form [:code 'defn] [{:keys [docstring raw forms] :as m}]
      (render-def-form m))
    ```

### render-form [:code defn-]

???+ info "(defmethod)"
    ```clojure
    (defmethod render-form [:code 'defn-] [{:keys [docstring raw forms] :as m}]
      (render-def-form m))
    ```

### render-form [:code defmacro]

???+ info "(defmethod)"
    ```clojure
    (defmethod render-form [:code 'defmacro] [{:keys [docstring raw forms] :as m}]
      (render-def-form m))
    ```

### render-form [:code defmulti]

???+ info "(defmethod)"
    ```clojure
    (defmethod render-form [:code 'defmulti] [{:keys [docstring raw forms] :as m}]
      (render-def-form m))
    ```

### render-form [:code defmethod]

???+ info "(defmethod)"
    ```clojure
    (defmethod render-form [:code 'defmethod]
      [{:keys [docstring raw forms verb method-value] :as m}]
      (str (str/join (repeat 3 "#")) " " (second forms) " " (eval method-value) "\n"
           "\n"
           (when docstring (str docstring "\n\n"))
           "???+ info \"(" verb ")\"\n" (code-block raw)))
    ```

### render-form [:comment]

???+ info "(defmethod)"
    ```clojure
    (defmethod render-form [:comment] [{:keys [docstring raw forms] :as m}]
      (if (str/starts-with? raw "=>")
        (str "Result:" (code-block raw))
        (str raw "\n\n")))
    ```

This is a comment

Result:    ```clojure
    => (+ 2 2)
    ```

## raw->forms

???+ tip "(defn)"
    ```clojure
    (defn raw->forms [raw]
      (binding [clojure.tools.reader/*read-eval* false]
        (let [forms (clojure.tools.reader/read-string raw)
              multimethod? (= (first forms) 'defmethod)]
          {:forms forms
           :verb (first forms)
           :var (second forms)
           :args (if multimethod? (get forms 3) (get forms 2))
           :method-value (when multimethod? (nth forms 2))})))
    ```

comment here

## save-md

Save markdown built from clojure source.

???+ tip "(defn)"
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

???+ tip "(defn)"
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

???+ tip "(defn)"
    ```clojure
    (defn uberdoc! [docs files options]
      (doseq [filename files]
        (save-md filename (assoc options :append true :target docs))))
    ```

