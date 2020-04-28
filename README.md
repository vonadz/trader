# trader

A crypto trader written in Clojure.

## Usage

The default assumes you have a Postgres database running called trader with default postgres user. If you want to use a differently named database/user, edit the connect-to-db function in trader/src/trader/core.clj.

As of now, fetching the data from Binance won't stop automatically, so you need to manually stop the loop when the number of rows in the database stops changing.

## License

Copyright © 2020 FIXME

This program and the accompanying materials are made available under the
terms of the Eclipse Public License 2.0 which is available at
http://www.eclipse.org/legal/epl-2.0.

This Source Code may also be made available under the following Secondary
Licenses when the conditions for such availability set forth in the Eclipse
Public License, v. 2.0 are satisfied: GNU General Public License as published by
the Free Software Foundation, either version 2 of the License, or (at your
option) any later version, with the GNU Classpath Exception which is available
at https://www.gnu.org/software/classpath/license.html.
# trader
