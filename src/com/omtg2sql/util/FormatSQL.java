package com.omtg2sql.util;

import java.util.ArrayList;
import java.util.List;

public class FormatSQL {

	public static String validateLengthName(String name) {
		return name.length() <= 20 ? name: name.substring(0,20);
	}

	public static String toStringWithTokenSeparator(List<String> list) {

		return toString(list, ",", "", "", "'");
	}

	public static String toString(List<String> list) {

		return toString(list, ",", "", "", "");
	}

	public static String toString(List<String> list, String prefix) {

		return toString(list, ",", prefix, "_", "");
	}

	public static String toStringMask(List<String> list) {

		return toString(list, "+", "", "", "");
	}

	public static String toString(List<String> list, String separator, String prefix, 
			String prefixSeparator, String tokenSeparator) {

		String output = "";
		for (int i = 0; i < list.size(); i++) {

			if (i == list.size()-1) {
				output += prefix + prefixSeparator + tokenSeparator + list.get(i) + tokenSeparator;
			}
			else {
				output += prefix + prefixSeparator + tokenSeparator + list.get(i) + tokenSeparator + separator;
			}
		}
		return output;
	}

	//select 'length = '||length||', qt_diametr = '||qt_diametr||', qt_profund = '||qt_profund||', tipo_rede = '||tipo_rede 
	//from agua where rowid = 'AAAODAAAEAADUGcAAA';

	public static String columnsToString(List<String> list) {

		String output = "";
		for (int i = 0; i < list.size(); i++) {

			output += "' " + list.get(i) + " = '||" + list.get(i);

			if (i < list.size()-1) {
				output += "||";
			}
		}

		return output;
	}

	//'cidade spatial relation with bairro idBairro = '||idBairro||' codBairro = '||codBairro||' is not contains,covers,overlap'

	public static String columnsToString2(List<String> list, String prefix1, String prefix2) {

		if (!prefix2.equals("")) {
			prefix2 += "_";
		}

		String output = "";
		for (int i = 0; i < list.size(); i++) {

			output += list.get(i) + " = '||" + prefix1 + prefix2 + list.get(i) + "||' ";

		}

		return output;
	}

	public static String replaceAll(String main, String regex, String replacement) {
		return main.replaceAll(regex, replacement);
	}

	public static String replaceAllWithPrefix(String main, String regex, String replacement, String prefix) {
		return main.replaceAll(regex, prefix+replacement);
	}

	public static String replace(String main, String regex, String replacement) {
		return main.replace(regex, replacement);
	}

	public static String tableToString(List<String> subClassesTableNames) {

		return tableToString(subClassesTableNames, "");
	}

	public static String tableToString(List<String> subClassesTableNames, String actualSubClass) {

		String output = "";
		int cont = 1;

		for (int i = 0; i < subClassesTableNames.size(); i++) {

			if (!subClassesTableNames.get(i).equalsIgnoreCase(actualSubClass)) {

				output += subClassesTableNames.get(i) + " sub" + cont++;

				if (i < subClassesTableNames.size()-1) {
					output += ", ";
				}
			} else if (i == subClassesTableNames.size()-1){
				output = output.substring(0, output.length()-2);
			}
		}

		return output;
	}
	
	public static String tableToString(String subClassesTableName) {

		return subClassesTableName + " sub";
	}

	public static String keysToString2(List<String> subClasseTablekeys, int nSubClasses) {

		String output = "";

		for (int i = 1; i < nSubClasses; i++) {
			for (int j = i+1; j <= nSubClasses; j++) {

				for (int k = 0; k < subClasseTablekeys.size(); k++) {

					String k1 = "sub" + i + "." + subClasseTablekeys.get(k);
					String k2 = "sub" + j + "." + subClasseTablekeys.get(k);

					if (k == subClasseTablekeys.size()-1 && i == nSubClasses-1 && j == nSubClasses) {
						output += k1 + " = " + k2;
					}
					else {
						output += k1 + " = " + k2 + ", ";
					}
				}
			}
		}

		return output;
	}


	public static String keysToString(List<String> subClasseTablekeys, String prefix) {

		String output = "";
		if (!prefix.equals("")) {
			prefix += "_";
		}

		for (int k = 0; k < subClasseTablekeys.size(); k++) {

			String k1 = ":NEW." + prefix + subClasseTablekeys.get(k);
			String k2 = "sub." + prefix + subClasseTablekeys.get(k);

			if (k == subClasseTablekeys.size()-1) {
				output += k1 + " = " + k2;
			}
			else {
				output += k1 + " = " + k2 + " AND ";
			}
		}

		return output;
	}

	public static String keysToString(List<String> subClasseTablekeys, int nSubClasses, String prefix1, String prefix2) {

		String output = "";
		if (!prefix1.equals("")) {
			prefix1 += "_";
		}
		if (!prefix2.equals("")) {
			prefix2 += "_";
		}

		for (int i = 0; i < nSubClasses; i++) {

			for (int k = 0; k < subClasseTablekeys.size(); k++) {

				String k1 = ":NEW." + prefix2 + subClasseTablekeys.get(k);
				String k2 = "sub" + (i+1) + "." + prefix1 + subClasseTablekeys.get(k);

				if (k == 0) {
					output += "(";
				}

				if (k == subClasseTablekeys.size()-1 && i == nSubClasses-1) {
					output += k1 + " = " + k2 + ")";
				}
				else if (k == subClasseTablekeys.size()-1) {
					output += k1 + " = " + k2 + ") OR ";
				}
				else {
					output += k1 + " = " + k2 + " AND ";
				}
			}
		}

		return output;
	}

	public static void main(String[] args) {

		List<String> list = new ArrayList<String>();
		list.add("id");
		list.add("cod");
		list.add("cpf");

		System.out.println(FormatSQL.keysToString2(list,4));

	}
}