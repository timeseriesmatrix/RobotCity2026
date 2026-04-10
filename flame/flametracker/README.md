# Flametracker

A Platform that records and analyses data from any devices of businesses.

## Build prerequisites

On Ubuntu/Debian, install the native development packages before configuring:

```bash
sudo apt install build-essential cmake pkg-config \
  libssl-dev libopencv-dev libtesseract-dev libpqxx-dev libpoppler-cpp-dev \
  libcairo2-dev libarrow-dev libparquet-dev
```

If this checkout was moved or copied from another path, re-run CMake against the current source tree instead of reusing an old `build/` directory cache. Example:

```bash
cmake -S /home/liam/projects/RobotCity26/flametracker \
      -B /home/liam/projects/RobotCity26/flametracker/build-robotcity
cmake --build /home/liam/projects/RobotCity26/flametracker/build-robotcity -j
```

## POS shop database connections

`PostgresApi` loads external FloreantPOS database credentials from `shop_databases.json` at startup. Each entry is an object with a required `shop_id` and either a full `conninfo` string or individual fields `host`, `port`, `dbname`, `user`, `password`. Example:

```
[
  {
    "shop_id": 1,
    "name": "Downtown Cafe (FloreantPOS)",
    "host": "localhost",
    "port": "5432",
    "dbname": "floreant_shop1",
    "user": "floreant",
    "password": "CHANGE_ME"
  },
  {
    "shop_id": 2,
    "conninfo": "host=pos-db.internal port=5432 dbname=floreant_shop2 user=floreant password=CHANGE_ME"
  }
]
```

### API to sync POS data

Send `POST /sync_pos_shops` with optional JSON body `{ "shop_ids": [1, 2], "reset_pos": true }` to ingest FloreantPOS `ticket`, `ticket_item`, and `menu_item` data from the configured shop databases into `customer_orders`, `order_items`, and `products` in FlameTrack. If `shop_ids` is omitted, all configured shops are processed. Set `reset_pos` to remove only POS-sourced rows for the selected shop(s) before syncing.
