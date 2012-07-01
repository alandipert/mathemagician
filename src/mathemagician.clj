(ns mathemagician
  "Namespace of functions that are proxies for methods on
   java.lang.Math for maximum developer convenience."
  (:use [clojure.string :only (lower-case join)]))

(def ^{:private true
       :doc "Set of method names not to generate a proxy function for."}
  exclusions #{"min" "max"})

(defn- methods-in
  "Returns a map of method names to Methods implemented in klass."
  [klass]
  (filter (comp #{klass} #(.getDeclaringClass %)) (.getMethods klass)))

(defn- names-arities
  "Given a collection of methods, returns a map of method names to sets of arities."
  [methods]
  (reduce (fn [sigs meth]
            (update-in sigs
                       [(.getName meth)]
                       (fnil conj #{})
                       (count (.getParameterTypes meth))))
          {}
          methods))

(defn- clojurize
  [s]
  (->> s
    (re-seq #"(?:[A-Z][a-z]+|[A-Z]+|[a-z]+)")
    (join "-")
    lower-case))

(defn- fn-impls
  "Returns a list of function implementations corresponding to all
  methods and their arities on klass, excluding any methods in
  exclude-names."
  [exclude-names klass]
  (for [[name arities] (names-arities (methods-in klass))
        :when (not (exclude-names name))]
    (let [args (map #(vec (take % (repeatedly gensym))) arities)
          clj-name (clojurize name)]
      `(defn ~(symbol clj-name)
         ~@(map (fn [argv] (list argv (list* '. klass (symbol name) argv)))
                args)))))

(defn- install-fns
  "Generates proxy functions for klass and installs them in this namespace."
  [klass]
  (eval (cons 'do (fn-impls exclusions klass))))

(install-fns Math)

;;; Before:
;;; (Math/abs -4) ;=> 4
;;; (Math/floor 2.3) ;=> 2.0

;;; Now:
;;; (use 'automath)
;;; (abs -4) ;=> 4
;;; (floor 2.3) ;=> 2.0
