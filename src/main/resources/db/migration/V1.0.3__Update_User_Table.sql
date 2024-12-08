-- Usuń klucze obce zależne od obecnego klucza głównego w tabeli `user`
ALTER TABLE shopping_list DROP CONSTRAINT IF EXISTS shopping_lists_owner_id_fkey;
ALTER TABLE shared_list DROP CONSTRAINT IF EXISTS shared_lists_user_id_fkey;

-- Usuń istniejący klucz główny z tabeli `user`
ALTER TABLE "user" DROP CONSTRAINT IF EXISTS users_pkey;

-- Zmień typ kolumny `id` na UUID, jeśli jeszcze nie jest
ALTER TABLE "user"
    ALTER COLUMN id SET DATA TYPE UUID USING gen_random_uuid(),
ALTER COLUMN id SET NOT NULL;

-- Ustaw `id` jako nowy klucz główny
ALTER TABLE "user" ADD CONSTRAINT pk_user PRIMARY KEY (id);

-- Ponownie dodaj klucze obce dla tabel zależnych
ALTER TABLE shopping_list ADD CONSTRAINT shopping_lists_owner_id_fkey FOREIGN KEY (owner_id) REFERENCES "user" (id) ON DELETE CASCADE;
ALTER TABLE shared_list ADD CONSTRAINT shared_lists_user_id_fkey FOREIGN KEY (user_id) REFERENCES "user" (id) ON DELETE CASCADE;
