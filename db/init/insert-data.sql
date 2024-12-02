-- Insert categories
INSERT INTO category (id, name) VALUES
    (1, 'Electronics'),
    (2, 'Beverages'),
    (3, 'Books'),
    (4, 'Toys'),
    (5, 'Furniture');

-- Insert products
INSERT INTO product (id, name, description, price, image_url, category_id, quantity) VALUES
    (1, 'iPhone 14 Pro', 'Apple iPhone 14 Pro with 256GB storage', 999.99, 'https://example.com/images/iphone14.jpg', 1, 2),
    (2, 'MacBook Pro', 'Apple MacBook Pro 16-inch with M1 Chip', 2499.99, 'https://example.com/images/macbookpro.jpg', 1, 2),
    (3, 'Coca-Cola', 'Classic Coca-Cola soft drink, 330ml can', 0.99, 'https://example.com/images/coca_cola.jpg', 2, 5),
    (4, 'Water Bottle', '500ml purified water bottle', 0.50, 'https://example.com/images/water.jpg', 2, 6),
    (5, 'The Great Gatsby', 'A classic novel by F. Scott Fitzgerald', 10.99, 'https://example.com/images/greatgatsby.jpg', 3, 4),
    (6, 'Chess Set', 'Wooden chess set with handcrafted pieces', 29.99, 'https://example.com/images/chessset.jpg', 4, 7),
    (7, 'Office Chair', 'Ergonomic office chair with lumbar support', 149.99, 'https://example.com/images/officechair.jpg', 5, 8),
    (8, 'Study Desk', 'Compact study desk made of oak wood', 199.99, 'https://example.com/images/studydesk.jpg', 5, 10);