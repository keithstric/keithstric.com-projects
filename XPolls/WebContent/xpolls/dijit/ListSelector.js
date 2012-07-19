dojo.provide("xpolls.dijit.ListSelector");
dojo.registerModulePath("extlib", "/xsp/.ibmxspres/.extlib");
dojo.require("dojo.cache");
dojo.require("dijit._Widget");
dojo.require("dijit._Templated");
dojo.require("extlib.dojo.data.XPagesRestStore");

dojo.declare("xpolls.dijit.ListSelector", 
[dijit._Widget, dijit._Templated], 
{
/**** SETUP ****/
	delcaredClass: "xpolls.dijit.ListSelector",
	templateString : dojo.cache("xpolls.dijit", "templates/ListSelector.html"),
	listHeight: "150px",
	listWidth: "230px",
	isAnswerList: false,
	isQuestionList: false,
	list: null,
	dataView: null,
	parentUnid: null,
	dataSource: null,
	postMixInProperties: function() {
		try {
			console.log("postMixInProperties running");
			this._onStart();
			this.inherited(arguments);
		}catch (errorObj) {
			var errorTxt = "postMixInProperties failed to complete";
			this._onError(errorTxt, errorObj);
		}
	},
	postCreate: function() {
		try {
			console.log("postCreate running");
			this.addStyle("xpolls.dijit.css.ListSelector");
			dojo.connect(this.newItemButton, "onclick", this, "_newItem");
			//TODO
			//dojo.connect(this.moveItemUpButton, "onclick", this, "_moveItemUp");
			//dojo.connect(this.moveItemDownButton, "onclick", this, "moveItemDown");
			dojo.connect(this.removeItemButton, "onclick", this, "_removeItem");
			this.list = {
				body: this.containerListBody,
				container: this.containerList,
				current: null
			}
			this._fetchData();
		}catch (errorObj) {
			var errorTxt = "postCreate failed to complete";
			this._onError(errorTxt, errorObj);
		}
	},
	addStyle : function(modulePath) {
		//summary: loads a CSS file based on a modulePath.
		var url = dojo.moduleUrl(modulePath).toString();
		//Adjust URL a bit so we get dojo.require-like behavior
		url = url.substring(0, url.length - 1) + ".css";
		var link = dojo.create("link", {
			type : "text/css",
			rel : "stylesheet",
			"data-mylibstylename" : modulePath,
			href : url
		});
		dojo.doc.getElementsByTagName("head")[0].appendChild(link);
	},
	
/**** DATA HANDLING ****/	
	_fetchData: function() {
		try {
			console.log("_fetchData running datasource=",this.dataSource);
			if (this.dataSource) {
				if (!this.parentUnid && this.isQuestionList) {					
					this.parentUnid = XPolls.getUrlParameter("documentId");
				}else if (this.isAnswerList) {
					this.parentUnid = XPolls.selectedQuestion;
					this._clearList();
				}
				var url = XPolls.getNsfUrl() + "/" + this.dataSource.viewName + "?readviewentries" + "&outputformat=json&restricttocategory=" + this.parentUnid;
				var labelCol = this.dataSource.labelCol;
				var valueCol = this.dataSource.valueCol;
				
				var _this = this;
				dojo.xhrGet({
					url: url,
					handleAs: "json",
					load: function(resp, ioArgs) {
						dojo.forEach(resp.viewentry, function(item) {
							_this._onItem(item, labelCol, valueCol);
						});
					},
					error: function(err, ioArgs) {
						if (ioArgs.xhr.status != 401) {
							var errorTxt = "You are not authorized to perform that action";
							this._onError(errorTxt,err);
						}
					}				
				});
			}else{
				var errorTxt = "_fetchData, No Datasource Defined for this dijit";
				this._onError(errorTxt);
			}
		}catch (errorObj) {
			var errorTxt = "_fetchData failed to complete";
			this._onError(errorTxt, errorObj);
		}		
	},
	_onItem: function(item, labelCol, valueCol) {
		try {
			console.log("_onItem running item/labelCol/valueCol=",item,labelCol,valueCol);
			var lbl = dojo.trim(item.entrydata[labelCol].text[0]);
			var val = dojo.trim(item.entrydata[valueCol].text[0]);
			var li = this._createLi(lbl,val, item["@unid"]);
		}catch (errorObj) {
			var errorTxt = "_onItem failed to complete";
			this._onError(errorTxt, errorObj);
		}
	},
	_selectItem: function(item) {
		try {
			console.log("_selectItem running item=",item);
			var unid = dojo.attr(item,"unid");
			console.log("working with unid " + unid);
			//Get any other nodes that are currently selected
			var selected = dojo.query(".listItemSelected");
			var parentDomNode = this.domNode;
			if (selected) {
				//Unselect any nodes that are not the newly selected node
				dojo.forEach(selected,function(selectedItem) {
					if (dojo.attr(item,"unid") != dojo.attr(selectedItem,"unid")) {
						if (dojo.isDescendant(selectedItem,parentDomNode)) {
							dojo.toggleClass(selectedItem,"listItemSelected");
						}
					}
				});
			}
			//Toggle the 'listItemSelected' class on the clicked on node
			dojo.toggleClass(item,"listItemSelected");
			if (this.isQuestionList) {
				if (unid) {
					var answerList = dijit.byId("listSelector2_container");
					if (!dojo.hasClass(item,"listItemSelected")) {
						console.log("question, no listItemSelected class")
						XPolls.selectedQuestion = null;
						frontEndRpc.selectItem(null,"question");
						if (answerList) {
							answerList._clearList();
						}
					}else{
						console.log("question, has listItemSelected class");
						XPolls.selectedQuestion = unid;
						frontEndRpc.selectItem(unid,"question");
						if (answerList) {
							answerList._fetchData();
						}
					}
					XPolls.selectedAnswer = null;
					frontEndRpc.selectItem(null,"answer");
				}else{
					XPolls.selectedQuestion = "newQuestion";
					frontEndRpc.selectItem("newQuestion","question");
				}
			}else if (this.isAnswerList) {
				if (unid) {
					if (!dojo.hasClass(item,"listItemSelected")) {
						XPolls.selectedAnswer = null;
						frontEndRpc.selectItem(null,"answer");
					}else{
						XPolls.selectedAnswer = unid;
						frontEndRpc.selectItem(unid,"answer");
					}
				}else{
					XPolls.selectedAnswer = "newAnswer";
					frontEndRpc.selectItem("newAnswer","answer");
				}
			}
			var container = dojo.query("[id$=':QuestionAnswerContainer']")[0];
			if (container) {
				XSP.partialRefreshGet(container.id,{});
			}
		}catch (errorObj) {
			var errorTxt = "_selectItem failed to complete";
			this._onError(errorTxt, errorObj);
		}
	},
	_doFullRefresh: function(eventId, refreshTarget, options) {
		XPolls.executeOnServer(eventId, refreshTarget, options);
	},
	
/**** LIST BUILDING ****/
	_newItem: function() {
		try {
			console.log("_newItem running");
			var _this = this;
			if (_this.isQuestionList) {
				frontEndRpc.createItem("question", XPolls.getUrlParameter("documentId")).addCallback(function(returnVal) {
					console.log("question - inside callback, returnVal=",returnVal);
					_this._createLi("New Item - Change Me", "", returnVal);
				});
			}else if (_this.isAnswerList) {
				frontEndRpc.createItem("answer", XPolls.selectedQuestion).addCallback(function(returnVal) {
					console.log("answer - insideCallback, returnVal=",returnVal);
					_this._createLi("New Item - Change Me", "", returnVal);
				});
			}
		}catch (errorObj) {
			var errorTxt = "_newItem failed to complete";
			this._onError(errorTxt, errorObj);
		}
	},
	_removeItem: function(evt) {
		try {
			console.log("_removeItem running event=",evt);
			var selectItem = dojo.query("li.listItemSelected", this.list.container);
			dojo.forEach(selectItem, function(item) {
				this.list.container.removeChild(item);
				if (dojo.attr(item,"unid")) {
					var type = this.isQuestion ? "question" : "answer";
					frontEndRpc.deleteDoc(dojo.attr(item,"unid"),type);
				}
			}, this);
			this._fetchData();
		}catch (errorObj) {
			var errorTxt = "_removeItem failed to complete";
			this._onError(errorTxt, errorObj);
		}
	},
	_createLi: function(label, value, unid) {
		console.log("_createLi running label=",label," value=",value," unid=",unid);
		try {
			//console.log("_createLi running, label/value/unid=",label,value,unid);
			if (!value && !label) {
				console.log("Missing something for createLi");
				label = "New Item";
				value = unid;
			}
			//console.log("creating a list item in createLi");
			var li = dojo.create("li",{
				val: value,
				tabIndex: 0,
				"class": "listItem",
				title: value,
				unselectable: "on",
				innerHTML: label,
				unid: unid
			}, this.list.container);
			return li;
		}catch (errorObj) {
			var errorTxt = "_createLi failed to complete";
			this._onError(errorTxt, errorObj);
		}
	},
	_clearList: function() {
		//console.log("_clearList running on ",this.list.container);
		try {
			var listKids = dojo.query(".listItem",this.list.container);
			dojo.forEach(listKids,function(kid){
				kid.parentNode.removeChild(kid);
			});
		}catch (errorObj) {
			var errorTxt = "_clearList failed to complete";
			this._onError(errorTxt, errorObj);
		}
	},
/**** NODE EVENTS ****/
	_listDblClick: function(evt) {
		//console.log("_listDblClick",evt);
	},
	_listKeyPress: function(evt) {
		//console.log("_listKeyPress",evt);
	},
	_listOnScroll: function(evt) {
		//console.log("_listOnScroll",evt);
	},
	_listSet: function(evt) {
		//console.log("_listSet",evt);
	},
	_listUnset: function(evt) {
		//console.log("_listUnset",evt);
	},
	_listClick: function(evt) {
		try {
			//console.log("_listClick",evt);
			this._selectItem(evt.target);
		}catch (errorObj) {
			var errorTxt = "_listClick failed to complete";
			this._onError(errorTxt, errorObj);
		}
	},

/**** DIJIT EVENTS ****/
	_onStart: function() {
		try {
			console.log("_onStart running");
			this.onStart();
		}catch (errorObj) {
			var errorTxt = "_start failed to complete";
			this._onError(errorTxt, errorObj);
		}
	},
	onStart: function(){},
	_onError: function(errorText, errorObj) {
		//console.log("_onError running");
		errorText = "XPolls encountered an error - " + errorText;
		if (errorObj) {
			console.error(errorText,errorObj);
		}else{
			console.error(errorText);
		}
		this.onError(errorText, errorObj);
	},
	onError: function(errorText, errorObj) {}
});