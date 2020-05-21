(ns trader.backtest
  (:require [trader.data :refer :all]
            [incanter.core :as i]))

(defn dedupe-custom
  "Returns a lazy sequence removing consecutive duplicates in coll.
  Returns a transducer when no collection is provided."
  ([]
   (fn [rf]
     (let [pv (volatile! ::none)]
       (fn
         ([] (rf))
         ([result] (rf result))
         ([result input]
            (let [prior @pv]
              (vreset! pv input)
              (if (= (prior :signal) (input :signal))
                result
                (rf result input))))))))
  ([coll] (sequence (dedupe-custom) coll)))

(defn update-vals-sell
  [trade ledger]
  (let [old-usd (ledger :usd)
        btc (ledger :btc)
        price (trade :close)
        new-usd (+ old-usd (* btc price))]
        ;;(println "Selling")
        ;;(println new-usd)
    (hash-map :usd new-usd :btc 0)))

(defn update-vals-buy
  [trade ledger]
  (let [usd (ledger :usd)
        old-btc (ledger :btc)
        price (trade :close)
        new-btc (+ old-btc (with-precision 10 (/ usd price)))]
        ;;(println "Buying")
        ;;(println new-btc)
    (hash-map :usd 0 :btc new-btc)))

(defn ema-test
  [short-interval long-interval]
  (let [data (map (fn [x] 
         (if (< (x :e1) (x :e5))
           (assoc x :signal "sell")
           (assoc x :signal "buy"))) 
         (get-candlestick-data-with-ema "BTCUSDT" "5m" short-interval long-interval))]
    (hash-map :short short-interval
              :long long-interval
              :ledger (reduce 
                        (fn [ledger trade]
                          (if (= (:signal trade) "sell")
                            (update-vals-sell trade ledger)
                            (update-vals-buy trade ledger)))
                        {:usd 1000 :btc 0}
                        (dedupe-custom data)))))

(defn broad-spectrum-ema-test
  []
  (map (fn [x]
         (map 
           (fn [y]
             (ema-test x y))
           (range 45 50)))
       (range 5 10)))