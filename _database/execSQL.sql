USE koala;
INSERT INTO magasiniernotification (idMagasinier, content) VALUES( 1 , ' notification.content ');


USE koala;

SELECT * from updateLog WHERE tag = 'magNot';















SELECT * from magasinierNotification;
UPDATE magasinierNotification set magasinierNotification.read = FALSE ,magasinierNotification.ignore = FALSE ;

CREATE TABLE IF NOT EXISTS `koala`.`updateLog` (
  `tag` VARCHAR(15) NOT NULL,
  `lastUpdate` DATETIME NULL,
  PRIMARY KEY (`tag`))
  ENGINE = InnoDB;

DROP TRIGGER updateLog;
Create TRIGGER updateLog AFTER INSERT ON magasinierNotification FOR EACH ROW
  UPDATE updateLog set lastUpdate = NOW() WHERE tag = "magNot";

Create TRIGGER deleteLog AFTER DELETE ON magasinierNotification FOR EACH ROW
  UPDATE updateLog set lastUpdate = NOW() WHERE tag = "magNot";

Create TRIGGER updateLogAlt AFTER UPDATE ON magasinierNotification FOR EACH ROW
  UPDATE updateLog set lastUpdate = NOW() WHERE tag = "magNot";
USE koala;
DROP TRIGGER updateLogAlt;


Create TRIGGER updateLog AFTER INSERT ON magasinierNotification FOR EACH ROW
  UPDATE updateLog set lastUpdate = NOW() WHERE tag = "magNot";


DELIMITER //
CREATE TRIGGER updateLogIf AFTER UPDATE ON magasinierNotification
FOR EACH ROW
  BEGIN
    if NEW.read < OLD.read OR NEW.ignore < OLD.ignore THEN
      UPDATE updateLog set lastUpdate = NOW() WHERE tag = "magNot";
    END IF;
  END;//
DELIMITER ;
USE koala;
DROP TRIGGER updateLogIf;


DELETE FROM magasinierNotification WHERE idmagasiniernotification = 1;




SELECT * from updateLog WHERE tag = 'magNot';



INSERT INTO Commande (idCommande, dateCommande, modePaiement, idClient, payer) VALUES (2, 2017-04-04 02:00:00 , 'BON', 1, true );


SELECT * FROM commandeunitaire WHERE idCuisiner=1;








CREATE TABLE IF NOT EXISTS `koala`.`magasinierNotification` (
  `idmagasinierNotification` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `idMagasinier` INT UNSIGNED NOT NULL,
  `idIngredient` INT UNSIGNED NOT NULL,
  `idCuisiner` INT UNSIGNED NOT NULL,
  `content` VARCHAR(255) NULL,
  `read` TINYINT(1) NULL,
  `ignore` TINYINT(1) NULL,
  PRIMARY KEY (`idmagasinierNotification`),
  INDEX `fk_magasinierNotification_magasinier1_idx` (`idMagasinier` ASC),
  INDEX `fk_magasinierNotification_miniStock1_idx` (`idIngredient` ASC, `idCuisiner` ASC),
  CONSTRAINT `fk_magasinierNotification_magasinier1`
  FOREIGN KEY (`idMagasinier`)
  REFERENCES `koala`.`magasinier` (`idMagasinier`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_magasinierNotification_miniStock1`
  FOREIGN KEY (`idIngredient` , `idCuisiner`)
  REFERENCES `koala`.`miniStock` (`idIngredient` , `idCuisiner`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
  ENGINE = InnoDB;




USE koala;
INSERT INTO magasiniernotification (idMagasinier, idIngredient, idCuisiner, magasiniernotification.read, magasiniernotification.ignore) VALUES(1, 1, 2, false, false)

;

select * from commandeunitaire;




USE koala;
INSERT INTO `koala`.`ministock` (`idIngredient`, `idCuisiner`, `quantite`, `seuil`) VALUES ('1', '3', '100', '0');
INSERT INTO `koala`.`ministock` (`idIngredient`, `idCuisiner`, `quantite`, `seuil`) VALUES ('2', '3', '100', '0');
INSERT INTO `koala`.`ministock` (`idIngredient`, `idCuisiner`, `quantite`, `seuil`) VALUES ('3', '3', '100', '0');
INSERT INTO `koala`.`ministock` (`idIngredient`, `idCuisiner`, `quantite`, `seuil`) VALUES ('4', '3', '1000', '0');
INSERT INTO `koala`.`ministock` (`idIngredient`, `idCuisiner`, `quantite`, `seuil`) VALUES ('5', '3', '100', '0');
INSERT INTO `koala`.`ministock` (`idIngredient`, `idCuisiner`, `quantite`, `seuil`) VALUES ('6', '3', '100', '0');
INSERT INTO `koala`.`ministock` (`idIngredient`, `idCuisiner`, `quantite`, `seuil`) VALUES ('7', '3', '100', '0');
INSERT INTO `koala`.`ministock` (`idIngredient`, `idCuisiner`, `quantite`, `seuil`) VALUES ('8', '3', '100', '0');
INSERT INTO `koala`.`ministock` (`idIngredient`, `idCuisiner`, `quantite`, `seuil`) VALUES ('9', '3', '100', '0');
INSERT INTO `koala`.`ministock` (`idIngredient`, `idCuisiner`, `quantite`, `seuil`) VALUES ('10', '3', '100', '0');
INSERT INTO `koala`.`ministock` (`idIngredient`, `idCuisiner`, `quantite`, `seuil`) VALUES ('11', '3', '100', '0');
INSERT INTO `koala`.`ministock` (`idIngredient`, `idCuisiner`, `quantite`, `seuil`) VALUES ('12', '3', '100', '0');
INSERT INTO `koala`.`ministock` (`idIngredient`, `idCuisiner`, `quantite`, `seuil`) VALUES ('13', '3', '100', '0');
INSERT INTO `koala`.`ministock` (`idIngredient`, `idCuisiner`, `quantite`, `seuil`) VALUES ('14', '3', '100', '0');






SELECT * FROM magasinierNotification;