package com.omtg2sql.util;

import java.io.File;

import javax.swing.filechooser.FileFilter;

public class DefaultFileFilter extends FileFilter
{

	/**
	 * Extension of accepted files.
	 */
	protected String ext;

	/**
	 * Description of accepted files.
	 */
	protected String desc;

	/**
	 * Constructs a new filter for the specified extension and descpription.
	 * 
	 * @param extension
	 *            The extension to accept files with.
	 * @param description
	 *            The description of the file format.
	 */
	public DefaultFileFilter(String extension, String description)
	{
		ext = extension.toLowerCase();
		desc = description;
	}

	/**
	 * Returns true if <code>file</code> is a directory or ends with
	 * {@link #ext}.
	 * 
	 * @param file
	 *            The file to be checked.
	 * @return Returns true if the file is accepted.
	 */
	public boolean accept(File file)
	{
		return file.isDirectory() || file.getName().toLowerCase().endsWith(ext);
	}

	/**
	 * Returns the description for accepted files.
	 * 
	 * @return Returns the description.
	 */
	public String getDescription()
	{
		return desc;
	}

	/**
	 * Returns the extension for accepted files.
	 * 
	 * @return Returns the extension.
	 */
	public String getExtension()
	{
		return ext;
	}

	/**
	 * Sets the extension for accepted files.
	 * 
	 * @param extension
	 *            The extension to set.
	 */
	public void setExtension(String extension)
	{
		this.ext = extension;
	}
}