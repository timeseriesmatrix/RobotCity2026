BEGIN;

-- 1) New parent table
CREATE TABLE IF NOT EXISTS shops (
    id SERIAL PRIMARY KEY,
    name TEXT NOT NULL,
    pos_info TEXT,
    contact_info TEXT,
    location TEXT
);

-- 2) Seed default/legacy shop row
INSERT INTO shops (id, name, pos_info)
VALUES (0, 'Default Shop', 'Unassigned')
ON CONFLICT (id) DO NOTHING;

-- 3) Add shop_id to existing tables (default to 0, backfill, then enforce FK)
ALTER TABLE ocr_scans       ADD COLUMN IF NOT EXISTS shop_id INTEGER NOT NULL DEFAULT 0;
ALTER TABLE suppliers       ADD COLUMN IF NOT EXISTS shop_id INTEGER NOT NULL DEFAULT 0;
ALTER TABLE products        ADD COLUMN IF NOT EXISTS shop_id INTEGER NOT NULL DEFAULT 0;
ALTER TABLE purchase_orders ADD COLUMN IF NOT EXISTS shop_id INTEGER NOT NULL DEFAULT 0;
ALTER TABLE purchase_items  ADD COLUMN IF NOT EXISTS shop_id INTEGER NOT NULL DEFAULT 0;
ALTER TABLE customer_orders ADD COLUMN IF NOT EXISTS shop_id INTEGER NOT NULL DEFAULT 0;
ALTER TABLE order_items     ADD COLUMN IF NOT EXISTS shop_id INTEGER NOT NULL DEFAULT 0;

-- Broaden customer_orders.order_type check constraint to include 'retail'
DO $$
BEGIN
    IF EXISTS (
        SELECT 1
        FROM pg_constraint c
        JOIN pg_class t ON c.conrelid = t.oid
        WHERE c.conname = 'customer_orders_order_type_check'
          AND t.relname = 'customer_orders'
          AND pg_get_constraintdef(c.oid) NOT LIKE '%retail%'
    ) THEN
        ALTER TABLE customer_orders DROP CONSTRAINT customer_orders_order_type_check;
    END IF;

    IF NOT EXISTS (
        SELECT 1
        FROM pg_constraint c
        JOIN pg_class t ON c.conrelid = t.oid
        WHERE c.conname = 'customer_orders_order_type_check'
          AND t.relname = 'customer_orders'
    ) THEN
        ALTER TABLE customer_orders
        ADD CONSTRAINT customer_orders_order_type_check
        CHECK (order_type IN ('dine-in', 'takeaway', 'delivery', 'retail'));
    END IF;
END$$;

-- 4) Backfill existing rows explicitly (harmless if already 0)
UPDATE ocr_scans       SET shop_id = 0 WHERE shop_id IS NULL;
UPDATE suppliers       SET shop_id = 0 WHERE shop_id IS NULL;
UPDATE products        SET shop_id = 0 WHERE shop_id IS NULL;
UPDATE purchase_orders SET shop_id = 0 WHERE shop_id IS NULL;
UPDATE purchase_items  SET shop_id = 0 WHERE shop_id IS NULL;
UPDATE customer_orders SET shop_id = 0 WHERE shop_id IS NULL;
UPDATE order_items     SET shop_id = 0 WHERE shop_id IS NULL;

-- 5) Add foreign keys (ON DELETE SET DEFAULT keeps rows if a shop is removed)
DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM information_schema.table_constraints 
                   WHERE constraint_name = 'ocr_scans_shop_fk' 
                   AND table_schema = 'public' 
                   AND table_name = 'ocr_scans') THEN
        ALTER TABLE ocr_scans
            ADD CONSTRAINT ocr_scans_shop_fk
            FOREIGN KEY (shop_id) REFERENCES shops(id) ON DELETE SET DEFAULT;
    END IF;
END$$;

DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM information_schema.table_constraints 
                   WHERE constraint_name = 'suppliers_shop_fk' 
                   AND table_schema = 'public' 
                   AND table_name = 'suppliers') THEN
        ALTER TABLE suppliers
            ADD CONSTRAINT suppliers_shop_fk
            FOREIGN KEY (shop_id) REFERENCES shops(id) ON DELETE SET DEFAULT;
    END IF;
END$$;

DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM information_schema.table_constraints 
                   WHERE constraint_name = 'products_shop_fk' 
                   AND table_schema = 'public' 
                   AND table_name = 'products') THEN
        ALTER TABLE products
            ADD CONSTRAINT products_shop_fk
            FOREIGN KEY (shop_id) REFERENCES shops(id) ON DELETE SET DEFAULT;
    END IF;
END$$;

DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM information_schema.table_constraints 
                   WHERE constraint_name = 'purchase_orders_shop_fk' 
                   AND table_schema = 'public' 
                   AND table_name = 'purchase_orders') THEN
        ALTER TABLE purchase_orders
            ADD CONSTRAINT purchase_orders_shop_fk
            FOREIGN KEY (shop_id) REFERENCES shops(id) ON DELETE SET DEFAULT;
    END IF;
END$$;

DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM information_schema.table_constraints 
                   WHERE constraint_name = 'purchase_items_shop_fk' 
                   AND table_schema = 'public' 
                   AND table_name = 'purchase_items') THEN
        ALTER TABLE purchase_items
            ADD CONSTRAINT purchase_items_shop_fk
            FOREIGN KEY (shop_id) REFERENCES shops(id) ON DELETE SET DEFAULT;
    END IF;
END$$;

DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM information_schema.table_constraints 
                   WHERE constraint_name = 'customer_orders_shop_fk' 
                   AND table_schema = 'public' 
                   AND table_name = 'customer_orders') THEN
        ALTER TABLE customer_orders
            ADD CONSTRAINT customer_orders_shop_fk
            FOREIGN KEY (shop_id) REFERENCES shops(id) ON DELETE SET DEFAULT;
    END IF;
END$$;

DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM information_schema.table_constraints 
                   WHERE constraint_name = 'order_items_shop_fk' 
                   AND table_schema = 'public' 
                   AND table_name = 'order_items') THEN
        ALTER TABLE order_items
            ADD CONSTRAINT order_items_shop_fk
            FOREIGN KEY (shop_id) REFERENCES shops(id) ON DELETE SET DEFAULT;
    END IF;
END$$;

COMMIT;
