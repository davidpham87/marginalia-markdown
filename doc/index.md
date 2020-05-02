# Materiala

Experience to leverage [marginalia](https://github.com/gdeer81/marginalia)
parser and [mkdocs-material](https://squidfunk.github.io/mkdocs-material/) to
create documentation.

Example: https://davidpham87.github.io/materiala/

## Rationale

I **really** liked [calva.io](https://calva.io) website for documentation [I
still use emacs+cider] , and I saw it was based `mkdocs-material`. Since then,
I wanted to use it for my own documentation. We already have marginalia, codox
and cljdoc, so this library does not add a lot, but I still wanted to have a
nice UI for personal project and as a nice project. My only experience before
with auto-documentation/literal programming was org-file, marginalia and codox.

## Technical Solution

Marginalia API was *easier* to leverage (and marginalia had a nice website),
although codox (because neanderthal and the uncomplicate ecosystem) was
considered for usage and extension by coding a specific writer.

## Quick start

``` bash
pip3 install mkdocs-material

# parse your src fodler and generate the files into ./doc
clj -m materiala.core -d doc src

mkdocs serve # dev
mkdocs build # release
```

## Example mkdocs.yml

Here is an example of `mkdocs.yml` file that could be used for a github
repository.


``` yaml
site_name: Materiala
repo_url: https://github.com/davidpham87/materiala
repo_name: github
docs_dir: doc
site_dir: docs # github like this one for displaying your website

theme:
  name: material
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

extra_javascript:
  - https://cdnjs.cloudflare.com/ajax/libs/mathjax/2.7.0/MathJax.js?config=TeX-MML-AM_CHTML

plugins:
  - search # necessary for search to work
  - awesome-pages
```

## License

Copyright (C) 2020 David Pham

Distributed under the Eclipse Public License, the same as Clojure.
