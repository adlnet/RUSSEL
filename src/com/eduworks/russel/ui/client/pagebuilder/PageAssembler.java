/*
Copyright (c) 2012 Eduworks Corporation
All rights reserved.
 
This Software (including source code, binary code and documentation) is provided by Eduworks Corporation to
the Government pursuant to contract number W31P4Q-12 -C- 0119 dated 21 March, 2012 issued by the U.S. Army 
Contracting Command Redstone. This Software is a preliminary version in development. It does not fully operate
as intended and has not been fully tested. This Software is provided to the U.S. Government for testing and
evaluation under the following terms and conditions:

	--Any redistribution of source code, binary code, or documentation must include this notice in its entirety, 
	 starting with the above copyright notice and ending with the disclaimer below.
	 
	--Eduworks Corporation grants the U.S. Government the right to use, modify, reproduce, release, perform,
	 display, and disclose the source code, binary code, and documentation within the Government for the purpose
	 of evaluating and testing this Software.
	 
	--No other rights are granted and no other distribution or use is permitted, including without limitation 
	 any use undertaken for profit, without the express written permission of Eduworks Corporation.
	 
	--All modifications to source code must be reported to Eduworks Corporation. Evaluators and testers shall
	 additionally make best efforts to report test results, evaluation results and bugs to Eduworks Corporation
	 using in-system feedback mechanism or email to russel@eduworks.com.
	 
THIS SOFTWARE IS PROVIDED "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE 
IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL 
THE COPYRIGHT HOLDER BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL 
DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR 
PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT 
LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN 
IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE
*/

package com.eduworks.russel.ui.client.pagebuilder;

import java.util.ArrayList;
import java.util.Vector;

import org.w3c.dom.Node;

import com.eduworks.gwt.russel.ui.client.net.AlfrescoNullCallback;
import com.eduworks.gwt.russel.ui.client.net.AlfrescoPacket;
import com.eduworks.russel.ui.client.Russel;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.Document;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.EventListener;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.Frame;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Hidden;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;


public class PageAssembler
{
	private FlowPanel body = new FlowPanel();
	private ArrayList<Widget> contents = new ArrayList<Widget>();
	private static PageAssembler instance;
	private long iDCounter;
	private String rootPanelName;
	final public static String A = "a";
	final public static String TEXT = "text";
	final public static String PASSWORD = "password";
	final public static String LABEL = "label";
	final public static String SELECT = "select";
	final public static String HIDDEN = "hidden";
	final public static String FILE = "file";
	final public static String FORM = "form";
	final public static String FRAME = "frame";
	final public static String CONTENT_HEADER = "contentHeader";
	final public static String CONTENT_FOOTER = "contentFooter";

	public void setTemplate(String rawHeader, String rawFooter, String rPanelName)
	{
		clearContents();
		RootPanel.get(CONTENT_HEADER).add(new HTML(rawHeader));
		RootPanel.get(CONTENT_FOOTER).add(new HTML(rawFooter));
		rootPanelName = rPanelName;

		RootPanel.get(rootPanelName).add(body);
	}

	public static final native void fireOnChange(String elementId) /*-{
		$wnd.$('#' + elementId).change();
	}-*/;
	
	public static final native boolean showConfirmBox(String msg) /*-{
		return confirm(msg);
	}-*/;
	
	private static final void fillBuildNumber() {
		((Label)elementToWidget("buildNumber", LABEL)).setText(Russel.buildNumber);										
	}
	
	public static final native void closePopup(String elementName) /*-{
		$wnd.$('#' + elementName).trigger('reveal:close');
	}-*/;

	public static final native Element getElementByClass(String elementClass) /*-{
		return $wnd.$(elementClass)[0];
	}-*/;
	
	public static final native void runCustomJSHooks() /*-{
		$wnd.boxedCustomAppJavascript();
	}-*/;
	
	public void ready(Widget obj)
	{
		contents.add(obj);
	}

