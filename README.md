# mathemagician

Automatically generate Clojure function proxies for java.lang.Math static methods.

[![Build Status](https://secure.travis-ci.org/alandipert/mathemagician.png?branch=master)](http://travis-ci.org/alandipert/mathemagician)

## Usage

```clojure
(use 'mathemagician)

(abs -3) ;=> 3
```

## Thanks

Thanks to Patrick Brown for the idea, Jon Distad for pairing with me
on the initial implementation, and to the Durham Clojure Roadshow
class for their participation.

## License

Copyright (C) 2012 Alan Dipert

Distributed under the Eclipse Public License, the same as Clojure.
