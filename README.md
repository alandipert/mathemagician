# mathemagician

Automatically generate Clojure function proxies for [java.lang.Math](http://docs.oracle.com/javase/7/docs/api/java/lang/Math.html) static methods.

[![Build Status](https://secure.travis-ci.org/alandipert/mathemagician.png?branch=master)](http://travis-ci.org/alandipert/mathemagician)

## Usage

Add this to your `project.clj` if you're using [Leiningen](https://github.com/technomancy/leiningen/):

```clojure
[alandipert/mathemagician "0.0.1"]
```

Then, in the namespace you want math functions in:

```clojure
(ns your-ns
  (:use [mathemagician :only (abs)]))

(abs -3) ;=> 3
```

## Thanks

Thanks to [Patrick Brown](https://github.com/patbrown) for the idea,
[Jon Distad](https://github.com/jondistad) for pairing with me on the
initial implementation, and to the Durham Clojure Roadshow class for
their participation.

## License

Copyright (C) 2012 Alan Dipert

Distributed under the Eclipse Public License, the same as Clojure.
