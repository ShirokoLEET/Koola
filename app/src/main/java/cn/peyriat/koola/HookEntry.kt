package cn.peyriat.koola
import android.app.Activity
import cn.peyriat.koola.util.LogUtils
import com.highcapable.yukihookapi.YukiHookAPI
import com.highcapable.yukihookapi.annotation.xposed.InjectYukiHookWithXposed
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.type.android.BundleClass
import com.highcapable.yukihookapi.hook.type.android.ContextClass
import com.highcapable.yukihookapi.hook.type.java.StringClass
import com.highcapable.yukihookapi.hook.xposed.proxy.IYukiHookXposedInit

@InjectYukiHookWithXposed
class HookEntry:IYukiHookXposedInit {
    override fun onHook() {
        YukiHookAPI.encase {
            loadApp(true) {
                "com.netease.android.protect.StubApp".toClass().apply {
                    method {
                        name = "attachBaseContext"
                        param(ContextClass)
                    }.hook {
                        after {
                            loadHooker(ActivityHook)
                            loadHooker(LibHook)
                        }
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
                        NativeHook.starthook()
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

