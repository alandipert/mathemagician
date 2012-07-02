(ns mathemagician-test
  (:use [clojure.test]
        [mathemagician]))

(deftest testing-big-picture
  (testing "at a high level that installed methods of different arities work correctly"
    (is (= java.lang.Double (class (random))))
    (is (= 3 (abs -3)))
    (is (= 9.0 (pow 3 2)))))

(deftest testing-clojure-case
  (testing "that the way we munge camelcase is correct"
    (is (= "next-thing-blah" (@#'mathemagician/clojure-case "nextThingBlah")))
    (is (= "ieee-remainder" (@#'mathemagician/clojure-case "IEEEremainder")))
    (is (= "another-ieee-remainder" (@#'mathemagician/clojure-case "anotherIEEEremainder")))))

(deftest testing-fn-impls
  (testing "a function implementation is included"
    (is (some
          #{'cos}
          (->> (@#'mathemagician/fn-impls #{"random"} Math) (map second)))))
  (testing "a function implementation is excluded"
    (is (nil?
          (some
            #{'random}
            (->> (@#'mathemagician/fn-impls #{"random"} Math) (map second)))))))
