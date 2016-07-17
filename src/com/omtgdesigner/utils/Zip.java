/**
 * Universidade Federal de Minas Gerais - UFMG
 * Departamento de Ciencia da Computacao - DCC
 * Laboratorio CS+X: www.labcsx.dcc.ufmg.br
 * 
 * omtg-designer
 */
package com.omtgdesigner.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * @author Luis Eduardo Oliveira Lizardo
 * @date: Jul 17, 2016
 *
 * @purpose:
 * 
 */
public class Zip {

	public static byte[] createZipFile(StringWriter ddlSW, StringWriter constraints) {
		
		
		return createZipFile(ddlSW, constraints, null);
	}
	
	/**
	 * @param ddlSW
	 * @param staticSW
	 * @param dynamicSW
	 * @return
	 */
	public static byte[] createZipFile(StringWriter ddlSW, StringWriter staticSW, StringWriter dynamicSW) {

		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		ZipOutputStream zipOutputStream = new ZipOutputStream(byteArrayOutputStream);

		try {

			zipOutputStream.putNextEntry(new ZipEntry("omtg-ddl-structure.sql"));
			byte[] ddlSWBuffer = ddlSW.toString().getBytes();
			zipOutputStream.write(ddlSWBuffer);
			zipOutputStream.closeEntry();

			if (staticSW != null && staticSW.toString().getBytes().length > 0) {
				zipOutputStream.putNextEntry(new ZipEntry("omtg-static-control.sql"));
				byte[] staticSWBuffer = staticSW.toString().getBytes();
				zipOutputStream.write(staticSWBuffer);
				zipOutputStream.closeEntry();
			}

			if (dynamicSW != null && dynamicSW.toString().getBytes().length > 0) {
				zipOutputStream.putNextEntry(new ZipEntry("omtg-dynamic-control.sql"));
				byte[] dynamicSWBuffer = dynamicSW.toString().getBytes();
				zipOutputStream.write(dynamicSWBuffer);
				zipOutputStream.closeEntry();
			}

			zipOutputStream.close();
			byteArrayOutputStream.close();

			return byteArrayOutputStream.toByteArray();

		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

}
