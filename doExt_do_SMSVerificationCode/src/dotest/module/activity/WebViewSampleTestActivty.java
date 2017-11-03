package dotest.module.activity;

import java.util.HashMap;
import java.util.Map;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import core.DoServiceContainer;
import core.object.DoInvokeResult;
import doext.implement.do_SMSVerificationCode_Model;
import dotest.module.frame.debug.DoService;

/**
 * webview组件测试样例
 */
public class WebViewSampleTestActivty extends DoTestActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	protected void initModuleModel() throws Exception {
		this.model = new do_SMSVerificationCode_Model();

	}

	@Override
	protected void initUIView() throws Exception {

	}

	@Override
	public void doTestProperties(View view) {
		Map<String, String> _paras_loadString = new HashMap<String, String>();
		_paras_loadString.put("phoneNumber", "");
		DoService.asyncMethod(this.model, "getSMSVerificationCode", _paras_loadString, new DoService.EventCallBack() {
			@Override
			public void eventCallBack(String _data) {// 回调函数
				DoServiceContainer.getLogEngine().writeDebug("异步方法回调：" + _data);
			}
		});
	}

	@Override
	protected void doTestSyncMethod() {
		Map<String, String> _paras_back = new HashMap<String, String>();
		DoService.syncMethod(this.model, "back", _paras_back);
	}

	@Override
	protected void doTestAsyncMethod() {
		Map<String, String> _paras_loadString = new HashMap<String, String>();
		_paras_loadString.put("code", edit.getText().toString());
		_paras_loadString.put("phoneNumber", "");
		DoService.asyncMethod(this.model, "verifySMSVerificationCode", _paras_loadString, new DoService.EventCallBack() {
			@Override
			public void eventCallBack(String _data) {// 回调函数
				DoServiceContainer.getLogEngine().writeDebug("异步方法回调：" + _data);
			}
		});
	}

	@Override
	protected void onEvent() {
		// 系统事件订阅
		DoService.subscribeEvent(this.model, "loaded", new DoService.EventCallBack() {
			@Override
			public void eventCallBack(String _data) {
				DoServiceContainer.getLogEngine().writeDebug("系统事件回调：name = loaded, data = " + _data);
				Toast.makeText(WebViewSampleTestActivty.this, "系统事件回调：loaded", Toast.LENGTH_LONG).show();
			}
		});
		// 自定义事件订阅
		DoService.subscribeEvent(this.model, "_messageName", new DoService.EventCallBack() {
			@Override
			public void eventCallBack(String _data) {
				DoServiceContainer.getLogEngine().writeDebug("自定义事件回调：name = _messageName, data = " + _data);
				Toast.makeText(WebViewSampleTestActivty.this, "自定义事件回调：_messageName", Toast.LENGTH_LONG).show();
			}
		});
	}

	@Override
	public void doTestFireEvent(View view) {
		// fire 自定义事件
		DoInvokeResult invokeResult = new DoInvokeResult(this.model.getUniqueKey());
		this.model.getEventCenter().fireEvent("_messageName", invokeResult);
	}

}
