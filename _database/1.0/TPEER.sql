# -- MySQL Script generated by MySQL Workbench
# -- 02/28/17 11:32:15
# -- Model: New Model    Version: 1.0
# -- MySQL Workbench Forward Engineering
#
# SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
# SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
# SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='TRADITIONAL,ALLOW_INVALID_DATES';
#
# -- -----------------------------------------------------
# -- Schema BDD1
# -- -----------------------------------------------------
#
# -- -----------------------------------------------------
# -- Schema BDD1
# -- -----------------------------------------------------
# CREATE SCHEMA IF NOT EXISTS `BDD1` DEFAULT CHARACTER SET utf8 ;
# USE `BDD1` ;
#
# -- -----------------------------------------------------
# -- Table `BDD1`.`Plat`
# -- -----------------------------------------------------
# CREATE TABLE IF NOT EXISTS `BDD1`.`Plat` (
#   `idPlat` INT NOT NULL,
#   `nom` VARCHAR(25) NOT NULL,
#   `prix` DOUBLE NULL,
#   `idCategorie` INT NOT NULL,
#   PRIMARY KEY (`idPlat`),
#   INDEX `fk_Plat_Categorie_idx` (`idCategorie` ASC),
#   CONSTRAINT `fk_Plat_Categorie`
#     FOREIGN KEY (`idCategorie`)
#     REFERENCES `BDD1`.`Categorie` (`idCategorie`)
#     ON DELETE NO ACTION
#     ON UPDATE NO ACTION)
# ENGINE = InnoDB;
#
#
# -- -----------------------------------------------------
# -- Table `BDD1`.`Categorie`
# -- -----------------------------------------------------
# CREATE TABLE IF NOT EXISTS `BDD1`.`Categorie` (
#   `idCategorie` INT NOT NULL,
#   `nom` VARCHAR(15) NOT NULL,
#   PRIMARY KEY (`idCategorie`))
# ENGINE = InnoDB;
#
#
# -- -----------------------------------------------------
# -- Table `BDD1`.`Plat`
# -- -----------------------------------------------------
# CREATE TABLE IF NOT EXISTS `BDD1`.`Plat` (
#   `idPlat` INT NOT NULL,
#   `nom` VARCHAR(25) NOT NULL,
#   `prix` DOUBLE NULL,
#   `idCategorie` INT NOT NULL,
#   PRIMARY KEY (`idPlat`),
#   INDEX `fk_Plat_Categorie_idx` (`idCategorie` ASC),
#   CONSTRAINT `fk_Plat_Categorie`
#     FOREIGN KEY (`idCategorie`)
#     REFERENCES `BDD1`.`Categorie` (`idCategorie`)
#     ON DELETE NO ACTION
#     ON UPDATE NO ACTION)
# ENGINE = InnoDB;
#
#
# -- -----------------------------------------------------
# -- Table `BDD1`.`Client`
# -- -----------------------------------------------------
# CREATE TABLE IF NOT EXISTS `BDD1`.`Client` (
#   `idClient` INT NOT NULL AUTO_INCREMENT,
#   `nom` VARCHAR(20) NULL,
#   `prenom` VARCHAR(20) NULL,
#   `email` VARCHAR(30) NULL,
#   PRIMARY KEY (`idClient`),
#   UNIQUE INDEX `email_UNIQUE` (`email` ASC))
# ENGINE = InnoDB;
#
#
# -- -----------------------------------------------------
# -- Table `BDD1`.`Commander`
# -- -----------------------------------------------------
# CREATE TABLE IF NOT EXISTS `BDD1`.`Commander` (
#   `idClient` INT NOT NULL,
#   `idPlat` INT NOT NULL,
#   `dateCommande` DATE NULL,
#   PRIMARY KEY (`idClient`, `idPlat`),
#   INDEX `fk_Client_has_Plat_Plat1_idx` (`idPlat` ASC),
#   INDEX `fk_Client_has_Plat_Client1_idx` (`idClient` ASC),
#   CONSTRAINT `fk_Client_has_Plat_Client1`
#     FOREIGN KEY (`idClient`)
#     REFERENCES `BDD1`.`Client` (`idClient`)
#     ON DELETE NO ACTION
#     ON UPDATE NO ACTION,
#   CONSTRAINT `fk_Client_has_Plat_Plat1`
#     FOREIGN KEY (`idPlat`)
#     REFERENCES `BDD1`.`Plat` (`idPlat`)
#     ON DELETE NO ACTION
#     ON UPDATE NO ACTION)
# ENGINE = InnoDB;
#
#
# SET SQL_MODE=@OLD_SQL_MODE;
# SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
# SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;

USE BDD1;
SHOW TABLE STATUS ;
DESCRIBE plat;
SELECT * from plat;
SELECT * from categorie;