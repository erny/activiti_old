/* Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/**
 * Activiti.component.Navigation
 *
 * Lets user start new process instances by using the drop down menu.
 *
 * @namespace Activiti
 * @class Activiti.component.Navigation
 */
(function()
{
  /**
   * Shortcuts
   */
  var Dom = YAHOO.util.Dom,
      Selector = YAHOO.util.Selector,
      Event = YAHOO.util.Event,
      $html = Activiti.util.decodeHTML;

  /**
   * Navigation constructor.
   *
   * @param {String} htmlId The HTML id of the parent element
   * @return {Activiti.component.Navigation} The new component.Navigation instance
   * @constructor
   */
  Activiti.component.Navigation = function Navigation_constructor(htmlId)
  {
    Activiti.component.Navigation.superclass.constructor.call(this, "Activiti.component.Navigation", htmlId);

    // Create new service instances and set this component to receive the callbacks
    this.services.processService = new Activiti.service.ProcessService(this);

    return this;
  };

  YAHOO.extend(Activiti.component.Navigation, Activiti.component.Base,
  {

    /**
     * Fired by YUI when parent element is available for scripting.
     * Template initialisation, including instantiation of YUI widgets and event listener binding.
     *
     * @method onReady
     */
    onReady: function Navigation_onReady()
    {
      // Create menu and listen for clicks
      this.widgets.startProcessButton = new YAHOO.widget.Button(this.id + "-startProcess-button", {
        type: "menu",
        menu: this.id + "-startProcess-menu" 
      });
      this.widgets.startProcessButton.getMenu().subscribe("click", this.onStartProcessMenuClick, this, this);
    },

    /**
     * Called when an option in the "Start new process..." menu has been selected.
     * Will start new process based on the option.
     *
     * @method onStartProcessMenuClick
     * @param eventType The name of the event
     * @param eventArgs Event arguments
     */
    onStartProcessMenuClick: function Navigation_onStartProcessMenuClick(eventType, eventArgs) {
      var oMenuItem = eventArgs[1];
      if (oMenuItem) {
        new Activiti.widget.StartProcessInstanceForm(this.id + "-startProcessInstanceForm", oMenuItem.value);
      }
    }

  });

})();
