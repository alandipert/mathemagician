(ns mathemagician
  "Namespace of functions that are proxies for methods on
   java.lang.Math for maximum developer convenience."
  (:use [clojure.string :only (lower-case join)]))

(def ^{:private true
       :doc "Set of method names not to generate a proxy function for."}
  exclusions #{"min" "max"})

(defn- methods-in
  "Returns Methods implemented in klass."
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

(defn- clojure-case
  "Converts camelCase to clojure-case."
  [s]
  (->> s
    (re-seq #"(?:[A-Z][a-z]+|[A-Z]+|[a-z]+)")
    (join "-")
    lower-case))

(defn- gendefn
  "Returns a defn as data with the clojure-case name and arities that
  calls through to the camelCase method of klass."
  [klass name arities]
  (let [argvs (map #(vec (take % (repeatedly gensym))) arities)]
    `(defn ~(symbol (clojure-case name))
       ~@(map #(list % (list* '. klass (symbol name) %)) argvs))))

(defn- fn-impls
  "Returns a list of function implementations corresponding to all
  methods and their arities on klass, excluding any methods in
  exclude-names."
  [exclude-names klass]
  (for [[name arities] (names-arities (methods-in klass))
        :when (not (exclude-names name))]
    (gendefn klass name arities)))

(defn- install-fns
  "Generates proxy functions for klass and installs them in this namespace."
  [klass]
  (eval (cons 'do (fn-impls exclusions klass))))

(install-fns Math)