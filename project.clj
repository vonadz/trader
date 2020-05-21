(defproject trader "0.1.0-SNAPSHOT"
  :description "Trading bot for binance using clojure."
  :url "http://example.com/FIXME"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.clojure/clojure "1.10.1"]
                 [clj-http "3.10.0"]
                 [org.clojure/data.json "1.0.0"]
                 [incanter "1.9.3"]
                 [seancorfield/next.jdbc "1.0.409"]
                 ;;[org.apache.logging.log4j/log4j-api "2.11.0"]
                 ;;[org.apache.logging.log4j/log4j-core "2.11.0"]
                 ;;[org.apache.logging.log4j/log4j-1.2-api "2.11.0"]
                 [org.postgresql/postgresql "42.2.10"]
                 [uncomplicate/neanderthal "0.31.0"]
                 [criterium "0.4.5"]]
  :repl-options {:init-ns trader.core}
  :jvm-opts ^:replace ["--add-opens=java.base/jdk.internal.ref=ALL-UNNAMED"])