	public static PageAssembler getInstance()
	{
		if (instance == null)
			instance = new PageAssembler();

		return instance;
	}

	/** Builds everything that has been readied into the rPanelName given in template setup, clears ready list after. */
	public void buildContents()
	{
		body.clear();

		for (int i = 0; i < contents.size(); i++)
			body.add(contents.get(i));

		fillBuildNumber();
		runCustomJSHooks();
		contents.clear();
	}

	public void clearContents()
	{
		iDCounter = 0;
		body.clear();
		contents.clear();
		RootPanel.get(CONTENT_HEADER).clear();
		RootPanel.get(CONTENT_FOOTER).clear();
		RootPanel.get(rootPanelName).clear();
	}

	/** @Returns a list of IDs that get put into the template */
	public Vector<String> inject(String elementName, String token, Widget w, boolean inFront) {
		boolean incrementIDCounter = false;
		Vector<String> convertedIDs = new Vector<String>();
		Vector<Element> nodeTree = new Vector<Element>();
		String elementID;
		Element e;
		int indexOfToken=-1;

		nodeTree.add(w.getElement());

		while(nodeTree.size()>0) {
			e = nodeTree.remove(0);
				for (int x=0;x<e.getChildCount();x++) {
					if (e.getChild(x).getNodeType()==Node.ELEMENT_NODE)
						nodeTree.add((Element)e.getChild(x));
				}
			elementID = e.getId();
			if (elementID!=null)
				indexOfToken = elementID.indexOf(token);
			if (indexOfToken!=-1) {
				incrementIDCounter = true;
				e.setId(elementID.substring(0, indexOfToken) + iDCounter + elementID.substring(indexOfToken + token.length()));
				convertedIDs.add(e.getId());
			} else if (e.getId()!="")
				convertedIDs.add(e.getId());
		}

		if (incrementIDCounter) iDCounter++;

		RootPanel.get(elementName).remove(w);
		if (inFront)
			RootPanel.get(elementName).insert(w, 0);
		else
			RootPanel.get(elementName).add(w);

		return convertedIDs;
	}
	
	public Vector<String> merge(String elementName, String token, Element incomingE) {
		boolean incrementIDCounter = false;
		Vector<String> convertedIDs = new Vector<String>();
		Vector<Element> nodeTree = new Vector<Element>();
		String elementID;
		Element e;
		int indexOfToken=-1;

		nodeTree.add(incomingE);

		while(nodeTree.size()>0) {
			e = nodeTree.remove(0);
				for (int x=0;x<e.getChildCount();x++) {
					if (e.getChild(x).getNodeType()==Node.ELEMENT_NODE)
						nodeTree.add((Element)e.getChild(x));
				}
			elementID = e.getId();
			if (elementID!=null)
				indexOfToken = elementID.indexOf(token);
			if (indexOfToken!=-1) {
				incrementIDCounter = true;
				e.setId(elementID.substring(0, indexOfToken) + iDCounter + elementID.substring(indexOfToken + token.length()));
				convertedIDs.add(e.getId());
			} else if (e.getId()!="")
				convertedIDs.add(e.getId());
		}

		if (incrementIDCounter) iDCounter++;
		
		RootPanel.get(elementName).getElement().appendChild(incomingE);

		return convertedIDs;
	}
	
	public static native JavaScriptObject getIFrameElement(Element iFrame, String objId) /*-{
		var doc = iFrame.contentDocument || iFrame.contentWindow;
		if (doc!=null)
			return doc.getElementById(objId);
		else
			return null;
	}-*/;
	
	/** @Returns if it worked */
	public static boolean attachHandler(String elementName, final int eventTypes, final AlfrescoNullCallback<AlfrescoPacket> nullCallback) {
		boolean result = false; 
		Element e = (Element)Document.get().getElementById(elementName);
		if (e!=null) {
			DOM.sinkEvents(e, eventTypes);
			DOM.setEventListener(e, new EventListener() {
										@Override
										public void onBrowserEvent(Event event) {
											if (event.getTypeInt()==eventTypes&&nullCallback!=null)
												nullCallback.onEvent(event);
										}
									});
			result = true;
		}
		return result;
	}
	
