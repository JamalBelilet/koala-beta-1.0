select ingredient.idIngredient,ingredient.nom,ingredient.description 
			FROM plat,contient,ingredient
				WHERE plat.idPlat=contient.idPlat AND plat.nom='burger' AND Ingredient.idIngredient=contient.idIngredient;