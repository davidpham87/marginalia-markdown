(ns materiala.markdown
  "Utilities for parsing code into markdown"
  (:require
   [clojure.java.io]
   [clojure.string :as str]
   [clojure.edn]
   [clojure.tools.reader]
   [marginalia.parser :as p]))

(defn path-to-doc [fn]
  {:ns (p/parse-ns (java.io.File. fn))
   :groups (p/parse-file fn)})

(defn indent
  "Indent string portion"
  [s indent-level]
  (let [indent-space (str/join "" (repeat indent-level " "))]
    (str/join "\n" (map #(str indent-space %) (str/split s #"\n")))))

(defn- code-block
  "Create code block from given string `s`"
  ([s] (code-block s 4))
  ([s indent-level]
   (indent (str "```clojure\n" s "\n" "```\n\n") indent-level)))

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

(defmulti render-form
  (fn [{:keys [type verb]}]
    (case type
      :comment [:comment]
      :code [:code verb]
      :default)))


(defmethod render-form :default [{:keys [docstring raw]}]
  (str docstring "\n\n" (code-block raw)))

(defmethod render-form [:code 'ns] [{:keys [docstring raw forms valid-call] :as m}]
  (render-def-form (assoc m :level 1)))

(defmethod render-form [:code 'def] [{:keys [docstring raw forms valid-call] :as m}]
  (render-def-form m))

(defmethod render-form [:code 'defn] [{:keys [docstring raw forms valid-call] :as m}]
  (render-def-form m))

(defmethod render-form [:code 'defn-] [{:keys [docstring raw forms valid-call] :as m}]
  (render-def-form m))

(defmethod render-form [:code 'defmacro] [{:keys [docstring raw forms valid-call] :as m}]
  (render-def-form m))

(defmethod render-form [:code 'defmulti] [{:keys [docstring raw forms valid-call] :as m}]
  (render-def-form m))

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

(defmethod render-form [:comment] [{:keys [docstring raw forms] :as m}]
  (if (str/starts-with? raw "=>")
    (str "Result:" (code-block raw) "\n\n")
    (str raw "\n\n")))

;; This is a comment for testing comments in forms
;; => (+ 2 2)

(defn- function-forms
  "Retrived function calling forms.

  Inspired by clojure.core/fn and clojure.core/multimethod definition. Returns
  The valid function calls are returned. Expects valid code without docstrings."
  [name & sigs]
  (let [sigs (if (vector? (first sigs)) (list sigs) sigs)]
    (mapv #(seq (into [name] (first %))) sigs)))

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

;; comment here

(defn save-md
  "Save markdown built from clojure source."
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

(defn multidoc!
  "Generate an output file for each of provided namespace."
  [docs files options]
  (doseq [filename files]
    (println filename)
    (let [file-ns (path-to-doc filename)
          target-filename (str docs "/" (str/replace (:ns file-ns) #"\." "/") ".md")]
      (save-md filename (assoc options :target target-filename)))))

(defn uberdoc! [docs files options]
  (doseq [filename files]
    (save-md filename (assoc options :append true :target docs))))

(comment
  (function-forms 'hello '([x y] 3) '([x] 3))
  (raw->forms '(defn hello ([x y] 3) ([x] 3)))

  (raw->forms "(defn hello [x] 3)")
  '{:forms (defn hello [x] 3), :verb defn, :var hello, :valid-call [(hello x)], :method-value nil}

  (raw->forms "(defn hello ([x] 3) ([x y] 3))")
  '{:forms (defn hello ([x] 3) ([x y] 3)), :verb defn, :var hello, :valid-call [(hello x) (hello x y)], :method-value nil}

  (raw->forms "(defmethod hello 3 [{:keys [a b]}] 3)")
  '{:forms (defmethod hello 3 [{:keys [a b]}] 3), :verb defmethod, :var hello, :valid-call [(hello {:keys [a b]})], :method-value 3}
  (raw->forms "(def hello 3)")
  '{:forms (def hello 3), :verb def, :var hello, :valid-call nil, :method-value nil}

  )
