# materiala.markdown-test



??? tip  "(`ns`)"

    ```clojure
    (ns materiala.markdown-test
      (:require [materiala.markdown :as sut]
                [clojure.test :as t :refer (deftest is are)]))
    ```

## `intent`



??? tip  "(`deftest`)"

    ```clojure
    (deftest intent
      (are [x s level] (= x (sut/indent s level))
        "a" "a" 0
        " a" "a" 1
        "  a" "a" 2
        "  a\n  b" "a\nb" 2))
    ```

## `code-block`



??? tip  "(`deftest`)"

    ```clojure
    (deftest code-block
      (are [s code indent] (= s (sut/code-block code indent))
        "```clojure\nx\n```" 'x 0
        "    ```clojure\n    x\n    ```" 'x 4))
    ```

## `function-forms`



??? tip  "(`deftest`)"

    ```clojure
    (deftest function-forms
      (are [calling-forms function-symbol fn-tail]
          (= calling-forms (apply sut/function-forms function-symbol fn-tail))
        '[(f x y) (f x)] 'f '[([x y] 3) ([x] 3)]
        '[(f x) (f x y)] 'f '[([x] 3) ([x y] 3)]
        '[(f x) (f x & args)] 'f '[([x] 3) ([x & args] 3)]))
    ```

## `raw->forms`



??? tip  "(`deftest`)"

    ```clojure
    (deftest raw->forms
      (are [code-string result] (= result (sut/raw->forms code-string))
        "(def hello 3)"
        '{:forms (def hello 3) :verb def :var hello}
        "(defn hello [x] 3)"
        '{:forms (defn hello [x] 3) :verb defn :var hello :valid-call [(hello x)]}
        "(defn hello ([x] 3) ([x y] 3))"
        '{:forms (defn hello ([x] 3) ([x y] 3)) :verb defn :var hello
          :valid-call [(hello x) (hello x y)]}
        "(defmethod hello 3 [{:keys [a b]}] 3)"
        '{:forms (defmethod hello 3 [{:keys [a b]}] 3) :verb defmethod :var hello
          :valid-call [(hello {:keys [a b]})] :method-value 3}
        "(defn hello {:pre (constantly true)} ([m] 3))"
        '{:forms (defn hello {:pre (constantly true)} ([m] 3)), :verb defn, :var hello, :valid-call [(hello m)]}
        "(reg-sub
    :hello
    (fn [m] 3))"
        '{:forms (reg-sub :hello (fn [m] 3)), :verb reg-sub, :var :hello}))
    ```

add test with malformed code.

