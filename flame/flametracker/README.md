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
cmake -S /home/liam/projects/RobotCity2026/flame/flametracker \
      -B /home/liam/projects/RobotCity2026/flame/flametracker/build-robotcity
cmake --build /home/liam/projects/RobotCity2026/flame/flametracker/build-robotcity -j
```

## POS shop database connections

`PostgresApi` loads per-shop source databases from `shop_databases.json` at startup. Each shop defines both a `pos` database and an `expense` database. `flametracker` queries those shop databases directly; it no longer depends on a central warehouse database. Example:

```
[
  {
    "shop_id": 1,
    "name": "Downtown Cafe",
    "timezone": "Pacific/Efate",
    "pos": {
      "host": "localhost",
      "port": "5432",
      "dbname": "pos01tst",
      "user_env": "SHOP01_POS_USER",
      "pass_env": "SHOP01_POS_PASS"
    },
    "expense": {
      "host": "localhost",
      "port": "5432",
      "dbname": "exp01tst",
      "user_env": "SHOP01_EXP_USER",
      "pass_env": "SHOP01_EXP_PASS"
    }
  }
]
```

`flametracker` refuses write-capable connections to databases whose names do not end with `tst`, unless `FLAME_ALLOW_PROD_WRITE=1` is set. This keeps refactor/test work away from live shop databases by default.

## Expense tracker schema

Each shop expense database owns a `tracker` schema for OCR, review, and purchase-tracking state:

- `tracker.ocr_scans`
- `tracker.suppliers`
- `tracker.products`
- `tracker.purchase_orders`
- `tracker.purchase_items`
- `tracker.receipt_reviews`
- `tracker.job_runs`

## Receipt OCR prompt template

The receipt vision system prompt now lives in:

- `prompts/receipt_vision_system_prompt.txt`

`flametracker` loads that file at runtime on every OCR request, so you can tune the prompt without editing C++ strings or rebuilding the binary. The loader resolves the template from:

- `FLAME_RECEIPT_PROMPT_TEMPLATE_PATH`, if set
- `prompts/receipt_vision_system_prompt.txt` in the current tree
- ancestor directories of the current working directory or executable

Keep the placeholder below if you want dynamic per-shop history to be injected inside the template:

- `{{SHOP_CONTEXT_BLOCK}}`

If the template file cannot be found, `flametracker` falls back to an embedded copy of the prompt and logs a warning once.

Create the schema in the selected shop expense DB with:

```bash
curl -X POST http://localhost:20000/init_expense_tracker \
  -H 'Content-Type: application/json' \
  -d '{"shop_id":1}'
```

The OCR and expense endpoints now require `shop_id` in the request body:

- `POST /receipt_upload` with `{"shop_id":1,"file_name":"receipt.png","mime_type":"image/png","content_base64":"..."}`
- `POST /receipt_queue` with `{"shop_id":1,"ocr_status":"","limit":100}`
- `POST /receipt_detail` with `{"shop_id":1,"ocr_id":123}`
- `POST /receipt_page_image` with `{"shop_id":1,"page_id":456}`
- `POST /receipt_run_ocr` with `{"shop_id":1,"ocr_id":123}`
- `POST /receipt_save_draft` with `{"shop_id":1,"ocr_id":123,"drafts":[...],"review_note":"...","reviewed_by":"..."}`
- `POST /purchased_summary` with `{"shop_id":1,"start_time":"...","end_time":"..."}`
- `POST /db_schema_overview` with `{"shop_id":1,"source_kind":"pos"}`
- `POST /execute_sql` with `{"shop_id":1,"source_kind":"expense","sql":"SELECT ..."}`
- `POST /table_update` with `{"shop_id":1,"source_kind":"expense",...}`
- `POST /table_delete` with `{"shop_id":1,"source_kind":"expense",...}`

## Current architecture

- Sales summaries read directly from each shop POS database.
- Purchase summaries and OCR workflows read/write directly in each shop expense database.
- There is no central fact sync path in this repo anymore.
