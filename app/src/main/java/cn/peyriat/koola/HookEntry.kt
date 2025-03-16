package cn.peyriat.koola
import android.app.Activity
import android.view.ViewGroup
import android.widget.TextView
import cn.peyriat.koola.util.LogUtils
import com.highcapable.yukihookapi.YukiHookAPI
import com.highcapable.yukihookapi.annotation.xposed.InjectYukiHookWithXposed
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.type.android.BundleClass
import com.highcapable.yukihookapi.hook.type.android.ContextClass
import com.highcapable.yukihookapi.hook.type.java.AnyClass
import com.highcapable.yukihookapi.hook.type.java.StringClass
import com.highcapable.yukihookapi.hook.xposed.proxy.IYukiHookXposedInit

@InjectYukiHookWithXposed
class HookEntry:IYukiHookXposedInit {
    private var floatingView: TextView? = null
    override fun onHook() {
        YukiHookAPI.encase {
            loadApp(true) {
                if (packageName == "com.google.android.webview") {return@loadApp}
                "com.netease.android.protect.StubApp".toClass().apply {
                    method {
                        name = "attachBaseContext"
                        param(ContextClass)
                    }.hook {
                        after {
                            loadHooker(ActivityHook)
                            loadHooker(RNhook)
                            loadHooker(LibHook)
                        }
                    }
                }
            }
        }
    }
    object RNhook:YukiBaseHooker() {
        override fun onHook() {
            "com.mojang.minecraftpe.MainActivity".toClass().apply {
                method {
                    name = "nativeJsCallCpp"
                    param(StringClass)
                }.hook {
                    before {
                        LogUtils.javaLog("RNMCBridge callCpp: ${args}}")
                    }
                }
                method{
                    name = "nativeGetUserDataPath"
                    returnType = StringClass
                }.hook{
                    after {
                        LogUtils.javaLog("hi")
                        LogUtils.javaLog("nativeGetUserDataPath: ${result as String}}")
                    }
                }
            }
        }
    }

    object LibHook : YukiBaseHooker() {
        override fun onHook() {
            Runtime::class.java.apply {
                method {
                    name = "loadLibrary0"
                    param(Class::class.java, StringClass)
                }.hook {
                    after {
                        LogUtils.javaLog(args[1] as String)
                        if (args[1] as String != "minecraftpe") {
                            return@after
                        }
                        if (NativeHook.getPlayer() == 0) {
                            LogUtils.javaLog("hook success")
                        } else {
                            LogUtils.javaLog("hook failed")
                        }

                    }
                }
            }
        }
    }

    object ActivityHook : YukiBaseHooker() {
        override fun onHook() {
            "com.mojang.minecraftpe.MainActivity".toClass().apply {
                method {
                    name = "onCreate"
                    param(BundleClass)
                }.hook {
                    after {
                        val activity = instance as? Activity ?: return@after

                    }
                }
            }
        }

    }



}

