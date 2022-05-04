package it.polito.tdp.metroparis.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleDirectedGraph;
import org.jgrapht.traverse.BreadthFirstIterator;
import org.jgrapht.traverse.GraphIterator;

import it.polito.tdp.metroparis.db.MetroDAO;

/*
 * ATTENZIONE! 
 * Non sempre è necessario complicare il programma. Alle volte è meglio fare un codice
 * lento e funzionante che NON funzionante
 */

public class Model {
	
	private Graph<Fermata, DefaultEdge> grafo ; // grafo NON pesato
	
	// !!!
	/* Non dentro al costruttore poiché il grafo potrebbe cambiare anche dopo che già sia stato costruito l'oggetto Model */
	public void creaGrafo() {
		this.grafo = new SimpleDirectedGraph<Fermata, DefaultEdge>(DefaultEdge.class);
		
		// connessione al DB per prendere le fermate e inserirle come vertici del grafo
		MetroDAO dao = new MetroDAO();
		List<Fermata> fermate = dao.getAllFermate();
		
		// pattern Identity Map
		Map<Integer, Fermata> fermateIdMap = new HashMap<>();
		for(Fermata f : fermate) {
			fermateIdMap.put(f.getIdFermata(), f);
		}
		
		// this.grafo.addVertex();
		Graphs.addAllVertices(grafo, fermate);
		
		/* METODO 1: itero su ogni coppia di vertici (n^2 query) */
		
//		for(Fermata partenza : fermate) {
//			for(Fermata arrivo : fermate) {
//				if(dao.isFermateConnesse(partenza, arrivo)) { // se esiste una connessione tra partenza e arrivo -- DB
//					   // ci può essere anche PIÙ DI UNA CONNESSIONE 
//					this.grafo.addEdge(partenza, arrivo);
//				}
//			}
//		}
		
		/* METODO 2: dato ciascun vertice si trovano quelli adiacenti (n query) */
		
		/* --> tramite restituzione id */
		
		// si può iterare su fermate oppure sfruttando i vertici del grafo.vertexSet()
		for(Fermata partenza : fermate) {
			List<Integer> idConnesse = dao.getIdFermateConnesse(partenza);
			for(Integer id : idConnesse) {
				Fermata arrivo = null; // fermata che possiede tale id
				for(Fermata f : fermate) // invece che scandire la lista si potrebbe creare un
										 // nuovo oggetto che abbia solo l'informazione sull'id
										 // poiché basta che siano implementati hashCode e equals()
					if(f.getIdFermata() == id) {
						arrivo = f;
						break;
					}
				this.grafo.addEdge(partenza, arrivo);
			}
		}
		
		/* --> il dao ritorna degli oggetti Fermata */
		for(Fermata partenza : fermate) {
			List<Fermata> arrivi = dao.getFermateConnesse(partenza);
			for(Fermata arrivo : arrivi)
				this.grafo.addEdge(partenza, arrivo);
		}
		
		/* --> il dao restituisce id numerici che vengono convertiti in oggetti tramite una Map<Integer,Fermata> */
		// pattern Identity Map
		
		for(Fermata partenza : fermate) {
			List<Integer> idConnesse = dao.getIdFermateConnesse(partenza);
			for(Integer id : idConnesse) {
				Fermata arrivo = fermateIdMap.get(id);
				this.grafo.addEdge(partenza, arrivo);
			}
		}
		
		/* METODO 3: una sola query che restituisca le coppie di fermate da collegare */
		/* --> usando una Map (pattern Identity Map) */
		
		List<CoppiaId> fermateDaCollegare = dao.getAllFermateConnesse();
		for(CoppiaId coppia : fermateDaCollegare) {
			this.grafo.addEdge(fermateIdMap.get(coppia.getIdPartenza()),
					fermateIdMap.get(coppia.getIdArrivo()));
		}
		
		System.out.println(this.grafo);
		System.out.println("Numero di vertici: " + this.grafo.vertexSet().size());
		System.out.println("Numero di archi: " + this.grafo.edgeSet().size());
		
		this.visitaGrafo(fermate.get(0));
	}
	
	public void visitaGrafo(Fermata partenza) {
														// Depth	  					// da dove partire
		GraphIterator<Fermata, DefaultEdge> visita = new BreadthFirstIterator<>(this.grafo, partenza);
		
		while(visita.hasNext()) {
			Fermata f = visita.next();
			
			// aggiungo a una collection o stampo
			System.out.println(f);
		}
	}

}
