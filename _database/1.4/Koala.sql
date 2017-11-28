-- MySQL Script generated by MySQL Workbench
-- 03/11/17 18:42:25
-- Model: New Model    Version: 1.0
-- MySQL Workbench Forward Engineering

SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='TRADITIONAL,ALLOW_INVALID_DATES';

-- -----------------------------------------------------
-- Schema koala
-- -----------------------------------------------------

-- -----------------------------------------------------
-- Schema koala
-- -----------------------------------------------------
CREATE SCHEMA IF NOT EXISTS `koala` DEFAULT CHARACTER SET utf8 ;
USE `koala` ;

-- -----------------------------------------------------
-- Table `koala`.`categorie`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `koala`.`categorie` (
  `idCategorie` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `nom` VARCHAR(45) NOT NULL,
  `image` LONGBLOB NULL DEFAULT NULL,
  `description` VARCHAR(255) NULL DEFAULT NULL,
  PRIMARY KEY (`idCategorie`),
  UNIQUE INDEX `nom_UNIQUE` (`nom` ASC))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `koala`.`client`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `koala`.`client` (
  `idClient` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `nom` VARCHAR(45) NULL DEFAULT NULL,
  `prenom` VARCHAR(45) NULL DEFAULT NULL,
  `email` VARCHAR(45) NULL DEFAULT NULL,
  `telephone` VARCHAR(15) NULL DEFAULT NULL,
  `dateInscription` DATE NULL,
  `adresse` VARCHAR(255) NULL DEFAULT NULL,
  `dateNaissance` DATE NULL DEFAULT NULL,
  `sexe` ENUM('M', 'F') NULL,
  `pointFidelite` DOUBLE UNSIGNED NULL DEFAULT 0,
  PRIMARY KEY (`idClient`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `koala`.`plat`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `koala`.`plat` (
  `idPlat` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `idCategorie` INT UNSIGNED NOT NULL,
  `nom` VARCHAR(45) NOT NULL,
  `prix` DOUBLE NULL DEFAULT NULL,
  `dateIntronisation` DATE NULL DEFAULT NULL,
  `description` VARCHAR(255) NULL DEFAULT NULL,
  `image` LONGBLOB NULL DEFAULT NULL,
  `tempsPreparation` INT NULL DEFAULT NULL,
  `disponible` TINYINT(1) NOT NULL DEFAULT 1,
  `parallele` INT UNSIGNED NULL DEFAULT 1,
  PRIMARY KEY (`idPlat`),
  INDEX `fk_Plat_Categorie1_idx` (`idCategorie` ASC),
  UNIQUE INDEX `nom_UNIQUE` (`nom` ASC),
  CONSTRAINT `fk_Plat_Categorie1`
    FOREIGN KEY (`idCategorie`)
    REFERENCES `koala`.`categorie` (`idCategorie`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `koala`.`commande`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `koala`.`commande` (
  `idCommande` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `dateCommande` DATETIME NULL DEFAULT NULL,
  `modePaiement` ENUM('cache', 'CB', 'cheque', 'bon') NULL,
  `idClient` INT UNSIGNED NOT NULL,
  `payer` TINYINT(1) NULL DEFAULT 0,
  PRIMARY KEY (`idCommande`),
  INDEX `fk_commande_client1_idx` (`idClient` ASC),
  CONSTRAINT `fk_commande_client1`
    FOREIGN KEY (`idClient`)
    REFERENCES `koala`.`client` (`idClient`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `koala`.`compte`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `koala`.`compte` (
  `idCompte` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `login` VARCHAR(45) NOT NULL,
  `password` VARCHAR(45) NOT NULL,
  `pin` VARCHAR(8) NULL,
  `pinEnabled` TINYINT(1) NULL DEFAULT 0,
  PRIMARY KEY (`idCompte`),
  UNIQUE INDEX `login_UNIQUE` (`login` ASC))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `koala`.`magasin`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `koala`.`magasin` (
  `idMagasin` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `nom` VARCHAR(45) NOT NULL,
  `localisation` VARCHAR(45) NULL,
  `adresse` VARCHAR(255) NULL,
  PRIMARY KEY (`idMagasin`))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `koala`.`cuisiner`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `koala`.`cuisiner` (
  `idCuisiner` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `idCompte` INT UNSIGNED NOT NULL,
  `idMagasin` INT UNSIGNED NOT NULL,
  `nom` VARCHAR(45) NOT NULL,
  `prenom` VARCHAR(45) NOT NULL,
  `sexe` ENUM('M', 'F') NULL DEFAULT NULL,
  `dateNaissance` DATE NULL DEFAULT NULL,
  `email` VARCHAR(45) NULL DEFAULT NULL,
  `telephone` VARCHAR(45) NULL DEFAULT NULL,
  `adresse` VARCHAR(255) NULL DEFAULT NULL,
  `numeroSecuriteSociale` VARCHAR(45) NULL,
  `dateEmbauche` DATE NULL DEFAULT NULL,
  `image` LONGBLOB NULL,
  `derniereEvaluation` DOUBLE NULL DEFAULT NULL,
  `salaire` DOUBLE NULL DEFAULT NULL,
  `salaireAnnuel` DOUBLE NULL,
  `salaireVerse` TINYINT(1) NULL DEFAULT 0,
  PRIMARY KEY (`idCuisiner`),
  INDEX `fk_employe_copy2_compte1_idx` (`idCompte` ASC),
  INDEX `fk_cuisiner_magasin1_idx` (`idMagasin` ASC),
  CONSTRAINT `fk_employe_copy2_compte1`
    FOREIGN KEY (`idCompte`)
    REFERENCES `koala`.`compte` (`idCompte`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_cuisiner_magasin1`
    FOREIGN KEY (`idMagasin`)
    REFERENCES `koala`.`magasin` (`idMagasin`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `koala`.`commandeUnitaire`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `koala`.`commandeUnitaire` (
  `idCommande` INT UNSIGNED NOT NULL,
  `idPlat` INT UNSIGNED NOT NULL,
  `idCuisinier` INT UNSIGNED NOT NULL,
  `quantite` INT UNSIGNED NULL DEFAULT 0,
  `etat` ENUM('T', 'E', 'F') NULL DEFAULT 'T',
  INDEX `fk_commandeUnitaire_plat1_idx` (`idPlat` ASC),
  INDEX `fk_commandeUnitaire_commande1_idx` (`idCommande` ASC),
  PRIMARY KEY (`idCommande`, `idPlat`),
  INDEX `fk_commandeUnitaire_cuisiner1_idx` (`idCuisinier` ASC),
  CONSTRAINT `fk_commandeUnitaire_plat1`
    FOREIGN KEY (`idPlat`)
    REFERENCES `koala`.`plat` (`idPlat`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_commandeUnitaire_commande1`
    FOREIGN KEY (`idCommande`)
    REFERENCES `koala`.`commande` (`idCommande`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_commandeUnitaire_cuisiner1`
    FOREIGN KEY (`idCuisinier`)
    REFERENCES `koala`.`cuisiner` (`idCuisiner`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `koala`.`ingredient`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `koala`.`ingredient` (
  `idIngredient` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `nom` VARCHAR(45) NOT NULL,
  `description` VARCHAR(255) NULL DEFAULT NULL,
  `calorie` DOUBLE NULL DEFAULT NULL,
  `proteine` DOUBLE NULL DEFAULT NULL,
  `glucide` DOUBLE NULL DEFAULT NULL,
  `lipide` DOUBLE NULL DEFAULT NULL,
  `gluten` TINYINT(1) NULL,
  PRIMARY KEY (`idIngredient`),
  UNIQUE INDEX `nom_UNIQUE` (`nom` ASC))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `koala`.`contient`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `koala`.`contient` (
  `idIngredient` INT UNSIGNED NOT NULL,
  `idPlat` INT UNSIGNED NOT NULL,
  `quantite` INT NULL DEFAULT NULL,
  PRIMARY KEY (`idIngredient`, `idPlat`),
  INDEX `fk_Ingredient_has_Plat_Plat1_idx` (`idPlat` ASC),
  INDEX `fk_Ingredient_has_Plat_Ingredient1_idx` (`idIngredient` ASC),
  CONSTRAINT `fk_Ingredient_has_Plat_Ingredient1`
    FOREIGN KEY (`idIngredient`)
    REFERENCES `koala`.`ingredient` (`idIngredient`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_Ingredient_has_Plat_Plat1`
    FOREIGN KEY (`idPlat`)
    REFERENCES `koala`.`plat` (`idPlat`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `koala`.`tag`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `koala`.`tag` (
  `idTag` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `nom` VARCHAR(45) NOT NULL,
  `description` VARCHAR(255) NULL DEFAULT NULL,
  PRIMARY KEY (`idTag`),
  UNIQUE INDEX `nom_UNIQUE` (`nom` ASC))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `koala`.`tagged`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `koala`.`tagged` (
  `idTag` INT UNSIGNED NOT NULL,
  `idPlat` INT UNSIGNED NOT NULL,
  PRIMARY KEY (`idTag`, `idPlat`),
  INDEX `fk_tag_has_Plat_Plat1_idx` (`idPlat` ASC),
  INDEX `fk_tag_has_Plat_tag1_idx` (`idTag` ASC),
  CONSTRAINT `fk_tag_has_Plat_Plat1`
    FOREIGN KEY (`idPlat`)
    REFERENCES `koala`.`plat` (`idPlat`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_tag_has_Plat_tag1`
    FOREIGN KEY (`idTag`)
    REFERENCES `koala`.`tag` (`idTag`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `koala`.`admin`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `koala`.`admin` (
  `idAdmin` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `idCompte` INT UNSIGNED NOT NULL,
  `idMagasin` INT UNSIGNED NOT NULL,
  `nom` VARCHAR(45) NOT NULL,
  `prenom` VARCHAR(45) NOT NULL,
  `sexe` ENUM('M', 'F') NULL DEFAULT NULL,
  `dateNaissance` DATE NULL DEFAULT NULL,
  `email` VARCHAR(45) NULL DEFAULT NULL,
  `telephone` VARCHAR(45) NULL DEFAULT NULL,
  `adresse` VARCHAR(255) NULL DEFAULT NULL,
  `numeroSecuriteSociale` VARCHAR(45) NULL,
  `dateEmbauche` DATE NULL DEFAULT NULL,
  `image` LONGBLOB NULL,
  `derniereEvaluation` DOUBLE NULL DEFAULT NULL,
  `salaire` DOUBLE NULL DEFAULT NULL,
  `salaireAnnuel` DOUBLE NULL,
  `salaireVerse` TINYINT(1) NULL DEFAULT 0,
  PRIMARY KEY (`idAdmin`),
  INDEX `fk_employe_copy1_compte1_idx` (`idCompte` ASC),
  INDEX `fk_admin_magasin1_idx` (`idMagasin` ASC),
  CONSTRAINT `fk_employe_copy1_compte1`
    FOREIGN KEY (`idCompte`)
    REFERENCES `koala`.`compte` (`idCompte`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_admin_magasin1`
    FOREIGN KEY (`idMagasin`)
    REFERENCES `koala`.`magasin` (`idMagasin`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `koala`.`koala.gui.magasinier`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `koala`.`koala.gui.magasinier` (
  `idMagasinier` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `idCompte` INT UNSIGNED NOT NULL,
  `idMagasin` INT UNSIGNED NOT NULL,
  `nom` VARCHAR(45) NOT NULL,
  `prenom` VARCHAR(45) NOT NULL,
  `sexe` ENUM('M', 'F') NULL DEFAULT NULL,
  `dateNaissance` DATE NULL DEFAULT NULL,
  `email` VARCHAR(45) NULL DEFAULT NULL,
  `telephone` VARCHAR(45) NULL DEFAULT NULL,
  `adresse` VARCHAR(255) NULL DEFAULT NULL,
  `numeroSecuriteSociale` VARCHAR(45) NULL,
  `dateEmbauche` DATE NULL DEFAULT NULL,
  `image` LONGBLOB NULL,
  `derniereEvaluation` DOUBLE NULL DEFAULT NULL,
  `salaire` DOUBLE NULL DEFAULT NULL,
  `salaireAnnuel` DOUBLE NULL,
  `salaireVerse` TINYINT(1) NULL DEFAULT 0,
  PRIMARY KEY (`idMagasinier`),
  INDEX `fk_employe_copy1_compte1_idx` (`idCompte` ASC),
  INDEX `fk_magasinier_magasin1_idx` (`idMagasin` ASC),
  CONSTRAINT `fk_employe_copy1_compte10`
    FOREIGN KEY (`idCompte`)
    REFERENCES `koala`.`compte` (`idCompte`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_magasinier_magasin1`
    FOREIGN KEY (`idMagasin`)
    REFERENCES `koala`.`magasin` (`idMagasin`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `koala`.`caissier`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `koala`.`caissier` (
  `idCaissier` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `idCompte` INT UNSIGNED NOT NULL,
  `idMagasin` INT UNSIGNED NOT NULL,
  `nom` VARCHAR(45) NOT NULL,
  `prenom` VARCHAR(45) NOT NULL,
  `sexe` ENUM('M', 'F') NULL DEFAULT NULL,
  `dateNaissance` DATE NULL DEFAULT NULL,
  `email` VARCHAR(45) NULL DEFAULT NULL,
  `telephone` VARCHAR(45) NULL DEFAULT NULL,
  `adresse` VARCHAR(255) NULL DEFAULT NULL,
  `numeroSecuriteSociale` VARCHAR(45) NULL,
  `dateEmbauche` DATE NULL DEFAULT NULL,
  `image` LONGBLOB NULL,
  `derniereEvaluation` DOUBLE NULL DEFAULT NULL,
  `salaire` DOUBLE NULL DEFAULT NULL,
  `salaireAnnuel` DOUBLE NULL,
  `salaireVerse` TINYINT(1) NULL DEFAULT 0,
  PRIMARY KEY (`idCaissier`),
  INDEX `fk_employe_copy2_compte1_idx` (`idCompte` ASC),
  INDEX `fk_caissier_magasin1_idx` (`idMagasin` ASC),
  CONSTRAINT `fk_employe_copy2_compte10`
    FOREIGN KEY (`idCompte`)
    REFERENCES `koala`.`compte` (`idCompte`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_caissier_magasin1`
    FOREIGN KEY (`idMagasin`)
    REFERENCES `koala`.`magasin` (`idMagasin`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `koala`.`stock`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `koala`.`stock` (
  `idIngredient` INT UNSIGNED NOT NULL,
  `idMagasin` INT UNSIGNED NOT NULL,
  `quantitie` INT NULL,
  `prixAchat` DOUBLE NULL,
  `datePeremption` DATE NULL,
  PRIMARY KEY (`idIngredient`, `idMagasin`),
  INDEX `fk_magasin_has_ingredient_ingredient1_idx` (`idIngredient` ASC),
  INDEX `fk_stock_magasin1_idx` (`idMagasin` ASC),
  CONSTRAINT `fk_magasin_has_ingredient_ingredient1`
    FOREIGN KEY (`idIngredient`)
    REFERENCES `koala`.`ingredient` (`idIngredient`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_stock_magasin1`
    FOREIGN KEY (`idMagasin`)
    REFERENCES `koala`.`magasin` (`idMagasin`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `koala`.`miniStock`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `koala`.`miniStock` (
  `idIngredient` INT UNSIGNED NOT NULL,
  `idCuisiner` INT UNSIGNED NOT NULL,
  `quantite` INT NULL,
  PRIMARY KEY (`idIngredient`, `idCuisiner`),
  INDEX `fk_ingredient_has_cuisiner_cuisiner1_idx` (`idCuisiner` ASC),
  INDEX `fk_ingredient_has_cuisiner_ingredient1_idx` (`idIngredient` ASC),
  CONSTRAINT `fk_ingredient_has_cuisiner_ingredient1`
    FOREIGN KEY (`idIngredient`)
    REFERENCES `koala`.`ingredient` (`idIngredient`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_ingredient_has_cuisiner_cuisiner1`
    FOREIGN KEY (`idCuisiner`)
    REFERENCES `koala`.`cuisiner` (`idCuisiner`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `koala`.`platProposer`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `koala`.`platProposer` (
  `idPlatProposer` INT NOT NULL,
  `nom` VARCHAR(45) NULL,
  `description` VARCHAR(510) NULL,
  `image` LONGBLOB NULL,
  `date` DATETIME NULL,
  `idCompte` INT UNSIGNED NOT NULL,
  PRIMARY KEY (`idPlatProposer`),
  INDEX `fk_platProposer_compte1_idx` (`idCompte` ASC),
  CONSTRAINT `fk_platProposer_compte1`
    FOREIGN KEY (`idCompte`)
    REFERENCES `koala`.`compte` (`idCompte`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;