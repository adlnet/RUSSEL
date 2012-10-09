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

import java.util.Vector;

import com.eduworks.russel.ui.client.pagebuilder.screen.LoginScreen;
import com.google.gwt.user.client.History;

public class ScreenDispatch {
	public Vector<HistoryClosure> history = new Vector<HistoryClosure>();
	private final int historyLimit = 30;
	
	public void loadScreen(ScreenTemplate st, boolean storeHistory) {
		if (st!=null) {
			if (history.size()==historyLimit) 
				history.remove(0);
			
			String token = st.getScreenName();
			if (history.size()>0&&token==history.lastElement().screen.getScreenName())
				history.lastElement().screen = st;
			else if (storeHistory) {
				history.add(new HistoryClosure(st, token));
				History.newItem(token, false);
			}
			st.display();
		}
	}
	
	public void loadHistoryScreen(String token) {
		for (int x=history.size()-1;x>-1;x--) {
			if (history.get(x).token.equals(token)) {
				if (history.get(x).screen!=null)
					history.get(x).screen.display();
				history.setSize(x+1);
				return;
			}
		}
		if (token.equals("Login") || history.size()==0) { 
			clearHistory();
			loadScreen(new LoginScreen(), true);
		} else if ((token=="#") || (token.equalsIgnoreCase(history.lastElement().token)))
			History.back();
	}
	
	public void clearHistory() {
		history.clear();
		String initToken = History.getToken();
		if (initToken.length()==0)
			History.newItem("Login");
	}
}