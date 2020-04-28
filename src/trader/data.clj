(ns trader.data
  (:require [incanter.core :as i]
            [next.jdbc :as jdbc]
            [next.jdbc.result-set :as rs]))

(defn connect-to-db
  "Function for connecting to the database."
  []
  (jdbc/get-datasource {:dbtype "postgresql" :dbname "trader" :user "postgres" :password "postgres"}))

(defn get-candlestick-data
  "Function for getting the last kline open time value."
  [symbol interval]
  (->> (jdbc/execute! (connect-to-db) [(str "select * from " symbol interval " order by open_time asc limit 50;")]
                                      {:builder-fn rs/as-unqualified-lower-maps})
       (i/dataset)))

(defn get-candlestick-data-with-ema
  "Function for getting the last kline open time value."
  [symbol interval short long]
  (println (str "select * from (select close_time, close, round(ema(close, " (double (/ 2 (+ 1 short))) ") over(w),8) as e1, round(ema(close, " (double (/ 2 (+ 1 long))) ") over(w),8) as e5 from " symbol interval " window w as (order by open_time asc)) as subq;"))
  (jdbc/execute! (connect-to-db) [(str "select * from (select close_time, close, round(ema(close, " (double (/ 2 (+ 1 short))) ") over(w),8) as e1, round(ema(close, " (double (/ 2 (+ 1 long))) ") over(w),8) as e5 from " symbol interval " window w as (order by open_time asc)) as subq;")]
                                 {:builder-fn rs/as-unqualified-lower-maps}))


