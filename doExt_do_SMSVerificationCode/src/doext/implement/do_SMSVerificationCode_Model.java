package doext.implement;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;
import core.DoServiceContainer;
import core.object.DoSingletonModule;

import org.json.JSONException;
import org.json.JSONObject;

import com.mob.MobSDK;

import core.helper.DoJsonHelper;
import core.helper.DoResourcesHelper;
import core.interfaces.DoIScriptEngine;
import core.object.DoInvokeResult;
import doext.define.do_SMSVerificationCode_IMethod;

/**
 * 自定义扩展SM组件Model实现，继承DoSingletonModule抽象类，并实现do_SMSVerificationCode_IMethod接口方法
 * ； #如何调用组件自定义事件？可以通过如下方法触发事件：
 * this.model.getEventCenter().fireEvent(_messageName, jsonResult);
 * 参数解释：@_messageName字符串事件名称，@jsonResult传递事件参数对象； 获取DoInvokeResult对象方式new
 * DoInvokeResult(this.getUniqueKey());
 */
public class do_SMSVerificationCode_Model extends DoSingletonModule implements do_SMSVerificationCode_IMethod {

	public do_SMSVerificationCode_Model() throws Exception {
		super();
		init();
	}

	private void init() {
		Context context = DoServiceContainer.getPageViewFactory().getAppContext();
		int _appKey = DoResourcesHelper.getRIdByString("MOBAppKey", "do_SMSVerificationCode");
		int _AppSecret = DoResourcesHelper.getRIdByString("MOBAppSecret", "do_SMSVerificationCode");
		MobSDK.init(context, context.getString(_appKey), context.getString(_AppSecret));
		// MobSDK.init(context, "20031788a896c",
		// "07e6f9acb7348b4eb7d20f5c0da7ad29");

		EventHandler eventHandler = new EventHandler() { // 操作回调
			@Override
			public void afterEvent(int event, int result, Object data) {
				Message msg = new Message();
				msg.arg1 = event;
				msg.arg2 = result;
				msg.obj = data;
				handler.sendMessage(msg);
			}
		};
		SMSSDK.registerEventHandler(eventHandler); // 注册回调接口
	}

	@SuppressLint("HandlerLeak")
	Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			int event = msg.arg1;
			int result = msg.arg2;
			Object data = msg.obj;

			if (result == SMSSDK.RESULT_COMPLETE) {
				// 如果操作成功
				if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {
					// 校验验证码，返回校验的手机和国家代码
					callback(true, "");
				} else if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE) {
					// 获取验证码成功，true为智能验证，false为普通下发短信
					callback(true, "");
				} else if (event == SMSSDK.EVENT_GET_SUPPORTED_COUNTRIES) {
					// 返回支持发送验证码的国家列表
				}
			} else {
				// 如果操作失败
				Throwable throwable = (Throwable) data;
				String errorMessage = "";
				if (throwable != null) {
					throwable.printStackTrace();
					errorMessage = throwable.getMessage();
				}
				callback(false, errorMessage);
			}
		}
	};

	/**
	 * 同步方法，JS脚本调用该组件对象方法时会被调用，可以根据_methodName调用相应的接口实现方法；
	 * 
	 * @_methodName 方法名称
	 * @_dictParas 参数（K,V），获取参数值使用API提供DoJsonHelper类；
	 * @_scriptEngine 当前Page JS上下文环境对象
	 * @_invokeResult 用于返回方法结果对象
	 */
	@Override
	public boolean invokeSyncMethod(String _methodName, JSONObject _dictParas, DoIScriptEngine _scriptEngine, DoInvokeResult _invokeResult) throws Exception {
		// ...do something
		return super.invokeSyncMethod(_methodName, _dictParas, _scriptEngine, _invokeResult);
	}

	/**
	 * 异步方法（通常都处理些耗时操作，避免UI线程阻塞），JS脚本调用该组件对象方法时会被调用， 可以根据_methodName调用相应的接口实现方法；
	 * 
	 * @_methodName 方法名称
	 * @_dictParas 参数（K,V），获取参数值使用API提供DoJsonHelper类；
	 * @_scriptEngine 当前page JS上下文环境
	 * @_callbackFuncName 回调函数名 #如何执行异步方法回调？可以通过如下方法：
	 *                    _scriptEngine.callback(_callbackFuncName,
	 *                    _invokeResult);
	 *                    参数解释：@_callbackFuncName回调函数名，@_invokeResult传递回调函数参数对象；
	 *                    获取DoInvokeResult对象方式new
	 *                    DoInvokeResult(this.getUniqueKey());
	 */
	@Override
	public boolean invokeAsyncMethod(String _methodName, JSONObject _dictParas, DoIScriptEngine _scriptEngine, String _callbackFuncName) throws Exception {
		if ("getSMSVerificationCode".equals(_methodName)) {
			this.getSMSVerificationCode(_dictParas, _scriptEngine, _callbackFuncName);
			return true;
		}
		if ("verifySMSVerificationCode".equals(_methodName)) {
			this.verifySMSVerificationCode(_dictParas, _scriptEngine, _callbackFuncName);
			return true;
		}
		return super.invokeAsyncMethod(_methodName, _dictParas, _scriptEngine, _callbackFuncName);
	}

	DoIScriptEngine scriptEngine;
	String callbackFuncName = "";

	/**
	 * 根据手机号获取短信验证码；
	 * 
	 * @throws JSONException
	 * 
	 * @_dictParas 参数（K,V），可以通过此对象提供相关方法来获取参数值（Key：为参数名称）；
	 * @_scriptEngine 当前Page JS上下文环境对象
	 * @_callbackFuncName 回调函数名
	 */
	@Override
	public void getSMSVerificationCode(JSONObject _dictParas, DoIScriptEngine _scriptEngine, String _callbackFuncName) throws JSONException {
		String _phoneNumber = DoJsonHelper.getString(_dictParas, "phoneNumber", "");
		scriptEngine = _scriptEngine;
		callbackFuncName = _callbackFuncName;
		SMSSDK.getVerificationCode("86", _phoneNumber);
	}

	/**
	 * 验证短信验证码；
	 * 
	 * @throws JSONException
	 * 
	 * @_dictParas 参数（K,V），可以通过此对象提供相关方法来获取参数值（Key：为参数名称）；
	 * @_scriptEngine 当前Page JS上下文环境对象
	 * @_callbackFuncName 回调函数名
	 */
	@Override
	public void verifySMSVerificationCode(JSONObject _dictParas, DoIScriptEngine _scriptEngine, String _callbackFuncName) throws JSONException {
		String _code = DoJsonHelper.getString(_dictParas, "code", "");
		String _phoneNumber = DoJsonHelper.getString(_dictParas, "phoneNumber", "");
		scriptEngine = _scriptEngine;
		callbackFuncName = _callbackFuncName;
		SMSSDK.submitVerificationCode("86", _phoneNumber, _code);
	}

	private void callback(boolean result, String error) {
		DoInvokeResult _invokeResult = new DoInvokeResult(this.getUniqueKey());
		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put("result", result);
			if (!TextUtils.isEmpty(error))
				jsonObject.put("errorMessage", error);
		} catch (JSONException e) {
		}
		_invokeResult.setResultNode(jsonObject);
		scriptEngine.callback(callbackFuncName, _invokeResult);
	}
}