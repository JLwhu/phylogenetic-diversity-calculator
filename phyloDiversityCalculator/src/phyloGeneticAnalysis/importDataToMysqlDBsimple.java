package phyloGeneticAnalysis;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.json.JSONArray;

import web.dao.SpeciesNameDao;
import web.dao.SpeciesSpotRecordDao;
import web.dao.impl.SpeciesNameDaoImpl;
import web.dao.impl.SpeciesSpotRecordDaoImpl;
import web.exception.DaoException;
import web.model.SpeciesSpotRecord;

public class importDataToMysqlDBsimple {

	public static void main(String[] args) {

		importDataToMysqlDBsimple idms = new importDataToMysqlDBsimple();
		Connection conn = idms.getConnection();

		// importDataToMongoDB idtd = new importDataToMongoDB();
		HashMap newnames = idms.importSpeciesname();
		HashMap sciName = idms.getAllSpeciesScientificNameMap(conn);
		/*
		 * Iterator iter = newnames.entrySet().iterator(); while
		 * (iter.hasNext()) { Map.Entry entry = (Map.Entry) iter.next(); Object
		 * key = entry.getKey(); Object val = entry.getValue();
		 * System.out.print((String)key+val+"\r\n"); }
		 */
		idms.importData(conn,newnames, sciName);
		idms.closeConnection(conn);

	}

