BEGIN;

-- External POS identifiers to align FloreantPOS tickets/items/products with FlameTracker records
ALTER TABLE customer_orders ADD COLUMN IF NOT EXISTS pos_ticket_id BIGINT;
ALTER TABLE order_items     ADD COLUMN IF NOT EXISTS pos_ticket_item_id BIGINT;
ALTER TABLE products        ADD COLUMN IF NOT EXISTS pos_menu_item_id BIGINT;

-- Enforce per-shop uniqueness to avoid double-ingesting the same POS records
DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM pg_indexes
        WHERE schemaname = 'public' AND indexname = 'customer_orders_shop_ticket_uidx'
    ) THEN
        CREATE UNIQUE INDEX customer_orders_shop_ticket_uidx
            ON customer_orders (shop_id, pos_ticket_id);
    END IF;
END$$;

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM pg_indexes
        WHERE schemaname = 'public' AND indexname = 'order_items_shop_ticket_item_uidx'
    ) THEN
        CREATE UNIQUE INDEX order_items_shop_ticket_item_uidx
            ON order_items (shop_id, pos_ticket_item_id);
    END IF;
END$$;

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM pg_indexes
        WHERE schemaname = 'public' AND indexname = 'products_shop_menu_item_uidx'
    ) THEN
        CREATE UNIQUE INDEX products_shop_menu_item_uidx
            ON products (shop_id, pos_menu_item_id);
    END IF;
END$$;

COMMIT;
