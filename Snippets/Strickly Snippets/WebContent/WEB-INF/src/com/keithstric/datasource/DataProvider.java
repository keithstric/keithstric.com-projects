package com.keithstric.datasource;

/**
 * DataProvider - viewScope bean to provide the data source for all forms within an application
 * One bean to rule them all
 * 
 * TODO We probably want to include some better error handling. Maybe print friendly errors out to the
 * FacesMessages block, create some custom exceptions or something
 */

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

import lotus.domino.Database;
import lotus.domino.DateTime;
import lotus.domino.Document;
import lotus.domino.EmbeddedObject;
import lotus.domino.Item;
import lotus.domino.MIMEEntity;
import lotus.domino.Name;
import lotus.domino.NotesException;
import lotus.domino.RichTextItem;
import lotus.domino.Stream;

import com.keithstric.utils.jsf.JSFUtil;
import com.ibm.domino.xsp.module.nsf.NotesContext;
import com.ibm.xsp.component.UIScriptCollector;
import com.ibm.xsp.component.UIFileuploadEx.UploadedFile;
import com.ibm.xsp.designer.context.ServletXSPContext;
import com.ibm.xsp.designer.context.XSPUrl;
import com.ibm.xsp.extlib.util.ExtLibUtil;
import com.ibm.xsp.http.IUploadedFile;
import com.ibm.xsp.http.MimeMultipart;
import com.ibm.xsp.model.DataObject;

public class DataProvider implements DataObject,Serializable {
	
	private static final long serialVersionUID = 1L;
	public Map<String, Object> cachedValues;
	public Map<String, Object> changedValues;
	private String docUNID;
	private transient Document curDoc;

	public DataProvider() {
		cachedValues = new HashMap<String, Object>();
		changedValues = new HashMap<String, Object>();
	}
	
	/**
	 * Get the current instance of the DataProvider bean
	 * @return DataProvider - the current instance
	 */
	public static DataProvider getCurrentInstance() {
		return (DataProvider) JSFUtil.getVariableValue("data");
	}
	
	public void save(String formName) {
		if (!changedValues.isEmpty()) {
			try {
				boolean isNewDoc = false;
				Document doc = getDocument();
				if (doc == null) {
					isNewDoc = true;
					Database db = ExtLibUtil.getCurrentDatabase();
					doc = db.createDocument();
					if (formName != null || !formName.equals("")) {
						doc.replaceItemValue("Form", formName);
					} else {
						//We should put some logic here to figure out the default form name
						doc.replaceItemValue("Form", "afe");
					}
				}
				setCurDoc(doc);
				for (String itemName : changedValues.keySet()) {
					Object itemValue = changedValues.get(itemName);
					writeItemValue(doc, itemName, itemValue);
				}
				doc.save(true,false);
				String UNID = doc.getUniversalID();
				if (isNewDoc) {
					redirectToSavedDoc(UNID);
				}
			} catch (NotesException e) {
				e.printStackTrace();
			}
			clearAllValues();
		} else {
			//Nothing to do here, move along...
		}
	}
	
	public void save() {
		save(null);
	}
	
	public Class<?> getType(Object fieldName) {
		if (fieldName != null){
			return fieldName.getClass();
		}else{
			return Object.class;
		}
	}

	public Object getValue(Object itemName) {
		String idStr = "";
		if (itemName != null) {
			idStr = itemName.toString();
		}else{
			return null;
		}
		if (changedValues.containsKey(idStr)) {
			return changedValues.get(idStr);
		} else if (cachedValues.containsKey(idStr)) {
			return cachedValues.get(idStr);
		} else {
			try {
				if ((getDocument() == null && getCurDoc() == null) || getDocUNID().equals("")) {
					return null;
				} else {
					Document doc = getCurDoc();
					Item notesItem = doc.getFirstItem(itemName.toString());
					if (notesItem != null) {
						Object itemValue = null;
						if (notesItem.getType() == Item.MIME_PART) {
							itemValue = notesItem.getMIMEEntity().getContentAsText();
						} else if (notesItem.getType() == Item.NUMBERS) {
							itemValue = notesItem.getValueDouble();
						} else if (notesItem.getType() == Item.DATETIMES) {
							itemValue = notesItem.getDateTimeValue().toJavaDate();
						} else if (notesItem.getType() == Item.NAMES) {
							if (notesItem.getValues().size() > 1) {
								itemValue = notesItem.getValues().toArray();
							}else{
								Name nameValue = NotesContext.getCurrent().getCurrentSession().createName(notesItem.getValueString());
								// Must return a String as a lotus.domino.Name is not serializable
								itemValue = nameValue.getCanonical();
							}
						} else if (notesItem.getType() == Item.ATTACHMENT) {
							// TODO - Not sure what we need to do here. Maybe return the file name?					
						} else {
							if (!notesItem.getValues().isEmpty()) {
								if (notesItem.getValues().size() > 1) {
									itemValue = notesItem.getValues();
								} else {
									itemValue = notesItem.getValueString();
								}
							}
						}
						cachedValues.put(doc.getFirstItem(itemName.toString()).getName(), itemValue);
						return itemValue;
					}
				}
			} catch (NotesException e) {
				e.printStackTrace();
			} catch (NullPointerException e) {
				return null;
			}
		}
		return null;
	}

