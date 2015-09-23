package com.eduworks.russel.ui.client.handler;

import java.util.Vector;

import com.eduworks.gwt.client.model.ZipRecord;
import com.eduworks.gwt.client.ui.handler.DragDropHandler;
import com.eduworks.russel.ui.client.model.RUSSELFileRecord;

public class FileHandler {
	public static int pendingFileUploads = 0;
	public static Vector<RUSSELFileRecord> pendingServerZipUploads = new Vector<RUSSELFileRecord>();
	public static Vector<ZipRecord> pendingZipUploads = new Vector<ZipRecord>();
	private static DragDropHandler ddh = null;

	/**
	 * addPendingServerZip Adds the provided packet to the pending server zip uploads list
	 * @param packet EPSSFileRecord
	 */
	public static void addPendingServerZip(RUSSELFileRecord packet) {
		pendingServerZipUploads.add(packet);
	}
	
	/**
	 * addPendingZip Adds the provided packet to the pending zip uploads list
	 * @param packet EPSSFileRecord
	 */
	public static void addPendingZip (ZipRecord packet) {
		pendingZipUploads.add(packet);
	}
	
	/**
	 * countUploads Counts the current total number of pending uploads
	 * @return int 
	 */
	public static int countUploads() {
		int acc = 0;
		if (ddh!=null&&ddh.readQueue!=null)
			acc += ddh.readQueue.size();
		if (pendingZipUploads!=null)
			acc += pendingZipUploads.size();
		if (pendingServerZipUploads!=null)
			acc += pendingServerZipUploads.size();
		acc += pendingFileUploads;
		return acc;
	}
	
	/**
	 * hookDropPanel Hooks the drop panel to the status window
	 * @param dropHandler DragDropHandler
	 */
	public static void hookDropPanel(DragDropHandler dropHandler) {
		ddh = dropHandler;
	}
}
