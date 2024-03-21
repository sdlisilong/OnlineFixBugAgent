功能：

通过该项目，可以达到实时更新运行中的方法区信息，达到热更新。

比如：需要修复一个bug，只需要将修复后的源码放入文本框中，然后提交，便可自动编译成class并装载到JVM中，达到热修复的功能。

目前只是demo版本。

依赖JVM底层暴露的扩展接口JVMTI(JVM Tool Interface),通过attach形式加载(agent on attach),将修改后的类信息加载到正在运行的JVM中。

步骤：

1.先打包OnlineFixBugAgent项目，生成agent。

2.运行demo类com.handle.Main，启动项目，循环调用Print.print类

3.运行com.handle.AttachTest类，执行热更新操作，替换Print.print方法。


com.handle.Main类修改：

1.com.handle.AttachTest.OnlineFixBugAgentPath修改为步骤1生成的agent路径
