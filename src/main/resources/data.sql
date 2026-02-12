INSERT INTO Orders (User_Id, Status, Created_At) VALUES
(101, 'CREATED',   '2026-02-01 10:00:00'),
(102, 'CREATED',   '2026-02-02 11:30:00'),
(103, 'PAID',      '2026-02-03 09:15:00'),
(101, 'SHIPPED',   '2026-02-04 14:45:00'),
(104, 'CANCELLED', '2026-02-05 16:20:00');

INSERT INTO Order_Items (Product_Id, Quantity, Price, Order_Id) VALUES
(1001, 2,  499.50, 1),
(1002, 1, 1299.00, 2),
(1003, 3,  199.99, 3),
(1004, 1, 2499.00, 4),
(1002, 2, 1299.00, 5);