	/** @Returns if it worked */
	public static boolean attachHandler(Element e, final int eventTypes, final AlfrescoNullCallback<AlfrescoPacket> nullCallback) {
		boolean result = false; 
		if (e!=null) {
			DOM.sinkEvents(e, eventTypes);
			DOM.setEventListener(e, new EventListener() {
										@Override
										public void onBrowserEvent(Event event) {
											if (nullCallback!=null)
												nullCallback.onEvent(event);
										}
									});
			result = true;
		}
		return result;
	}
	
	/** preserves event handlers on element to be wrapped */
	public static Widget elementToWidget(Element e, String typ) {
		Widget result = null;
		if (e!=null) {
			int eventsSunk = DOM.getEventsSunk(e);
			EventListener el = DOM.getEventListener(e);
			if (typ==TEXT)
				 result = TextBox.wrap(e);
			else if (typ==PASSWORD)
				result = PasswordTextBox.wrap(e);
			else if (typ==LABEL)
				result = Label.wrap(e);
			else if (typ==A)
				result = Anchor.wrap(e);
			else if (typ==SELECT)
				result = ListBox.wrap(e);
			else if (typ==HIDDEN)
				result = Hidden.wrap(e);
			else if (typ==FILE)
				result = FileUpload.wrap(e);
			else if (typ==FORM)
				result = FormPanel.wrap(e);
			else if (typ==FRAME)
				result = Frame.wrap(e);
			DOM.sinkEvents(e, eventsSunk);
			DOM.setEventListener(e, el);
		} else {
			if (typ==TEXT)
				 result = new TextBox();
			else if (typ==PASSWORD)
				result = new PasswordTextBox();
			else if (typ==LABEL)
				result = new Label();
			else if (typ==A)
				result = new Anchor();
			else if (typ==SELECT)
				result = new ListBox();
			else if (typ==HIDDEN)
				result = new Hidden();
			else if (typ==FILE)
				result = new FileUpload();
			else if (typ==FORM)
				result = new FormPanel();
			else if (typ==FRAME)
				result = new Frame();;
		}
		return result;
	}
	
	/** preserves event handlers on element to be wrapped  */
	public static Widget elementToWidget(String elementName, String typ) {
		Widget result = null;
		Element e = DOM.getElementById(elementName);
		if (e!=null) {
			int eventsSunk = DOM.getEventsSunk(e);
			EventListener el = DOM.getEventListener(e);
			if (typ==TEXT)
				 result = TextBox.wrap(e);
			else if (typ==PASSWORD)
				result = PasswordTextBox.wrap(e);
			else if (typ==LABEL)
				result = Label.wrap(e);
			else if (typ==A)
				result = Anchor.wrap(e);
			else if (typ==SELECT)
				result = ListBox.wrap(e);
			else if (typ==HIDDEN)
				result = Hidden.wrap(e);
			else if (typ==FILE)
				result = FileUpload.wrap(e);
			else if (typ==FORM)
				result = FormPanel.wrap(e, true);
			else if (typ==FRAME)
				result = Frame.wrap(e);
			DOM.sinkEvents(e, eventsSunk);
			DOM.setEventListener(e, el);
		} else {
			if (typ==TEXT)
				 result = new TextBox();
			else if (typ==PASSWORD)
				result = new PasswordTextBox();
			else if (typ==LABEL)
				result = new Label();
			else if (typ==A)
				result = new Anchor();
			else if (typ==SELECT)
				result = new ListBox();
			else if (typ==HIDDEN)
				result = new Hidden();
			else if (typ==FILE)
				result = new FileUpload();
			else if (typ==FORM)
				result = new FormPanel();
			else if (typ==FRAME)
				result = new Frame();
		}
		return result;
	}
}
