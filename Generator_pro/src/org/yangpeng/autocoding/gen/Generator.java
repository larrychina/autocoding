package org.yangpeng.autocoding.gen;



import freemarker.template.Configuration;
import freemarker.template.Template;
import org.apache.log4j.Logger;
import org.yangpeng.autocoding.common.Column;
import org.yangpeng.autocoding.common.Table;
import org.yangpeng.autocoding.utils.CamelCaseUtils;
import org.yangpeng.autocoding.utils.FileHelper;

import java.io.*;
import java.net.URL;
import java.net.URLDecoder;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.util.*;

/**
 * 自动生成代码
 * @author yangpeng on 2016-08-28
 *
 */
public class Generator {
	private Logger logger = Logger.getLogger(this.getClass());
	private Properties properties = null;
	
	public Generator() throws Exception{
		properties = new Properties();
		// 此方式打完jar包配置文件读取出错
		// String fileDir = this.getClass().getClassLoader().getResource("generator.xml").getFile();
		InputStream fi = this.getClass().getClassLoader().getResourceAsStream("generator.xml");
		properties.loadFromXML(fi);
	}

	/**
	 * 解析标生成
	 * @param tableName
	 * @param url
	 * @param username
	 * @param password
	 * @return
	 * @throws Exception
	 */
	public Table parseTable(String tableName,String url,String username,String password) throws Exception{
		String driverName = properties.getProperty("jdbc.driver");
		url = "jdbc:mysql://" + url.trim() + "?useUnicode=true&amp;characterEncoding=UTF-8" ;

		String catalog = properties.getProperty("jdbc.catalog");
		String schema = properties.getProperty("jdbc.schema");
		schema = schema == null ? "%" : schema;
		String column = "%";

		logger.debug("driver>>"+driverName);
		logger.debug("url>>"+url);
		logger.debug("name>>"+username);
		logger.debug("password>>"+password);
		logger.debug("catalog>>"+catalog);
		logger.debug("schema>>"+schema);

		Class.forName(driverName);
		Connection conn = java.sql.DriverManager.getConnection(url, username, password);
		DatabaseMetaData dmd = conn.getMetaData();

		dmd.getTableTypes() ;

		ResultSet rs = dmd.getColumns(catalog, schema, tableName, column);
		List<Column> columns = new ArrayList<Column>();
		while (rs.next()) {
			Column c = new Column();

			c.setLabel(rs.getString("REMARKS"));

			String name = rs.getString("COLUMN_NAME");
			c.setName(CamelCaseUtils.toCamelCase(name));
			c.setDbName(name);

			String dbType = rs.getString("TYPE_NAME");
			String type = properties.getProperty(dbType);
			c.setDbType(dbType);
			c.setType(type == null ? "String" : type);

			c.setLength(rs.getInt("COLUMN_SIZE"));
			c.setDecimalDigits(rs.getInt("DECIMAL_DIGITS"));
			c.setNullable(rs.getBoolean("NULLABLE"));
			columns.add(c);
		}

		List<Column> pkColumns = new ArrayList<Column>();
		ResultSet pkrs = dmd.getPrimaryKeys(catalog, schema, tableName);
		while(pkrs.next()){
			Column c = new Column();
			String name = pkrs.getString("COLUMN_NAME");
			c.setName(CamelCaseUtils.toCamelCase(name));
			c.setDbName(name);
			pkColumns.add(c);
		}

		conn.close();

		Table t = new Table();

		String prefiex = properties.getProperty("tableRemovePrefixes");
		String name = tableName;
		if( prefiex != null && !"".equals(prefiex) ){
			name = tableName.split(prefiex)[0];
		}
		t.setName(CamelCaseUtils.toCamelCase(name));
		t.setDbName(tableName);
		t.setColumns(columns);
		t.setPkColumns(pkColumns);
		return t;
	}

