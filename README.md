# MVVM
Kotlin+MVVM+Retrofit+协程+ViewBinding+EventBus

注意：使用ViewBinding需要AndroidStudio版本为4.0+

#本框架的特点：
1.使用Kotlin语言  
2.使用MVVM+协程开发模式，相较于常用的MVP+RXJava开发模式，会减省大量的MvpView的创建，以及大量的接口回调，并且不再需要Presenter的注册和注销，减少内存泄漏风险  
3.ViewBinding将会使你不再需要进行findViewById的繁琐工作，比ButterKinfer更加方便  
4.关于消息传递，github有LiveData改造的LiveDataBus，作用及使用方法都类似于EventBus，而本项目选择继续使用EventBus的原因，则是因为EventBus的稳定性和灵活性  

#Example
编写Activity：

    class TestActivity : BaseActivity<BaseViewModel, ActivityTestBinding>() {
    
    
        override fun initView() {
    
        }
    
        override fun initClick() {
         
        }
    
        override fun initData() {
    
        }
    
        override fun initVM() {
    
        }
    
    }

Fragment同！

列表adapter：

    class ArticleListAdapter(context: Activity, listDatas: ArrayList<ArticleBean>) :
        BaseAdapter<ItemArticleBinding, ArticleBean>(context, listDatas) {
    
        override fun convert(holder: BaseViewHolder, t: ArticleBean, position: Int) {
            val v = holder.v as ItemArticleBinding
            Glide.with(mContext).load(t.envelopePic).into(v.ivCover)
            v.tvTitle.text = t.title
            v.tvDes.text = t.desc
        }
    
    }
