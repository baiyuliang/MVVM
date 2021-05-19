# MVVM
Kotlin+MVVM+Retrofit+协程+ViewBinding+EventBus

注意：使用ViewBinding需要AndroidStudio版本为4.0+

项目框架整体架构图：

![架构图](https://img-blog.csdnimg.cn/20200601152544441.png)

## 本框架的特点：  

1.使用Kotlin语言

2.使用MVVM+协程开发模式，相较于常用的MVP+RXJava开发模式，会减省大量的MvpView的创建及大量的接口回调，并且不再需要Presenter的注册和注销，减少内存泄漏风险 
 
3.ViewBinding（根据xml自动生成），你将不再需要进行findViewById的繁琐工作，比ButterKinfer更加方便  

4.关于消息传递，github上有基于LiveData的LiveEventBus(https://github.com/JeremyLiao/LiveEventBus)，优点是具有生命周期感知能力，不需要主动注册和注销，但缺点是书写相对麻烦，且无法统一配置，衍生版SmartEventBus虽然支持定制，但配置依然麻烦，而本项目选择继续使用EventBus的原因，则是因为EventBus的强大以及它的稳定性和灵活性，且方便统一配置（下面有讲到）；

## Example

## 编写Activity（只需要传入对应的ViewModel和ViewBinding即可）：

    class TestActivity : BaseActivity<TestViewModel, ActivityTestBinding>() {
    
    
    }

Fragment同！

## 编写Adapter（只需要传入数据model类型和item的ViewBinding即可）：

    class ArticleListAdapter(context: Activity, listDatas: ArrayList<ArticleBean>) :
        BaseAdapter<ItemArticleBinding, ArticleBean>(context, listDatas) {
    
        override fun convert(v: ItemArticleBinding, t: ArticleBean, position: Int) {
            Glide.with(mContext).load(t.envelopePic).into(v.ivCover)
            v.tvTitle.text = t.title
            v.tvDes.text = t.desc
        }
    
    }

## 添加接口（ApiService）:

    @GET("test")
    suspend fun test(@QueryMap options: HashMap<String, String?>): BaseResult<TestModel>
    
注意：suspend不可缺少！

## 创建ViewModel：

    class MainViewModel : BaseViewModel() {
    
        var articlesData = MutableLiveData<ArticleListBean>()
    
        fun getArticleList(page: Int, isShowLoading: Boolean) {
            launch({ httpUtil.getArticleList(page) }, articlesData, isShowLoading)
        }
    
    }
    
## 调用接口：
在Activity或Fragment中直接通过传入的ViewModel调用：

    vm.getArticleList()//调用接口
    
    vm.articlesData.observe(this, Observer {//返回结果
       
    })

## 消息传递：

本项目中，像EventBus的注册与注销，以及消息接收全部放在了BaseActivity中，并提供了一个对外的消息处理方法，利用消息Code来区分不同消息，在需要使用消息的界面，重写该方法即可：

    发送消息：App.post(EventMessage(EventCode.REFRESH))

        /**
         * 接收消息
         */
        override fun handleEvent(msg: EventMessage) {
            super.handleEvent(msg)
            if (msg.code == EventCode.REFRESH) {
                ToastUtil.showToast(mContext, "主页：刷新")
                page = 0
                vm.getArticleList(page,false)
            }
        }

这样做的好处是

1：不在需要你去手动在每个界面去注册和注销EventBus，你只用关心什么时候post消息，和什么时间接受消息即可，大大减少出错几率，并提高代码可读性；

2：可以随时更换消息传递框架，方便快捷；

当然，缺点，只有一个，就是发送消息所有活动界面都会收到，但这个所谓的缺点其实完全可以忽略！

该框架已应用到自己公司项目中，运行良好，如果后续发现有坑的地方，会及时更新！
        
## 2021.5.19 更新内容：

1.使用协程请求接口时，不再需要withContext-IO，有suspend关键字即可；

2.将UI更新部分，放在了viewmodel中进行，在ui中仅调用接口请求方法即可，例：

    class MainActivityViewModel : BaseViewModel() {
    
        var articlesData = MutableLiveData<ArticleListBean>()
    
        fun getArticleList(page: Int, isShowLoading: Boolean = false) {
            launch({ httpUtil.getArticleList(page) }, articlesData, isShowLoading)
        }
    
        override fun observe(activity: Activity, owner: LifecycleOwner, viewBinding: ViewBinding) {
            val mContext = activity as MainActivity
            val vb = viewBinding as ActivityMainBinding
            articlesData.observe(owner, Observer {
                vb.refreshLayout.finishRefresh()
                vb.refreshLayout.finishLoadMore()
                if (mContext.page == 0) mContext.list!!.clear()
                it.datas?.let { it1 -> mContext.list!!.addAll(it1) }
                mContext.adapter!!.notifyDataSetChanged()
            })
            errorData.observe(owner, Observer {
                vb.refreshLayout.finishRefresh()
                vb.refreshLayout.finishLoadMore()
            })
        }
    }

observe方法在BaseActivity和BaseFragment中调用，子ViewModel中重写即可，重点是有两个强制转化：

     val mContext = activity as MainActivity
     val vb = viewBinding as ActivityMainBinding
     
mContext也可以是Fragment，即获取该ui界面声明的变量，vb则是当前ui的ViewBinding！

当然，这不是强制的，你也可以选择不使用这种方式，依然在ui界面更新ui！  

第二种方式：在BaseViewModel中传入VB泛型,这样就不需要再传入ViewBinding强转了（可以对比一下第一种和第二种写法）：

      abstract class BaseActivity<VM : BaseViewModel<VB>, VB : ViewBinding> : AppCompatActivity() {
          lateinit var mContext: FragmentActivity
          lateinit var vm: VM
          lateinit var vb: VB
      
          private var loadingDialog: ProgressDialog? = null
      
          @Suppress("UNCHECKED_CAST")
          override fun onCreate(savedInstanceState: Bundle?) {
              super.onCreate(savedInstanceState)
              initResources()
              var pathfinders = ArrayList<GenericParadigmUtil.Pathfinder>()
              pathfinders.add(GenericParadigmUtil.Pathfinder(0, 0))
              val clazzVM = GenericParadigmUtil.parseGenericParadigm(javaClass, pathfinders) as Class<VM>
              vm = ViewModelProvider(this).get(clazzVM)
      
              pathfinders = ArrayList()
              pathfinders.add(GenericParadigmUtil.Pathfinder(0, 1))
              val clazzVB = GenericParadigmUtil.parseGenericParadigm(javaClass, pathfinders)
              val method = clazzVB.getMethod("inflate", LayoutInflater::class.java)
              vb = method.invoke(null, layoutInflater) as VB
      
              vm.binding(vb)
              vm.observe(this, this)
      
              setContentView(vb.root)
              
              ...
               
    open class BaseViewModel<VB : ViewBinding> : ViewModel() {
    
        lateinit var vb: VB
    
        fun binding(vb: VB) {
            this.vb = vb
        }
    
        open fun observe(activity: Activity, owner: LifecycleOwner) {
    
        }
    
        open fun observe(fragment: Fragment, owner: LifecycleOwner) {
    
        }
        
    class MainActivityViewModel : BaseViewModel<ActivityMainBinding>() {
    
        var articlesData = MutableLiveData<ArticleListBean>()
    
        fun getArticleList(page: Int, isShowLoading: Boolean = false) {
            launch({ httpUtil.getArticleList(page) }, articlesData, isShowLoading)
        }
    
        override fun observe(activity: Activity, owner: LifecycleOwner) {
            val mContext = activity as MainActivity
            articlesData.observe(owner, Observer {
                vb.refreshLayout.finishRefresh()
                vb.refreshLayout.finishLoadMore()
                if (mContext.page == 0) mContext.list!!.clear()
                it.datas?.let { it1 -> mContext.list!!.addAll(it1) }
                mContext.adapter!!.notifyDataSetChanged()
            })
            errorData.observe(owner, Observer {
                vb.refreshLayout.finishRefresh()
                vb.refreshLayout.finishLoadMore()
            })
        }
    }       

## 2020.9.23 简化Adapter

子Adapter继承BaseAdapter，不需要再强转ViewBinding了：

BaseAdapter：

        override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        convert(holder.v as VB, listDatas[position], position)
    }

    abstract fun convert(v: VB, t: T, position: Int)

子类Adapter：

        override fun convert(v: ItemArticleBinding, t: ArticleBean, position: Int) {
        Glide.with(mContext).load(t.envelopePic).into(v.ivCover)
        v.tvTitle.text = t.title
        v.tvDes.text = t.desc
    }
    
直接传入item对应的ViewBinding对象，更加简单便捷！

    
## 2020.08.31

关于BaseAdapter，这里解释下原来的说明，为什么recycleview的item高度要设置为wrap？

由于item的ViewBindding也是通过反射得到，但得到后itemView的宽高会自动被系统设为wrap，所以这里需要重新赋值宽高，之前的做法是将父容器宽高给了item，这里有问题，item的父容器就是RecyclerView，所以如果RecyclerView设置了宽高后，item显示就出问题了，因此，现在修改为item重置自身宽高，宽度match_parent，高度wrap_content，此时就要注意，item的最外层父布局的的宽高同样为match_parent和wrap_content，这适用于大多数item的布局，如果确实有需求要对item设置固定宽高，建议在子Adapter中通过代码动态设置宽高！

     vb.root.layoutParams = RecyclerView.LayoutParams(
            RecyclerView.LayoutParams.MATCH_PARENT,
            RecyclerView.LayoutParams.WRAP_CONTENT
        )
        
## 2020.06.15

在使用viewpager+fragment过程中发现，某些机型应用在按返回键退出时，fragment中的contentView未销毁：

        if (null == contentView) {
            contentView = v.root
            //...
        }
        return contentView
        
导致再次打开app时，fragment并未重建，直接用的原来缓存在内存中的View致使页面出现问题，对于这种情况，目前的解决办法是在Fragment销毁时，将contentView=null:

        override fun onDestroyView() {
        super.onDestroyView()
        contentView = null
    }


## 2020.06.05：
接口调用流程简化，新增接口只需要在ApiService中添加后，即可直接在ViewModel中通过httpUtil调用，一步到位！

另附上文件上传案例代码，需要时以作参考：

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

ApiService:

        @Multipart
        @POST("/upload")
        suspend fun upLoadFile(@PartMap map: HashMap<String, RequestBody>): BaseResult<UploadModel>



    