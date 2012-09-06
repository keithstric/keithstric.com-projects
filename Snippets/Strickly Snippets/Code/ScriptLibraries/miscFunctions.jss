function collapseAll(viewPanelId) {
	var viewPanel = getComponent(viewPanelId);
	var model:com.ibm.xsp.model.domino.DominoViewDataModel = viewPanel.getDataModel();
	var container:com.ibm.xsp.model.domino.DominoViewDataContainer = model.getDominoViewDataContainer();
	container.collapseAll(); 
}

function expandAll(viewPanelId) {
	var viewPanel = getComponent(viewPanelId);
	var model:com.ibm.xsp.model.domino.DominoViewDataModel = viewPanel.getDataModel();
	var container:com.ibm.xsp.model.domino.DominoViewDataContainer = model.getDominoViewDataContainer();
	container.expandAll();
}

function getUserRoles(canonicalUserName) {
	var name:NotesName = session.createName(canonicalUserName);
	var acl:NotesACL = session.getCurrentDatabase().getACL();
	var aclEnt:NotesACLEntry = acl.getEntry(name.getAbbreviated());
	if (aclEnt) {
		var userRoles:java.util.Vector = aclEnt.getRoles();
		return userRoles;
	}else{
		aclEnt = acl.getFirstEntry();
		while (aclEnt != null) {
			var aclName:NotesName = aclEnt.getNameObject();
			if (aclEnt.getUserType() == 4 || aclEnt.getUserType() == 3) {
				var members:java.util.Vector = getGroupMembers(aclName.getAbbreviated());
				if (members && members.contains(name.getCanonical())) {
					return aclEnt.getRoles();
				}
			}
			aclEnt = acl.getNextEntry();
		}
		return null;
	}
}

/**
 * Get the month/week number of the passed date as an integer
 */
function getMonthWeekNum(dateTime) {
	var wkDay;
	var tempWkDay;
	var curMonth;
	var workDate;
	var tempWeekNum;
	
	wkDay = dateTime.getDay();
	curMonth = dateTime.getMonth();
	
	//Set the date to the first of the month
	workDate = new Date(dateTime.getFullYear(),dateTime.getMonth(),1);
	tempWkDay = workDate.getDay();
	if (wkDay > tempWkDay) {
		workDate.setDate(wkDay-tempWkDay);
	}else if (wkDay < tempWkDay) {
		workDate.setDate(wkDay-tempWkDay);
	}
	
	//Figure out if the first of the month is the weekday we're looking for, if not then adjust
	if (workDate.getMonth() < curMonth) {
		tempWeekNum = 0
	}else{
		tempWeekNum = 1
	}
	
	//loop through incrementing the workDate by 7 days until it
	//equals dateTime
	if (workDate.toDateString() == dateTime.toDateString()) {
		tempWeekNum = 1
	}else{
		do {
			tempWeekNum = tempWeekNum + 1;
			workDate.setDate(workDate.getDate()+7);
		} 
		while (workDate.valueOf() <= dateTime.valueOf())
	}
	return tempWeekNum;
}

/**
 * Get the time difference between 2 dates in days
 */
function timeDayDifference(endDate,startDate) {
	var difference = endDate.getTime() - startDate.getTime();	
	difference -= (Math.floor(difference/1000/60/60/24)*1000*60*60*24);
	return difference;
}

/**
 * Get the time difference between 2 dates in hours
 */
function timeHourDifference(endDate,startDate) {
	var difference = endDate.getTime() - startDate.getTime();
	difference -= (Math.floor(difference/1000/60/60)*1000*60*60);
	return difference;
}

/**
 * Get the time difference between 2 dates in minutes
 */
function timeMinuteDifference(endDate,startDate) {
	var difference = endDate.getTime() - startDate.getTime();
	difference -= (Math.floor(difference/1000/60)*1000*60);
	return difference;
}

/**
 * Get the time difference between 2 dates in seconds
 */
function timeSecondDifference(endDate,startDate) {
	var difference = endDate.getTime() - startDate.getTime();
	difference = Math.floor(difference/1000);
	return difference;
}
function getGroupMembers(groupName) {
	var nabDb:NotesDatabase = session.getDatabase("","names.nsf");
	var userView:NotesView = nabDb.getView("($Users)");
	var members:java.util.Vector = null;
	if (groupName) {
		var groupDoc:NotesDocument = userView.getDocumentByKey(groupName);
		if (groupDoc) {
			members = groupDoc.getItemValue("Members");
		}
	}
	return members;
}

function addFacesMessage(message, component){
	try {
		if(typeof component === 'string' ){
			component = getComponent(component);
		}
		var clientId = null;
		if (component) {
			clientId = component.getClientId(facesContext);
		}
		facesContext.addMessage(clientId,
		new javax.faces.application.FacesMessage(message+@NewLine()));
	} catch(e){
		throw new Error(e.message + " in " + thisLib + "::addFacesMessage\tmessage = [" + message + "]\tcomponent.id=[" + component.id + "]\r");
	}
}