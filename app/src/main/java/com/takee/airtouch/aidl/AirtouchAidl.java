/*
 * This file is auto-generated.  DO NOT MODIFY.
 * Original file: E:\\my_workspace\\takee_airtouch\\src\\com\\takee\\airtouch\\aidl\\AirtouchAidl.aidl
 */
package com.takee.airtouch.aidl;
/**
 * @项目名称：com.takee.airtouch
 * @类名称:AirtouchAidl
 * @描述：开放的接口
 * @author bao
 */
public interface AirtouchAidl extends android.os.IInterface
{
/** Local-side IPC implementation stub class. */
public static abstract class Stub extends android.os.Binder implements com.takee.airtouch.aidl.AirtouchAidl
{
private static final java.lang.String DESCRIPTOR = "com.takee.airtouch.aidl.AirtouchAidl";
/** Construct the stub at attach it to the interface. */
public Stub()
{
this.attachInterface(this, DESCRIPTOR);
}
/**
 * Cast an IBinder object into an com.takee.airtouch.aidl.AirtouchAidl interface,
 * generating a proxy if needed.
 */
public static com.takee.airtouch.aidl.AirtouchAidl asInterface(android.os.IBinder obj)
{
if ((obj==null)) {
return null;
}
android.os.IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
if (((iin!=null)&&(iin instanceof com.takee.airtouch.aidl.AirtouchAidl))) {
return ((com.takee.airtouch.aidl.AirtouchAidl)iin);
}
return new com.takee.airtouch.aidl.AirtouchAidl.Stub.Proxy(obj);
}
@Override public android.os.IBinder asBinder()
{
return this;
}
@Override public boolean onTransact(int code, android.os.Parcel data, android.os.Parcel reply, int flags) throws android.os.RemoteException
{
switch (code)
{
case INTERFACE_TRANSACTION:
{
reply.writeString(DESCRIPTOR);
return true;
}
case TRANSACTION_setAirTouchEnable:
{
data.enforceInterface(DESCRIPTOR);
boolean _arg0;
_arg0 = (0!=data.readInt());
boolean _result = this.setAirTouchEnable(_arg0);
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_getAirTouchIsEnable:
{
data.enforceInterface(DESCRIPTOR);
boolean _result = this.getAirTouchIsEnable();
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_getCursorPosition:
{
data.enforceInterface(DESCRIPTOR);
int[] _result = this.getCursorPosition();
reply.writeNoException();
reply.writeIntArray(_result);
return true;
}
case TRANSACTION_openAirtouch:
{
data.enforceInterface(DESCRIPTOR);
int _result = this.openAirtouch();
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_openOuterAirtouch:
{
data.enforceInterface(DESCRIPTOR);
int _result = this.openOuterAirtouch();
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_closeOuterAirtouch:
{
data.enforceInterface(DESCRIPTOR);
boolean _result = this.closeOuterAirtouch();
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_openInnerAirtouch:
{
data.enforceInterface(DESCRIPTOR);
int _result = this.openInnerAirtouch();
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_closeInnerAirtouch:
{
data.enforceInterface(DESCRIPTOR);
boolean _result = this.closeInnerAirtouch();
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_releaseAirTouch:
{
data.enforceInterface(DESCRIPTOR);
boolean _result = this.releaseAirTouch();
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_getOuterAirTouchState:
{
data.enforceInterface(DESCRIPTOR);
int _result = this.getOuterAirTouchState();
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_getInnerAirTouchState:
{
data.enforceInterface(DESCRIPTOR);
int _result = this.getInnerAirTouchState();
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_getCurrentAirTouchSensor:
{
data.enforceInterface(DESCRIPTOR);
int _result = this.getCurrentAirTouchSensor();
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_applyUseGesture:
{
data.enforceInterface(DESCRIPTOR);
boolean _result = this.applyUseGesture();
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_closeGestureUse:
{
data.enforceInterface(DESCRIPTOR);
boolean _result = this.closeGestureUse();
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_setAirTouchMode:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
boolean _result = this.setAirTouchMode(_arg0);
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_getCurrentAirTouchMode:
{
data.enforceInterface(DESCRIPTOR);
int _result = this.getCurrentAirTouchMode();
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_getAirTouchGesture:
{
data.enforceInterface(DESCRIPTOR);
int _result = this.getAirTouchGesture();
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_setCursorDown:
{
data.enforceInterface(DESCRIPTOR);
boolean _result = this.setCursorDown();
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_setCursorUp:
{
data.enforceInterface(DESCRIPTOR);
boolean _result = this.setCursorUp();
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_setCursorClick:
{
data.enforceInterface(DESCRIPTOR);
boolean _result = this.setCursorClick();
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_getCursorAction:
{
data.enforceInterface(DESCRIPTOR);
int _result = this.getCursorAction();
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_getAirTouchDeviceList:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
java.util.List<android.bluetooth.BluetoothDevice> _result = this.getAirTouchDeviceList(_arg0);
reply.writeNoException();
reply.writeTypedList(_result);
return true;
}
case TRANSACTION_correctionPosition:
{
data.enforceInterface(DESCRIPTOR);
int[] _arg0;
_arg0 = data.createIntArray();
boolean _result = this.correctionPosition(_arg0);
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_setAutomicState:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
boolean _result = this.setAutomicState(_arg0);
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_getAutomicState:
{
data.enforceInterface(DESCRIPTOR);
int _result = this.getAutomicState();
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_changeAirtouchAddress:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
boolean _result = this.changeAirtouchAddress(_arg0);
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_sendCommandToAirtouch:
{
data.enforceInterface(DESCRIPTOR);
byte _arg0;
_arg0 = data.readByte();
byte[] _arg1;
_arg1 = data.createByteArray();
boolean _result = this.sendCommandToAirtouch(_arg0, _arg1);
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_getResponseForAirtouch:
{
data.enforceInterface(DESCRIPTOR);
byte[] _result = this.getResponseForAirtouch();
reply.writeNoException();
reply.writeByteArray(_result);
return true;
}
}
return super.onTransact(code, data, reply, flags);
}
private static class Proxy implements com.takee.airtouch.aidl.AirtouchAidl
{
private android.os.IBinder mRemote;
Proxy(android.os.IBinder remote)
{
mRemote = remote;
}
@Override public android.os.IBinder asBinder()
{
return mRemote;
}
public java.lang.String getInterfaceDescriptor()
{
return DESCRIPTOR;
}
/**
	 * 功能：    设置太空眼是否可用(仅管理应用可以)
	 * @param boolean isenable 
	 * <p>设置可以使用true
	 * <p>设置不使用为false
	 * @return boolean 
	 * <p>设置成功： true
	 * <p>设置失败：false
	 */
@Override public boolean setAirTouchEnable(boolean isenable) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(((isenable)?(1):(0)));
mRemote.transact(Stub.TRANSACTION_setAirTouchEnable, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
/**
	 * 功能：    获取太空眼是否可用(仅管理应用可以)
	 * @return boolean
	 * <p>已经打开： true
	 * <p>已经禁用：false
	 */
@Override public boolean getAirTouchIsEnable() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getAirTouchIsEnable, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
/**
	 * 功能：   获取手势坐标
	 * @return int[] 
	 * <p>返回长度为2 的整形数组，
	 * <p>int[]  i= getGesturePosition();
	 * <p>i[0] 为x轴坐标
	 * <p>i[1] 为y轴坐标
	 */
@Override public int[] getCursorPosition() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int[] _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getCursorPosition, _data, _reply, 0);
_reply.readException();
_result = _reply.createIntArray();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
/**
	 * 功能：   打开太空眼 （优先打开太空眼，如果内置的太空眼已打开则会被关闭，如果外置太空眼不可用则打开内置太空眼）
	 * @return int
	 * <p>打开成功:  i =  1
	 * <p>正在使用：     i =  2
	 * <p>打开失败:  i = -1
	 * <p>其它原因:  i = -99
	 */
@Override public int openAirtouch() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_openAirtouch, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
/**
	 * 功能：   打开外置的太空眼
	 * @return int
	 * <p>打开成功:  i =  1
	 * <p>正在使用：     i =  2
	 * <p>打开失败:  i = -1
	 * <p>其它原因:  i = -99
	 */
@Override public int openOuterAirtouch() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_openOuterAirtouch, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
/**
	 * 功能：   关闭外置的太空眼
	 * @return boolean
	 * <p>释放成功为： true
	 * <p>释放失败为： false
	 */
@Override public boolean closeOuterAirtouch() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_closeOuterAirtouch, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
/**
	 * 功能：   打开内置太空眼
	 * @return int
	 * <p>打开成功:  i =  1
	 * <p>正在使用：     i =  2
	 * <p>打开失败:  i = -1
	 * <p>其它原因:  i = -99
	 */
@Override public int openInnerAirtouch() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_openInnerAirtouch, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
/**
	 * 功能：   关闭内置的太空眼
	 * @return boolean
	 * <p>释放成功为： true
	 * <p>释放失败为： false
	 */
@Override public boolean closeInnerAirtouch() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_closeInnerAirtouch, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
/**
	 * 功能：   释放所有的太空眼
	 * @return boolean
	 * <p>释放成功为： true
	 * <p>释放失败为： false
	 */
@Override public boolean releaseAirTouch() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_releaseAirTouch, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
/**
	 * 功能：   获取太空眼的状态
	 * @return int
	 * <p>正在使用为： 2
	 * <p>没有使用为： 0
	 */
@Override public int getOuterAirTouchState() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getOuterAirTouchState, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
/**
	 * 功能：   获取内置sensor的状态
	 * @return int
	 * <p>正在使用为： 2
	 * <p>没有使用为： 0
	 */
@Override public int getInnerAirTouchState() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getInnerAirTouchState, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
/**
	 * 功能：   获取当前使用的设备
	 * @return int
	 * <p>太空眼设备： 1
	 * <p>内置的sensor： 2
	 * <p>没有使用设备： 0
	 */
@Override public int getCurrentAirTouchSensor() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getCurrentAirTouchSensor, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
/**
	 * 功能：   申请使用手势
	 * @return boolean
	 * <p>设置成功： true
	 * <p>设置失败：false
	 */
@Override public boolean applyUseGesture() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_applyUseGesture, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
/**
	 * 功能：   关闭使用手势
	 * @return boolean
	 * <p>设置成功： true
	 * <p>设置失败：false
	 */
@Override public boolean closeGestureUse() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_closeGestureUse, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
/**
	 * 功能：   设置为手势模式或者是普通模式
	 * @param int type
	 * <p>普通模式  ：1
	 * <p>手势模式  ：2
	 * @return boolean
	 * <p>设置成功： true
	 * <p>设置失败：false
	 */
@Override public boolean setAirTouchMode(int modeType) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(modeType);
mRemote.transact(Stub.TRANSACTION_setAirTouchMode, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
/**
	 * 功能：   获取当前状态是手势模式或者是普通模式（默认为普通模式）
	 * @return int
	 * <p>普通模式  ：1
	 * <p>手势模式  ：2
	 * <p>没有设置  ：0
	 */
@Override public int getCurrentAirTouchMode() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getCurrentAirTouchMode, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
/**
	 * 功能：   获取当前手势的类型
	 * @return int
	 * <p>返回9种手势模式
	 * <p>没有手势：0
	 * <p>往右：1
	 * <p>往左：2
	 * <p>往上：3
	 * <p>往下：4
	 * <p>接近 sensor：5
	 * <p>远离 ensor：6
	 * <p>顺时针旋转：7
	 * <p>逆时针旋转：8
	 * <p>快速三次挥手：9
	 */
@Override public int getAirTouchGesture() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getAirTouchGesture, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
/**
	 * 功能：   设置鼠标按下
	 * @return boolean
	 * <p>设置成功： true
	 * <p>设置失败：false
	 */
@Override public boolean setCursorDown() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_setCursorDown, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
/**
	 * 功能：   设置鼠标弹起
	 * @return boolean
	 * <p>设置成功： true
	 * <p>设置失败：false
	 */
@Override public boolean setCursorUp() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_setCursorUp, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
/**
	 * 功能：   设置鼠标点击
	 * @return boolean
	 * <p>设置成功： true
	 * <p>设置失败：false
	 */
@Override public boolean setCursorClick() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_setCursorClick, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
/**
	 * 功能：   获取鼠标动作
	 * @return int 
	 * <p>按下为ACTION_DOWN:  1
	 * <p>弹起为ACTION_UP:  2
	 * <p>点击CLICK: 3 
	 * <p>没有设置NONE：0 
	 */
@Override public int getCursorAction() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getCursorAction, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
/**
	 * 功能：    模糊查询附近sensor设备的列表并返回
	 * @param String deviceName  
	 * <p>  需传入要搜索的设备的名字 
	 * <p>比如：
	 * <p>"Takee_Air_Touch_00"   
	 * <p>值为空则查询所有设备
	 * @return List &ltBluetoothDevice&gt 
	 * <p> 返回名字类似于Takee_Air_Touch_00的设备列表
	 * <p>如：
	 * <p> "Takee_Air_Touch_001"
	 * <p> "Takee_Air_Touch_002"
	 * <p> "Takee_Air_Touch_0011"
	 */
@Override public java.util.List<android.bluetooth.BluetoothDevice> getAirTouchDeviceList(java.lang.String deviceName) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
java.util.List<android.bluetooth.BluetoothDevice> _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(deviceName);
mRemote.transact(Stub.TRANSACTION_getAirTouchDeviceList, _data, _reply, 0);
_reply.readException();
_result = _reply.createTypedArrayList(android.bluetooth.BluetoothDevice.CREATOR);
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
/**
	 * 功能：    校正坐标 
	 * @param int[] deviceName 
	 * <p> int[] i =new int[];
	 * <p> i[0]   为x轴的坐标
	 * <p>i[1]   为y轴的坐标
	 * @return boolean
	 * <p> 调用成功：  true
	 * <p>调用失败： false
	 */
@Override public boolean correctionPosition(int[] position) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeIntArray(position);
mRemote.transact(Stub.TRANSACTION_correctionPosition, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
/**
	 * 功能：    设置自动判断 down 、 click和 up的状态
	 * @param int 
	 * <p>automicState = 0 不自动判断
	 * <p>automicState = 1 自动判断 down 和 up 消息
	 * <p>automicState = 2 自动判断 click 消息
	 * @return boolean
	 * <p> 调用成功：  true
	 * <p>调用失败： false
	 */
@Override public boolean setAutomicState(int state) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(state);
mRemote.transact(Stub.TRANSACTION_setAutomicState, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
/**
	 * 功能：    获取设置自动判断 down 、 click和 up的状态
	 * @return int
	 * <p>不自动判断 0 
	 * <p>自动判断 down 和 up 消息  1 
	 * <p>自动判断 click 消息  2 
	 */
@Override public int getAutomicState() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getAutomicState, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
/**
	 * 功能：   切换太空眼的连接
	 * @param String address
	 * <p>要切换的太空眼的地址
	 * @return boolean
	 * <p>切换成功返回 true
	 * <p>切换失败返回 false
	 */
@Override public boolean changeAirtouchAddress(java.lang.String address) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(address);
mRemote.transact(Stub.TRANSACTION_changeAirtouchAddress, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
/**
		 * 功能：   发送指令给太空眼
		 * @param byte command 指令
		 *		  <p>byte[] params 参数
		 * @return boolean
		 * <p>发送成功返回 true
		 * <p>发送失败返回 false
		 */
@Override public boolean sendCommandToAirtouch(byte command, byte[] params) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeByte(command);
_data.writeByteArray(params);
mRemote.transact(Stub.TRANSACTION_sendCommandToAirtouch, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
/**  获取发送指令给太空眼之后     太空眼返回的值
		 * @return byte[]
		 * <p>返回太空眼发送过来的指令
		 */
@Override public byte[] getResponseForAirtouch() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
byte[] _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getResponseForAirtouch, _data, _reply, 0);
_reply.readException();
_result = _reply.createByteArray();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
}
static final int TRANSACTION_setAirTouchEnable = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
static final int TRANSACTION_getAirTouchIsEnable = (android.os.IBinder.FIRST_CALL_TRANSACTION + 1);
static final int TRANSACTION_getCursorPosition = (android.os.IBinder.FIRST_CALL_TRANSACTION + 2);
static final int TRANSACTION_openAirtouch = (android.os.IBinder.FIRST_CALL_TRANSACTION + 3);
static final int TRANSACTION_openOuterAirtouch = (android.os.IBinder.FIRST_CALL_TRANSACTION + 4);
static final int TRANSACTION_closeOuterAirtouch = (android.os.IBinder.FIRST_CALL_TRANSACTION + 5);
static final int TRANSACTION_openInnerAirtouch = (android.os.IBinder.FIRST_CALL_TRANSACTION + 6);
static final int TRANSACTION_closeInnerAirtouch = (android.os.IBinder.FIRST_CALL_TRANSACTION + 7);
static final int TRANSACTION_releaseAirTouch = (android.os.IBinder.FIRST_CALL_TRANSACTION + 8);
static final int TRANSACTION_getOuterAirTouchState = (android.os.IBinder.FIRST_CALL_TRANSACTION + 9);
static final int TRANSACTION_getInnerAirTouchState = (android.os.IBinder.FIRST_CALL_TRANSACTION + 10);
static final int TRANSACTION_getCurrentAirTouchSensor = (android.os.IBinder.FIRST_CALL_TRANSACTION + 11);
static final int TRANSACTION_applyUseGesture = (android.os.IBinder.FIRST_CALL_TRANSACTION + 12);
static final int TRANSACTION_closeGestureUse = (android.os.IBinder.FIRST_CALL_TRANSACTION + 13);
static final int TRANSACTION_setAirTouchMode = (android.os.IBinder.FIRST_CALL_TRANSACTION + 14);
static final int TRANSACTION_getCurrentAirTouchMode = (android.os.IBinder.FIRST_CALL_TRANSACTION + 15);
static final int TRANSACTION_getAirTouchGesture = (android.os.IBinder.FIRST_CALL_TRANSACTION + 16);
static final int TRANSACTION_setCursorDown = (android.os.IBinder.FIRST_CALL_TRANSACTION + 17);
static final int TRANSACTION_setCursorUp = (android.os.IBinder.FIRST_CALL_TRANSACTION + 18);
static final int TRANSACTION_setCursorClick = (android.os.IBinder.FIRST_CALL_TRANSACTION + 19);
static final int TRANSACTION_getCursorAction = (android.os.IBinder.FIRST_CALL_TRANSACTION + 20);
static final int TRANSACTION_getAirTouchDeviceList = (android.os.IBinder.FIRST_CALL_TRANSACTION + 21);
static final int TRANSACTION_correctionPosition = (android.os.IBinder.FIRST_CALL_TRANSACTION + 22);
static final int TRANSACTION_setAutomicState = (android.os.IBinder.FIRST_CALL_TRANSACTION + 23);
static final int TRANSACTION_getAutomicState = (android.os.IBinder.FIRST_CALL_TRANSACTION + 24);
static final int TRANSACTION_changeAirtouchAddress = (android.os.IBinder.FIRST_CALL_TRANSACTION + 25);
static final int TRANSACTION_sendCommandToAirtouch = (android.os.IBinder.FIRST_CALL_TRANSACTION + 26);
static final int TRANSACTION_getResponseForAirtouch = (android.os.IBinder.FIRST_CALL_TRANSACTION + 27);
}
/**
	 * 功能：    设置太空眼是否可用(仅管理应用可以)
	 * @param boolean isenable 
	 * <p>设置可以使用true
	 * <p>设置不使用为false
	 * @return boolean 
	 * <p>设置成功： true
	 * <p>设置失败：false
	 */
public boolean setAirTouchEnable(boolean isenable) throws android.os.RemoteException;
/**
	 * 功能：    获取太空眼是否可用(仅管理应用可以)
	 * @return boolean
	 * <p>已经打开： true
	 * <p>已经禁用：false
	 */
public boolean getAirTouchIsEnable() throws android.os.RemoteException;
/**
	 * 功能：   获取手势坐标
	 * @return int[] 
	 * <p>返回长度为2 的整形数组，
	 * <p>int[]  i= getGesturePosition();
	 * <p>i[0] 为x轴坐标
	 * <p>i[1] 为y轴坐标
	 */
public int[] getCursorPosition() throws android.os.RemoteException;
/**
	 * 功能：   打开太空眼 （优先打开太空眼，如果内置的太空眼已打开则会被关闭，如果外置太空眼不可用则打开内置太空眼）
	 * @return int
	 * <p>打开成功:  i =  1
	 * <p>正在使用：     i =  2
	 * <p>打开失败:  i = -1
	 * <p>其它原因:  i = -99
	 */
public int openAirtouch() throws android.os.RemoteException;
/**
	 * 功能：   打开外置的太空眼
	 * @return int
	 * <p>打开成功:  i =  1
	 * <p>正在使用：     i =  2
	 * <p>打开失败:  i = -1
	 * <p>其它原因:  i = -99
	 */
public int openOuterAirtouch() throws android.os.RemoteException;
/**
	 * 功能：   关闭外置的太空眼
	 * @return boolean
	 * <p>释放成功为： true
	 * <p>释放失败为： false
	 */
public boolean closeOuterAirtouch() throws android.os.RemoteException;
/**
	 * 功能：   打开内置太空眼
	 * @return int
	 * <p>打开成功:  i =  1
	 * <p>正在使用：     i =  2
	 * <p>打开失败:  i = -1
	 * <p>其它原因:  i = -99
	 */
public int openInnerAirtouch() throws android.os.RemoteException;
/**
	 * 功能：   关闭内置的太空眼
	 * @return boolean
	 * <p>释放成功为： true
	 * <p>释放失败为： false
	 */
public boolean closeInnerAirtouch() throws android.os.RemoteException;
/**
	 * 功能：   释放所有的太空眼
	 * @return boolean
	 * <p>释放成功为： true
	 * <p>释放失败为： false
	 */
public boolean releaseAirTouch() throws android.os.RemoteException;
/**
	 * 功能：   获取太空眼的状态
	 * @return int
	 * <p>正在使用为： 2
	 * <p>没有使用为： 0
	 */
public int getOuterAirTouchState() throws android.os.RemoteException;
/**
	 * 功能：   获取内置sensor的状态
	 * @return int
	 * <p>正在使用为： 2
	 * <p>没有使用为： 0
	 */
public int getInnerAirTouchState() throws android.os.RemoteException;
/**
	 * 功能：   获取当前使用的设备
	 * @return int
	 * <p>太空眼设备： 1
	 * <p>内置的sensor： 2
	 * <p>没有使用设备： 0
	 */
public int getCurrentAirTouchSensor() throws android.os.RemoteException;
/**
	 * 功能：   申请使用手势
	 * @return boolean
	 * <p>设置成功： true
	 * <p>设置失败：false
	 */
public boolean applyUseGesture() throws android.os.RemoteException;
/**
	 * 功能：   关闭使用手势
	 * @return boolean
	 * <p>设置成功： true
	 * <p>设置失败：false
	 */
public boolean closeGestureUse() throws android.os.RemoteException;
/**
	 * 功能：   设置为手势模式或者是普通模式
	 * @param int type
	 * <p>普通模式  ：1
	 * <p>手势模式  ：2
	 * @return boolean
	 * <p>设置成功： true
	 * <p>设置失败：false
	 */
public boolean setAirTouchMode(int modeType) throws android.os.RemoteException;
/**
	 * 功能：   获取当前状态是手势模式或者是普通模式（默认为普通模式）
	 * @return int
	 * <p>普通模式  ：1
	 * <p>手势模式  ：2
	 * <p>没有设置  ：0
	 */
public int getCurrentAirTouchMode() throws android.os.RemoteException;
/**
	 * 功能：   获取当前手势的类型
	 * @return int
	 * <p>返回9种手势模式
	 * <p>没有手势：0
	 * <p>往右：1
	 * <p>往左：2
	 * <p>往上：3
	 * <p>往下：4
	 * <p>接近 sensor：5
	 * <p>远离 ensor：6
	 * <p>顺时针旋转：7
	 * <p>逆时针旋转：8
	 * <p>快速三次挥手：9
	 */
public int getAirTouchGesture() throws android.os.RemoteException;
/**
	 * 功能：   设置鼠标按下
	 * @return boolean
	 * <p>设置成功： true
	 * <p>设置失败：false
	 */
public boolean setCursorDown() throws android.os.RemoteException;
/**
	 * 功能：   设置鼠标弹起
	 * @return boolean
	 * <p>设置成功： true
	 * <p>设置失败：false
	 */
public boolean setCursorUp() throws android.os.RemoteException;
/**
	 * 功能：   设置鼠标点击
	 * @return boolean
	 * <p>设置成功： true
	 * <p>设置失败：false
	 */
public boolean setCursorClick() throws android.os.RemoteException;
/**
	 * 功能：   获取鼠标动作
	 * @return int 
	 * <p>按下为ACTION_DOWN:  1
	 * <p>弹起为ACTION_UP:  2
	 * <p>点击CLICK: 3 
	 * <p>没有设置NONE：0 
	 */
public int getCursorAction() throws android.os.RemoteException;
/**
	 * 功能：    模糊查询附近sensor设备的列表并返回
	 * @param String deviceName  
	 * <p>  需传入要搜索的设备的名字 
	 * <p>比如：
	 * <p>"Takee_Air_Touch_00"   
	 * <p>值为空则查询所有设备
	 * @return List &ltBluetoothDevice&gt 
	 * <p> 返回名字类似于Takee_Air_Touch_00的设备列表
	 * <p>如：
	 * <p> "Takee_Air_Touch_001"
	 * <p> "Takee_Air_Touch_002"
	 * <p> "Takee_Air_Touch_0011"
	 */
public java.util.List<android.bluetooth.BluetoothDevice> getAirTouchDeviceList(java.lang.String deviceName) throws android.os.RemoteException;
/**
	 * 功能：    校正坐标 
	 * @param int[] deviceName 
	 * <p> int[] i =new int[];
	 * <p> i[0]   为x轴的坐标
	 * <p>i[1]   为y轴的坐标
	 * @return boolean
	 * <p> 调用成功：  true
	 * <p>调用失败： false
	 */
public boolean correctionPosition(int[] position) throws android.os.RemoteException;
/**
	 * 功能：    设置自动判断 down 、 click和 up的状态
	 * @param int 
	 * <p>automicState = 0 不自动判断
	 * <p>automicState = 1 自动判断 down 和 up 消息
	 * <p>automicState = 2 自动判断 click 消息
	 * @return boolean
	 * <p> 调用成功：  true
	 * <p>调用失败： false
	 */
public boolean setAutomicState(int state) throws android.os.RemoteException;
/**
	 * 功能：    获取设置自动判断 down 、 click和 up的状态
	 * @return int
	 * <p>不自动判断 0 
	 * <p>自动判断 down 和 up 消息  1 
	 * <p>自动判断 click 消息  2 
	 */
public int getAutomicState() throws android.os.RemoteException;
/**
	 * 功能：   切换太空眼的连接
	 * @param String address
	 * <p>要切换的太空眼的地址
	 * @return boolean
	 * <p>切换成功返回 true
	 * <p>切换失败返回 false
	 */
public boolean changeAirtouchAddress(java.lang.String address) throws android.os.RemoteException;
/**
		 * 功能：   发送指令给太空眼
		 * @param byte command 指令
		 *		  <p>byte[] params 参数
		 * @return boolean
		 * <p>发送成功返回 true
		 * <p>发送失败返回 false
		 */
public boolean sendCommandToAirtouch(byte command, byte[] params) throws android.os.RemoteException;
/**  获取发送指令给太空眼之后     太空眼返回的值
		 * @return byte[]
		 * <p>返回太空眼发送过来的指令
		 */
public byte[] getResponseForAirtouch() throws android.os.RemoteException;
}
