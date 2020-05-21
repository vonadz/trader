(ns trader.nn
    (:require [uncomplicate.commons.core :refer [with-release let-release Releaseable release]]
              [uncomplicate.neanderthal
                [native :refer [dv dge]]
                [core :refer [mv! axpy! scal! transfer!]]
                [vect-math :refer [tanh! linear-frac!]]]
              [criterium.core :refer [quick-bench]])
    (import (clojure.lang.IFn)))

(defprotocol Parameters
  (weights [this])
  (bias [this]))

(deftype FullyConnectedInference [w b activ-fn]
  Releaseable
  (release [_]
    (release w)
    (release b))
  Parameters
  (weights [this] w)
  (bias [this] b)
  IFn
  (invoke [_ x ones a]
    (activ-fn (rk! -1.0 b ones (mm! 1.0 w x 0.0 a)))))

(defn fully-connected [activ-fn in-dim out-dim]
  (let-release [w (dge out-dim in-dim)
                bias (dv out-dim)]
    (->FullyConnectedInference w bias activ-fn)))

(defn sigmoid! [x]
  (linear-frac! 0.5 (tanh! (scal! 0.5 x)) 0.5))

(defn activ-tanh! [bias x]
  (tanh! (axpy! -1.0 bias x)))

