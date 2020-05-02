# materiala.core

Simple markdown conversion with some cool features, such as math rendering
  $$ e^{i\pi} = -1 $$
  



??? tip "(ns)"
    ```clojure
    (ns materiala.core
      "Simple markdown conversion with some cool features, such as math rendering
      $$ e^{i\\pi} = -1 $$
      "
      (:require [clojure.java.io :as io]
                [clojure.tools.cli :refer (parse-opts)]
                [marginalia.core :as mc]
                [materiala.markdown]))
    ```
## *docs*



??? tip "(def)"
    ```clojure
    (def ^{:dynamic true} *docs* "./docs")
    ```
## cli-opts



??? tip "(def)"
    ```clojure
    (def cli-opts
      [["-d" "--dir DIR" "Directory into which the documentation will be written" :default "./docs"]
       ["-f" "--file FILE" "File into which the documentation will be written"]
       ["-n" "--name NAME" "Project name - if not given will be taken from project.clj"]
       ["-v" "--version VERSION" "Project version - if not given will be taken from project.clj"]
       ["-D" "--desc DESC" "Project description - if not given will be taken from project.clj"]
       ["-a" "--deps DEPS" "Project dependencies in the form <group1>:<artifact1>:<version1>;<group2>...
                                     If not given will be taken from project.clj"]
       ["-m" "--multi" "Generate each namespace documentation as a separate file"  :default true]
       ["-l" "--leiningen" "Generate the documentation for a Leiningen project file."]
       ["-e" "--exclude EXCLUDE"
        "Exclude source file(s) from the document generation process <file1>;<file2>...
                                     If not given will be taken from project.clj"]
       ["-h" "--help" "Show this help"]])
    ```
## run-materiala

Default generation: given a collection of filepaths in a project, find the .clj
   files at these paths and, if Clojure source files are found:

   1. Print out a message to std out letting a user know which files are to be processed;
   1. Create the docs directory inside the project folder if it doesn't already exist;
   1. Call the uberdoc! function to generate the output file at its default location,
     using the found source files and a project file expected to be in its default location.

   If no source files are found, complain with a usage message.

```clojure
(run-materiala & args)
```

??? tip "(defn)"
    ```clojure
    (defn run-materiala
      [& args]
      (let [user-parsed-options (parse-opts args cli-opts)
            {:keys [dir file name version desc deps multi
                    leiningen exclude arguments help]} (:options user-parsed-options)
            files arguments
            sources (distinct (mc/format-sources (seq files)))
            sources (if leiningen (cons leiningen sources) sources)]
        (when help
          (println (:summary user-parsed-options)))
        (if (and sources (not help))
          (binding [*docs* dir]
            (let [project-clj (when (.exists (io/file "project.clj"))
                                (mc/parse-project-file))
                  choose #(or %1 %2)
                  marg-opts (merge-with choose
                                        {:exclude (when exclude (.split exclude ";"))
                                         :leiningen leiningen}
                                        (:marginalia project-clj))
                  opts (merge-with choose
                                   {:name name
                                    :version version
                                    :description desc
                                    :dependencies (mc/split-deps deps)
                                    :multi multi
                                    :marginalia marg-opts}
                                   project-clj)
                  sources (->> sources
                               (filter #(not (mc/source-excluded? % opts)))
                               (into []))]
              (println "Generating Marginalia documentation for the following source files:")
              (doseq [s sources]
                (println "  " s))
              (println)
              (mc/ensure-directory! *docs*)
              (if multi
                (materiala.markdown/multidoc! *docs* sources opts)
                (materiala.markdown/uberdoc!  (str *docs* "/" file) sources opts))
              (println "Done generating your documentation in" *docs*)
              (println "")))
          (when-not help
            (println "Wrong number of arguments passed to Marginalia.")
            (println (:summary user-parsed-options))))))
    ```
## -main

```clojure
(-main & args)
```

??? tip "(defn)"
    ```clojure
    (defn -main [& args]
      (apply run-materiala args))
    ```
