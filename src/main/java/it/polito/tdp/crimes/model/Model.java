package it.polito.tdp.crimes.model;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import it.polito.tdp.crimes.db.EventsDao;

public class Model {
	
	private Graph<String, DefaultWeightedEdge> grafo;
	private EventsDao dao;
	
	private List<String> best;
	
	public Model() {
		dao = new EventsDao();
	}
	
	public void creaGrafo(String categoria, int mese) {
		grafo = new SimpleWeightedGraph<String, DefaultWeightedEdge>(DefaultWeightedEdge.class);
		//AGGIUNGO I VERTICI
		Graphs.addAllVertices(grafo, dao.getVertici(categoria, mese));
		
		//AGGIUNGO GLI ARCHI
		List<Adiacenza> archi = dao.getArchi(categoria, mese);
		for(Adiacenza a : archi) {
			Graphs.addEdgeWithVertices(grafo, a.getV1(), a.getV2(), a.getPeso());
		}
		
		System.out.println("VERTICI: " + grafo.vertexSet().size()+"\nARCHI: " + grafo.edgeSet().size());
	}
	
	public List<Adiacenza> getArchiMaggioriPM(){ 
		double pesoTot = 0;
		for(DefaultWeightedEdge e : grafo.edgeSet()) {
			pesoTot+= this.grafo.getEdgeWeight(e); 
		}
		double avg = pesoTot/this.grafo.edgeSet().size();
		
		List<Adiacenza> result = new ArrayList<Adiacenza>();
		for(DefaultWeightedEdge e : grafo.edgeSet()) {
			if(this.grafo.getEdgeWeight(e)>avg)
				result.add(new Adiacenza(this.grafo.getEdgeSource(e), this.grafo.getEdgeTarget(e),
						(int) this.grafo.getEdgeWeight(e)));
		}
		return result;
				
	}
	
	public List<String> calcolaPercorso(String sorgente, String destinazione){
		best = new LinkedList<>();
		List<String> parziale = new LinkedList<>();
		parziale.add(sorgente);
		cerca(parziale, destinazione);
		return best;
	}
	
	private void cerca(List<String> parziale, String destinazione) {
		
		if(parziale.get(parziale.size()-1).equals(destinazione)) {	//CASO TERMINALE
			//CONTROLLO SE E LA SOLUZIONE MIGLIORE
			if(parziale.size()>best.size())
				best= new LinkedList<>(parziale);
			return;
		}
		
		//SCORRO I VICINI DELL ULTIMO INSERITO E PROVO LE VARIE STRADE
		for(String v : Graphs.neighborListOf(grafo, parziale.get(parziale.size()-1))) {
			if(!parziale.contains(v)) {
				parziale.add(v);
				cerca(parziale, destinazione);
				parziale.remove(parziale.size()-1);
			}
		}
		
	}
	
	
}
