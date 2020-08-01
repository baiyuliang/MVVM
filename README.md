# MVVM

Kotlin+MVVM+Retrofit+协程+ViewBinding+EventBus

项目框架整体架构图：

![架构图](https://img-blog.csdnimg.cn/20200601152544441.png)

## 本框架的特点

1. 使用 Kotlin 语言，减少代码量；

2. 使用 MVVM + 协程开发模式，相较于常用的 MVP+RxJava 开发模式，会减省大量的 MvpView 的创建及大量的接口回调，并且不再需要 Presenter 的注册和注销，减少内存泄漏风险；

3. ViewBinding（根据 xml 自动生成）将会使你不再需要进行 findViewById 的繁琐工作，比 ButterKnife 更加方便；

4. 关于消息传递，GitHub 上有 LiveData 改造的 LiveDataBus，作用及使用方法都类似于 EventBus，而本项目选择继续使用 EventBus 的原因，则是因为 EventBus 的强大以及它的稳定性和灵活性； 

## Example

### 编写 Activity
只需要传入对应的 ViewModel 和 ViewBinding 即可，abstract 方法自定义。

```kotlin
class TestActivity : BaseActivity<TestViewModel, ActivityTestBinding>() {

    override fun initView() {

    }

    override fun initClick() {
     
    }

    override fun initData() {

    }

    override fun initVM() {

    }

}
```

> 无对应的 ViewModel，传入 BaseViewModel 即可。

Fragment 同！

### 编写 Adapter
只需要传入数据 model 类型和 item 的 ViewBinding 即可。

```kotlin
class ArticleListAdapter(context: Activity, listDatas: ArrayList<ArticleBean>) :
    BaseAdapter<ItemArticleBinding, ArticleBean>(context, listDatas) {

    override fun convert(holder: BaseViewHolder, t: ArticleBean, position: Int) {
        val v = holder.v as ItemArticleBinding
        Glide.with(mContext).load(t.envelopePic).into(v.ivCover)
        v.tvTitle.text = t.title
        v.tvDes.text = t.desc
    }

}
```

一个 `convert` 方法解决，注意 `val v = holder.v as ItemArticleBinding` 必须写！

### 添加接口（ApiService）

```kotlin
@GET("test")
suspend fun test(@QueryMap options: HashMap<String, String?>): BaseResult<TestModel>
```

注意：`suspend` 不可缺少！

### 创建 ViewModel

```kotlin
class MainViewModel : BaseViewModel() {

    var articlesData = MutableLiveData<ArticleListBean>()

    fun getArticleList(page: Int, isShowLoading: Boolean) {
        launch({ httpUtil.getArticleList(page) }, articlesData, isShowLoading)
    }

}
```

### 调用接口

在 Activity 或 Fragment 中直接通过传入的 ViewModel 调用：

```kotlin
vm.getArticleList()//调用接口

vm.articlesData.observe(this, Observer {//返回结果
   
})
```

### 消息传递

本项目中，像 EventBus 的注册与注销，以及消息接收全部放在了 BaseActivity 中，并提供了一个对外的消息处理方法，利用消息 Code 来区分不同消息，在需要使用消息的界面，重写该方法即可：

```kotlin
// 发送消息：
Event.post(EventMessage(EventCode.REFRESH))

/**
 * 接收消息
 */
override fun handleEvent(msg: EventMessage) {
    super.handleEvent(msg)
    if (msg.code == EventCode.REFRESH) {
        showMessage("主页：刷新")
        page = 0
        vm.getArticleList(page, false)
    }
}
```

这样做的好处是：

1. 不再需要你去手动在每个界面去注册和注销 EventBus，你只用关心什么时候 post 消息，和什么时间接收消息即可，大大减少出错几率，并提高代码可读性；

2. 可以随时更换消息传递框架，方便快捷；

当然，缺点就是发送一个消息，所有活动界面都会收到，个人认为利大于弊，弊则可以忽略。



该框架已应用到自己公司项目中，运行良好，如果后续发现有坑的地方，会及时更新！

## 更新

### 2020.06.05

接口调用流程简化，新增接口只需要在 ApiService 中添加后，即可直接在 ViewModel 中通过 httpUtil 调用，一步到位！

另附上文件上传案例代码，需要时以作参考：

```kotlin
fun uploadFile(path: String) {
    val file = File(path)
    val map: HashMap<String, RequestBody> = LinkedHashMap()
    val requestBody: RequestBody = RequestBody.create(MediaType.parse("image/*"), file)
    map["file\"; filename=\"" + file.name] = requestBody//file为后台规定参数
    map["name"] = RequestBody.create(MediaType.parse("text/plain"), file.name)
    map["arg1"] = RequestBody.create(MediaType.parse("text/plain"), "arg1")//普通参数
    map["arg2"] = RequestBody.create(MediaType.parse("text/plain"), "arg2")

    //签名（根据服务器规则）
    val params = LinkedHashMap<String, String?>()
    params["name"] = file.name
    params["arg1"] = "arg1"
    params["arg2"] = "arg2"
    val sign: String = getSign(params)
    map["sign"] = RequestBody.create(MediaType.parse("text/plain"), sign)

    launch({ httpUtil.upLoadFile(map) }, uploadData)
 }
```

ApiService:

```kotlin
@Multipart
@POST("/upload")
suspend fun upLoadFile(@PartMap map: HashMap<String, RequestBody>): BaseResult<UploadModel>
```

### 2020.06.15

在使用 viewpager+fragment 过程中发现，某些机型应用在按返回键退出时，fragment 中的 contentView 未销毁：

```kotlin
if (null == contentView) {
    contentView = v.root
    //...
}
return contentView
```

导致再次打开 App 时，fragment 并未重建，直接用的原来缓存在内存中的 View 致使页面出现问题。对于这种情况，目前的解决办法是在 Fragment 销毁时，将 contentView = null：

```kotlin
override fun onDestroyView() {
    super.onDestroyView()
    contentView = null
}
```

