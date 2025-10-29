mysql <<EOFMYSQL
use ejc012;
show tables;

DROP TABLE IF EXISTS Purchase;
DROP TABLE IF EXISTS Copy;
DROP TABLE IF EXISTS Book;
DROP TABLE IF EXISTS BookStore;

CREATE TABLE BookStore(
bookstoreID int,
bookstoreName varchar(25) NOT NULL,
state char(2) NOT NULL,
city varchar(15) NOT NULL,
PRIMARY KEY 	(bookstoreID)
);

CREATE TABLE Book(
bookID int,
bookName varchar(25) NOT NULL,
author varchar(25) NOT NULL,
publicationDate date,
type char(3),
PRIMARY KEY 	(bookID),
CHECK(type IN ('fic','non'))
);

CREATE TABLE Copy(
copyID int,
bookstoreID int,
bookID int,
price decimal(4,2),
PRIMARY KEY 	(copyID),
FOREIGN KEY(bookstoreID) REFERENCES BookStore(bookstoreID) ON DELETE RESTRICT ON UPDATE CASCADE,
FOREIGN KEY(bookID) REFERENCES Book(bookID) ON DELETE SET NULL ON UPDATE CASCADE 
);

CREATE TABLE Purchase(
purchaseID int,
copyID int,
date date,
time time,
PRIMARY KEY 	(purchaseID),
CHECK (date BETWEEN '2025-01-01' AND CURDATE()),
FOREIGN KEY (copyID) REFERENCES Copy(copyID) ON DELETE CASCADE ON UPDATE CASCADE
);

INSERT INTO BookStore(bookstoreID, bookstoreName, state, city) VALUES
(0, 'Barnes and Noble', 'MO', 'Kansas City'),
(5, 'Dickson Street Bookshop', 'AR', 'Fayetteville'),
(11, 'Pearl\'s Books', 'AR', 'Fayetteville');

INSERT INTO Book(bookID, bookName, author, publicationDate, type) VALUES
(9, 'Brave New World', 'Aldous Huxley', '1932-02-04', 'fic'),
(10, 'To Kill a Mockingbird', 'Harper Lee', '1960-07-11', 'fic'),
(13, 'Godel, Escher, Bach', 'Douglas Hofstadter', '1979-01-01', 'non'),
(21, 'The Brothers Karamazov', 'Fyodor Dostoevsky', '1880-11-01', 'fic'),
(15, 'The Hiding Place', 'Corrie Ten Boom', '1971-01-01', 'non'),
(16, 'The Grapes of Wrath', 'John Steinbeck', '1939-04-14', 'fic'),
(37, 'Watchmen', 'Alan Moore', '1986-05-13', 'fic'),
(4, 'Life of Pi', 'Yann Martel', '2001-09-11', 'fic'),
(29, 'Unbroken', 'Laura Hillenbrand', '2010-11-16', 'non'),
(42, 'The Return of The King', 'J. R. R. Tolkien', '1955-10-20', 'fic');

INSERT INTO Copy(copyID, bookstoreID, bookID, price) VALUES
(0, 0, 42, 25.00),
(1, 0, 13, 35.00),
(2, 0, 37, 18.75),
(3, 0, 10, 12.00),
(4, 0, 29, 15.00),
(5, 5, 16, 10.00),
(6, 5, 42, 15.00),
(7, 5, 4, 8.25),
(8, 5, 21, 21.00),
(9, 11, 10, 15.00),
(10, 11, 9, 12.00),
(11, 11, 15, 10.00);

INSERT INTO Purchase(purchaseID, copyID, date, time) VALUES
(0, 6, '2025-01-15', '10:32'),
(1, 8, '2025-01-18', '08:45'),
(2, 11, '2025-01-04', '14:37'),
(3, 4, '2025-01-29', '18:00'),
(4, 5, '2025-02-01', '12:18'),
(5, 1, '2025-02-03', '21:30'),
(6, 10, '2025-02-09', '16:18'),
(7, 7, '2025-02-14', '23:00'),
(8, 9, '2025-02-21', '20:05');

EOFMYSQL
