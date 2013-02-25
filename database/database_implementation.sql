CREATE TABLE User (
  varchar(50) name;
  int id AUTO_INCREMENT;
  PRIMARY KEY(id);
);

CREATE TABLE Debts (
  int debtid AUTO_INCREMENT;
  int creditorid;
  int debtorid;
  PRIMARY KEY(debtid);
  FOREIGN KEY(creditorid) REFERENCES User(id);
  FOREIGN KEY(debtorid) REFERENCES User(id);
);
