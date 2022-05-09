package it.polito.tdp.metroparis.model;

import java.util.Map;

import org.jgrapht.Graph;
import org.jgrapht.event.ConnectedComponentTraversalEvent;
import org.jgrapht.event.EdgeTraversalEvent;
import org.jgrapht.event.TraversalListener;
import org.jgrapht.event.VertexTraversalEvent;
import org.jgrapht.graph.DefaultEdge;

public class RegistraAlberoDiVisita implements TraversalListener<Fermata, DefaultEdge>{

	private Map<Fermata, Fermata> alberoInverso;
	private Graph<Fermata, DefaultEdge> grafo;
	


	public RegistraAlberoDiVisita(Map<Fermata, Fermata> alberoInverso, Graph<Fermata, DefaultEdge> grafo) {
		super();
		this.alberoInverso = alberoInverso;
		this.grafo = grafo;
	}

	@Override
	public void connectedComponentFinished(ConnectedComponentTraversalEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void connectedComponentStarted(ConnectedComponentTraversalEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void edgeTraversed(EdgeTraversalEvent<DefaultEdge> e) {
		System.out.println(e.getEdge());
		
		// !!!
		// e.getSource() è un metodo dell'evento NON GRAFO
		Fermata source = this.grafo.getEdgeSource(e.getEdge());
		Fermata target = this.grafo.getEdgeTarget(e.getEdge());
		
//		System.out.println(source+".."+target);
		
		if(!alberoInverso.containsKey(target)) { // se target ancora non è stato raggiunto da nessun altro allora lo aggiungo, altriment no
			alberoInverso.put(target, source);
//			System.out.println(target + " si raggiunge da " + source);
		}
		else if(!alberoInverso.containsKey(source)) { // se source ancora non era stato scoperto, lo scopro da target (che già c'è) --> lo aggiungo
			alberoInverso.put(source, target);
//			System.out.println(source + " si raggiunge da " + target);
		}
	}

	@Override
	public void vertexTraversed(VertexTraversalEvent<Fermata> e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void vertexFinished(VertexTraversalEvent<Fermata> e) {
		// TODO Auto-generated method stub
		
	}

}
 