	public List<String> getTabels(	String url,String username,String password) throws Exception{
		String driverName = properties.getProperty("jdbc.driver");
		url = "jdbc:mysql://" + url + "?useUnicode=true&amp;characterEncoding=UTF-8" ;

		String catalog = properties.getProperty("jdbc.catalog");
		String schema = properties.getProperty("jdbc.schema");
		schema = schema == null ? "%" : schema;
		String column = "%";

		logger.debug("driver>>" + driverName);
		logger.debug("url>>" + url);
		logger.debug("name>>" + username);
		logger.debug("password>>" + password);
		logger.debug("catalog>>"+catalog);
		logger.debug("schema>>"+schema);

		Class.forName(driverName);
		Connection conn = java.sql.DriverManager.getConnection(url, username, password);
		DatabaseMetaData dmd = conn.getMetaData();

		ResultSet rs = dmd.getTables(catalog, schema, null, new String[]{"TABLE"}) ;

		List<String> tables = new ArrayList<String>();
		while (rs.next()) {
			tables.add(rs.getString("TABLE_NAME"));
		}

		return tables ;
	}
	
	/**
	 * <p>Discription:[生成映射文件和实体类]</p>
	 * Created on 2016年6月28日
	 * @param tableName 要声称映射文件和实体类的表名称
	 * @throws Exception
	 * @author:[杨鹏]
	 */
	public void gen(String tableName,String url,String username,String password,String basepackage,String outRoot) throws Exception{
		Configuration cfg = new Configuration(Configuration.VERSION_2_3_21);

		Map<String, Object> root = new HashMap<String, Object>();
		Table t = this.parseTable(tableName,url,username,password);
		root.put("table", t);
		root.put("className", t.getNameUpper());
		root.put("classNameLower", t.getName());
		root.put("package", basepackage);

		// 此方法在jar下运行失败
		// String templateDir = this.getClass().getClassLoader().getResource("templates").getPath();
		// System.out.println(this.getClass().getClassLoader().getResource("templates").getPath());
		String templateDir = Generator.getProjectPath() + "\\templates";
		File tdf = new File(templateDir);
		List<File> files = FileHelper.findAllFile(tdf);

		for(File f: files){
			String parentDir = "";
			if( f.getParentFile().compareTo(tdf) != 0 ){
				parentDir = f.getParent().split("templates")[1];
			}
			cfg.setClassForTemplateLoading(this.getClass(), "/templates" + parentDir);

			Template template = cfg.getTemplate(f.getName());
			template.setEncoding("UTF-8");

			String parentFileDir = FileHelper.genFileDir(parentDir, root);
			parentFileDir = parentFileDir.replace(".", "/");
			String file = FileHelper.genFileDir(f.getName(),root).replace(".ftl", ".java");


			File newFile = FileHelper.makeFile(outRoot + parentFileDir + "/" + file);
			Writer out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream( newFile ), "UTF-8"));
			template.process(root, out);
			logger.debug("已生成文件：" + outRoot + parentFileDir + "/" + file);
		}
	}

	
	public static void main(String[] args) throws Exception{
		Generator g = new Generator();
		List<String> tables =  g.getTabels("192.168.8.128:3306/permission_pro","root","root") ;
		for (int i = 0; i < tables.size() ; i++) {
			System.out.println(tables.get(i));
		}
		// g.gen("sys_role");
		System.out.println("模版文件生成完毕……");
	}


	public static String getProjectPath() throws UnsupportedEncodingException {
		URL url = Generator.class.getProtectionDomain().getCodeSource().getLocation();
		String filePath = URLDecoder.decode(url.getPath(), "utf-8");
		if (filePath.endsWith(".jar")) {
			filePath = filePath.substring(0, filePath.lastIndexOf("/") + 1);
		}
		File file = new File(filePath);
		filePath = file.getAbsolutePath();
		return filePath;
	}

	public static String getRealPath() {
		String realPath = Generator.class.getClassLoader().getResource("").getFile();
		File file = new File(realPath);
		realPath = file.getAbsolutePath();
		try {
			realPath = URLDecoder.decode(realPath, "utf-8");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return realPath;
	}
}
