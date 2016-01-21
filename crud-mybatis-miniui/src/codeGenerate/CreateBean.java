package codeGenerate;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import codeGenerate.def.CodeResourceUtil;
import codeGenerate.def.TableConvert;

/**
 * 处理主类
 * 
 * @author LiLong
 * 
 */
public class CreateBean {
	private Connection connection = null;
	static String url;
	static String username;
	static String password;
	static String rt = "\r\t";
	String SQLTables = "show tables";
	private String method;
	private String argv;
	static String selectStr = "select ";
	static String from = " from ";

	static {
		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void setMysqlInfo(String url, String username, String password) {
		this.url = url;
		this.username = username;
		this.password = password;
	}

	public void setConnection(Connection connection) {
		this.connection = connection;
	}

	public Connection getConnection() throws SQLException {
		return DriverManager.getConnection(url, username, password);
	}

	public List<String> getTables() throws SQLException {
		Connection con = getConnection();
		PreparedStatement ps = con.prepareStatement(this.SQLTables);
		ResultSet rs = ps.executeQuery();
		List list = new ArrayList();
		while (rs.next()) {
			String tableName = rs.getString(1);
			list.add(tableName);
		}
		rs.close();
		ps.close();
		con.close();
		return list;
	}

	public List<ColumnData> getColumnDatas(String tableName)
			throws SQLException {
		String SQLColumns = "select "
				+ "column_name ,"
				+ "data_type,"
				+ "column_comment,"
				+ "0,"
				+ "0,"
				+ "character_maximum_length,"
				+ "is_nullable "
				+ "nullable from information_schema.columns where table_name =  '"
				+ tableName + "' " + "and table_schema =  '"
				+ CodeResourceUtil.DATABASE_NAME + "'";

		Connection con = getConnection();
		PreparedStatement ps = con.prepareStatement(SQLColumns);
		List columnList = new ArrayList();
		ResultSet rs = ps.executeQuery();
		StringBuffer str = new StringBuffer();
		StringBuffer getset = new StringBuffer();
		while (rs.next()) {
			// String name = rs.getString(1).toLowerCase();
			String name = rs.getString(1);
			String columnName = getFieldNameToPropertyName(name);
			String type = rs.getString(2);
			String comment = "";
			if ("create_date".equals(name)) {
				comment = "创建时间";
			} else if ("modify_date".equals(name)) {
				comment = "修改时间";
			} else if ("id".equals(name)) {
				comment = "ID";
			} else {
				comment = rs.getString(3);
			}
			String precision = rs.getString(4);
			String scale = rs.getString(5);
			String charmaxLength = rs.getString(6) == null ? "" : rs
					.getString(6);
			String nullable = TableConvert.getNullAble(rs.getString(7));
			type = getType(type, precision, scale);

			ColumnData cd = new ColumnData();
			cd.setColumnName(columnName);// columnName//miniUI项目用name，订单项目用columnName
			cd.setDataType(type);
			if (StringUtils.isNotBlank(comment)) {
				String[] t = comment.split(" ");
				cd.setColumnComment(t[0]);
				cd.setColumnCommentLong(comment);
			} else {
				cd.setColumnComment(name);
				cd.setColumnCommentLong(name);
			}
			cd.setColumnType(rs.getString(2));
			cd.setPrecision(precision);
			cd.setScale(scale);
			cd.setCharmaxLength(charmaxLength);
			cd.setNullable(nullable);
			cd.setColumnDBName(name);
			formatFieldClassType(cd);
			columnList.add(cd);
		}
		this.argv = str.toString();
		this.method = getset.toString();
		rs.close();
		ps.close();
		con.close();
		return columnList;
	}

	public String getBeanFeilds(String tableName) throws SQLException {
		List<ColumnData> dataList = getColumnDatas(tableName);
		StringBuffer str = new StringBuffer();
		StringBuffer getset = new StringBuffer();
		for (ColumnData d : dataList) {
			String name = d.getColumnName();
			String type = d.getDataType();
			String comment = d.getColumnComment();

			String maxChar = name.substring(0, 1).toUpperCase();
			str.append("\r\t").append("private ").append(type + " ")
					.append(name).append(";//   ").append(comment);
			String method = maxChar + name.substring(1, name.length());
			getset.append("\r\t").append("public ").append(type + " ")
					.append("get" + method + "() {\r\t");
			getset.append("    return this.").append(name).append(";\r\t}");
			getset.append("\r\t")
					.append("public void ")
					.append("set" + method + "(" + type + " " + name
							+ ") {\r\t");
			getset.append("    this." + name + "=").append(name)
					.append(";\r\t}");
		}
		this.argv = str.toString();
		this.method = getset.toString();
		return this.argv + this.method;
	}

	public List<ColumnData> getBeanFeildList(String tableName)
			throws SQLException {
		return getColumnDatas(tableName);
	}

	private void formatFieldClassType(ColumnData columnt) {
		String fieldType = columnt.getColumnType();
		String scale = columnt.getScale();

		if ("N".equals(columnt.getNullable())) {
			columnt.setOptionType("required=\"true\"");
		}
		if (("datetime".equals(fieldType)) || ("time".equals(fieldType))) {
			columnt.setClassType("mini-datepicker");
			columnt.setFormat(" dateFormat=\"yyyy-MM-dd HH:mm:ss\" ");
			if (StringUtils.isNotBlank(columnt.getOptionType()))
				columnt.setOptionType(columnt.getOptionType()
						+ " format=\"yyyy-MM-dd HH:mm:ss\" timeFormat=\"H:mm:ss\" showTime=\"true\" style=\"width: 160px;\" ");
			else
				columnt.setOptionType(" format=\"yyyy-MM-dd HH:mm:ss\" timeFormat=\"H:mm:ss\" showTime=\"true\" style=\"width: 160px;\" ");
		} else if ("date".equals(fieldType)) {
			columnt.setClassType("mini-datepicker");
			columnt.setFormat(" dateFormat=\"yyyy-MM-dd\" ");
			if (StringUtils.isNotBlank(columnt.getOptionType()))
				columnt.setOptionType(columnt.getOptionType()
						+ " format=\"yyyy-MM-dd\" style=\"width: 160px;\" ");
			else
				columnt.setOptionType(" format=\"yyyy-MM-dd HH:mm:ss\" style=\"width: 160px;\" ");
		} else if ("int".equals(fieldType)) {
			columnt.setClassType("mini-spinner");
			columnt.setClassType("mini-spinner");
			if (StringUtils.isNotBlank(columnt.getOptionType()))
				columnt.setOptionType(columnt.getOptionType()
						+ " allowLimitValue=\"false\" style=\"width: 160px;\" ");
			else
				columnt.setOptionType(" allowLimitValue=\"false\" style=\"width: 160px;\" ");
		} else if ("number".equals(fieldType)) {
			if ((StringUtils.isNotBlank(scale))
					&& (Integer.parseInt(scale) > 0)) {
				columnt.setClassType("mini-spinner");
			} else {
				columnt.setClassType("mini-spinner");
			}
			if (StringUtils.isNotBlank(columnt.getOptionType()))
				columnt.setOptionType(columnt.getOptionType()
						+ " allowLimitValue=\"false\" style=\"width: 160px;\" ");
			else
				columnt.setOptionType(" allowLimitValue=\"false\" style=\"width: 160px;\" ");
		} else if (("float".equals(fieldType)) || ("double".equals(fieldType))
				|| ("decimal".equals(fieldType))) {
			columnt.setClassType("mini-spinner");
			if (StringUtils.isNotBlank(columnt.getOptionType()))
				columnt.setOptionType(columnt.getOptionType()
						+ " allowLimitValue=\"false\" style=\"width: 160px;\" ");
			else
				columnt.setOptionType(" allowLimitValue=\"false\" style=\"width: 160px;\" ");
		} else {
			columnt.setClassType("mini-textbox");
			if (StringUtils.isNotBlank(columnt.getOptionType()))
				columnt.setOptionType(columnt.getOptionType()
						+ " vtype=\"rangeLength:1,50\" style=\"width: 160px;\" ");
			else
				columnt.setOptionType(" vtype=\"rangeLength:1,50\" style=\"width: 160px;\" ");
		}
	}

	public String getType(String dataType, String precision, String scale) {
		dataType = dataType.toLowerCase();
		if (dataType.contains("char"))
			dataType = "java.lang.String";
		else if (dataType.contains("int"))
			dataType = "java.lang.Integer";
		else if (dataType.contains("float"))
			dataType = "java.lang.Float";
		else if (dataType.contains("double"))
			dataType = "java.lang.Double";
		else if (dataType.contains("number")) {
			if ((StringUtils.isNotBlank(scale))
					&& (Integer.parseInt(scale) > 0))
				dataType = "java.math.BigDecimal";
			else if ((StringUtils.isNotBlank(precision))
					&& (Integer.parseInt(precision) > 6))
				dataType = "java.lang.Long";
			else
				dataType = "java.lang.Integer";
		} else if (dataType.contains("decimal"))
			dataType = "BigDecimal";
		else if (dataType.contains("date"))
			dataType = "java.util.Date";
		else if (dataType.contains("time"))
			dataType = "java.sql.Timestamp";
		else if (dataType.contains("clob"))
			dataType = "java.sql.Clob";
		else {
			dataType = "java.lang.Object";
		}
		return dataType;
	}

	public void getPackage(int type, String createPath, String content,
			String packageName, String className, String extendsClassName,
			String[] importName) throws Exception {
		if (packageName == null) {
			packageName = "";
		}
		StringBuffer sb = new StringBuffer();
		sb.append("package ").append(packageName).append(";\r");
		sb.append("\r");
		for (int i = 0; i < importName.length; i++) {
			sb.append("import ").append(importName[i]).append(";\r");
		}
		sb.append("\r");
		sb.append("/**\r *  entity. @author wolf Date:"
				+ new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
						.format(new Date()) + "\r */");
		sb.append("\r");
		sb.append("\rpublic class ").append(className);
		if (extendsClassName != null) {
			sb.append(" extends ").append(extendsClassName);
		}
		if (type == 1)
			sb.append(" ").append("implements java.io.Serializable {\r");
		else {
			sb.append(" {\r");
		}
		sb.append("\r\t");
		sb.append("private static final long serialVersionUID = 1L;\r\t");
		String temp = className.substring(0, 1).toLowerCase();
		temp = temp + className.substring(1, className.length());
		if (type == 1) {
			sb.append("private " + className + " " + temp + "; // entity ");
		}
		sb.append(content);
		sb.append("\r}");
		System.out.println(sb.toString());
		createFile(createPath, "", sb.toString());
	}

	public String getTablesNameToClassName(String tableName) {
		String[] split = tableName.split("_");
		if (split.length > 1) {
			StringBuffer sb = new StringBuffer();
			for (int i = 0; i < split.length; i++) {
				String tempTableName = split[i].substring(0, 1).toUpperCase()
						+ split[i].substring(1, split[i].length());
				sb.append(tempTableName);
			}

			return sb.toString();
		}
		String tempTables = split[0].substring(0, 1).toUpperCase()
				+ split[0].substring(1, split[0].length());
		return tempTables;
	}

	public String getFieldNameToPropertyName(String fieldName) {
		String[] split = fieldName.split("_");
		if (split.length > 1) {
			StringBuffer sb = new StringBuffer();
			for (int i = 1; i < split.length; i++) {
				try {
					String tempTableName = split[i].substring(0, 1).toUpperCase()
							+ split[i].substring(1, split[i].length());
					sb.append(tempTableName);
				} catch (Exception e) {
					System.out.println(split[i]);
					e.printStackTrace();
				}
				
			}

			return split[0] + sb.toString();
		}
		return fieldName;
	}

	public void createFile(String path, String fileName, String str)
			throws IOException {
		FileWriter writer = new FileWriter(new File(path + fileName));
		writer.write(new String(str.getBytes("utf-8")));
		writer.flush();
		writer.close();
	}

	public Map<String, Object> getAutoCreateSql(String tableName)
			throws Exception {
		Map sqlMap = new HashMap();
		List columnDatas = getColumnDatas(tableName);
		String columns = getColumnSplit(columnDatas);
		String columnsDB = getColumnDBSplit(columnDatas);
		String tbColumnsDB = getTBColumnDBSplit(columnDatas,tableName);
		String[] columnList = getColumnList(columns);
		String[] columnDBList = getColumnList(columnsDB);
		String columnDBFields = getColumnFields(columnsDB);
		String tbColumnDBFields = getColumnFields(tbColumnsDB);
		String insert = "insert into " + tableName + "("
				+ columnsDB.replaceAll("\\|", ",") + ")\n values(#{"
				+ columns.replaceAll("\\|", "},#{") + "})";
		// String update = getUpdateSql(tableName, columnDBList,columnList);
		String selectById = getSelectByIdSql(tableName, columnList);
		String delete = getDeleteSql(tableName, columnList);

		String updateSelective = getUpdateSelectiveSql(tableName, columnDatas);
		// sqlMap.put("columnList", columnList);
//		sqlMap.put("columnFields", columnDBFields);
		sqlMap.put("columnFields", tbColumnDBFields);
		sqlMap.put("insert", insert);// .replace("#{createTime}",
										// "now()").replace("#{updateTime}",
										// "now()")
		// sqlMap.put("update",update);//.replace("#{createTime}",
		// "now()").replace("#{updateTime}", "now()")
		sqlMap.put("update", updateSelective);// .replace("#{createTime}",
												// "now()").replace("#{updateTime}",
												// "now()")
		sqlMap.put("delete", delete);
		sqlMap.put("selectById", selectById);
		// sqlMap.put("updateSelective", updateSelective);
		return sqlMap;
	}

	public String getDeleteSql(String tableName, String[] columnsList)
			throws SQLException {
		StringBuffer sb = new StringBuffer();
		sb.append("delete ");
		sb.append("\t from ").append(tableName).append(" where ");
		sb.append(columnsList[0]).append(" = #{").append(columnsList[0])
				.append("}");
		return sb.toString();
	}

	public String getSelectByIdSql(String tableName, String[] columnsList)
			throws SQLException {
		StringBuffer sb = new StringBuffer();
		sb.append("select <include refid=\"Column_List\" /> \n");
		sb.append("\t from ").append(tableName).append(" where ");
		sb.append(columnsList[0]).append(" = #{").append(columnsList[0])
				.append("}");
		return sb.toString();
	}

	public String getColumnFields(String columns) throws SQLException {
		String fields = columns;
		if ((fields != null) && (!"".equals(fields))) {
			fields = fields.replaceAll("[|]", ",");
		}
		return fields;
	}

	public String[] getColumnList(String columns) throws SQLException {
		String[] columnList = columns.split("[|]");
		return columnList;
	}

	// public String getUpdateSql(String tableName, String[] columnsList,
	// String[] columnDBList)
	// throws SQLException {
	// StringBuffer sb = new StringBuffer();
	//
	// for (int i = 1; i < columnsList.length; i++) {
	// String column = columnsList[i];
	// String columnDB = columnDBList[i];
	// if ("create_date".equals(columnDB)) {
	// continue;
	// }
	// // if (!"CREATETIME".equals(column.toUpperCase())) {
	// // if ("UPDATETIME".equals(column.toUpperCase()))
	// // sb.append(column + "=now()");
	// // else {
	// sb.append(columnDB + "=#{" + column + "}");
	// // }
	// if (i + 1 < columnsList.length)
	// sb.append(",");
	// // }
	// }
	// String update = "update " + tableName + " set " + sb.toString()
	// + " where " + columnsList[0] + "=#{" + columnsList[0] + "}";
	// return update;
	// }

	public String getUpdateSelectiveSql(String tableName,
			List<ColumnData> columnList) throws SQLException {
		StringBuffer sb = new StringBuffer();
		ColumnData cd = (ColumnData) columnList.get(0);
		sb.append("\t<trim  suffixOverrides=\",\" >\n");
		for (int i = 1; i < columnList.size(); i++) {
			ColumnData data = (ColumnData) columnList.get(i);
			String columnName = data.getColumnName();
			// if ("create_date".equals(columnName)) {
			// continue;
			// }
			sb.append("\t<if test=\"").append(columnName).append(" != null ");

			if ("String" == data.getDataType()) {
				sb.append(" and ").append(columnName).append(" != ''");
			}
			sb.append(" \">\n\t\t");
			sb.append(data.getColumnDBName() + "=#{" + columnName + "},\n");
			sb.append("\t</if>\n");
		}
		sb.append("\t</trim>");
		String update = "update " + tableName + " set \n" + sb.toString()
				+ " where " + cd.getColumnDBName() + "=#{" + cd.getColumnName()
				+ "}";
		return update;
	}

	public String getColumnDBSplit(List<ColumnData> columnList)
			throws SQLException {
		StringBuffer commonColumns = new StringBuffer();
		for (ColumnData data : columnList) {
			commonColumns.append(data.getColumnDBName() + "|");
		}
		return commonColumns.delete(commonColumns.length() - 1,
				commonColumns.length()).toString();
	}
	public String getTBColumnDBSplit(List<ColumnData> columnList,String tableName)
			throws SQLException {
		StringBuffer commonColumns = new StringBuffer();
		for (ColumnData data : columnList) {
			commonColumns.append(tableName);
			commonColumns.append(".");
			commonColumns.append(data.getColumnDBName() + "|");
		}
		return commonColumns.delete(commonColumns.length() - 1,
				commonColumns.length()).toString();
	}
	public String getColumnSplit(List<ColumnData> columnList)
			throws SQLException {
		StringBuffer commonColumns = new StringBuffer();
		for (ColumnData data : columnList) {
			commonColumns.append(data.getColumnName() + "|");
		}
		return commonColumns.delete(commonColumns.length() - 1,
				commonColumns.length()).toString();
	}
}
