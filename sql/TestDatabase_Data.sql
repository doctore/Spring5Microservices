INSERT INTO eat.pizza (id, name, cost) 
VALUES (1, 'Carbonara', 7.50)
      ,(2, 'Hawaiian', 8)
      ,(3, 'Margherita', 7);

SELECT setval('eat.pizza_id_seq', (SELECT count(*) FROM eat.pizza));

INSERT INTO eat.ingredient (id, name) 
VALUES (1, 'Bacon')
      ,(2, 'Cheese')
      ,(3, 'Egg')
      ,(4, 'Ham')
      ,(5, 'Mozzarella')
      ,(6, 'Oregano')
      ,(7, 'Parmesan')
      ,(8, 'Pineapple')
      ,(9, 'Tomato sauce');

SELECT setval('eat.ingredient_id_seq', (SELECT count(*) FROM eat.ingredient));


INSERT INTO eat.pizza_ingredient (pizza_id, ingredient_id) 
VALUES (1, 1), (1, 3), (1, 5), (1, 7)
      ,(2, 2), (2, 4), (2, 8)
      ,(3, 5), (3, 6), (3, 9);


INSERT INTO eat.order (id, code, created) 
VALUES (1, 'Order 1', '2018-12-31 16:00:00'::TIMESTAMP)
      ,(2, 'Order 2', '2019-01-02 18:00:00'::TIMESTAMP);
  
SELECT setval('eat.order_id_seq', (SELECT count(*) FROM eat.order));


INSERT INTO eat.order_line(id, order_id, pizza_id, cost, amount)
VALUES (1, 1, 1, 15, 2), (2, 1, 2, 8, 1)
      ,(3, 2, 1, 7.50, 1), (4, 2, 2, 16, 2), (5, 2, 3, 21, 3);

SELECT setval('eat.order_line_id_seq', (SELECT count(*) FROM eat.order_line));





