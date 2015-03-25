package eu.mgolisch.exacteditions;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import java.util.Vector;

import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.sqlite.SQLiteConnection;

import com.lowagie.text.pdf.PdfCopy;
import com.lowagie.text.pdf.PdfReader;


public class Utils {

	public static Path getHomeDirPath()
	{
		return Paths.get(System.getProperty("user.home"));
	}
	
	public static Path getDotDirPath()
	{
		return getHomeDirPath().resolve(".exacteditions-downloader");
	}
	
	public static Path getPropertiesPath()
	{
		return getDotDirPath().resolve("config.properties");
	}
	
	public static Path getDbPath()
	{
		return getDotDirPath().resolve("db.db");
	}
	
	public static Properties loadProperties()
	{
		Path PropertiesPath = getPropertiesPath();
		Properties prop = new Properties();
		if(Files.exists(getPropertiesPath()))
		{
			try {
				prop.load(Files.newInputStream(PropertiesPath));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return prop;
	}
	
	public static void storeProperties(Properties props){
		try {
		Path DotDirPath = getDotDirPath();
		Path properiesPath = getPropertiesPath();
		if(!Files.exists(DotDirPath))
			Files.createDirectories(DotDirPath);
		props.store(Files.newOutputStream(properiesPath), null);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}
	
	public static SQLiteConnection getDbConnection() {
		
		List<String> init_statements = new ArrayList<String>();
		init_statements.add("CREATE TABLE config (name TEXT ,  value TEXT)");
		init_statements.add("CREATE TABLE magazines ( id INTEGER PRIMARY KEY,name TEXT , titleid NUMERIC,pages NUMERIC)");
		init_statements.add("CREATE TABLE issues (id INTEGER PRIMARY KEY,name TEXT,titleid NUMERIC,issueid NUMERIC)");
		SQLiteConnection conn = null;
		try {
		Path dbpath = getDbPath();
		Path DotDirPath = getDotDirPath();
		if(!Files.exists(DotDirPath))
			Files.createDirectories(DotDirPath);
		boolean create = !Files.exists(dbpath);
		
		
			Class.forName("org.sqlite.JDBC");
			conn = (SQLiteConnection) DriverManager.getConnection("jdbc:sqlite:" + dbpath.toString());
			if(create)
				for(String sql : init_statements)
					conn.createStatement().execute(sql);
			} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return conn;
	}
	
	public static DefaultTableModel getIssuesModel(String titleid) {
		SQLiteConnection conn = getDbConnection();
		DefaultTableModel model = new DefaultTableModel();
		try {
			ResultSet rs = conn.createStatement().executeQuery("select * from issues where titleid="+titleid);
			ResultSetMetaData metaData = rs.getMetaData();

			// names of columns
			Vector<String> columnNames = new Vector<String>();
			int columnCount = metaData.getColumnCount();
			for (int column = 1; column <= columnCount; column++) {
			    columnNames.add(metaData.getColumnName(column));
			}

			// data of the table
			Vector<Vector<Object>> data = new Vector<Vector<Object>>();
			while (rs.next()) {
			    Vector<Object> vector = new Vector<Object>();
			    for (int columnIndex = 1; columnIndex <= columnCount; columnIndex++) {
			        vector.add(rs.getObject(columnIndex));
			    }
			    data.add(vector);
			}

			model = new DefaultTableModel(data, columnNames);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return model;
		
	}
	
	public static void DownloadPdf(String issueid,String name,int pages) {
		String downloaddir = Utils.loadProperties().getProperty("downloaddir");
		Path download_path = Paths.get(downloaddir);
		DownloadHelper helper = new DownloadHelper();
		String download_url_template = "http://www.exacteditions.com/displayPage.do?issue=%s&page=%s&size=3&format=pdf";
		List<byte[]> pdf_pages = new ArrayList<byte[]>();
		boolean islastpage = false;
		byte[] last = null;
		int page = 1;
		try {
			while(!islastpage) {
				String download_url = String.format(download_url_template, issueid,page);
				byte[] current = helper.GetPdfPage(download_url, true);
				pdf_pages.add(current);
				//if(last != null)
				//doesnt work as all pdf are generated on the fly with different create/modify times
				//maybe we can use itext to check if the content is equal??
				//	if(Arrays.equals(current, last))
				//		islastpage = true;
				last = current;
				page++;
				if(page >pages)
					islastpage=true;
			}
			 com.lowagie.text.Document document = new com.lowagie.text.Document();
			 PdfCopy copy = new PdfCopy(document, new FileOutputStream(download_path.resolve(name+".pdf").toFile().getAbsolutePath()));
		     document.open();
		     PdfReader reader;
		     int n;
			 for(byte[] pdf_page : pdf_pages) {
				 reader = new PdfReader(pdf_page);
			     // loop over the pages in that document
			     n = reader.getNumberOfPages();
			     for (int i = 0; i < n; ) {
			    	 copy.addPage(copy.getImportedPage(reader, ++i));
			    	 }
			     copy.freeReader(reader);
			     reader.close();
			     }
			 document.close();
			    
			
			
		} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		JOptionPane.showMessageDialog(null,"Pdf downloaded to "+ download_path.resolve(name+".pdf").toFile().getAbsolutePath()+".","Download finished",JOptionPane.INFORMATION_MESSAGE);
	}
	
	
	public static void updateIssues(String titleid) {
		List<HashMap<String,String>> issues = refreshIssues(titleid);
		SQLiteConnection conn = getDbConnection();
		Statement stmt;
		try {
			stmt = conn.createStatement();
			stmt.execute("delete from issues where titleid=" +titleid);
			for(HashMap<String,String> issue : issues)
				stmt.execute("insert into issues(name,titleid,issueid) values('" + issue.get("name") + "'," + titleid + ","+issue.get("issueid")+")");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	public static void AddMagazine(String name,String titleid,String pages)
	{
		SQLiteConnection conn = getDbConnection();
		try {
			conn.createStatement().execute("insert into magazines(name,titleid,pages) values('" + name +"',"+titleid+","+pages+")");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void RemoveMagazine(String id)
	{
		SQLiteConnection conn = getDbConnection();
		try {
			conn.createStatement().execute("delete from magazines where id=" + id);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public static int getPagesforMagazine(String titleid) {
		int ret = 0;
		SQLiteConnection conn = getDbConnection();
		try {
			Statement  stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery("select pages from magazines where titleid="+titleid);
			rs.next();
			ret = rs.getInt(1);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ret;
	}
	public static DefaultTableModel getMagazinesModel() {
		SQLiteConnection conn = getDbConnection();
		DefaultTableModel model = new DefaultTableModel();
		try {
			ResultSet rs = conn.createStatement().executeQuery("select * from magazines");
			ResultSetMetaData metaData = rs.getMetaData();

			// names of columns
			Vector<String> columnNames = new Vector<String>();
			int columnCount = metaData.getColumnCount();
			for (int column = 1; column <= columnCount; column++) {
			    columnNames.add(metaData.getColumnName(column));
			}

			// data of the table
			Vector<Vector<Object>> data = new Vector<Vector<Object>>();
			while (rs.next()) {
			    Vector<Object> vector = new Vector<Object>();
			    for (int columnIndex = 1; columnIndex <= columnCount; columnIndex++) {
			        vector.add(rs.getObject(columnIndex));
			    }
			    data.add(vector);
			}

			model = new DefaultTableModel(data, columnNames);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return model;
		
	}
	
	public static List<TitleComboItem> getMagazinesCombo() {
		SQLiteConnection conn = getDbConnection();
		List<TitleComboItem> list = new ArrayList<TitleComboItem>();
		try {
			ResultSet rs = conn.createStatement().executeQuery("select name,titleid from magazines");
			while (rs.next()) {
				TitleComboItem item = new TitleComboItem(rs.getString(1), ((Integer) rs.getInt(2)).toString());
				list.add(item);
			    }
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return list;
		
	}
	
	public static List<HashMap<String,String>> refreshIssues(String titleid) {
		DownloadHelper helper = new DownloadHelper();
		ArrayList<HashMap<String,String>> ret = new ArrayList<HashMap<String,String>>();
		String page_url = "https://www.exacteditions.com/browseEditions.do?titleId="+titleid;
		try {
			boolean hasmorepages = true;
			while(hasmorepages) {
			String page =  helper.GetPageContent(page_url);
			Document doc = Jsoup.parse(page);
			Elements issuedivs = doc.getElementsByClass("pageLink");
			for(Element issuediv:issuedivs) {
				Element link = issuediv.getElementsByTag("a").first();
				String href = link.attr("href");
				String name = link.attr("title");
				String issueid = href.substring(href.lastIndexOf("-")+1);
				System.out.println("Name: " + name + " Issue: " + issueid);
				HashMap<String,String> map = new HashMap<String, String>();
				map.put("name", name);
				map.put("issueid", issueid);
				map.put("titleid", titleid);
				ret.add(map);
			}
			Element nextpage = doc.getElementsByAttributeValue("title", "Next page").first();
			if(nextpage == null)
				hasmorepages = false;
			else
				page_url = nextpage.attr("abs:href").replace(";", "?");
			System.out.println(page_url);
			
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ret;
	}
}
