(ns mathemagician
  "Namespace of functions that are proxies for methods on
   java.lang.Math for maximum developer convenience."
  (:use [clojure.string :only (lower-case replace)]))

(def ^{:private true
       :doc "Set of method names not to generate a proxy function for."}
  exclusions #{"min" "max"})

(defn- methods-in
  "Returns Methods implemented in klass."
  [klass]
  (filter (comp #{klass} #(.getDeclaringClass %)) (.getMethods klass)))

(defn- names-sigs
  "Given a collection of methods, returns a map of method names to vectors of signatures."
  [methods]
  (reduce (fn [sigs meth]
            (update-in sigs
                       [(.getName meth)]
                       (fnil conj #{})
                       (->> meth
                            .getParameterTypes
                            (map (comp symbol #(.getName %))))))
          {}
          methods))

(defn- clojure-case
  "Converts camelCase to clojure-case."
  [s]
  (-> s
      lower-case
      (str/replace #"([a-zA-Z])(?=[A-Z])" "$1-")))

(defn- genmulti [klass mname sigs]
  (let [msym (symbol mname)
        fname (symbol (clojure-case mname))
        ;; TODO: handle mixed signatures with one or more supported primitives
        primitive-sigs (filter #(every? '#{long double} %) sigs)
        arities (set (map count sigs))
        multi `(defmulti ~fname #(map class %&))
        methods (for [sig primitive-sigs
                      :let [argv (mapv #(with-meta (gensym) {:tag %}) sig)
                            args (map #(vary-meta % dissoc :tag) argv)]]
                  `(defmethod ~fname
                     ~(mapv {'double Double 'long Long} sig)
                     ~argv
                     ~`(. ~klass ~msym ~@args)))
        default `(defmethod ~fname
                   :default
                   ~@(map #(let [argv (vec (take % (repeatedly gensym)))]
                             `(~argv
                               ~`(. ~klass ~msym ~@argv)))
                          arities))]
    `(do ~multi ~@methods ~default)))

(defn- fn-impls
  "Returns a list of function implementations corresponding to all
  methods and their arities on klass, excluding any methods in
  exclude-names."
  [exclude-names klass]
  (for [[name sigs] (names-sigs (methods-in klass))
        :when (not (exclude-names name))]
    (genmulti klass name sigs)))

(defn- install-fns
  "Generates proxy functions for klass and installs them in this namespace."
  [klass]
  (eval (cons 'do (fn-impls exclusions klass))))

(install-fns Math)