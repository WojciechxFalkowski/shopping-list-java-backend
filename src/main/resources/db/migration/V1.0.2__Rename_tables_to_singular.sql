-- Zmieniamy nazwy tabel na liczbę pojedynczą
ALTER TABLE users RENAME TO "user";
ALTER TABLE shopping_lists RENAME TO shopping_list;
ALTER TABLE list_items RENAME TO list_item;
ALTER TABLE shared_lists RENAME TO shared_list;
