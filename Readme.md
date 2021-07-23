* Mvp实现
* `RxJava` & `RxAndroid`
* 权限适配 `RxPermission`
* 事件订阅默认采用 `RxBus`
* 网络交互：
	* `Retrofit` + `rx`
	* `Https`
	* **统一异常处理**
	* 缓存
	* **支持多个baseUrl**
	* 。。。。
* 无需担心rx内存泄漏

保留：
* 提供`XActivity`、`XFragment`、`SimpleRecAdapter`、`SimpleListAdapter`等基类，可快速进行开发
* 完整封装`XRecyclerView`，可实现绝大部分需求
* `XStateController`、`XRecyclerContentLayout`实现loading、error、empty、content四种状态的自由切换
* 实现了`Memory`、`Disk`、`SharedPreferences`三种方式的缓存，可自由扩展
* 内置了`RxBus`，可自由切换到其他事件订阅库
* 内置`Glide`，可自由切换其他图片加载库
* 可输出漂亮的`Log`，支持`Json`、`Xml`、`Throwable`等，蝇量级实现
* 内置链式路由
* 内置常用工具类：`package`、`random`、`file`...,提供的都是非常常用的方法
* 内置加密工具类 `XCodec`，你想要的加密姿势都有
