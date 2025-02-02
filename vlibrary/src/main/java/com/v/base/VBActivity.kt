package com.v.base

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.gyf.immersionbar.ktx.immersionBar
import com.noober.background.BackgroundLibrary
import com.v.base.databinding.VbRootActivityBinding
import com.v.base.dialog.VBLoadingDialog
import com.v.base.utils.*
import java.lang.reflect.ParameterizedType


abstract class VBActivity<VB : ViewDataBinding, VM : VBViewModel> : AppCompatActivity() {


    lateinit var mContext: AppCompatActivity


    val mTitleBar by lazy {
        mRootDataBinding.vbTitleBar
    }

    val mRootDataBinding by lazy {
        mContext.vbGetDataBinding<VbRootActivityBinding>(R.layout.vb_root_activity)
    }


    /**
     * 通过反射拿到ViewModel并且注册
     */
    protected val mViewModel: VM by lazy {
        val type = javaClass.genericSuperclass as ParameterizedType
        val aClass = type.actualTypeArguments[1] as Class<VM>
        aClass.getDeclaredConstructor().isAccessible = true
        if (useViewModelApplication()) {
            getApplicationViewModel(application, aClass)
        } else {
            ViewModelProvider(this).get(aClass)
        }

    }

    /**
     * 通过反射拿到DataBinding
     */
    protected val mDataBinding: VB by lazy {
        val type = javaClass.genericSuperclass as ParameterizedType
        val aClass = type.actualTypeArguments[0] as Class<*>
        val method = aClass.getDeclaredMethod("inflate", LayoutInflater::class.java)
        method.isAccessible = true
        method.invoke(null, layoutInflater) as VB
    }

    /**
     * 初始化加载框
     */
    private val loadDialog by lazy {
        VBLoadingDialog(this).apply {
            setDialogCancelable(false)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        //注册BackgroundLibrary 可以直接在xml里面写shape
        BackgroundLibrary.inject2(this)
        super.onCreate(savedInstanceState)
        mContext = this

        //把mDataBinding添加到baseDataBinding里面去
        if (toolBarTitle()) {
            val rootView: View = mRootDataBinding.root
            mRootDataBinding.layoutContent.addView(mDataBinding.root)
            super.setContentView(rootView)
        } else {
            super.setContentView(mDataBinding.root)
        }

        mRootDataBinding.lifecycleOwner = this
        mDataBinding.lifecycleOwner = this

        //注册加载框
        registerUiChange()
        initToolBar()
        initData()
        createObserver()
        javaClass.name.log()
    }


    private fun initToolBar() {
        statusBarColor()
        toolBarTitle()
    }


    /**
     * 设置状态栏颜色
     * @param color 颜色
     * @param isDarkFont  状态栏字体深色或亮色
     */
    protected open fun statusBarColor(
        color: String = VBConfig.options.statusBarColor,
        isDarkFont: Boolean = isWhiteColor(Color.parseColor(color)),
    ) {
        immersionBar {
            if (useTranslucent()) {
                transparentStatusBar()  //沉浸式状态栏(布局会顶进状态栏)
            } else {
                statusBarColor(color)     //状态栏颜色，不写默认透明色
                fitsSystemWindows(true)  //使用该属性,必须指定状态栏颜色
            }
            //状态栏颜色趋近于白色时，会智能将状态栏字体颜色变换为黑色
            statusBarDarkFont(isDarkFont)
            navigationBarDarkIcon(isDarkFont)
        }


    }

    /**
     * 显示Toolbar
     * @param title 文字 [title]不为空才会显示TitleBar
     * @param titleColor 文字颜色
     * @param isShowBottomLine 是否显示Toolbar下面的分割线
     * @param resLeft 返回键图片
     * @param listenerLeft 返回键点击事件
     */
    protected open fun toolBarTitle(
        title: String = "",
        titleColor: Int = VBConfig.options.toolbarTitleColor,
        isShowBottomLine: Boolean = true,
        resLeft: Int = VBConfig.options.toolbarBackRes,
        listenerLeft: View.OnClickListener? = null,
    ): Boolean {
        return if (title.isNullOrEmpty()) {
            false
        } else {
            mTitleBar.useToolbar(true)
            mTitleBar.setTitle(title, titleColor, isShowBottomLine)
            if (listenerLeft == null) {
                mTitleBar.setLeft(resLeft) {
                    finish()
                }
            } else {
                mTitleBar.setLeft(resLeft, listenerLeft)
            }
            true
        }
    }

    /**
     * 是否沉浸式状态栏
     */
    protected open fun useTranslucent(): Boolean = false


    /**
     * ViewModel是否绑定Application生命周期
     */
    protected open fun useViewModelApplication(): Boolean = false

    /**
     * 初始化数据
     */
    protected abstract fun initData()

    /**
     * liveData 数据监听
     */
    protected abstract fun createObserver()


    /**
     * 注册UI 事件
     */
    private fun registerUiChange() {
        //显示弹窗
        mViewModel.loadingChange.showDialog.observe(this, Observer {
            if (!loadDialog.isShowing) {
                loadDialog.show()
                loadDialog.setMsg(it)
            }
        })
        //关闭弹窗
        mViewModel.loadingChange.dismissDialog.observe(this, Observer {
            if (loadDialog.isShowing) {
                loadDialog.dismiss()
            }

        })

        //toast
        mViewModel.loadingChange.showToast.observe(this, Observer {
            if (!it.isNullOrEmpty()) {
                it.toast()
            }

        })
    }

    override fun onDestroy() {
        super.onDestroy()
        mDataBinding.unbind()
        mRootDataBinding.unbind()
    }

}