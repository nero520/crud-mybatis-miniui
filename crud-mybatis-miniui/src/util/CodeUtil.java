package util;

import java.util.ArrayList;
import java.util.List;

import codeGenerate.factory.CodeGenerateFactory;

/**
 * 入口函数
 * 
 * @author LiLong
 * 
 */
public class CodeUtil {
	public static void main(String[] args) {
		List<String> tables = new ArrayList<String>();
		// **************汽配********************************
		//tables.add("b_check_bill,CheckBill,检查单,02");
		tables.add("c_brand,Brand,品牌,01");
		
		
//		tables.add("b_bill,Bill,结算表,02");
		for (String tableStr : tables) {
			String[] table = tableStr.split(",");
			CodeGenerateFactory.codeGenerate(table[0], table[1], table[2],
					table[3]);
		}
		// String tableName = "c_brand"; //
		// String className = "brand";
		// String codeName = "品牌信息";
		// String keyType = "02";
		// CodeGenerateFactory.codeGenerate(tableName, className, codeName,
		// keyType);
	}
}