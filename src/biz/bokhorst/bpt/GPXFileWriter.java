package biz.bokhorst.bpt;

/*
 Copyright 2011-2015 Marcel Bokhorst
 All Rights Reserved

 This program is free software; you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation; either version 3 of the License, or
 (at your option) any later version.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with this program; if not, write to the Free Software
 Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date; //import java.util.Locale;
import java.util.Locale;

//import me.guillaumin.android.osmtracker.db.DataHelper;
import android.database.Cursor;

public class GPXFileWriter {

	// Some constants
	private static final String XML_HEADER = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>";
	private static final String TAG_GPX = "<gpx" + " xmlns=\"http://www.topografix.com/GPX/1/1\"" + " version=\"1.1\""
			+ " xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\""
			+ " xsi:schemaLocation=\"http://www.topografix.com/GPX/1/1 http://www.topografix.com/GPX/1/1/gpx.xsd\">";
	private static final SimpleDateFormat POINT_DATE_FORMATTER = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'",
			Locale.getDefault());

	// Main logic
	public static void writeGpxFile(String trackName, Cursor cTrackPoints, Cursor cWayPoints, File target)
			throws IOException {
		FileWriter fw = new FileWriter(target);
		fw.write(XML_HEADER + "\n");
		fw.write(TAG_GPX + "\n");
		writeTrackPoints(trackName, fw, cTrackPoints);
		writeWayPoints(fw, cWayPoints);
		fw.write("</gpx>");
		fw.close();
	}

	private static void writeTrackPoints(String trackName, FileWriter fw, Cursor c) throws IOException {
		fw.write("\t" + "<trk>" + "\n");
		fw.write("\t\t" + "<name>" + trackName + "</name>" + "\n");
		fw.write("\t\t" + "<trkseg>" + "\n");

		c.moveToNext();
		while (!c.isAfterLast()) {
			StringBuffer out = new StringBuffer();
			out.append("\t\t\t" + "<trkpt lat=\"" + c.getDouble(c.getColumnIndex("LATITUDE")) + "\" " + "lon=\""
					+ c.getDouble(c.getColumnIndex("LONGITUDE")) + "\">" + "\n");
			out.append("\t\t\t\t" + "<ele>" + c.getDouble(c.getColumnIndex("ALTITUDE")) + "</ele>" + "\n");
			out.append("\t\t\t\t" + "<time>"
					+ POINT_DATE_FORMATTER.format(new Date(c.getLong(c.getColumnIndex("TIME")))) + "</time>" + "\n");
			out.append("\t\t\t\t" + "<cmt>speed=" + c.getString(c.getColumnIndex("SPEED")) + "</cmt>" + "\n");
			String activity = (c.getString(c.getColumnIndex("ACTIVITY")));
			if (activity != null)
				out.append("\t\t\t\t" + "<desc>" + activity + "</desc>" + "\n");
			out.append("\t\t\t\t" + "<hdop>" + c.getString(c.getColumnIndex("ACCURACY")) + "</hdop>" + "\n");
			out.append("\t\t\t" + "</trkpt>" + "\n");

			fw.write(out.toString());
			c.moveToNext();
		}
		c.close();

		fw.write("\t\t" + "</trkseg>" + "\n");
		fw.write("\t" + "</trk>" + "\n");
	}

	private static void writeWayPoints(FileWriter fw, Cursor c) throws IOException {
		c.moveToNext();
		while (!c.isAfterLast()) {
			StringBuffer out = new StringBuffer();
			out.append("\t" + "<wpt lat=\"" + c.getDouble(c.getColumnIndex("LATITUDE")) + "\" " + "lon=\""
					+ c.getDouble(c.getColumnIndex("LONGITUDE")) + "\">" + "\n");
			out.append("\t\t" + "<ele>" + c.getDouble(c.getColumnIndex("ALTITUDE")) + "</ele>" + "\n");
			out.append("\t\t" + "<time>" + POINT_DATE_FORMATTER.format(new Date(c.getLong(c.getColumnIndex("TIME"))))
					+ "</time>" + "\n");
			out.append("\t\t" + "<name>" + c.getString(c.getColumnIndex("NAME")) + "</name>" + "\n");
			out.append("\t\t" + "<cmt>speed=" + c.getString(c.getColumnIndex("SPEED")) + "</cmt>" + "\n");
			out.append("\t\t" + "<hdop>" + c.getString(c.getColumnIndex("ACCURACY")) + "</hdop>" + "\n");
			out.append("\t" + "</wpt>" + "\n");

			fw.write(out.toString());
			c.moveToNext();
		}
		c.close();
	}
}
