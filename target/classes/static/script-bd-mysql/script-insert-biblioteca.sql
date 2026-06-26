/* USAR LA BASE DE DATOS CREADA */
USE `db_appbiblioteca` ;

INSERT INTO libros (isbn, titulo, autor, editorial, categoria_id, precio_unitario, stock, url_imagen, estado, eliminado) VALUES
('9780132350884', 'Clean Code', 'Robert C. Martin', 'Prentice Hall', 1, 189.90, 50, 'https://images.cdn3.buscalibre.com/fit-in/360x360/10/fb/10fb170d7732b7dca25ebb81ded2572d.jpg', 'ACTIVO', FALSE),
('9780134494166', 'Clean Architecture', 'Robert C. Martin', 'Prentice Hall', 3, 199.90, 40, 'https://m.media-amazon.com/images/I/619ht2WrGTL._UF1000,1000_QL80_.jpg', 'ACTIVO', FALSE),
('9781617294945', 'Spring in Action', 'Craig Walls', 'Manning', 1, 179.50, 50, 'https://m.media-amazon.com/images/I/71zlUV5cBDL._AC_UF1000,1000_QL80_.jpg', 'ACTIVO', FALSE),
('9780321125217', 'Domain-Driven Design', 'Eric Evans', 'Addison-Wesley', 3, 220.00, 30, 'https://m.media-amazon.com/images/I/81ykBjVaUjL.jpg', 'ACTIVO', FALSE),
('9780134685991', 'Effective Java', 'Joshua Bloch', 'Addison-Wesley', 1, 185.00, 60, 'https://m.media-amazon.com/images/I/7167aaVxs3L.jpg', 'ACTIVO', FALSE),
('9781449373320', 'Designing Data-Intensive Applications', 'Martin Kleppmann', 'OReilly', 2, 210.00, 30, 'https://alfaomega.com.mx/wp-content/uploads/2023/07/6-203.png', 'ACTIVO', FALSE),
('9780596009205', 'Head First Design Patterns', 'Eric Freeman', 'OReilly', 1, 165.00, 50, 'https://m.media-amazon.com/images/I/71crsCqQ34L._AC_UF1000,1000_QL80_.jpg', 'ACTIVO', FALSE),
('9781492078005', 'SQL Antipatterns', 'Bill Karwin', 'Pragmatic Bookshelf', 2, 145.00, 40, 'https://m.media-amazon.com/images/I/81ThYDR0oaL._UF1000,1000_QL80_.jpg', 'ACTIVO', FALSE),
('9780307887894', 'The Lean Startup', 'Eric Ries', 'Crown Business', 4, 120.00, 50, 'https://m.media-amazon.com/images/I/71lpDUnEbQL._UF1000,1000_QL80_.jpg', 'ACTIVO', FALSE),
('9780307474278', 'Cien años de soledad', 'Gabriel García Márquez', 'Vintage Español', 5, 95.00, 100, 'https://www.penguinlibros.com/pe/6306586/cien-anos-de-soledad.jpg', 'ACTIVO', FALSE);
