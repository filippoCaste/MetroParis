package it.polito.tdp.metroparis.model;

public class CoppiaId {

	Integer idPartenza;
	Integer idArrivo;
	
	public CoppiaId(Integer idPartenza, Integer idArrivo) {
		super();
		this.idPartenza = idPartenza;
		this.idArrivo = idArrivo;
	}
	
	public Integer getIdPartenza() {
		return idPartenza;
	}
	public void setIdPartenza(Integer idPartenza) {
		this.idPartenza = idPartenza;
	}
	public Integer getIdArrivo() {
		return idArrivo;
	}
	public void setIdArrivo(Integer idArrivo) {
		this.idArrivo = idArrivo;
	}
	
	
}
