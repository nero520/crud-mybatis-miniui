package codeGenerate.factory;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.velocity.VelocityContext;

import codeGenerate.ColumnData;
import codeGenerate.CommonPageParser;
import codeGenerate.CreateBean;
import codeGenerate.def.CodeResourceUtil;

public class CodeGenerateFactory {
	private static final Log log = LogFactory.getLog(CodeGenerateFactory.class);
	private static String url = CodeResourceUtil.URL;
	private static String username = CodeResourceUtil.USERNAME;
	private static String passWord = CodeResourceUtil.PASSWORD;

	private static String buss_package = CodeResourceUtil.bussiPackage;
	private static String view_package = CodeResourceUtil.viewPackageUrl;
	private static String projectPath = getProjectPath();

	public static void codeGenerate(String tableName, String className,
			String codeName, String keyType) {
		CreateBean createBean = new CreateBean();
		createBean.setMysqlInfo(url, username, passWord);

		// String className = createBean.getTablesNameToClassName(tableName);
		className = className.substring(0, 1).toUpperCase()
				+ className.substring(1, className.length());
		String lowerName = className.substring(0, 1).toLowerCase()
				+ className.substring(1, className.length());
		String srcPath = projectPath + CodeResourceUtil.source_root_package
				+ "\\";
		String pckPath = srcPath + CodeResourceUtil.bussiPackageUrl + "\\";
		String webPath = projectPath + CodeResourceUtil.web_root_package + "\\"
				+ CodeResourceUtil.viewPackageUrl + "\\" + lowerName +"\\";

		String beanPath = "entity\\" + className + "Entity.java";
		String mapperPath = "mapper\\" + className + "Mapper.java";
		String servicePath = "service\\" + className + "Service.java";
		String serviceImplPath = "service\\Impl\\" + className + "ServiceImpl.java";
		String controllerPath = "controller\\" + className + "Controller.java";
		String sqlMapperPath = "mapper\\" + className + "Mapper.xml";

		String JspListPath = lowerName + "_list.jsp";
		String JspEditPath = lowerName + "_edit.jsp";
		String JsListPath = lowerName + "_list.js";
		String JsEditPath = lowerName + "_edit.js";

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

		VelocityContext context = new VelocityContext();
		context.put("className", className);
		context.put("lowerName", lowerName);
		context.put("codeName", codeName);
		context.put("tableName", tableName);
		context.put("bussPackage", buss_package);
		context.put("viewPackage", view_package);
		String viewControllerPackage = view_package.substring(view_package.lastIndexOf("/"));
		context.put("viewControllerPackage", viewControllerPackage);
		context.put("dateTime", sdf.format(new Date()));
		context.put("keyType", keyType);
		
		try {
			context.put("feilds", createBean.getBeanFeildList(tableName));
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			Map sqlMap = createBean.getAutoCreateSql(tableName);
			List<ColumnData> columnDatas = createBean.getColumnDatas(tableName);
			List<ColumnData> tbColumDatas = new ArrayList<ColumnData>();
			for (ColumnData columnData : columnDatas) {
				columnData.setTbColumnDBName(tableName + "." + columnData.getColumnDBName());
				tbColumDatas.add(columnData);
			}
			//"[{'id':'id','text':'id'},{'id':'name','text':'名字'}]"
			StringBuffer searchField = new StringBuffer();
			for (ColumnData columnData : columnDatas) {
				if (!"create_date".equals(columnData.getColumnName())&&!"modify_date".equals(columnData.getColumnName())) {
					searchField.append(",{'id':'"+columnData.getColumnName()+"','text':'");
					if (StringUtils.isNotBlank(columnData.getColumnComment())) {
						searchField.append(columnData.getColumnComment());
					}else {
						searchField.append(columnData.getColumnName());
					}
					searchField.append("'}");
				}
			}
			context.put("columnDatas", columnDatas);
			context.put("tbColumnDatas", tbColumDatas);
			context.put("searchField", "[" + searchField.substring(1) + "]");
			context.put("SQL", sqlMap);
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}

		CommonPageParser.WriterPage(context, "EntityTemplate.ftl", pckPath,	beanPath);
		CommonPageParser.WriterPage(context, "MapperTemplate.ftl", pckPath,	mapperPath);
		CommonPageParser.WriterPage(context, "ServiceTemplate.ftl", pckPath,servicePath);
		CommonPageParser.WriterPage(context, "ServiceImplTemplate.ftl",	pckPath, serviceImplPath);
		CommonPageParser.WriterPage(context, "MapperTemplate.xml", pckPath,sqlMapperPath);
		CommonPageParser.WriterPage(context, "ControllerTemplate.ftl", pckPath,	controllerPath);
		CommonPageParser.WriterPage(context, "JspListTemplate.ftl", webPath,JspListPath);
		CommonPageParser.WriterPage(context, "JspEditTemplate.ftl", webPath,JspEditPath);
		CommonPageParser.WriterPage(context, "JsListTemplate.ftl", webPath, JsListPath);
		CommonPageParser.WriterPage(context, "JsEditTemplate.ftl", webPath, JsEditPath);

		log.info("----------------------------代码生成完毕---------------------------");
	}

	public static String getProjectPath() {
		String path = System.getProperty("user.dir").replace("\\", "/") + "/";
		return path;
	}
}
