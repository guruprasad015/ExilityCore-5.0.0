/* *******************************************************************************************************
Copyright (c) 2015 EXILANT Technologies Private Limited

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in
all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
THE SOFTWARE.
 ******************************************************************************************************** */
package com.exilant.exility.ide;

import com.exilant.exility.core.CustomCodeInterface;
import com.exilant.exility.core.DataAccessType;
import com.exilant.exility.core.DataCollection;
import com.exilant.exility.core.DbHandle;
import com.exilant.exility.core.Page;
import com.exilant.exility.core.ResourceManager;
import com.exilant.exility.core.Spit;
import com.exilant.exility.core.Value;

/**
 * Get all resources as sorted lists
 * 
 */
public class CreateMissingDataElements implements CustomCodeInterface {

	private static final String FILE_NAME = "fileName";
	private static final String TRACE_TEXT = "traceText";
	private static final String INITIAL_TEXT = "<?xml version=\"1.0\" encoding=\"utf-8\" ?>\n<dataDictionary xmlns=\"http://com.exilant.exility/schema\" >\n\t<groups>"
			+ "\n\t\t<dataGroup name=\"autoGenerated\" label=\"Auto Generated \">\n\t\t\t\t<elements>";
	private static final String FINAL_TEXT = "\n\t\t\t</elements>\n\t\t</dataGroup>\n\t</groups>\n</dataDictionary>";

	@Override
	public int execute(DataCollection dc, DbHandle dbHandle, String gridName,
			String[] parameters) {

		Value value = dc.getValue(FILE_NAME);

		if (value == null) {
			dc.addError("File name is to be sent in a field named " + FILE_NAME);
			return 0;
		}

		String fileName = value.getTextValue();
		StringBuilder sbf = new StringBuilder();
		StringBuilder fieldList = new StringBuilder();
		int nbrIncludes = 0;
		int nbrAdded = 0;
		String[] pages = ResourceManager.getResourceList("page", ".xml");
		for (String pageName : pages) {
			Object object = ResourceManager.loadResource("page." + pageName,
					Page.class);
			if (object == null) {
				Spit.out(pageName + " could not be loaded.");
				continue;
			}
			if (object instanceof Page == false) {
				Spit.out(pageName + " is an inlude file.");
				nbrIncludes++;
				continue;
			}
			// Page page = (Page) object;
			int n = 0; // page.addMissingDataElements(sbf, dc, fieldList);
			Spit.out(pageName + " had " + n
					+ " missing data dictionary entries.");
			nbrAdded += n;
		}

		String headerText = pages.length + " pages and " + nbrIncludes
				+ " include panels scanned\n";
		if (nbrAdded > 0) {
			String textToWrite = INITIAL_TEXT + sbf.toString() + FINAL_TEXT;
			fileName = ResourceManager.getResourceFolder() + '/' + fileName;
			ResourceManager.saveText(fileName + ".xml", textToWrite);
			ResourceManager.saveText(fileName + ".xls", fieldList.toString());
			headerText += '"'
					+ fileName
					+ ".xml\" saved in your resource root folder with "
					+ nbrAdded
					+ " data elements. A .xls file is crated in the same folder with list of fields that are added";
		} else {
			headerText += "Congratulations!!. Your project has NO missing data dictionary entries.";
		}

		dc.addValue(TRACE_TEXT, Value.newValue(headerText));
		return 1;

	}

	@Override
	public DataAccessType getDataAccessType() {
		return DataAccessType.NONE;
	}
}
