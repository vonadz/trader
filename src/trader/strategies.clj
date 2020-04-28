(ns trader.strategies
    (:require [trader.data :refer :all]))
;; strategies should be symbol and interval agnostic
;; strategies should emit a buy or sell signal, with the symbol and amount
;; 

(defmulti ema-cross (fn [status shorter-ema longer-ema data] status))

(defmethod ema-cross "starting"
;; Strategy starting
  [status shorter-ema longer-ema data]
  (map data )
  (shorter-ema)
)

(defmethod ema-cross "waiting to buy"
;; Buy when short crosses over long
  [status shorter-ema longer-ema data]
  (shorter-ema)
)

(defmethod ema-cross "waiting to sell"
;; Sell when short falls under longer
  [status shorter-ema longer-ema data]
  (longer-ema)
)
  