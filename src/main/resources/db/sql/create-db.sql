
DROP TABLE IF EXISTS orders;

CREATE TABLE orders (
  id           INTEGER NOT NULL auto_increment PRIMARY KEY,
  userid       VARCHAR(30),
  quantity     DECIMAL,
  pricePerKilo DECIMAL,
  orderType    VARCHAR(10)
);