-- Adiciona a nova coluna
ALTER TABLE tb_product
ADD COLUMN image_url VARCHAR(255);

--Atualiza os registros existentes
UPDATE tb_product SET image_url = 'https://example.cm/images/iphone15.jpg' WHERE model = 'iPhone 15 256GB';
UPDATE tb_product SET image_url = 'https://example.cm/images/motog85.jpg' WHERE model = 'G85 256GB';
UPDATE tb_product SET image_url = 'https://example.cm/images/redmi13c.jpg' WHERE model = 'Redmi 13C 256GB';
UPDATE tb_product SET image_url = 'https://example.cm/images/s23ultra.jpg' WHERE model = 'S23 Ultra 256GB';