	public boolean isReadOnly(Object arg0) {
		return false;
	}

	public void setValue(Object itemName, Object itemValue) {
		Object oldValue = getValue(itemName);
		Boolean valueChanged = false;
		if (itemValue == null && oldValue != null) {
			valueChanged = true;
		}else if (itemValue != null && oldValue == null) {
			valueChanged = true;
		}else if ((oldValue != null && itemValue != null) && !itemValue.equals(oldValue)) {
			valueChanged = true;
		}
		if (valueChanged) {
			try {
				if (itemValue == null || itemValue.equals("")) {
					itemValue = null;
				} else {
					if (itemValue instanceof DateTime) {
						DateTime nDate = (DateTime) itemValue;
						itemValue = nDate.toJavaDate();
					}
				}
				changedValues.put(itemName.toString(), itemValue);
			} catch (NotesException e) {
				e.printStackTrace();
			}
		}
	}

	private void updateCachedValue(String keyName, Object itemValue) {
		if (changedValues.containsKey(keyName)) {
			cachedValues.put(keyName, itemValue);
		}
	}
	
	private void clearAllValues() {
		cachedValues.clear();
		changedValues.clear();
	}
	
	public String getDocUNID() {
		if (docUNID == null) {
			Map<String,Object> paramValues = (Map) JSFUtil.getVariableValue("paramValues");
			if (paramValues.containsKey("documentId")) {
				String[] documentIdArr = (String[]) paramValues.get("documentId");
				if (documentIdArr == null || documentIdArr.length == 0) {
					String[] documentUNIDArr = (String[]) paramValues.get("unid");
					if (documentUNIDArr == null || documentUNIDArr.length == 0) {
						docUNID = "";
					} else {
						docUNID = documentUNIDArr[0];
					}
				} else {
					docUNID = documentIdArr[0];
				}
			}
		}
		return docUNID;
	}

	public void setDocUNID(String docUNID) {
		this.docUNID = docUNID;
	}

	public Document getCurDoc() {
		try {
			if (curDoc != null) {
				return curDoc;
			}else{
				return getDocument();
			}
		} catch (NotesException e) {
			e.printStackTrace();
		}
		return null;
	}

	public void setCurDoc(Document curDoc) {
		this.curDoc = curDoc;
	}

	public Document getDocument() throws NotesException {
		String documentUNID = getDocUNID();
		if (documentUNID == null || documentUNID.equals("")) {
			return null;
		} else {
			Database db = ExtLibUtil.getCurrentDatabase();
			Document doc = db.getDocumentByUNID(documentUNID);
			return doc;
		}
	}
	
