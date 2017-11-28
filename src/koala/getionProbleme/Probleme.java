package koala.getionProbleme;

import java.sql.Date;

public class Probleme {
	
	private int idProbleme;
	private int idCompte;
	private String description;
	private int degre;
	private Date date;

	public Probleme() {
	}

	public Probleme(int idCompte, String description, int degre, Date date) {
		this.idCompte = idCompte;
		this.description = description;
		this.degre = degre;
		this.date = date;
	}

	public int getIdProbleme() {
		return idProbleme;
	}
	public void setIdProbleme(int idProbleme) {
		this.idProbleme = idProbleme;
	}
	public int getIdCompte() {
		return idCompte;
	}
	public void setIdCompte(int idCompte) {
		this.idCompte = idCompte;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public int getDegre() {
		return degre;
	}
	public void setDegre(int degre) {
		this.degre = degre;
	}
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
 
}
