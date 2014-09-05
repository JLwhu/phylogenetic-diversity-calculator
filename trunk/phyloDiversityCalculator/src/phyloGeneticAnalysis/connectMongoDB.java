package phyloGeneticAnalysis;

import java.net.UnknownHostException;
import java.util.List;
import java.util.Set;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.GenericXmlApplicationContext;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.geo.Circle;
import org.springframework.data.mongodb.core.geo.Point;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import web.dao.impl.MongoSpeciesSpotRecordDaoImpl;
import web.exception.DaoException;
import web.model.MongoSpeciesName;
import web.model.MongoSpeciesRecord;

public class connectMongoDB {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		// For XML
		// ApplicationContext ctx = new
		// GenericXmlApplicationContext("SpringConfig.xml");

		// For Annotation
		// ApplicationContext ctx =
		// new AnnotationConfigApplicationContext(SpringMongoConfig.class);
		// MongoOperations mongoOperation = (MongoOperations)
		// ctx.getBean("mongoTemplate");

		ApplicationContext ctx = new GenericXmlApplicationContext(
				"classpath:./META-INF/SpringConfig.xml");
		MongoOperations mongoOperation = (MongoOperations) ctx
				.getBean("mongoTemplate");

		List<MongoSpeciesRecord> mongoSpeciesRecords;

		Point point = new Point(40, -100);

		Set<String> colls = mongoOperation.getCollectionNames();
		for (String s : colls) {
			System.out.println(s);
		}

		MongoSpeciesSpotRecordDaoImpl mssrdi = new MongoSpeciesSpotRecordDaoImpl();
		try {
			mongoSpeciesRecords = mssrdi.getSpeciesSpotRecordByYearLocation(40,
					-100, 1,"2009");
		//	mongoSpeciesRecords = mssrdi.getSpeciesSpotRecordByYear("2009");
			System.out.println("4. Number = " + mongoSpeciesRecords.size());
		} catch (DaoException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