	public void redirectToSavedDoc(String unid) {
		try {
			ServletXSPContext thisContext = (ServletXSPContext) JSFUtil.getVariableValue("context");
			XSPUrl thisUrl = thisContext.getUrl();
			thisUrl.removeAllParameters();
			FacesContext.getCurrentInstance().getExternalContext().redirect(thisUrl + "?documentId=" + unid);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void writeRichText(Document doc, Item notesItem, String itemValue) throws NotesException {
		if (notesItem != null) {
			MIMEEntity entity = notesItem.getMIMEEntity();
			if (entity == null) {
				if (doc == null) {
					// TODO: probably need to throw an error here as several
					// things have went wrong already
					return;
				} else {
					String itemName = notesItem.getName();
					doc.removeItem(itemName);
					entity = doc.createMIMEEntity(itemName);
				}
			}
			Stream stream = NotesContext.getCurrent().getCurrentSession().createStream();
			stream.writeText(itemValue);
			entity.setContentFromText(stream, "text/html;charset=UTF-8", entity.ENC_IDENTITY_7BIT);
		}
	}
	
	private void writeMultiValues(Item notesItem, String itemValue) throws NotesException {
		if (itemValue.contains(",")) {
			String[] itemValues = itemValue.split(",");
			Vector values = new Vector(Arrays.asList(itemValues));
			notesItem.setValues(values);
		}
	}
	
	private void writeUploadedFile(RichTextItem notesItem, IUploadedFile uploadedFile) throws NotesException {
		File fileTemp = uploadedFile.getServerFile();
		File fileCorrected = new File(fileTemp.getParentFile().getAbsolutePath() + File.separator + uploadedFile.getClientFileName());
		fileTemp.renameTo(fileCorrected);
		notesItem.embedObject(EmbeddedObject.EMBED_ATTACHMENT, null, fileCorrected.getAbsolutePath(), null);
	}
	
	public void deleteAttachment(String itemName, String fileName) {
		Document doc;
		try {
			doc = getDocument();
			if (doc != null) {
				Item notesItem = doc.getFirstItem(itemName);
				if (notesItem != null && notesItem instanceof RichTextItem) {
					RichTextItem notesRTItem = (RichTextItem) notesItem;
					EmbeddedObject eo = notesRTItem.getEmbeddedObject(fileName);
					if (eo != null) {
						eo.remove();
						doc.save(true, false);
						UIComponent refreshTarget = JSFUtil.findComponent("attachmentHolder");
						if (refreshTarget != null) {
							UIScriptCollector.find().addScript(
									"XSP.addOnLoad(function(){" + "XSP.partialRefreshGet('"
											+ refreshTarget.getClientId(FacesContext.getCurrentInstance()) + "');" + "});");
						}
					}
				}
			}
		} catch (NotesException e) {
			e.printStackTrace();
		}
	}
	
	private void writeItemValue(Document doc, String itemName, Object itemValue) {
		try {
			if (itemValue == null) {
				doc.removeItem(itemName);
			} else {
				if (doc == null) {
					doc = getCurDoc();
				}
				Item notesItem = doc.getFirstItem(itemName);
				if (notesItem != null) {
					if ((notesItem.getType() == Item.MIME_PART || notesItem.getType() == Item.HTML || notesItem.getType() == Item.RICHTEXT)
							&& (!(itemValue instanceof UploadedFile))) {
						MimeMultipart rtValue = (MimeMultipart) itemValue;
						writeRichText(doc, notesItem, rtValue.getHTML());
					} else if (notesItem.getType() == Item.ATTACHMENT) {
						UploadedFile upFile = (UploadedFile) itemValue;
						IUploadedFile uploadedFile = upFile.getUploadedFile();
						writeUploadedFile((RichTextItem) notesItem, uploadedFile);
					} else if (notesItem.getType() == Item.DATETIMES) {
						if (itemValue instanceof Date) {
							DateTime nDate = NotesContext.getCurrent().getCurrentSession().createDateTime((Date) itemValue);
							doc.replaceItemValue(itemName, nDate);
						} else if (itemValue instanceof DateTime) {
							doc.replaceItemValue(itemName, itemValue);
						} else {
							//TODO: Probably need to throw an error here
						}
					} else if (itemValue instanceof UploadedFile) {
						UploadedFile upFile = (UploadedFile) itemValue;
						IUploadedFile uploadedFile = upFile.getUploadedFile();
						RichTextItem notesRTItem = (RichTextItem) notesItem;
						writeUploadedFile(notesRTItem, uploadedFile);
					} else if (itemValue instanceof ArrayList) {
						ArrayList itemValueArr = (ArrayList) itemValue;
						Vector newItemValue = new Vector(itemValueArr);
						doc.replaceItemValue(itemName, newItemValue);
					}else if (notesItem.getType() == Item.NAMES) {
						Name nameValue = null;
						if (itemValue instanceof Name) {
							nameValue = (Name) itemValue;
						}else{
							nameValue = NotesContext.getCurrent().getCurrentSession().createName(itemValue.toString());
						}
						itemValue = nameValue.getCanonical();
						doc.replaceItemValue(itemName, nameValue);
					} else {
						doc.replaceItemValue(itemName, itemValue);
					}
				} else {
					if (itemValue instanceof MimeMultipart) {
						MimeMultipart rtValue = (MimeMultipart) itemValue;
						notesItem = doc.createRichTextItem(itemName);
						writeRichText(doc, notesItem, rtValue.getHTML());
					} else if (itemValue instanceof Long || itemValue instanceof Double) {
						notesItem = doc.replaceItemValue(itemName, itemValue);
					} else if (itemValue instanceof String) {
						doc.replaceItemValue(itemName, itemValue);
					} else if (itemValue instanceof UploadedFile) {
						UploadedFile upFile = (UploadedFile) itemValue;
						IUploadedFile uploadedFile = upFile.getUploadedFile();
						RichTextItem notesRTItem = doc.createRichTextItem(itemName);
						writeUploadedFile(notesRTItem, uploadedFile);
					} else if (itemValue instanceof Date || itemValue instanceof DateTime) {
						Date javaDate;
						DateTime nDate;
						String itemValueStr = itemValue.toString();
						// Dunno why we're having to do this, null should be
						// caught by the first null check
						// for some reason it's not seeing the itemValue as null
						// until we turn it into a string
						if (itemValue != null && itemValueStr != null && !itemValueStr.isEmpty()) {
							if (itemValue instanceof DateTime) {
								nDate = NotesContext.getCurrent().getCurrentSession().createDateTime(itemValue.toString());
							} else {
								javaDate = (Date) itemValue;
								nDate = NotesContext.getCurrent().getCurrentSession().createDateTime(javaDate);
							}
						} else {
							javaDate = new Date();
							nDate = NotesContext.getCurrent().getCurrentSession().createDateTime(javaDate);
						}
						doc.replaceItemValue(itemName, nDate);
					} else if (itemValue instanceof ArrayList) {
						ArrayList itemValueArr = (ArrayList) itemValue;
						Vector newItemValue = new Vector(itemValueArr);
						doc.replaceItemValue(itemName, newItemValue);
					} else if (itemValue instanceof Name) {
						doc.replaceItemValue(itemName, itemValue);
					} else {
						doc.replaceItemValue(itemName, itemValue);
					}
					updateCachedValue(itemName, itemValue);
				}
			}
		} catch (NotesException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
