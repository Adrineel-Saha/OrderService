INSERT INTO Orders (User_Id, Status, Created_At) VALUES
(1, 'CREATED',   '2026-02-01 10:00:00'),
(2, 'CREATED',   '2026-02-02 11:30:00'),
(3, 'PAID',      '2026-02-03 09:15:00'),
(4, 'SHIPPED',   '2026-02-04 14:45:00'),
(5, 'CANCELLED', '2026-02-05 16:20:00');

INSERT INTO Order_Items (Product_Id, Quantity, Order_Id) VALUES
(1, 2, 1),
(2, 1, 2),
(3, 3, 3),
(4, 1, 4),
(5, 2, 5);