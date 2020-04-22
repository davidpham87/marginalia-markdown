# Marginalia Markdown

Experience to leverage [marginalia](https://github.com/gdeer81/marginalia)
parser and [mkdocs-material](https://squidfunk.github.io/mkdocs-material/) to
create documentation.

# Quick start

``` bash
pip3 install mkdocs-material

# parse your src fodler and generate the files into ./docs
find src -type "f" -name "*.clj*" | xargs clj -m marginalia-md.core

mkdocs serve # dev
mkdocs build # releas
```
