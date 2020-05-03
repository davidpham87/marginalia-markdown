# Materiala (mkdocs-material + marginalia)

![logo](images/logo-black.png) Experience to leverage
[marginalia](https://github.com/gdeer81/marginalia) parser and
[mkdocs-material](https://squidfunk.github.io/mkdocs-material/) to create
documentation.

Example: https://davidpham87.github.io/materiala/

## Rationale

I **really** liked [calva.io](https://calva.io) website for documentation [I
still use emacs+cider] , and I saw it was based `mkdocs-material`. Since then,
I wanted to use it for my own documentation. We already have marginalia, codox
and cljdoc, so this library does not add a lot, but I still wanted to have a
nice UI for personal project and as a nice project. My only experience before
with auto-documentation/literal programming was org-file, marginalia and codox.


## Quick start

``` bash
pip3 install mkdocs-material mkdocs-awesome-pages-plugin

mkdir doc # save the output here
# parse your src folder and generate the files into ./doc
clojure -Sdeps '{:deps {materiala {:git/url "https://github.com/davidpham87/materiala/" :sha "d8fb049709819af52af0f0bfe7423dc8b4f94c7d"}}}' -m materiala.core src

mkdocs build # release, use `mkdocs serve` for dev
cd doc && python3 -m http.server
firefox localhost:8000 # or your most favorite browser (I use google-chrome)
```

## Installation

Add the following dependency to your `deps.edn`

``` clojure
materiala {:git/url "https://github.com/davidpham87/materiala/"
           :sha "b4f85c96b1bf9b96b47714e751b6bff6196533bb"}
```

Change for the latest *sha* code, if required. Note the *group-id* and
*artifact-id* of the package might change once I get enough money to buy a
domain.

## Status

Status: **alpha**, although I will follow
[spec-ulation](https://www.youtube.com/watch?v=oyLBGkS5ICk) to make my best to
avoid breaking changes. I want to be a nice person, so I will do my best to not break
your code. The biggest issue is the library leverages on marginalia's parser.

## Technical Solution

Marginalia API was *easier* to leverage (and marginalia had a nice website),
although codox (because neanderthal and the uncomplicate ecosystem) was
considered for usage and extension by coding a specific writer.

## Is it a good idea?

Well, smashing strings together and having a DSL goes definitively goes against
Clojure's philosophy and main lessons. But from this particular problem, I just
wanted to have some user friendly searchable docs for my coworkers/friends and
thanks to Lisp homoiconicity and Clojure simplicity, parsing is not *too*
hard. For big open source libraries published on Clojars, I would still look at
[cljdoc](https://cljdoc.org/).

## Extension

See [here](extension) for an example of extension.

## Commande line args

``` bash
clojure -m materiala.core -h
  -d, --dir DIR          ./doc  Directory into which the documentation will be written
  -f, --file FILE               File into which the documentation will be written
  -n, --name NAME               Project name - if not given will be taken from project.clj
  -v, --version VERSION         Project version - if not given will be taken from project.clj
  -D, --desc DESC               Project description - if not given will be taken from project.clj
  -a, --deps DEPS               Project dependencies in the form <group1>:<artifact1>:<version1>;<group2>...
                                 If not given will be taken from project.clj
  -m, --multi                   Generate each namespace documentation as a separate file
  -l, --leiningen               Generate the documentation for a Leiningen project file.
  -e, --exclude EXCLUDE         Exclude source file(s) from the document generation process <file1>;<file2>...
                                 If not given will be taken from project.clj
  -h, --help                    Show this help
```

I mainly copied `marginalia` options and adapted it to the newest version of
[clojure.tools.cli](https://github.com/clojure/tools.cli).

### Limitation

The `default` symbol or `:default` key are reserved because on how multimethod
works.

## Dependencies

You will have to install the following python dependencies:

``` bash
pip3 install mkdocs-material mkdocs-awesome-pages-plugin
```

## Example mkdocs.yml

Here is an example of `mkdocs.yml` file that could be used for a github
repository.


``` yaml
site_name: Materiala
repo_url: https://github.com/davidpham87/materiala
edit_uri: edit/master/doc
repo_name: github
docs_dir: doc
site_dir: docs

theme:
  name: material
  logo: images/logo.png
  palette:
    primary: red

markdown_extensions:
  - admonition
  - codehilite:
      guess_lang: false
  - toc:
      permalink: true
  - pymdownx.arithmatex
  - pymdownx.superfences
  - pymdownx.inlinehilite
  - pymdownx.details

extra_javascript: # Math
  - https://cdnjs.cloudflare.com/ajax/libs/mathjax/2.7.0/MathJax.js?config=TeX-MML-AM_CHTML

plugins:
  - search # necessary for search to work
  - awesome-pages # automatic navigation
```

## Examples
- [Materiala](https://davidpham87.github.io/materiala/), obviously.

## See also

- [marginalia](https://github.com/gdeer81/marginalia)
- [mkdocs-material](https://squidfunk.github.io/mkdocs-material/)
- [codox](https://github.com/weavejester/codox)
- [cljdoc](https://cljdoc.org/)


## What's up with logo

Well, I was too lazy to find a proper one, so I took the latex mathematical
integral sign, which (*hopefully*) is still open source.

## Development

Run a repl (add your cider deps as well)

``` bash
clojure -Adev
```

Run the test (todo: create some specs and generate with `test.check`)

``` bash
clojure -Atest
```

## TODO

- Create an option to copy codox/cljcdoc link to source code instead of showing
  the raw code directly.

## License

Copyright (C) 2020 David Pham

Distributed under the Eclipse Public License, the same as Clojure.
