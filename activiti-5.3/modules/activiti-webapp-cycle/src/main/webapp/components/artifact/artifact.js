(function() 
{
  /**
   * Shortcuts
   */
  var Dom = YAHOO.util.Dom,
      Selector = YAHOO.util.Selector,
      Event = YAHOO.util.Event,
      Pagination = Activiti.util.Pagination,
      $html = Activiti.util.decodeHTML;
  
  /**
   * Artifact constructor.
   *
   * @param {String} htmlId The HTML id of the parent element
   * @return {Activiti.component.Artifact} The new component.Artifact instance
   * @constructor
   */
  Activiti.component.Artifact = function Artifact_constructor(htmlId)
  {
    Activiti.component.Artifact.superclass.constructor.call(this, "Activiti.component.Artifact", htmlId);
    // Create new service instances and set this component to receive the callbacks
    this.services.repositoryService = new Activiti.service.RepositoryService(this);
    // Listen for events that interest this component
    this.onEvent(Activiti.event.updateArtifactView, this.onUpdateArtifactView);
    this.onEvent(Activiti.event.clickFormEventButton, this.onClickFormEventButton);

    this.waitDialog = 
    		new YAHOO.widget.Panel("wait",  
    			{ width:"200px", 
    			  fixedcenter:true, 
    			  close:false, 
    			  draggable:false, 
    			  zindex:4,
    			  modal:true,
    			  visible:false
    			} 
    		);
    this.waitDialog.setBody('<div id="action-waiting-dialog"/>');
    this.waitDialog.render(document.body);

    this._tabView = {};
    this._connectorId = "";
    this._repositoryNodeId = "";
    this._isRepositoryArtifact = false;
    this._name = "";
    this._activeTabIndex = 0;

    this._fileChooserDialog = {};
    this._linksDataTable = {};
    this._linksDataSource = {};
    this._backlinksDataTable = {};

    return this;
  };

  YAHOO.extend(Activiti.component.Artifact, Activiti.component.Base,
  {
    /**
    * Fired by YUI when parent element is available for scripting.
    * Template initialisation, including instantiation of YUI widgets and event listener binding.
    *
    * @method onReady
    */
    onReady: function Artifact_onReady()
    {
      var size = parseInt(Dom.getStyle('content', 'width'), 10); 
      var left, main;
      var resize = new YAHOO.util.Resize('left', {
          handles: ['r']
        });
      left = Dom.get('left');
      main = Dom.get('main');
      resize.on('resize', function(ev) {
          var w = ev.width;
          Dom.setStyle(left, 'height', '');
          Dom.setStyle(main, 'width', (size - w - 37) + 'px');
        });
      
      
    },
    
    /**
     * This method is invoked when a node in the tree is selected or the active tab chages. 
     * It first checks whether the node is still the same and updates the active tab if 
     * needed. If a new node was selected in the tree, the current artifact view is removed
     * and the loadArtifact method is invoked with the new artifacts id.
     * 
     * @method onUpdateArtifactView
     * @param event {String} the name of the event that triggered this method invokation
     * @param args {array} an array of object literals
     */
    onUpdateArtifactView: function Artifact_onUpdateArtifactView(event, args) {
      
      var eventValue = args[1].value;
      
      this._connectorId = eventValue.connectorId;
      this._repositoryNodeId = eventValue.repositoryNodeId;
      this._isRepositoryArtifact = eventValue.isRepositoryArtifact;
      this._name = eventValue.name;
      this._activeTabIndex = eventValue.activeTabIndex;

      // get the header el of the content area
      var headerEl = Selector.query("h1", this.id, true);
      // determine whether the node is still the same
      if("header-" + eventValue.repositoryNodeId === headerEl.id) {
        // still the same node, if the tab view is instanciated, the tab selection should be updated
        if(this._tabView.set) {
          // Update active tab selection silently, without firing an event (last parameter 'true')
          this._tabView.set("activeTab", this._tabView.getTab(this._activeTabIndex), true);
        }
      } else {
        // a new node has been selected in the tree
        var tabViewHtml = YAHOO.util.Selector.query('div', 'artifact-div', true);
        // check whether an artifact was selected before. If yes, remove tabViewHtml and actions
        if(tabViewHtml) {
          var artifactDiv = document.getElementById('artifact-div');
          artifactDiv.removeChild(tabViewHtml);
          var optionsDiv = document.getElementById('options-div');
          optionsDiv.innerHTML = "";
          optionsDiv.removeAttribute("class");
        }
        if(eventValue.repositoryNodeId) {
          // instantiate the tagging component
          new Activiti.component.TaggingComponent(this.id, {connectorId: eventValue.connectorId, repositoryNodeId: eventValue.repositoryNodeId, repositoryNodeLabel: eventValue.name}, "tags-div");
        }
        // Check whether the selected node is a file node. If so, load its data
        if(eventValue.isRepositoryArtifact ) {
          this.services.repositoryService.loadArtifact(eventValue.connectorId, eventValue.repositoryNodeId);
        }
        // Update the heading that displays the name of the selected node
        headerEl.id = "header-" + eventValue.repositoryNodeId;
        headerEl.innerHTML = eventValue.name||'';
        // Remove the comments
        var commentsDiv = YAHOO.util.Dom.get(this.id + '-comments');
        if(commentsDiv) {
          commentsDiv.innerHTML = '';
        }
      }
    },
    
    /**
     * This method is invoked when an artifact is loaded successfully. It will draw a
     * yui tab view component to display the different content representations of the
     * artifact and create an options panel for links, downloads and actions.
     *
     * @method onLoadArtifactSuccess
     * @param response {object} The callback response
     * @param obj {object} Helper object
     */
    onLoadArtifactSuccess: function Artifact_RepositoryService_onLoadArtifactSuccess(response, obj)
    {
      var me = this;
      
      if(response.json.authenticationError) {
        return new Activiti.component.AuthenticationDialog(this.id, response.json.repoInError, response.json.authenticationError);
      }

      this._tabView = new YAHOO.widget.TabView(); 
      
      // Retrieve rest api response
      var artifactJson = response.json;
      // create a tab for each content representation
      for(var i = 0; i<artifactJson.contentRepresentations.length; i++) {
        var tab = new YAHOO.widget.Tab({ 
          label: artifactJson.contentRepresentations[i], 
          dataSrc: this.loadTabDataURL(artifactJson.connectorId, artifactJson.artifactId, artifactJson.contentRepresentations[i]), 
          cacheData: true
        });
        tab.addListener("contentChange", this.onTabDataLoaded);
        tab.loadHandler.success = function(response) {
          me.onLoadTabSuccess(this /* the tab */, response);
        };
        tab.loadHandler.failure = function(response) {
          me.onLoadTabFailure(this /* the tab */, response);
        };
        this._tabView.addTab(tab);
      }

      var linksTab = new YAHOO.widget.Tab({ 
        label: "Links", 
        dataSrc: Activiti.constants.URL_CONTEXT + 'component/links?htmlid=' + this.id + '_links_tab&connectorId=' + artifactJson.connectorId + '&artifactId=' + artifactJson.artifactId,
        cacheData: true
      });
      linksTab.addListener("contentChange", this.onTabDataLoaded);
      linksTab.loadHandler.success = function(response) {
        
        this.set('content', response.responseText);
        
        var scripts = [];
        var script = null;
        var regexp = /<script[^>]*>([\s\S]*?)<\/script>/gi;
        while ((script = regexp.exec(response.responseText)))
        {
          scripts.push(script[1]);
        }
        scripts = scripts.join("\n");

        // Remove the script from the responseText so it doesn't get executed twice
        response.responseText = response.responseText.replace(regexp, "");

        // Use setTimeout to execute the script. Note scope will always be "window"
        window.setTimeout(scripts, 0);

      };
      this._tabView.addTab(linksTab);

      this._tabView.appendTo('artifact-div');

      // replace the tabViews onActiveTabChange evnet handler with our own one
      this._tabView.unsubscribe("activeTabChange", this._tabView._onActiveTabChange);
      this._tabView.subscribe("activeTabChange", this.onActiveTabChange, null, this);

      // Select the active tab without firing an event (last parameter is 'silent=true')
      this._tabView.set("activeTab", this._tabView.getTab(this._activeTabIndex), true);

      // Get the options panel
      var optionsDiv = document.getElementById("options-div");

      // Add a dropdowns for actions, links and downloads
      if(artifactJson.actions.length > 0 || artifactJson.links.length > 0 || artifactJson.downloads.length > 0) {
        var actionsMenuItems = [];
        var actions = [];
        for(i = 0; i<artifactJson.actions.length; i++) {
          actions.push({ text: artifactJson.actions[i].label, value: {connectorId: artifactJson.connectorId, artifactId: artifactJson.artifactId, actionName: artifactJson.actions[i].name}, onclick: { fn: this.onExecuteActionClick } });
        }
        if(actions.length > 0) {
          actionsMenuItems.push(actions);
        }
        var links = [];
        for(i=0; i<artifactJson.links.length; i++) {
          links.push({ text: artifactJson.links[i].label, url: artifactJson.links[i].url, target: "_blank"});
        }
        if(links.length > 0) {
          actionsMenuItems.push(links);
        }
        var downloads = [];
        for(i=0; i<artifactJson.downloads.length; i++) {
          downloads.push({ text: artifactJson.downloads[i].label, url: artifactJson.downloads[i].url, target: "_blank"});
        }
        if(downloads.length > 0) {
          actionsMenuItems.push(downloads);
        }
        // TODO: i18n
        var optionsMenu = new YAHOO.widget.Button({ type: "menu", label: "Actions", name: "options", menu: actionsMenuItems, container: optionsDiv });
      }
      optionsDiv.setAttribute('class', 'active');
      
      this.services.repositoryService.loadComments({connectorId: this._connectorId, nodeId: this._repositoryNodeId});
      
    },

    onLoadTabSuccess: function Artifact_onLoadTabSuccess(tab, response) {
      
      try {
        var responseJson = YAHOO.lang.JSON.parse(response.responseText);
        // parse response, create tab content and set it to the tab
        
        var tabContent;
        if(responseJson.renderInfo == "IMAGE") {
          tabContent = '<div class="artifact-image"><img id="' + responseJson.contentRepresentationId + '" src="' + Activiti.service.REST_PROXY_URI_RELATIVE + "content?connectorId=" + encodeURIComponent(responseJson.connectorId) + "&artifactId=" + encodeURIComponent(responseJson.artifactId) + "&contentRepresentationId=" + encodeURIComponent(responseJson.contentRepresentationId) + '" border=0></img></div>';
        } else if (responseJson.renderInfo == "HTML") {
          tabContent = '<div class="artifact-html"><iframe src ="' + Activiti.service.REST_PROXY_URI_RELATIVE + "content?connectorId=" + encodeURIComponent(responseJson.connectorId) + "&artifactId=" + encodeURIComponent(responseJson.artifactId) + "&contentRepresentationId=" + encodeURIComponent(responseJson.contentRepresentationId) + '"><p>Your browser does not support iframes.</p></iframe></div>';
        } else if (responseJson.renderInfo == "HTML_REFERENCE") {
          tabContent = '<div class="artifact-html-reference"><iframe src ="' + responseJson.url + '"><p>Your browser does not support iframes.</p></iframe></div>';
        } else if (responseJson.renderInfo == "BINARY") {
          // TODO: show some information but no content for binary
          tabContent = '<div class="artifact-binary"><p>No preview available...</p></div>';
        } else if (responseJson.renderInfo == "CODE") {
          tabContent = '<div class="artifact-code"><pre id="' + responseJson.contentRepresentationId + '" class="prettyprint" >' + responseJson.content + '</pre></div>';
        } else if (responseJson.renderInfo == "TEXT_PLAIN") {
          tabContent = '<div class="artifact-text-plain"><pre id="' + responseJson.contentRepresentationId + '">' + responseJson.content + '</pre></div>';
        }
        tab.set('content', tabContent);
      }
      catch (e) {
          alert("Invalid response for tab data");
      }
    },

    onLoadTabFailure: function Artifact_onLoadTabFailure(tab, response) {
      var responseJson = YAHOO.lang.JSON.parse(response.responseText);
      var tabContent = "<h3>Java Stack Trace:</h3>";
      for(var line in responseJson.callstack) {
        if( line == 1 || (responseJson.callstack[line].indexOf("org.activiti.cycle") != -1) || responseJson.callstack[line].indexOf("org.activiti.rest.api.cycle") != -1) {
          tabContent += "<span class=\"cycle-stack-trace-highlight\">" + responseJson.callstack[line] + "</span>";
        } else {
          tabContent += "<span class=\"cycle-stack-trace\">" + responseJson.callstack[line] + "</span>";
        }
      }
      tab.set('content', tabContent);
      Activiti.widget.PopupManager.displayError(responseJson.message);
    },

    onLoadCommentsSuccess: function Artifact_onLoadCommentSuccess(response, obj) {      
      var commentsDiv = YAHOO.util.Dom.get(this.id + '-comments');
      
      if(commentsDiv) {
        commentsDiv.innerHTML = '';
      } else {
        commentsDiv = document.createElement('div');  
      }
      
      if(response.json.authenticationError) {
        return new Activiti.component.AuthenticationDialog(this.id, response.json.repoInError, response.json.authenticationError);
      }
      // Retrieve rest api response
      var commentsJson = response.json;

      var artifactEl = document.getElementById("artifact-div");

      commentsDiv.setAttribute('class', 'comments');
      commentsDiv.setAttribute('id', this.id + '-comments');
      commentsDiv.innerHTML += "<h2>Comments</h2>"
      
      for(var item in commentsJson) {
        if(!commentsJson[item].RepositoryNodeComment.answeredCommentId) {
          this.composeCommentHtml(commentsDiv, commentsJson[item].RepositoryNodeComment, commentsJson);
        }
      }
      
      commentsDiv.innerHTML += '<form><textarea id="comment-input" name="comment" value=""></textarea></form><span id="addCommentButton" class="yui-button"><span class="first-child"><button type="button">Add Comment</button></span></span>';
      
      artifactEl.appendChild(commentsDiv);
      
      var replyLinks = Dom.getElementsByClassName("comment-reply-link", 'a', commentsDiv);
      YAHOO.util.Event.addListener(replyLinks, "click", this.onReplyLinkClick, null, this);
      
      var addCommentButton = new YAHOO.widget.Button("addCommentButton", { label:"Add comment", id:"addCommentButton" });
      addCommentButton.addListener("click", this.onClickAddCommentButton, null, this);
      
      this.waitDialog.hide();
    },

    onReplyLinkClick: function Artifact_onReplyLinkClick(event, obj)
    {
      var replyDiv = document.createElement('div');
      replyDiv.innerHTML = '<form id="comment-reply-form-' + event.target.id + '" class="comment-reply"><input type="hidden" name="comment-id" value="' + event.target.id + '" /><textarea id="comment-reply-input" name="comment-reply" value=""></textarea></form><span id="reply-button' + event.target.id + '" class="yui-button"><span class="first-child"><button type="button">Reply</button></span></span>';
      Dom.insertAfter(replyDiv, event.target);
      
      var replyButton = new YAHOO.widget.Button("reply-button" + event.target.id, { label:"Reply" });
      replyButton.addListener("click", this.onReplyButtonClick, 'comment-reply-form-' + event.target.id, this);
      
      // remove the 'reply' link
      event.target.parentNode.removeChild(event.target);
      
      YAHOO.util.Event.preventDefault(event);
    },

    onReplyButtonClick: function Artifact_onReplyButtonClick(event, id)
    {
      var replyForm = YAHOO.util.Dom.get(id);
      var data = {connectorId: this._connectorId, nodeId: this._repositoryNodeId};
      for(var prop in replyForm.childNodes) {
        if(replyForm.childNodes[prop] && replyForm.childNodes[prop].name == "comment-id") {
          data['answeredCommentId'] = replyForm.childNodes[prop].value;
        }
        if(replyForm.childNodes[prop] && replyForm.childNodes[prop].name == "comment-reply") {
          data['content'] = replyForm.childNodes[prop].value;
        }
      }
      if(data.content) {
        this.waitDialog.show();
        this.services.repositoryService.saveComment(data); 
      }
    },

    /**
     * Click event listener for the "Add Comment" button.
     * 
     * @param event {object} The event that was triggered
     * @param args {Array} The event values     
     */
    onClickAddCommentButton: function Artifact_onClickAddLinkButton(event, args)
    {
      var comment = YAHOO.util.Dom.get("comment-input");
      if(comment.value) {
        this.waitDialog.show();
        this.services.repositoryService.saveComment({connectorId: this._connectorId, nodeId: this._repositoryNodeId, content: comment.value}); 
      }
    },
    
    onSaveCommentSuccess: function Artifact_onSaveCommentSuccess(response, obj)
    {
      this.services.repositoryService.loadComments({connectorId: this._connectorId, nodeId: this._repositoryNodeId});
      // TODO: i18n
      Activiti.widget.PopupManager.displayMessage({
        text: 'Successfully added comment'
      });
    },

    onSaveCommentFailure: function Artifact_onSaveCommentFailure(response, obj)
    {
      this.waitDialog.hide();
    },

    onExecuteActionClick: function Artifact_onExecuteActionClick(e)
    {
      return new Activiti.widget.ExecuteArtifactActionForm(this.id + "-executeArtifactActionForm", this.value.connectorId, this.value.artifactId, this.value.actionName);
    },
    
    onTabDataLoaded: function Artifact_onTabDataLoaded()
    {
      prettyPrint();
    },

    loadTabDataURL: function Artifact_loadTabDataURL(connectorId, artifactId, representationId)
    {
      return Activiti.service.REST_PROXY_URI_RELATIVE + "content-representation?connectorId=" + encodeURIComponent(connectorId) + "&artifactId=" + encodeURIComponent(artifactId) + "&representationId=" + encodeURIComponent(representationId);
    },

    onActiveTabChange: function Artifact_onActiveTabChange(event)
    {
      var newActiveTabIndex = this._tabView.getTabIndex(event.newValue);
      this.fireEvent(Activiti.event.updateArtifactView, {"connectorId": this._connectorId, "repositoryNodeId": this._repositoryNodeId, "isRepositoryArtifact": this._isRepositoryArtifact, "name": this._name, "activeTabIndex": newActiveTabIndex}, null, true);
      YAHOO.util.Event.preventDefault(event);
    },

    onClickFormEventButton: function Artifact_onClickFormEventButton(event, args)
    {
      return new Activiti.component.FileChooserDialog(this.id, args[1].value.callback, false, null, true, false);
    },
    
    composeCommentHtml: function Artifact_composeCommentHtml(commentEl, comment, comments) {
      var replyEl = document.createElement('div');
      replyEl.setAttribute('class', 'comment');
      replyEl.innerHTML = '<span class="comment-author">' + Activiti.util.encodeHTML(comment.author) + '</span><span class="comment-date">' + Activiti.util.encodeHTML(comment.creationDate) + '</span><span class="comment-content">' + Activiti.util.encodeHTML(comment.content) + '</span><a href="#" id="' + comment.id + '" class="comment-reply-link">reply</a>';
      var currentId = comment.id;
      for(var reply in comments) {
        if(comments[reply].RepositoryNodeComment.answeredCommentId == currentId) {
          this.composeCommentHtml(replyEl, comments[reply].RepositoryNodeComment, comments);
        }  
      }
      commentEl.appendChild(replyEl);
    }
    
  });

})();
