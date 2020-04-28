(ns trader.core
  (:require [trader.data :refer :all]
            [clj-http.client :as client]
            [clojure.data.json :as json]
            [clojure.java.io :as io]
            [next.jdbc :as jdbc]
            [next.jdbc.result-set :as rs]
            [next.jdbc.sql :as sql]
            [clojure.string :as str]
            [clojure.edn :as edn]
            [incanter.core :as i]))

(defn get-symbol-data
  "Function for getting the symbol data from binance"
  [symbol interval startTime limit]
  (let [response (client/get "https://api.binance.com/api/v3/klines" 
                  {:query-params {:symbol symbol :interval interval :startTime startTime :limit limit}})]
    (if (= 429 (response :status))
      (println "got a 429! need to sleep secs: " (get-in response [:headers :Retry-After])) 
      (edn/read-string (response :body)))))

(defn create-db
  "Creates the database if it doesn't exist."
  [symbol interval]
  (jdbc/execute! (jdbc/get-datasource {:dbtype "postgresql" :dbname "trader" :user "postgres" :password "postgres"})
                 [(str "CREATE TABLE IF NOT EXISTS " symbol interval " (
                 open_time bigint PRIMARY KEY,
                 open numeric(14,8) not null,
                 high numeric(14,8) not null,
                 low numeric(14,8) not null,
                 close_time bigint not null,
                 volume numeric(16,8) not null, 
                 close numeric(14,8) not null,
                 quote_asset_volume numeric(16,8) not null,
                 number_of_trades int not null,
                 taker_buy_base_asset_volume numeric(16,8) not null,
                 taker_buy_quote_asset_volume numeric(16,8) not null,
                 ignore numeric(16,8) not null
                 );")]))

(declare get-time-offset)

(defn get-last-candle-time
  "Function for getting the last kline open time value."
  [symbol interval limit]
  (let [last-time (jdbc/execute! (connect-to-db) [(str "select open_time from " symbol interval " order by open_time asc limit 1;")]
                                                 {:builder-fn rs/as-unqualified-lower-maps})]
    (if (seq last-time)
;; yes records, need to start from the end of the records we have.
      ;;(println last-time)
      (- (get (first last-time) :open_time) (get-time-offset limit interval))
;; no records, need to start from the beginning.
      (- (inst-ms (java.util.Date.)) (get-time-offset limit interval)))))

(defn get-time-offset
  "Function for finding the time offset based off of the limit and interval"
  [limit interval]
  (* (get {:1m 60000 :3m 180000 :5m 300000 :15m 900000 :30m 1800000 :1h 3600000 :2h 7200000 :4h 14400000 :6h 21600000 :8h 28800000 :12h 43200000 :1d 86400000 :3d 259200000 :1w 604800000 :1M 2592000000} (keyword interval)) limit))
  
(defn format-data
  "Function for formatting the edn data string to postgres compatible types."
  [data]
  (for [row data]
    (map bigdec row)))

(declare fetch-candlestick-data)

(defn populate-database
  "Function for inserting the data into the database."
  [data symbol interval limit]
  (sql/insert-multi! (connect-to-db) (str symbol interval) ["open_time" "open" "high" "low" "close" "volume" "close_time" "quote_asset_volume" "number_of_trades" "taker_buy_base_asset_volume" "taker_buy_quote_asset_volume" "ignore"] data {:suffix "ON CONFLICT DO NOTHING"})
  ;;(Thread/sleep 500)
  (fetch-candlestick-data symbol (- (first (first data)) (get-time-offset limit interval)) interval limit))

(defn fetch-candlestick-data
  "Function for getting all of the candlestick data for a symbol."
  [symbol last-time interval limit]
  (if (< (bigint last-time) 1502942400000)
    (println "no more data!")
    (let [data (get-symbol-data symbol interval last-time limit)]
      (populate-database (format-data data) symbol interval limit))))

(defn average [coll]
  (/ (reduce + coll)
      (count coll)))

(defn ma [period coll] 
  (lazy-cat (repeat (dec period) nil) 
            (map average (partition period 1  coll))))