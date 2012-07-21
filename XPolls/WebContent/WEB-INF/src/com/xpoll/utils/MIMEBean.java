package com.xpoll.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import lotus.domino.Database;
import lotus.domino.Document;
import lotus.domino.MIMEEntity;
import lotus.domino.Session;
import lotus.domino.Stream;

import com.ibm.xsp.extlib.util.ExtLibUtil;

public class MIMEBean {

	public static Serializable restoreState(Document doc, String itemName) {
		Serializable result = null;
		try {
			Session session = ExtLibUtil.getCurrentSession();
			Stream mimeStream = session.createStream();
			MIMEEntity entity = doc.getMIMEEntity(itemName);
			entity.getContentAsBytes(mimeStream);
			ByteArrayOutputStream streamOut = new ByteArrayOutputStream();
			mimeStream.getContents(streamOut);
			byte[] stateBytes = streamOut.toByteArray();
			ByteArrayInputStream byteStream = new ByteArrayInputStream(stateBytes);
			ObjectInputStream objectStream = new ObjectInputStream(byteStream);
			Serializable restored = (Serializable) objectStream.readObject();
			result = restored;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	public static void saveState(Serializable object, Document doc, String itemName) {
		try {
			String unid = doc.getUniversalID();
			Database docParent = doc.getParentDatabase();
			String server = docParent.getServer();
			String filePath = docParent.getFilePath();
			saveStateAsSigner(object, server, filePath, unid, itemName);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void saveStateAsSigner(Serializable object, String server, String filePath, String unid, String itemName) {
		try {
			Session signerSession = ExtLibUtil.getCurrentSessionAsSigner();
			signerSession.setConvertMIME(false);
			Database database = signerSession.getDatabase(server, filePath);
			Document doc = database.getDocumentByUNID(unid);
			ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
			ObjectOutputStream objectStream = new ObjectOutputStream(byteStream);
			objectStream.writeObject(object);
			objectStream.flush();
			objectStream.close();
			Stream mimeStream = signerSession.createStream();
			MIMEEntity entity = null;
			MIMEEntity previousState = doc.getMIMEEntity(itemName);
			if (previousState == null) {
				// Do Nothing
			} else {
				previousState.remove();
				doc.save();
				doc.recycle();
				doc = database.getDocumentByUNID(unid);
			}
			entity = doc.createMIMEEntity(itemName);
			ByteArrayInputStream byteIn = new ByteArrayInputStream(byteStream.toByteArray());
			mimeStream.setContents(byteIn);
			entity.setContentFromBytes(mimeStream, "text/plain", MIMEEntity.ENC_QUOTED_PRINTABLE);
			doc.save();
			doc.recycle();
			database.recycle();
			signerSession.setConvertMIME(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
