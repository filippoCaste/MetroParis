package it.polito.tdp.metroparis.model;

import java.util.ArrayList;
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
	private List<Fermata> fermate = null;
	private Map<Integer, Fermata> fermateIdMap = null;
	
	
	// !!!: di fatto può anche essere private come metodo, al controller non interessa
	/* Non dentro al costruttore poiché il grafo potrebbe cambiare anche dopo che già sia stato costruito l'oggetto Model */
	private void creaGrafo() {
		this.grafo = new SimpleDirectedGraph<Fermata, DefaultEdge>(DefaultEdge.class);
		
//		// connessione al DB per prendere le fermate e inserirle come vertici del grafo
//		List<Fermata> fermate = dao.getAllFermate();
		
		/* */
		
//		// pattern Identity Map
//		Map<Integer, Fermata> fermateIdMap = new HashMap<>();
//		for(Fermata f : fermate) {
//			fermateIdMap.put(f.getIdFermata(), f);
//		}
		
		/* */
		
		// this.grafo.addVertex();
		/**/
		
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
//		for(Fermata partenza : fermate) {
//			List<Integer> idConnesse = dao.getIdFermateConnesse(partenza);
//			for(Integer id : idConnesse) {
//				Fermata arrivo = null; // fermata che possiede tale id
//				for(Fermata f : fermate) // invece che scandire la lista si potrebbe creare un
//										 // nuovo oggetto che abbia solo l'informazione sull'id
//										 // poiché basta che siano implementati hashCode() ed equals()
//					if(f.getIdFermata() == id) {
//						arrivo = f;
//						break;
//					}
//				this.grafo.addEdge(partenza, arrivo);
//			}
//		}
//		
//		/* --> il dao ritorna degli oggetti Fermata */
//		for(Fermata partenza : fermate) {
//			List<Fermata> arrivi = dao.getFermateConnesse(partenza);
//			for(Fermata arrivo : arrivi)
//				this.grafo.addEdge(partenza, arrivo);
//		}
//		
//		/* --> il dao restituisce id numerici che vengono convertiti in oggetti tramite una Map<Integer,Fermata> */
//		// pattern Identity Map
//		
//		for(Fermata partenza : fermate) {
//			List<Integer> idConnesse = dao.getIdFermateConnesse(partenza);
//			for(Integer id : idConnesse) {
//				Fermata arrivo = fermateIdMap.get(id);
//				this.grafo.addEdge(partenza, arrivo);
//			}
//		}
		
		/* METODO 3: una sola query che restituisca le coppie di fermate da collegare */
		/* --> usando una Map (pattern Identity Map) */
		
		Graphs.addAllVertices(grafo, this.getFermate());
		MetroDAO dao = new MetroDAO();
		
		List<CoppiaId> fermateDaCollegare = dao.getAllFermateConnesse();
		for(CoppiaId coppia : fermateDaCollegare) {
			this.grafo.addEdge(fermateIdMap.get(coppia.getIdPartenza()),
					fermateIdMap.get(coppia.getIdArrivo()));
		}
		
//		System.out.println(this.grafo);
//		System.out.println("Numero di vertici: " + this.grafo.vertexSet().size());
//		System.out.println("Numero di archi: " + this.grafo.edgeSet().size());
		
		this.visitaGrafo(fermate.get(0));
	}
	
	public Map<Fermata, Fermata> visitaGrafo(Fermata partenza) {
														// Depth	  					// da dove partire
		GraphIterator<Fermata, DefaultEdge> visita = new BreadthFirstIterator<>(this.grafo, partenza);
		
		// per registrare il percorso
		/*
		 * 1- creare una classe che implementi jgrapht.TraversalListener
		 * 2- implementare i metodi dell'interfaccia
		 * 3- aggiungere il listener nel percorso
		 * 4- il Listener opera chiamando i suoi metodi quando vengono chiamati next() e hasNext()
		 * 5- salvo nella Mappa 'inversa', inserendo root che è precedeuto da null
		 *    (key: vertice, value: da quale vertice si raggiunge key)
		 * 6- nella classe Listener salvo la 'mappa' di albero inverso poiché dal metodo edgeTraversed passa tutto
		 * 7- senza fare nulla si riempie da solo l'albero inverso nella classe listener
		 * 8- print
		 */
		
		Map<Fermata, Fermata> alberoInverso = new HashMap<>();
		alberoInverso.put(partenza, null); // root non ha predecessore
		
		// concedo al Listener di ascoltare
		visita.addTraversalListener(new RegistraAlberoDiVisita(alberoInverso, this.grafo));
		
		while(visita.hasNext()) {
			@SuppressWarnings("unused")
			Fermata f = visita.next();
			
			// aggiungo a una collection o stampo
			//System.out.println(f);
			
			// senza fare nulla si riempie da solo l'albero inverso nella classe listener
		}
		
		return alberoInverso;
	}

	public List<Fermata> getFermate() {
		if(this.fermate==null) {
			MetroDAO dao = new MetroDAO();
			this.fermate = dao.getAllFermate();

			// pattern Identity Map
			this.fermateIdMap = new HashMap<>();
			for(Fermata f : fermate) {
				fermateIdMap.put(f.getIdFermata(), f);
			}
		}
		return fermate;
	}
	
	public List<Fermata> calcolaPercorso(Fermata partenza, Fermata arrivo) {
		this.creaGrafo();
		
		Map<Fermata, Fermata> alberoInverso = this.visitaGrafo(partenza);
		
		Fermata corrente = arrivo;
		List<Fermata> percorso = new ArrayList<>();
		
		while(corrente!=null) { //null è il predecessore della fermata root
			percorso.add(0, corrente); // aggiunge in testa alla lista, spostando gli altri
			corrente = alberoInverso.get(corrente);
		}
			
		return percorso;
	}
}
