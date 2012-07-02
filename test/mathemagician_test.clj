(ns mathemagician-test
  (:use [clojure.test]
        [mathemagician]))

(deftest testing-big-picture
  (testing "at a high level that installed methods of different arities work correctly"
    (is (= java.lang.Double (class (random))))
    (is (= 3 (abs -3)))
    (is (= 9.0 (pow 3 2)))))

(defmacro with-private-vars
  "Refers private fns from ns and runs tests in context."
  [[ns vars] & tests]
  `(let ~(reduce #(conj %1 %2 `@(ns-resolve '~ns '~%2)) [] vars)
     ~@tests))

(with-private-vars [mathemagician [clojure-case fn-impls]]
  
  (deftest testing-clojure-case
    (testing "that the way we munge camelcase is correct"
      (are [clj java] (= clj (clojure-case java))
           "next-thing-blah" "nextThingBlah"
           "ieee-remainder" "IEEEremainder"
           "another-ieee-remainder" "anotherIEEEremainder")))
  
  (deftest testing-fn-impls
    (testing "a function implementation is included"
      (is (some
           #{'cos}
           (->> (fn-impls #{"random"} Math) (map (comp second second))))))
    (testing "a function implementation is excluded"
      (is (nil?
           (some
            #{'random}
            (->> (fn-impls #{"random"} Math) (map (comp second second)))))))))