	public static HashMap importSpeciesname() {
		String fileName = "C:\\Users\\jingliu5\\UFLwork\\UFL web\\Jing_BirdWebsite\\dataset\\newspecies.txt";
		// String outfileName =
		// "C:\\Users\\jingliu5\\UFLwork\\UFL web\\Jing_BirdWebsite\\dataset\\NewCommonNamesNofound.txt";
		FileReader file = null;
		FileWriter fout = null;
		PrintWriter pw = null;
		HashMap speciesname = new HashMap();

		try {
			file = new FileReader(fileName);
			BufferedReader reader = new BufferedReader(file);
			// fout = new FileWriter(outfileName);

			String line = "";
			line = reader.readLine();

			while (line != null && line != "") { // && i<10
				String line1 = line;
				System.out.println(line);
				int idx = 0;
				String old = line.substring(0, line.indexOf("\t"));
				idx = line.indexOf("\t") + 1;
				line = line.substring(idx);
				String newname = line;
				speciesname.put(old, newname);
				line = reader.readLine();
			}

		} catch (FileNotFoundException e) {
			throw new RuntimeException("File not found");
		} catch (IOException e) {
			throw new RuntimeException("IO Error occured");
		} finally {
			if (file != null) {
				try {
					file.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			if (fout != null) {
				try {
					fout.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (pw != null)
				pw.close();

		}
		return speciesname;
	}

	public static void importData(Connection conn, HashMap newnames,
			HashMap sciName) {
		String fileName = "C:\\Users\\jingliu5\\UFLwork\\UFL web\\Jing_BirdWebsite\\dataset\\AllFilesForJing.ED.txt";
		String outfileName = "C:\\Users\\jingliu5\\UFLwork\\UFL web\\Jing_BirdWebsite\\dataset\\unfoundSpecies.txt";
		FileReader file = null;
		FileWriter fout = null;
		PrintWriter pw = null;

		try {
			file = new FileReader(fileName);
			BufferedReader reader = new BufferedReader(file);
			fout = new FileWriter(outfileName);

			String line = "";
			int i = 0;
			int j = 0;
			JSONArray resultJSONArray = new JSONArray();
			line = reader.readLine();

			// HashMap map = (HashMap) mongoSpeciesNameDao.getAllSpeciesNames();
			HashMap unfoundSpecies = new HashMap();
			String lastname = "";
			String currname = "";

			while (line != null && line != "") { // && i<10
				String line1 = line;
				System.out.println(line);
				int idx = 0;
				String recordName = line.substring(0, line.indexOf("\t"));
				idx = line.indexOf("\t") + 1;
				line = line.substring(idx);
				String speciesname = line.substring(0, line.indexOf("\t"));
				int idx1 = speciesname.indexOf(' ');
				speciesname = speciesname.substring(0, idx1) + "_"
						+ speciesname.substring(idx1 + 1);
				speciesname.replace(' ', '_');
				if (newnames.containsKey(speciesname)) {
					speciesname = (String) newnames.get(speciesname);
				}
				currname = speciesname;

				idx = line.indexOf("\t") + 1;
				line = line.substring(idx);
				String lat = line.substring(0, line.indexOf("\t"));
				idx = line.indexOf("\t") + 1;
				line = line.substring(idx);
				String lng = line.substring(0, line.indexOf("\t"));
				idx = line.indexOf("\t") + 1;
				line = line.substring(idx);
				String year = line.substring(0, line.indexOf("\t"));
				idx = line.indexOf("\t") + 1;
				line = line.substring(idx);
				String month = line.substring(0, line.indexOf("\t"));
				idx = line.indexOf("\t") + 1;
				line = line.substring(idx);
				String date = line.substring(0, line.indexOf("\t"));
				String spotdate = year + "-" + month + "-" + date;
				idx = line.indexOf("\t") + 1;
				line = line.substring(idx);
				String abundance = line.substring(0, line.indexOf("\t"));

				/* if (!lastname.equals(currname)){ */
				if (sciName.containsValue(speciesname)) {
					Integer speciesid = null;
					Iterator iter = sciName.entrySet().iterator();
					while (iter.hasNext()) {
						Map.Entry entry = (Map.Entry) iter.next();
						if (entry.getValue().equals(speciesname)) {
							speciesid = (Integer) entry.getKey();
							break;
						}
					}

					if (Double.valueOf(lng) <= 180
							&& Double.valueOf(lng) >= -180
							&& Double.valueOf(lat) <= 90
							&& Double.valueOf(lat) >= -90) {
						j++;
					}

					Statement stmt;
					stmt = conn.createStatement();
					// 插入数据
					if (abundance == null || abundance.equals(""))
					stmt.executeUpdate("insert into speciesspotrecord1 (recordName, speciesid, lat, lng, spotdate) values ('"
							+ recordName
							+ "',"
							+ speciesid
							+ ","
							+ lat
							+ ","
							+ lng + ",'" + spotdate + "')");
					else
						stmt.executeUpdate("insert into speciesspotrecord1 (recordName, speciesid, lat, lng, spotdate,abundance) values ('"
								+ recordName
								+ "',"
								+ speciesid
								+ ","
								+ lat
								+ ","
								+ lng + ",'" + spotdate + "',"+abundance+")");
						
					stmt.close();
				}
				/*
				 * } else { // fout.write(line1); //
				 * fout.write(speciesname+"\r\n"); if
				 * (!unfoundSpecies.containsKey(speciesname))
				 * unfoundSpecies.put(speciesname, speciesname); } lastname =
				 * currname; }
				 */
				i++;
				// if (i>100) break;
				line = reader.readLine();
			}
			System.out.println("all=" + i);
			System.out.println("database=" + j);

			// } catch (DaoException e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
		} catch (FileNotFoundException e) {
			throw new RuntimeException("File not found");
		} catch (IOException e) {
			throw new RuntimeException("IO Error occured");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally {
			if (file != null) {
				try {
					file.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			if (fout != null) {
				try {
					fout.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (pw != null)
				pw.close();

		}
	}

	public Connection getConnection() {
		Connection conn = null;
		// 1. 注册驱动
		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (ClassNotFoundException ex) {
			ex.printStackTrace();
		}

		try {
			// 2. 获取数据库的连接
			conn = DriverManager
					.getConnection(
							"jdbc:mysql://localhost:3306/phylogenetics?useUnicode=true&characterEncoding=GBK",
							"root", "1234");
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return conn;

	}

	public void closeConnection(Connection conn) {
		try {
			if (conn != null) {
				conn.close();
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public HashMap getAllSpeciesScientificNameMap(Connection conn) {

		HashMap retmap = new HashMap();
		Statement stmt = null; // 数据库表达式
		ResultSet rs = null; // 结果集
		// 3. 获取表达式
		try {
			stmt = conn.createStatement();

			rs = stmt.executeQuery("select * from speciesname");

			while (rs.next()) {
				retmap.put(rs.getInt("speciesid"),
						rs.getString("scientificName"));
			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
				if (stmt != null) {
					stmt.close();
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return retmap;

	}

}
