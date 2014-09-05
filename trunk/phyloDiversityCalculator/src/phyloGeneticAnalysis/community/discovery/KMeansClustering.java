package phyloGeneticAnalysis.community.discovery;

import java.util.Vector;

import pal.tree.Tree;
import weka.clusterers.SimpleKMeans;
import weka.core.Attribute;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Range;


// TODO: Auto-generated Javadoc
/**
 * The Class KMeansClustering.
 */
public class KMeansClustering extends CommunityDiscoveryStrategy {
	
	/** The options. */
	private String[] options;
	
	/** The kmeans variant. */
	private int kmeansVariant = KMEANS_VARIANTS.SKMENVC;
			
	/**
	 * The Class KMEANS_VARIANTS.
	 */
	public class KMEANS_VARIANTS {		
		
		
		/** Simple Kmeans Extension. */
		public static final int SKME = 1;		
		
		/** Simple Kmeans Extension Non Virtual Center.*/
		public static final int SKMENVC = 2;
		
		/** Simple Kmeans Extension Virtual Cernter Greedy. */
		public static final int SKMEVCG = 3;
	}

	/**
	 * Instantiates a new k means clustering.
	 *
	 * @param options the options
	 */
	public KMeansClustering(String[] options) {
		this.options = options;
	}
	
	/**
	 * Sets the kmeans variant.
	 *
	 * @param kmeansV the new kmeans variant
	 */
	public void setKmeansVariant(int kmeansV) {
		kmeansVariant = kmeansV;
	}

	/* (non-Javadoc)
	 * @see phyloGeneticAnalysis.community.discovery.CommunityDiscoveryStrategy#commnityDiscovery(pal.tree.Tree, int[][])
	 */
	@Override
	public String commnityDiscovery(Tree tree, int[][] regionSpecies) {
		Instances regionInstances = createRegionInstances(tree, regionSpecies);
		return commnityDiscovery(tree, regionInstances);
	}

	/* (non-Javadoc)
	 * @see phyloGeneticAnalysis.community.discovery.CommunityDiscoveryStrategy#commnityDiscovery(pal.tree.Tree, weka.core.Instances)
	 */
	@Override
	public String commnityDiscovery(Tree tree, Instances regionSpecies) {
	//	SimpleKMeans clusterer = new SimpleKMeans();
	//	SimpleKMeansExtension clusterer = new SimpleKMeansExtension();
		SimpleKMeansExtension clusterer;
		if (kmeansVariant ==KMEANS_VARIANTS.SKME )
			clusterer = new SimpleKMeansExtension();
		else if (kmeansVariant ==KMEANS_VARIANTS.SKMENVC)
			clusterer = new SimpleKMeansExtensionNonVirtualCenter();
		else
			clusterer = new SimpleKMeansExtensionVirtualCenterGreedy();
	    StringBuffer text = new StringBuffer();
		try {
	//		text.append(ClusterEvaluation.evaluateClusterer(clusterer,
	//				options));	
			

			clusterer.setOptions(options);     // set the options
			clusterer.setDistanceFunction(super.getDiversityStrategy());
			regionSpecies.setClassIndex(-1);
			clusterer.buildClusterer(regionSpecies);    // build the clusterer  
		//	text.append(ClusterEvaluation.printClusterings(clusterer, regionSpecies, null));
			text.append(ClusterEvaluation.printClusterStats(clusterer,
					regionSpecies));	
		//	ClusterEvaluation ce = new ClusterEvaluation();
		//	text.append(ce.clusterResultsToString());
			text.append("\r\n=== Cluster assignment for training data ===\r\n\r\n");
			int[] assignments = clusterer.getAssignments();			
			for (int i=0;i<assignments.length;i++){
				text.append(String.valueOf(assignments[i])+" ");
			}
			return text.toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

}
