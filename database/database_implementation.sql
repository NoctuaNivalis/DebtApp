CREATE TABLE User (
  name varchar(50) NOT NULL,
  id integer AUTO_INCREMENT NOT NULL,
  PRIMARY KEY(id)
);

CREATE TABLE Debts (
  debtid integer AUTO_INCREMENT NOT NULL,
  creditorid integer NOT NULL,
  debtorid integer NOT NULL,
  PRIMARY KEY(debtid),
  FOREIGN KEY(creditorid) REFERENCES User(id),
  FOREIGN KEY(debtorid) REFERENCES User(id)
);
