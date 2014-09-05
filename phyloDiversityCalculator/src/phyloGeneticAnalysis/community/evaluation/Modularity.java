package phyloGeneticAnalysis.community.evaluation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;


import edu.uci.ics.jung.graph.DirectedGraph;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.UndirectedGraph;


// TODO: Auto-generated Javadoc
/**
 * The Class Modularity.
 *
 * @param <V> the value type
 * @param <E> the element type
 */
/**
 * @author jingliu5
 *
 * @param <V>
 * @param <E>
 */
public class Modularity {
	
	/**
	 * Gets the graph modularity. 
	 * 
	 * The modularity of a graph with respect to some
	 * division (or vertex types) measures how good the division is, or how
	 * separated are the different vertex types from each other. It is defined as
	 * Q=1/(2m) * sum(Aij-ki*kj/(2m)delta(ci,cj),i,j), here ��m�� is the number of
	 * edges, ��Aij�� is the element of the ��A�� adjacency matrix in row ��i�� and
	 * column ��j��, ��ki�� is the degree of ��i��, ��kj�� is the degree of ��j��, ��ci�� is
	 * the type (or component) of ��i��, ��cj�� that of ��j��, the sum goes over all
	 * ��i�� and ��j�� pairs of vertices, and ��delta(x,y)�� is one if x=y and zero
	 * otherwise. Modularity on weighted graphs is also meaningful. When taking
	 * edge weights into account, ��Aij�� becomes the weight of the corresponding
	 * edge (or 0 if there is no edge), ��ki�� is the total weight of edges
	 * incident on vertex ��i��, ��kj�� is the total weight of edges incident on
	 * vertex ��j�� and ��m�� is the total weight of all edges. See also MEJ Newman
	 * and M Girvan: Finding and evaluating community structure in networks.
	 * Physical Review E 69 026113, 2004.
	 * 
	 * @param distanceM
	 *            the distance m
	 * @param membership
	 *            the membership
	 * @return the graph modularity
	 */
	public double getGraphModularity(double[][] distanceM, int[] membership) {
		double modularity = 0;
		if (distanceM.length > 0) {
			int row = distanceM.length;
			int column = row;
			int types = 0;
			for (int i = 0; i < membership.length; i++) {
				if (types < membership[i])
					types = membership[i];
			}
			types++;
			double[] e = new double[types];
			double[] a = new double[types];
			double m = 0;
			for (int i = 0; i < row; i++) {
				for (int j = 0; j < row; j++) {
					if (distanceM[i][j] != 0) {
						if (membership[i] == membership[j])
							e[membership[i]] += distanceM[i][j];
						else {
							a[membership[i]] += distanceM[i][j];
						}
						m += distanceM[i][j];
					}
				}
			}

			for (int i = 0; i < types; i++) {
				modularity += (e[i] - a[i] * a[i] / m) / m;
			}

		}
		return modularity;
	}
	
	/**
	 * Gets the graph modularity.
	 *
	 * @param graph the graph
	 * @param edgeTrans the edge trans
	 * @param membership the membership
	 * @return the graph modularity
	 */
	/*public double getGraphModularity(Graph<V,E> graph, Transformer<E,Number> edgeTrans, int[] membership) {
		double modularity = 0;
		if (graph.getEdgeCount() > 0) {
			int nodeNum = graph.getVertexCount();
			
			Collection<E> d_set = new HashSet<E>();
            d_set.addAll(graph.getEdges());
			if (edgeTrans == null)
				edgeTrans = new Transformer<E, Number>() {
					public Number transform(E e) {
						return 1;
					}
				};
		    
			List<V> id = new ArrayList<V>(graph.getVertices());//Indexer.getIndexer(graph);
			double[][] distanceM = new double[nodeNum][nodeNum];
			for (E e : d_set) {
				int source_id = id.indexOf(graph.getEndpoints(e).getFirst()) ;
				int target_id = id.indexOf(graph.getEndpoints(e).getSecond());
				float weight = edgeTrans.transform(e).floatValue();
				distanceM[source_id][target_id]= weight;
			}	

			modularity = getGraphModularity(distanceM, membership);

		}
		return modularity;
	}*/
	
	/*
	public double getGraphModularity(Graph<V,E> graph, Transformer<E,Number> edgeTrans, int[] membership) {
		double modularity = 0;
		if (graph.getEdgeCount() > 0) {
			int nodeNum = graph.getVertexCount();
			
			int types = 0;
			for (int i = 0; i < membership.length; i++) {
				if (types < membership[i])
					types = membership[i];
			}
			types++;
			
			double[] e = new double[types];
			double[] a = new double[types];
			
			Collection<E> d_set = new HashSet<E>();
            d_set.addAll(graph.getEdges());
			if (edgeTrans == null)
				edgeTrans = new Transformer<E, Number>() {
					public Number transform(E e) {
						return 1;
					}
				};

			boolean directed = graph instanceof DirectedGraph;
			boolean undirected = graph instanceof UndirectedGraph;
			
			List<V> id = new ArrayList<V>(graph.getVertices());//Indexer.getIndexer(graph);
			double m = 0;
			for (E edge : d_set) {
				int source_id = id.indexOf(graph.getEndpoints(edge).getFirst()) ;
				int target_id = id.indexOf(graph.getEndpoints(edge).getSecond());
				float weight = edgeTrans.transform(edge).floatValue();
				if (weight != 0) {
					if (membership[source_id] == membership[target_id]){
						if (directed) 
							e[membership[source_id]] += weight;	
						if (undirected) 
							e[membership[source_id]] += 2*weight;	
					}
					else {
						if (directed) 
							a[membership[source_id]] += weight;
						if (undirected){
							a[membership[source_id]] += weight;
							a[membership[target_id]] += weight;
						}
					}
					m += weight;
				}

			}	

			for (int i = 0; i < types; i++) {
				modularity += (e[i] - a[i] * a[i] / m) / m;
			}		

		}
		return modularity;
	}
	*/

}
