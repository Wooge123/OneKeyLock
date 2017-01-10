package com.example.onekeylock;

import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Process;
import android.view.View;

public class MainActivity extends Activity {

	private DevicePolicyManager policyManager;
	private ComponentName componentName;
	private static final int REQUEST_CODE = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 获取设备管理服务
		policyManager = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);

		// AdminReceiver 继承自 DeviceAdminReceiver
		componentName = new ComponentName(this, LockReceiver.class);
		if (policyManager.isAdminActive(componentName)) {
			// 判断是否激活了设备管理器
			policyManager.lockNow(); // 直接锁屏
			Process.killProcess(Process.myPid());
		} else
			activeManager(); // 激活设备管理器获取权限
	}

	// 解除绑定
	public void Bind(View v) {
		if (componentName != null) {
			policyManager.removeActiveAdmin(componentName);
			activeManager();
		}
	}

	private void activeManager() {
		// 使用隐式意图调用系统方法来激活指定的设备管理器
		Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
		intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, componentName);
		intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "一键锁屏");
		startActivityForResult(intent, REQUEST_CODE);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// 获取权限成功，立即锁屏并finish自己，否则继续获取权限
		if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
			policyManager.lockNow();
			finish();
		} else
			activeManager();
		super.onActivityResult(requestCode, resultCode, data);
	}

}
